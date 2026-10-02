import { describe, expect, it, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { MemoryRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { ReactElement, ReactNode } from 'react';
import { TutorialQuestionList } from './TutorialQuestionList';
import { TutorialSearchResults } from './TutorialSearchResults';
import { TutorialBreadcrumb } from './TutorialBreadcrumb';
import { TutorialSidebarTree } from './TutorialSidebarTree';
import type {
  TutorialQuestionResponse,
  TutorialSearchResult,
  TutorialTreeNode,
} from '../types/tutorial.types';

vi.mock('../hooks/useTutorialQueries', () => ({
  useTutorialTreeQuery: vi.fn(),
}));

import { useTutorialTreeQuery } from '../hooks/useTutorialQueries';

const mockedUseTutorialTreeQuery = vi.mocked(useTutorialTreeQuery);

function Providers({
  children,
  initialEntries = ['/tutorials'],
}: {
  children: ReactNode;
  initialEntries?: string[];
}) {
  const client = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return (
    <QueryClientProvider client={client}>
      <ThemeProvider theme={createTheme()}>
        <MemoryRouter initialEntries={initialEntries}>{children}</MemoryRouter>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

function renderWithProviders(ui: ReactElement, initialEntries?: string[]) {
  return render(<Providers initialEntries={initialEntries}>{ui}</Providers>);
}

const sampleQuestions: TutorialQuestionResponse[] = [
  {
    id: 'q1',
    topicId: 't1',
    question: 'What is the Factory Pattern?',
    answer: 'A creational pattern.',
    sortOrder: 0,
    updatedAt: '2026-01-01T00:00:00Z',
  },
  {
    id: 'q2',
    topicId: 't1',
    question: 'What is Bulkhead?',
    answer: 'Failure isolation.',
    sortOrder: 1,
    updatedAt: '2026-01-01T00:00:00Z',
  },
  {
    id: 'q3',
    topicId: 't1',
    question: 'What is Circuit Breaker?',
    answer: 'Stops cascading failures.',
    sortOrder: 2,
    updatedAt: '2026-01-01T00:00:00Z',
  },
];

describe('TutorialQuestionList', () => {
  it('toggles Show Answer independently per question', async () => {
    const user = userEvent.setup();
    renderWithProviders(<TutorialQuestionList questions={sampleQuestions} />);

    const showButtons = screen.getAllByRole('button', { name: 'Show Answer' });
    expect(showButtons).toHaveLength(3);

    await user.click(showButtons[1]);

    expect(screen.getByRole('button', { name: 'Hide Answer' })).toBeInTheDocument();
    expect(screen.getAllByRole('button', { name: 'Show Answer' })).toHaveLength(2);
    expect(screen.getByText('Failure isolation.')).toBeInTheDocument();
    expect(screen.queryByText('A creational pattern.')).not.toBeInTheDocument();
    expect(screen.queryByText('Stops cascading failures.')).not.toBeInTheDocument();
  });

  it('keeps all answers hidden by default', () => {
    renderWithProviders(<TutorialQuestionList questions={sampleQuestions} />);
    expect(screen.getAllByRole('button', { name: 'Show Answer' })).toHaveLength(3);
    expect(screen.queryByRole('button', { name: 'Hide Answer' })).not.toBeInTheDocument();
    expect(screen.queryByText('A creational pattern.')).not.toBeInTheDocument();
    expect(screen.queryByText('Failure isolation.')).not.toBeInTheDocument();
    expect(screen.queryByText('Stops cascading failures.')).not.toBeInTheDocument();
  });

  it('hides an answer without affecting other questions', async () => {
    const user = userEvent.setup();
    renderWithProviders(<TutorialQuestionList questions={sampleQuestions} />);
    await user.click(screen.getAllByRole('button', { name: 'Show Answer' })[0]);
    await user.click(screen.getAllByRole('button', { name: 'Show Answer' })[0]);
    expect(screen.getByText('A creational pattern.')).toBeInTheDocument();
    expect(screen.getByText('Failure isolation.')).toBeInTheDocument();

    await user.click(screen.getAllByRole('button', { name: 'Hide Answer' })[0]);
    await waitFor(() => {
      expect(screen.queryByText('A creational pattern.')).not.toBeInTheDocument();
    });
    expect(screen.getByText('Failure isolation.')).toBeInTheDocument();
  });
});

describe('TutorialBreadcrumb', () => {
  it('renders dynamic hierarchy links', () => {
    renderWithProviders(
      <TutorialBreadcrumb
        items={[
          { id: '1', title: 'Design Patterns', slug: 'design-patterns', path: 'design-patterns' },
          {
            id: '2',
            title: 'Creational',
            slug: 'creational',
            path: 'design-patterns/creational',
          },
        ]}
        leaf="Concept"
      />,
    );
    expect(screen.getByRole('link', { name: 'Tutorials' })).toHaveAttribute('href', '/tutorials');
    expect(screen.getByRole('link', { name: 'Design Patterns' })).toHaveAttribute(
      'href',
      '/tutorials/design-patterns/concept',
    );
    expect(screen.getByText('Concept')).toBeInTheDocument();
  });
});

describe('TutorialSearchResults', () => {
  it('renders grid cards with snippet, content type, and open link', () => {
    const results: TutorialSearchResult[] = [
      {
        topicId: '1',
        title: 'Bulkhead Pattern',
        path: 'microservices-design/bulkhead',
        breadcrumb: [
          {
            id: 'a',
            title: 'Microservices Design',
            slug: 'microservices-design',
            path: 'microservices-design',
          },
          {
            id: 'b',
            title: 'Bulkhead',
            slug: 'bulkhead',
            path: 'microservices-design/bulkhead',
          },
        ],
        snippet: '...bulkhead isolates <mark>failures</mark> between components...',
        contentType: 'Concept',
        rank: 0.9,
      },
    ];
    renderWithProviders(<TutorialSearchResults results={results} />);
    expect(screen.getByText('Bulkhead Pattern')).toBeInTheDocument();
    expect(screen.getByText(/Content Type: Concept/)).toBeInTheDocument();
    expect(screen.getByText('failures')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Open Tutorial' })).toHaveAttribute(
      'href',
      '/tutorials/microservices-design/bulkhead/concept',
    );
  });
});

describe('TutorialSidebarTree', () => {
  beforeEach(() => {
    mockedUseTutorialTreeQuery.mockReset();
  });

  it('renders dynamic collapsible hierarchy', async () => {
    const user = userEvent.setup();
    const tree: TutorialTreeNode[] = [
      {
        id: '1',
        title: 'Design Patterns',
        slug: 'design-patterns',
        path: 'design-patterns',
        sortOrder: 0,
        hasConcept: true,
        hasQuestions: false,
        children: [
          {
            id: '2',
            title: 'Creational',
            slug: 'creational',
            path: 'design-patterns/creational',
            sortOrder: 0,
            hasConcept: true,
            hasQuestions: true,
            children: [],
          },
        ],
      },
    ];
    mockedUseTutorialTreeQuery.mockReturnValue({
      data: tree,
      isLoading: false,
      isError: false,
    } as unknown as ReturnType<typeof useTutorialTreeQuery>);

    renderWithProviders(<TutorialSidebarTree />);
    expect(screen.getByText('Design Patterns')).toBeInTheDocument();
    expect(screen.queryByText('Creational')).not.toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Expand' }));
    expect(screen.getByText('Creational')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Creational' })).toHaveAttribute(
      'href',
      '/tutorials/design-patterns/creational/concept',
    );
  });

  it('shows empty state when no tutorials exist', () => {
    mockedUseTutorialTreeQuery.mockReturnValue({
      data: [],
      isLoading: false,
      isError: false,
    } as unknown as ReturnType<typeof useTutorialTreeQuery>);
    renderWithProviders(<TutorialSidebarTree />);
    expect(screen.getByText('No tutorials yet')).toBeInTheDocument();
  });

  it('shows loading and error states', () => {
    mockedUseTutorialTreeQuery.mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
    } as unknown as ReturnType<typeof useTutorialTreeQuery>);
    const { rerender } = renderWithProviders(<TutorialSidebarTree />);
    expect(screen.getByText('Loading tutorials…')).toBeInTheDocument();

    mockedUseTutorialTreeQuery.mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
    } as unknown as ReturnType<typeof useTutorialTreeQuery>);
    rerender(
      <Providers>
        <TutorialSidebarTree />
      </Providers>,
    );
    expect(screen.getByText('Unable to load tutorials')).toBeInTheDocument();
  });
});
