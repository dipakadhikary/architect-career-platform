import { ThemeProvider, createTheme } from '@mui/material/styles';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AgentAiPage } from '@/features/ai/pages/AgentAiPage';
import type { AgentExecution } from '@/features/ai/types/ai.types';

vi.mock('@/features/ai/hooks/useAiHealth', () => ({
  useAiAvailability: () => ({
    canInvoke: true,
    toggleEnabled: true,
    unavailableReason: null,
  }),
}));

vi.mock('@/features/ai/api/ai.api', () => ({
  aiApi: {
    executeAgent: vi.fn(),
    decideAgent: vi.fn(),
    cancelAgent: vi.fn(),
  },
}));

import { aiApi } from '@/features/ai/api/ai.api';

const executeAgent = vi.mocked(aiApi.executeAgent);

const completed: AgentExecution = {
  executionId: 'exec-1',
  status: 'COMPLETED',
  answer: 'Kafka transactions append atomically.',
  errorCode: '',
  sources: [
    {
      contentId: 'note-1',
      title: 'Kafka transactions',
      contentType: 'note',
      section: 'Overview',
      path: '/knowledge/note-1',
      url: '/knowledge/note-1',
      chunkId: 'chunk-1',
      score: 0.9,
    },
  ],
  steps: [
    {
      stepId: 'step-1',
      tool: 'search_knowledge',
      label: 'Searching ACOS knowledge',
      status: 'COMPLETED',
    },
  ],
  approvalRequired: false,
  proposedAction: '',
};

function renderPage() {
  return render(
    <ThemeProvider theme={createTheme()}>
      <AgentAiPage />
    </ThemeProvider>,
  );
}

describe('AgentAiPage', () => {
  beforeEach(() => {
    executeAgent.mockReset();
  });

  it('shows the safe step label and the sourced answer', async () => {
    executeAgent.mockResolvedValue(completed);
    const user = userEvent.setup();
    renderPage();
    await user.type(screen.getByLabelText(/goal/i), 'Find Kafka transactions');
    await user.click(screen.getByRole('button', { name: 'Analyze with AI' }));
    expect(await screen.findByText('Searching ACOS knowledge')).toBeInTheDocument();
    expect(screen.getByText(/append atomically/)).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Kafka transactions' })).toHaveAttribute(
      'href',
      '/knowledge/note-1',
    );
  });
});
