import { describe, expect, it, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { TutorialsHomePage } from '@/pages/tutorials/TutorialsHomePage';

vi.mock('@/features/tutorials/hooks/useTutorialQueries', async () => {
  const actual = await vi.importActual<
    typeof import('@/features/tutorials/hooks/useTutorialQueries')
  >('@/features/tutorials/hooks/useTutorialQueries');
  return {
    ...actual,
    useTutorialTreeQuery: vi.fn(),
    useTutorialSearchQuery: vi.fn(),
    useCreateTutorialTopicMutation: vi.fn(),
  };
});

import {
  useCreateTutorialTopicMutation,
  useTutorialSearchQuery,
  useTutorialTreeQuery,
} from '@/features/tutorials/hooks/useTutorialQueries';

const mockedTree = vi.mocked(useTutorialTreeQuery);
const mockedSearch = vi.mocked(useTutorialSearchQuery);
const mockedCreate = vi.mocked(useCreateTutorialTopicMutation);

function renderHome() {
  const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={client}>
      <ThemeProvider theme={createTheme()}>
        <MemoryRouter initialEntries={['/tutorials']}>
          <Routes>
            <Route path="/tutorials" element={<TutorialsHomePage />} />
          </Routes>
        </MemoryRouter>
      </ThemeProvider>
    </QueryClientProvider>,
  );
}

describe('TutorialsHomePage search', () => {
  beforeEach(() => {
    mockedTree.mockReturnValue({
      data: [],
      isLoading: false,
      isError: false,
      refetch: vi.fn(),
    } as unknown as ReturnType<typeof useTutorialTreeQuery>);
    mockedSearch.mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: false,
      refetch: vi.fn(),
    } as unknown as ReturnType<typeof useTutorialSearchQuery>);
    mockedCreate.mockReturnValue({
      mutateAsync: vi.fn(),
      isPending: false,
    } as unknown as ReturnType<typeof useCreateTutorialTopicMutation>);
  });

  it('does not search while typing and searches only after clicking Search', async () => {
    const user = userEvent.setup();
    renderHome();

    expect(mockedSearch).toHaveBeenCalledWith('', false);

    await user.type(screen.getByLabelText('Search tutorials'), 'circuit breaker');
    expect(mockedSearch.mock.calls.at(-1)?.[1]).toBe(false);

    await user.click(screen.getByRole('button', { name: 'Search' }));
    expect(mockedSearch.mock.calls.at(-1)).toEqual(['circuit breaker', true]);
  });

  it('shows no-result empty state for submitted empty matches', async () => {
    const user = userEvent.setup();
    mockedSearch.mockImplementation((q: string, enabled: boolean) => {
      if (enabled) {
        return {
          data: { query: q, content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 },
          isLoading: false,
          isError: false,
          refetch: vi.fn(),
        } as unknown as ReturnType<typeof useTutorialSearchQuery>;
      }
      return {
        data: undefined,
        isLoading: false,
        isError: false,
        refetch: vi.fn(),
      } as unknown as ReturnType<typeof useTutorialSearchQuery>;
    });

    renderHome();
    await user.type(screen.getByLabelText('Search tutorials'), 'missing-term');
    await user.click(screen.getByRole('button', { name: 'Search' }));
    expect(screen.getByText("No results found for 'missing-term'.")).toBeInTheDocument();
    expect(screen.getByText('Try searching with a different keyword.')).toBeInTheDocument();
  });
});

describe('TutorialsHomePage loading/error', () => {
  it('renders loading overlay while tree loads', () => {
    mockedTree.mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
      refetch: vi.fn(),
    } as unknown as ReturnType<typeof useTutorialTreeQuery>);
    mockedSearch.mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: false,
      refetch: vi.fn(),
    } as unknown as ReturnType<typeof useTutorialSearchQuery>);
    mockedCreate.mockReturnValue({
      mutateAsync: vi.fn(),
      isPending: false,
    } as unknown as ReturnType<typeof useCreateTutorialTopicMutation>);

    renderHome();
    expect(screen.getByText('Loading tutorials…')).toBeInTheDocument();
  });

  it('renders API error state for tree failures', () => {
    mockedTree.mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
      error: new Error('boom'),
      refetch: vi.fn(),
    } as unknown as ReturnType<typeof useTutorialTreeQuery>);
    mockedSearch.mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: false,
      refetch: vi.fn(),
    } as unknown as ReturnType<typeof useTutorialSearchQuery>);
    mockedCreate.mockReturnValue({
      mutateAsync: vi.fn(),
      isPending: false,
    } as unknown as ReturnType<typeof useCreateTutorialTopicMutation>);

    renderHome();
    expect(screen.getByText('boom')).toBeInTheDocument();
  });
});
