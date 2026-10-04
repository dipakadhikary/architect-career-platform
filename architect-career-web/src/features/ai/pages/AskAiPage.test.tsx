import { ThemeProvider, createTheme } from '@mui/material/styles';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AskAiPage } from '@/features/ai/pages/AskAiPage';
import type { AssistantConversationPage, AssistantMessagePair } from '@/features/ai/types/ai.types';

vi.mock('@/features/ai/hooks/useAiHealth', () => ({
  useAiAvailability: () => ({
    canInvoke: true,
    toggleEnabled: true,
    unavailableReason: null,
  }),
}));

vi.mock('@/features/ai/api/ai.api', () => ({
  aiApi: {
    listConversations: vi.fn(),
    createConversation: vi.fn(),
    getConversation: vi.fn(),
    renameConversation: vi.fn(),
    deleteConversation: vi.fn(),
    sendConversationMessage: vi.fn(),
  },
}));

import { aiApi } from '@/features/ai/api/ai.api';

const listConversations = vi.mocked(aiApi.listConversations);
const createConversation = vi.mocked(aiApi.createConversation);
const getConversation = vi.mocked(aiApi.getConversation);
const renameConversation = vi.mocked(aiApi.renameConversation);
const deleteConversation = vi.mocked(aiApi.deleteConversation);
const sendConversationMessage = vi.mocked(aiApi.sendConversationMessage);

const emptyPage: AssistantConversationPage = {
  content: [],
  page: 0,
  size: 20,
  totalElements: 0,
  totalPages: 0,
  first: true,
  last: true,
};

function pair(answer: string, sources: AssistantMessagePair['assistantMessage']['sources'] = []): AssistantMessagePair {
  return {
    userMessage: {
      id: 'user-1',
      role: 'USER',
      content: 'What is dependency injection?',
      sequenceNumber: 1,
      status: 'COMPLETED',
      createdAt: '2026-10-04T00:00:00Z',
      model: '',
      provider: '',
      grounded: false,
      sources: [],
    },
    assistantMessage: {
      id: 'assistant-1',
      role: 'ASSISTANT',
      content: answer,
      sequenceNumber: 2,
      status: 'COMPLETED',
      createdAt: '2026-10-04T00:00:01Z',
      model: 'gpt-test',
      provider: 'openai',
      grounded: sources.length > 0,
      sources,
    },
  };
}

function renderPage() {
  return render(
    <ThemeProvider theme={createTheme()}>
      <MemoryRouter>
        <AskAiPage />
      </MemoryRouter>
    </ThemeProvider>,
  );
}

