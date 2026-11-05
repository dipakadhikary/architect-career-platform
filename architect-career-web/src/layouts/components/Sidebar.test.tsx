import { describe, expect, it, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { MemoryRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { ReactNode } from 'react';
import { Sidebar } from './Sidebar';

vi.mock('@/features/tutorials/components/TutorialSidebarTree', () => ({
  TutorialSidebarTree: () => <div>Tutorial tree</div>,
}));

function renderSidebar(initialPath = '/tutorials/java') {
  const client = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  const wrapper = ({ children }: { children: ReactNode }) => (
    <QueryClientProvider client={client}>
      <ThemeProvider theme={createTheme()}>
        <MemoryRouter initialEntries={[initialPath]}>{children}</MemoryRouter>
      </ThemeProvider>
    </QueryClientProvider>
  );
  return render(<Sidebar mobileOpen onMobileClose={() => undefined} />, { wrapper });
}

describe('Sidebar Tutorials collapse', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    Object.defineProperty(window, 'matchMedia', {
      writable: true,
      value: vi.fn().mockImplementation((query: string) => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: vi.fn(),
        removeListener: vi.fn(),
        addEventListener: vi.fn(),
        removeEventListener: vi.fn(),
        dispatchEvent: vi.fn(),
      })),
    });
  });

  it('collapses the Tutorials tree while on a tutorials route', async () => {
    const user = userEvent.setup();
    renderSidebar('/tutorials/java');

    expect(screen.getByText('Tutorial tree')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Collapse Tutorials' })).toHaveAttribute(
      'aria-expanded',
      'true',
    );

    await user.click(screen.getByRole('button', { name: 'Collapse Tutorials' }));

    expect(screen.queryByText('Tutorial tree')).not.toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Expand Tutorials' })).toHaveAttribute(
      'aria-expanded',
      'false',
    );
  });

  it('expands again after a manual collapse', async () => {
    const user = userEvent.setup();
    renderSidebar('/tutorials');

    await user.click(screen.getByRole('button', { name: 'Collapse Tutorials' }));
    expect(screen.queryByText('Tutorial tree')).not.toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Expand Tutorials' }));
    expect(screen.getByText('Tutorial tree')).toBeInTheDocument();
  });
});
