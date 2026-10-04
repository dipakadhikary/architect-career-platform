import { ThemeProvider, createTheme } from '@mui/material/styles';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AiAuthoringPanel } from '@/features/ai/components/AiAuthoringPanel';
import type { AuthoringProposal } from '@/features/ai/types/ai.types';
import type { KnowledgeNoteResponse } from '@/features/knowledge/types/knowledge.types';
import { ApiClientError } from '@/shared/api/types';

vi.mock('@/features/ai/api/ai.api', () => ({
  aiApi: {
    generateProposal: vi.fn(),
    editProposal: vi.fn(),
    acceptProposal: vi.fn(),
    rejectProposal: vi.fn(),
    regenerateProposal: vi.fn(),
  },
}));

vi.mock('@/features/knowledge/api/knowledge.api', () => ({
  knowledgeApi: {
    update: vi.fn(),
  },
}));

import { aiApi } from '@/features/ai/api/ai.api';
import { knowledgeApi } from '@/features/knowledge/api/knowledge.api';

const generateProposal = vi.mocked(aiApi.generateProposal);
const editProposal = vi.mocked(aiApi.editProposal);
const acceptProposal = vi.mocked(aiApi.acceptProposal);
const rejectProposal = vi.mocked(aiApi.rejectProposal);
const updateNote = vi.mocked(knowledgeApi.update);

const note: KnowledgeNoteResponse = {
  id: 'note-1',
  title: 'Kafka',
  summary: 'A log',
  content: 'Current note',
  category: null,
  tags: [],
  createdAt: '2026-10-04T00:00:00Z',
  updatedAt: '2026-10-04T00:00:00Z',
  version: 10,
};

function proposal(status: AuthoringProposal['status'], content = '## Improved\n\nA clearer note.'): AuthoringProposal {
  return {
    proposalId: 'proposal-1',
    operation: 'IMPROVE',
    status,
    content,
    questions: [],
    sources: [
      {
        contentId: 'note-1',
        title: 'Kafka',
        contentType: 'NOTE',
        section: 'Intro',
        path: 'Kafka',
        url: '/knowledge/note-1',
        chunkId: 'chunk-1',
        score: 0.4,
      },
    ],
    warnings: [],
    model: 'fake',
    provider: 'fake',
    promptVersion: 'v1',
    grounded: true,
    authoritative: false,
    contentId: 'note-1',
    sourceVersion: 10,
  };
}

function renderPanel() {
  return render(
    <ThemeProvider theme={createTheme()}>
      <MemoryRouter>
        <AiAuthoringPanel note={note} onSaved={vi.fn()} />
      </MemoryRouter>
    </ThemeProvider>,
  );
}

describe('AiAuthoringPanel', () => {
  beforeEach(() => {
    generateProposal.mockReset();
    editProposal.mockReset();
    acceptProposal.mockReset();
    rejectProposal.mockReset();
    updateNote.mockReset();
  });

  it('previews a draft and does not save until it is accepted', async () => {
    generateProposal.mockResolvedValue(proposal('GENERATED'));
    editProposal.mockResolvedValue(proposal('EDITING', 'Edited draft'));
    acceptProposal.mockResolvedValue(proposal('APPROVED', 'Edited draft'));
    updateNote.mockResolvedValue({ ...note, content: 'Edited draft', version: 11 });
    const user = userEvent.setup();
    renderPanel();

    await user.click(screen.getByRole('button', { name: 'AI Assist' }));
    await user.click(screen.getByRole('menuitem', { name: 'Improve' }));
    await user.click(screen.getByRole('button', { name: 'Generate' }));

    expect(await screen.findAllByText('AI generated draft')).not.toHaveLength(0);
    expect(screen.getByText('Not published')).toBeInTheDocument();
    expect(screen.getByText('Current note')).toBeInTheDocument();
    expect(updateNote).not.toHaveBeenCalled();

    const editor = screen.getByLabelText('Edit draft');
    await user.clear(editor);
    await user.type(editor, 'Edited draft');
    await user.click(screen.getByRole('button', { name: 'Accept' }));
    expect(updateNote).not.toHaveBeenCalled();
    const save = await screen.findByRole('button', { name: 'Save to ACOS' });
    await waitFor(() => expect(save).toBeEnabled());
    await user.click(save);
    expect(updateNote).toHaveBeenCalledWith(
      'note-1',
      expect.objectContaining({ content: 'Edited draft', expectedVersion: 10 }),
    );
  });

  it('rejects a draft without updating the note', async () => {
    generateProposal.mockResolvedValue(proposal('GENERATED'));
    rejectProposal.mockResolvedValue(proposal('REJECTED'));
    const user = userEvent.setup();
    renderPanel();
    await user.click(screen.getByRole('button', { name: 'AI Assist' }));
    await user.click(screen.getByRole('menuitem', { name: 'Summarize' }));
    await user.click(screen.getByRole('button', { name: 'Generate' }));
    await user.click(await screen.findByRole('button', { name: 'Reject' }));
    expect(rejectProposal).toHaveBeenCalledWith('proposal-1');
    expect(updateNote).not.toHaveBeenCalled();
  });

  it('shows a version conflict and does not hide the latest note', async () => {
    generateProposal.mockResolvedValue(proposal('GENERATED'));
    editProposal.mockResolvedValue(proposal('EDITING'));
    acceptProposal.mockResolvedValue(proposal('APPROVED'));
    updateNote.mockRejectedValue(
      new ApiClientError('KnowledgeNote changed. Reload the latest note before saving.', {
        code: 'VERSION_CONFLICT',
        status: 409,
      }),
    );
    const user = userEvent.setup();
    renderPanel();
    await user.click(screen.getByRole('button', { name: 'AI Assist' }));
    await user.click(screen.getByRole('menuitem', { name: 'Improve' }));
    await user.click(screen.getByRole('button', { name: 'Generate' }));
    await user.click(await screen.findByRole('button', { name: 'Accept' }));
    await user.click(await screen.findByRole('button', { name: 'Save to ACOS' }));
    expect(await screen.findByText(/changed after the draft was created/i)).toBeInTheDocument();
  });

  it('does not render script markup from a draft', async () => {
    generateProposal.mockResolvedValue(
      proposal('GENERATED', '<script>alert(1)</script>\n\n[click](javascript:alert(1))'),
    );
    const user = userEvent.setup();
    renderPanel();
    await user.click(screen.getByRole('button', { name: 'AI Assist' }));
    await user.click(screen.getByRole('menuitem', { name: 'Explain' }));
    await user.click(screen.getByRole('button', { name: 'Generate' }));
    expect(await screen.findByText('click')).toBeInTheDocument();
    expect(document.querySelector('script')).toBeNull();
    for (const link of document.querySelectorAll('a')) {
      expect(link.getAttribute('href') ?? '').not.toMatch(/^javascript:/i);
    }
  });
});
