import { useCallback, useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AddIcon from '@mui/icons-material/Add';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import { Box, Button, Chip, IconButton, Stack, Tooltip, Typography } from '@mui/material';
import { moduleConfig } from '@/app/config/module.config';
import {
  ConfirmationDialog,
  DataTable,
  EmptyState,
  ErrorPanel,
  FormDialog,
  LoadingOverlay,
  PageHeader,
  SearchBar,
  type DataTableColumn,
  type SortDirection,
} from '@/shared/components';
import { useDebouncedValue } from '@/shared/hooks/useDebouncedValue';
import { useNotification } from '@/shared/hooks/useNotification';
import { formatDateTime } from '@/shared/utils/date';
import { getErrorMessage } from '@/shared/utils/error';
import { KnowledgeFilters } from '@/features/knowledge/components/KnowledgeFilters';
import { KnowledgeNoteForm } from '@/features/knowledge/components/KnowledgeNoteForm';
import {
  useCreateKnowledgeNoteMutation,
  useDeleteKnowledgeNoteMutation,
} from '@/features/knowledge/hooks/useKnowledgeMutations';
import { useKnowledgeNotesQuery } from '@/features/knowledge/hooks/useKnowledgeQueries';
import {
  toKnowledgeNoteRequest,
  type KnowledgeNoteFormValues,
} from '@/features/knowledge/schemas/knowledge.schemas';
import type { KnowledgeNoteResponse } from '@/features/knowledge/types/knowledge.types';

const SORTABLE_COLUMNS = new Set(['title', 'updatedAt', 'createdAt']);

export function KnowledgeListPage() {
  const navigate = useNavigate();
  const notification = useNotification();

  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState<number>(moduleConfig.knowledge.defaultPageSize);
  const [sortBy, setSortBy] = useState('updatedAt');
  const [sortDirection, setSortDirection] = useState<SortDirection>('desc');
  const [searchQuery, setSearchQuery] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [createOpen, setCreateOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<KnowledgeNoteResponse | null>(null);

  const createSubmitRef = useRef<(() => void) | null>(null);

  const debouncedSearch = useDebouncedValue(searchQuery);
  const isSearchActive = debouncedSearch.trim().length > 0;
  const sort = `${sortBy},${sortDirection}`;

  const notesQuery = useKnowledgeNotesQuery({
    page,
    size: pageSize,
    sort,
    q: isSearchActive ? debouncedSearch.trim() : undefined,
  });

  const createMutation = useCreateKnowledgeNoteMutation();
  const deleteMutation = useDeleteKnowledgeNoteMutation();

  const filteredRows = useMemo(() => {
    const content = notesQuery.data?.content ?? [];
    const categoryTerm = categoryFilter.trim().toLowerCase();
    if (!categoryTerm) return content;

    return content.filter((note) => note.category?.name.toLowerCase().includes(categoryTerm));
  }, [categoryFilter, notesQuery.data?.content]);

  const handleSortChange = useCallback(
    (columnId: string) => {
      if (!SORTABLE_COLUMNS.has(columnId)) return;
      if (sortBy === columnId) {
        setSortDirection((current) => (current === 'asc' ? 'desc' : 'asc'));
      } else {
        setSortBy(columnId);
        setSortDirection('asc');
      }
      setPage(0);
    },
    [sortBy],
  );

  const handleCreate = useCallback(
    async (values: KnowledgeNoteFormValues) => {
      try {
        const note = await createMutation.mutateAsync(toKnowledgeNoteRequest(values));
        notification.success('Knowledge note created');
        setCreateOpen(false);
        navigate(`/knowledge/${note.id}`);
      } catch (error) {
        notification.error(getErrorMessage(error, 'Failed to create note'));
      }
    },
    [createMutation, navigate, notification],
  );

  const handleDeleteConfirm = useCallback(async () => {
    if (!deleteTarget) return;
    try {
      await deleteMutation.mutateAsync(deleteTarget.id);
      notification.success('Knowledge note deleted');
      setDeleteTarget(null);
    } catch (error) {
      notification.error(getErrorMessage(error, 'Failed to delete note'));
    }
  }, [deleteMutation, deleteTarget, notification]);

  const columns = useMemo<DataTableColumn<KnowledgeNoteResponse>[]>(
    () => [
      {
        id: 'title',
        label: 'Title',
        sortable: true,
        render: (row) => (
          <Box>
            <Typography variant="body2" fontWeight={600}>
              {row.title}
            </Typography>
            <Typography
              variant="caption"
              color="text.secondary"
              noWrap
              sx={{ maxWidth: 360, display: 'block' }}
            >
              {row.summary}
            </Typography>
          </Box>
        ),
      },
      {
        id: 'category',
        label: 'Category',
        render: (row) =>
          row.category ? (
            <Chip label={row.category.name} size="small" variant="outlined" />
          ) : (
            <Typography variant="body2" color="text.secondary">
              —
            </Typography>
          ),
      },
      {
        id: 'tags',
        label: 'Tags',
        render: (row) =>
          row.tags.length > 0 ? (
            <Stack direction="row" spacing={0.5} flexWrap="wrap" useFlexGap>
              {row.tags.slice(0, 3).map((tag) => (
                <Chip key={tag} label={tag} size="small" />
              ))}
              {row.tags.length > 3 ? (
                <Chip label={`+${row.tags.length - 3}`} size="small" variant="outlined" />
              ) : null}
            </Stack>
          ) : (
            <Typography variant="body2" color="text.secondary">
              —
            </Typography>
          ),
      },
      {
        id: 'updatedAt',
        label: 'Updated',
        sortable: true,
        width: 160,
        render: (row) => formatDateTime(row.updatedAt),
      },
      {
        id: 'actions',
        label: '',
        align: 'right',
        width: 56,
        render: (row) => (
          <Tooltip title="Delete note">
            <IconButton
              size="small"
              aria-label={`Delete ${row.title}`}
              onClick={(event) => {
                event.stopPropagation();
                setDeleteTarget(row);
              }}
            >
              <DeleteOutlineIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        ),
      },
    ],
    [],
  );

  const showEmptyState = !notesQuery.isLoading && !notesQuery.isError && filteredRows.length === 0;

  return (
    <>
      <PageHeader
        title="Knowledge"
        description="Capture architecture notes, patterns, and learnings in one place."
        actions={
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => setCreateOpen(true)}>
            New note
          </Button>
        }
      />

      <Stack spacing={2} sx={{ mb: 2 }}>
        <SearchBar
          value={searchQuery}
          onChange={(value) => {
            setSearchQuery(value);
            setPage(0);
          }}
          placeholder="Search notes by title, summary, or content…"
        />
        <KnowledgeFilters
          categoryFilter={categoryFilter}
          onCategoryFilterChange={setCategoryFilter}
          onClear={() => setCategoryFilter('')}
          isSearchActive={isSearchActive}
        />
      </Stack>

      {notesQuery.isError ? (
        <ErrorPanel
          message={getErrorMessage(notesQuery.error, 'Failed to load knowledge notes')}
          onRetry={() => void notesQuery.refetch()}
        />
      ) : showEmptyState ? (
        <EmptyState
          title={isSearchActive || categoryFilter ? 'No matching notes' : 'No knowledge notes yet'}
          description={
            isSearchActive
              ? 'Try a different search term or clear filters.'
              : categoryFilter
                ? 'No notes on this page match the category filter.'
                : 'Create your first note to start building your knowledge base.'
          }
          actionLabel={!isSearchActive && !categoryFilter ? 'Create note' : undefined}
          onAction={!isSearchActive && !categoryFilter ? () => setCreateOpen(true) : undefined}
        />
      ) : (
        <DataTable
          columns={columns}
          rows={filteredRows}
          rowKey={(row) => row.id}
          page={page}
          pageSize={pageSize}
          totalElements={notesQuery.data?.totalElements ?? 0}
          onPageChange={setPage}
          onPageSizeChange={(size) => {
            setPageSize(size);
            setPage(0);
          }}
          sortBy={sortBy}
          sortDirection={sortDirection}
          onSortChange={handleSortChange}
          loading={notesQuery.isLoading}
          emptyMessage={
            categoryFilter.trim()
              ? 'No notes on this page match the category filter'
              : 'No notes found'
          }
          onRowClick={(row) => navigate(`/knowledge/${row.id}`)}
        />
      )}

      <FormDialog
        open={createOpen}
        title="Create knowledge note"
        submitLabel="Create"
        loading={createMutation.isPending}
        maxWidth="md"
        onClose={() => {
          if (!createMutation.isPending) setCreateOpen(false);
        }}
        onSubmit={() => createSubmitRef.current?.()}
      >
        <KnowledgeNoteForm
          onRegisterSubmit={(submit) => {
            createSubmitRef.current = submit;
          }}
          onSubmit={(values) => void handleCreate(values)}
        />
      </FormDialog>

      <ConfirmationDialog
        open={Boolean(deleteTarget)}
        title="Delete knowledge note"
        description={
          deleteTarget ? `Delete "${deleteTarget.title}"? This action cannot be undone.` : ''
        }
        confirmLabel="Delete"
        danger
        loading={deleteMutation.isPending}
        onConfirm={() => void handleDeleteConfirm()}
        onCancel={() => {
          if (!deleteMutation.isPending) setDeleteTarget(null);
        }}
      />

      <LoadingOverlay
        open={notesQuery.isFetching && !notesQuery.isLoading}
        label="Refreshing notes…"
      />
    </>
  );
}
