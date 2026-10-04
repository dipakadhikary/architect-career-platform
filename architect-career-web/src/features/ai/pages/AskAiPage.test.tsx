import { ThemeProvider, createTheme } from '@mui/material/styles';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AskAiPage } from '@/features/ai/pages/AskAiPage';

vi.mock('@/features/ai/hooks/useAiHealth', () => ({
  useAiAvailability: () => ({
    canInvoke: true,
    toggleEnabled: true,
    unavailableReason: null,
  }),
}));

vi.mock('@/features/ai/api/ai.api', () => ({
  aiApi: {
    ask: vi.fn(),
  },
}));

import { aiApi } from '@/features/ai/api/ai.api';

const ask = vi.mocked(aiApi.ask);

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
    ask.mockReset();
  });

  it('shows the answer after a successful ask', async () => {
    ask.mockResolvedValue({
      answer: '## Dependency Injection\n\nA design pattern.',
      model: 'gpt-test',
      provider: 'openai',
    });
    const user = userEvent.setup();
    renderPage();

    await user.type(screen.getByLabelText('Chat message'), 'What is dependency injection?');
    await user.click(screen.getByRole('button', { name: 'Ask' }));

    expect(await screen.findByText('What is dependency injection?')).toBeInTheDocument();
    expect(await screen.findByText('Dependency Injection')).toBeInTheDocument();
    expect(ask).toHaveBeenCalledWith({
      messages: [{ role: 'user', content: 'What is dependency injection?' }],
    });
  });

  it('shows an error and retries the same question', async () => {
    ask
      .mockRejectedValueOnce(new Error('down'))
      .mockResolvedValueOnce({
        answer: 'Recovered answer',
        model: 'gpt-test',
        provider: 'openai',
      });
    const user = userEvent.setup();
    renderPage();

    await user.type(screen.getByLabelText('Chat message'), 'What is dependency injection?');
    await user.click(screen.getByRole('button', { name: 'Ask' }));

    expect(await screen.findByRole('button', { name: 'Retry' })).toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: 'Retry' }));

    expect(await screen.findByText('Recovered answer')).toBeInTheDocument();
    expect(ask).toHaveBeenCalledTimes(2);
  });

  it('shows a source link from the response metadata', async () => {
    ask.mockResolvedValue({
      answer: 'The bulkhead isolates failures.',
      model: 'gpt-test',
      provider: 'openai',
      grounded: true,
      sources: [
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
      ],
    });
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
});
