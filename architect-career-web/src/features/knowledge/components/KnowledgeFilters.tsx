import { TextField, Typography } from '@mui/material';
import { FilterPanel } from '@/shared/components';

interface KnowledgeFiltersProps {
  categoryFilter: string;
  onCategoryFilterChange: (value: string) => void;
  onClear: () => void;
  isSearchActive: boolean;
}

export function KnowledgeFilters({
  categoryFilter,
  onCategoryFilterChange,
  onClear,
  isSearchActive,
}: KnowledgeFiltersProps) {
  return (
    <FilterPanel title="Filters" onClear={onClear}>
      <TextField
        label="Category"
        size="small"
        value={categoryFilter}
        onChange={(event) => onCategoryFilterChange(event.target.value)}
        placeholder="Filter by category name…"
        helperText="Filters notes on the current page only"
        fullWidth
      />
      <Typography variant="body2" color="text.secondary" sx={{ alignSelf: 'center' }}>
        {isSearchActive
          ? 'Search matches title, summary, and content on the server.'
          : 'Use the search bar for full-text search across all notes.'}
      </Typography>
    </FilterPanel>
  );
}
