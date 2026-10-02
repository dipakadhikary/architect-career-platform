import { describe, expect, it, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import type { ReactElement } from 'react';
import { StatusChip } from './StatusChip';
import { EmptyState } from './EmptyState';
import { SearchBar } from './SearchBar';
import { ConfirmationDialog } from './ConfirmationDialog';
import { LoadingSkeleton } from './LoadingSkeleton';

function renderWithTheme(ui: ReactElement) {
  return render(<ThemeProvider theme={createTheme()}>{ui}</ThemeProvider>);
}

describe('StatusChip', () => {
  it('renders formatted status labels', () => {
    renderWithTheme(<StatusChip status="IN_PROGRESS" />);
    expect(screen.getByText('In Progress')).toBeInTheDocument();
  });
});

describe('EmptyState', () => {
  it('invokes action callbacks', async () => {
    const user = userEvent.setup();
    const onAction = vi.fn();
    renderWithTheme(<EmptyState title="Nothing here" actionLabel="Create" onAction={onAction} />);
    await user.click(screen.getByRole('button', { name: 'Create' }));
    expect(onAction).toHaveBeenCalledTimes(1);
  });
});

describe('SearchBar', () => {
  it('clears the current value', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    renderWithTheme(<SearchBar value="architecture" onChange={onChange} />);
    await user.click(screen.getByRole('button', { name: 'Clear search' }));
    expect(onChange).toHaveBeenCalledWith('');
  });
});

describe('ConfirmationDialog', () => {
  it('exposes accessible title and confirm action', async () => {
    const user = userEvent.setup();
    const onConfirm = vi.fn();
    renderWithTheme(
      <ConfirmationDialog
        open
        title="Delete note"
        description="This cannot be undone."
        onConfirm={onConfirm}
        onCancel={vi.fn()}
      />,
    );
    expect(screen.getByRole('dialog', { name: 'Delete note' })).toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: 'Confirm' }));
    expect(onConfirm).toHaveBeenCalled();
  });
});

describe('LoadingSkeleton', () => {
  it('renders without crashing for page variant', () => {
    const { container } = renderWithTheme(<LoadingSkeleton variant="page" />);
    expect(container.querySelectorAll('.MuiSkeleton-root').length).toBeGreaterThan(0);
  });
});
