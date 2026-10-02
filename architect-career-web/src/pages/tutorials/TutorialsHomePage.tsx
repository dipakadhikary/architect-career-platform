import { useCallback, useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AddIcon from '@mui/icons-material/Add';
import { Box, Button, Stack, TextField, Typography } from '@mui/material';
import {
  EmptyState,
  ErrorPanel,
  FormDialog,
  LoadingOverlay,
  PageHeader,
} from '@/shared/components';
import { useNotification } from '@/shared/hooks/useNotification';
import { getErrorMessage } from '@/shared/utils/error';
import {
  TutorialTopicForm,
  flattenTutorialOptions,
} from '@/features/tutorials/components/TutorialTopicForm';
import { TutorialSearchResults } from '@/features/tutorials/components/TutorialSearchResults';
import {
  useCreateTutorialTopicMutation,
  useTutorialSearchQuery,
  useTutorialTreeQuery,
} from '@/features/tutorials/hooks/useTutorialQueries';
import type { TutorialTopicFormValues } from '@/features/tutorials/schemas/tutorial.schemas';

export function TutorialsHomePage() {
  const navigate = useNavigate();
  const notification = useNotification();
  const [draftQuery, setDraftQuery] = useState('');
  const [submittedQuery, setSubmittedQuery] = useState('');
  const [searchEnabled, setSearchEnabled] = useState(false);
  const [createOpen, setCreateOpen] = useState(false);
  const createSubmitRef = useRef<(() => void) | null>(null);

  const treeQuery = useTutorialTreeQuery();
  const searchQuery = useTutorialSearchQuery(submittedQuery, searchEnabled);
  const createMutation = useCreateTutorialTopicMutation();

  const parentOptions = useMemo(
    () => flattenTutorialOptions(treeQuery.data ?? []),
    [treeQuery.data],
  );

  const handleSearch = useCallback(() => {
    const next = draftQuery.trim();
    setSubmittedQuery(next);
    setSearchEnabled(next.length > 0);
  }, [draftQuery]);

  const handleCreate = useCallback(
    async (values: TutorialTopicFormValues) => {
      try {
        const topic = await createMutation.mutateAsync({
          title: values.title,
          slug: values.slug?.trim() ? values.slug.trim() : null,
          parentId: values.parentId ?? null,
          sortOrder: values.sortOrder ?? null,
        });
        notification.success('Tutorial topic created');
        setCreateOpen(false);
        navigate(`/tutorials/${topic.path}/concept`);
      } catch (error) {
        notification.error(getErrorMessage(error, 'Failed to create tutorial topic'));
      }
    },
    [createMutation, navigate, notification],
  );

  return (
    <>
      <PageHeader
        title="Tutorials"
        description="Hierarchical learning material with concepts and Q&A."
        actions={
          <Button startIcon={<AddIcon />} variant="contained" onClick={() => setCreateOpen(true)}>
            New topic
          </Button>
        }
      />

      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5} sx={{ mb: 3 }}>
        <TextField
          value={draftQuery}
          onChange={(event) => setDraftQuery(event.target.value)}
          placeholder="Search tutorials..."
          size="small"
          fullWidth
          onKeyDown={(event) => {
            if (event.key === 'Enter') {
              event.preventDefault();
              handleSearch();
            }
          }}
          inputProps={{ 'aria-label': 'Search tutorials' }}
        />
        <Button variant="contained" onClick={handleSearch} sx={{ minWidth: 120 }}>
          Search
        </Button>
      </Stack>

      {!searchEnabled ? (
        treeQuery.isLoading ? (
          <LoadingOverlay open label="Loading tutorials…" />
        ) : treeQuery.isError ? (
          <ErrorPanel
            message={getErrorMessage(treeQuery.error, 'Failed to load tutorials')}
            onRetry={() => void treeQuery.refetch()}
          />
        ) : (treeQuery.data?.length ?? 0) === 0 ? (
          <EmptyState
            title="No tutorials yet"
            description="Create your first topic to start building a learning hierarchy."
            actionLabel="New topic"
            onAction={() => setCreateOpen(true)}
          />
        ) : (
          <Typography color="text.secondary">
            Browse topics from the Tutorials section in the left navigation, or search across
            concepts, questions, and answers.
          </Typography>
        )
      ) : searchQuery.isLoading ? (
        <LoadingOverlay open label="Searching tutorials…" />
      ) : searchQuery.isError ? (
        <ErrorPanel
          message={getErrorMessage(searchQuery.error, 'Tutorial search failed')}
          onRetry={() => void searchQuery.refetch()}
        />
      ) : (searchQuery.data?.content.length ?? 0) === 0 ? (
        <EmptyState
          title={`No results found for '${submittedQuery}'.`}
          description="Try searching with a different keyword."
        />
      ) : (
        <Box>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            {searchQuery.data?.totalElements} result
            {(searchQuery.data?.totalElements ?? 0) === 1 ? '' : 's'} for “{submittedQuery}”
          </Typography>
          <TutorialSearchResults results={searchQuery.data?.content ?? []} />
        </Box>
      )}

      <FormDialog
        open={createOpen}
        title="Create tutorial topic"
        onClose={() => setCreateOpen(false)}
        onSubmit={() => createSubmitRef.current?.()}
        submitLabel="Create"
        loading={createMutation.isPending}
      >
        <TutorialTopicForm
          parentOptions={parentOptions}
          onSubmit={handleCreate}
          onRegisterSubmit={(submit) => {
            createSubmitRef.current = submit;
          }}
        />
      </FormDialog>
    </>
  );
}