describe('AskAiPage', () => {
  beforeEach(() => {
    listConversations.mockReset();
    createConversation.mockReset();
    getConversation.mockReset();
    renameConversation.mockReset();
    deleteConversation.mockReset();
    sendConversationMessage.mockReset();
    listConversations.mockResolvedValue(emptyPage);
    createConversation.mockResolvedValue({
      id: 'conv-1',
      title: 'New conversation',
      createdAt: '2026-10-04T00:00:00Z',
      updatedAt: '2026-10-04T00:00:00Z',
    });
  });

  it('shows the answer after a successful ask', async () => {
    sendConversationMessage.mockResolvedValue(pair('## Dependency Injection\n\nA design pattern.'));
    const user = userEvent.setup();
    renderPage();

    await user.type(screen.getByLabelText('Chat message'), 'What is dependency injection?');
    await user.click(screen.getByRole('button', { name: 'Ask' }));

    expect(await screen.findByText('What is dependency injection?')).toBeInTheDocument();
    expect(await screen.findByText('Dependency Injection')).toBeInTheDocument();
    expect(createConversation).toHaveBeenCalledTimes(1);
    expect(sendConversationMessage).toHaveBeenCalledWith(
      'conv-1',
      'What is dependency injection?',
      expect.any(String),
    );
  });

  it('shows an error and retries the same question', async () => {
    sendConversationMessage
      .mockRejectedValueOnce(new Error('down'))
      .mockResolvedValueOnce(pair('Recovered answer'));
    const user = userEvent.setup();
    renderPage();

    await user.type(screen.getByLabelText('Chat message'), 'What is dependency injection?');
    await user.click(screen.getByRole('button', { name: 'Ask' }));

    expect(await screen.findByRole('button', { name: 'Retry' })).toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: 'Retry' }));

    expect(await screen.findByText('Recovered answer')).toBeInTheDocument();
    expect(sendConversationMessage).toHaveBeenCalledTimes(2);
    const firstKey = sendConversationMessage.mock.calls[0]?.[2];
    const secondKey = sendConversationMessage.mock.calls[1]?.[2];
    expect(firstKey).toBe(secondKey);
  });

  it('shows a source link from the response metadata', async () => {
    sendConversationMessage.mockResolvedValue(
      pair('The bulkhead isolates failures.', [
        {
          contentId: 'bulkhead',
          title: 'Bulkhead Pattern',
          contentType: 'CONCEPT',
          section: 'Introduction',
          path: 'Microservices / Bulkhead',
          url: '/tutorials/microservices/bulkhead/concept',
          chunkId: 'chunk-1',
          score: 0.91,
        },
      ]),
    );
    const user = userEvent.setup();
    renderPage();
    await user.type(screen.getByLabelText('Chat message'), 'What is the bulkhead pattern?');
    await user.click(screen.getByRole('button', { name: 'Ask' }));
    expect(await screen.findByText('Bulkhead Pattern')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Open source' })).toHaveAttribute(
      'href',
      '/tutorials/microservices/bulkhead/concept',
    );
    expect(screen.queryByText('https://malicious.example')).not.toBeInTheDocument();
  });

  it('does not render script markup from an assistant message', async () => {
    sendConversationMessage.mockResolvedValue(
      pair('<script>alert(1)</script>\n\n[click](javascript:alert(1))'),
    );
    const user = userEvent.setup();
    renderPage();
    await user.type(screen.getByLabelText('Chat message'), 'show markup');
    await user.click(screen.getByRole('button', { name: 'Ask' }));
    expect(await screen.findByText('click')).toBeInTheDocument();
    expect(document.querySelector('script')).toBeNull();
    expect(document.body.innerHTML).not.toMatch(/javascript:/i);
  });

  it('creates one conversation when New conversation is clicked twice quickly', async () => {
    let release: (value: Awaited<ReturnType<typeof aiApi.createConversation>>) => void = () => undefined;
    createConversation.mockImplementation(
      () =>
        new Promise((resolve) => {
          release = resolve;
        }),
    );
    renderPage();
    const button = await screen.findByRole('button', { name: 'New conversation' });
    fireEvent.click(button);
    fireEvent.click(button);
    expect(createConversation).toHaveBeenCalledTimes(1);
    release({
      id: 'conv-1',
      title: 'New conversation',
      createdAt: '2026-10-04T00:00:00Z',
      updatedAt: '2026-10-04T00:00:00Z',
    });
    expect(await screen.findByText('New conversation')).toBeInTheDocument();
  });

  it('renames and deletes a conversation', async () => {
    listConversations.mockResolvedValue({
      ...emptyPage,
      content: [
        {
          id: 'conv-9',
          title: 'Kafka notes',
          createdAt: '2026-10-04T00:00:00Z',
          updatedAt: '2026-10-04T00:00:00Z',
        },
      ],
      totalElements: 1,
    });
    renameConversation.mockResolvedValue({
      id: 'conv-9',
      title: 'Partitions',
      createdAt: '2026-10-04T00:00:00Z',
      updatedAt: '2026-10-04T00:01:00Z',
    });
    deleteConversation.mockResolvedValue();
    const user = userEvent.setup();
    renderPage();

    expect(await screen.findByText('Kafka notes')).toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: 'Rename Kafka notes' }));
    await user.clear(screen.getByLabelText('Title'));
    await user.type(screen.getByLabelText('Title'), 'Partitions');
    await user.click(screen.getByRole('button', { name: 'Save' }));
    await waitFor(() => expect(screen.queryByRole('dialog')).not.toBeInTheDocument());
    expect(await screen.findByText('Partitions')).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Delete Partitions' }));
    await user.click(screen.getByRole('button', { name: 'Delete' }));
    expect(deleteConversation).toHaveBeenCalledWith('conv-9');
    expect(screen.queryByText('Partitions')).not.toBeInTheDocument();
  });
});
