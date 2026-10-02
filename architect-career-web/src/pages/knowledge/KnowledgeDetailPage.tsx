import { useCallback, useMemo, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { Box, Button, Chip, Divider, Paper, Stack, Typography } from '@mui/material';
import {
  ConfirmationDialog,
  EmptyState,
  ErrorPanel,
  FormDialog,
  LoadingOverlay,
  MarkdownViewer,
  PageHeader,
} from '@/shared/components';
import { useNotification } from '@/shared/hooks/useNotification';
import { formatDate, formatDateTime } from '@/shared/utils/date';
import { getErrorMessage } from '@/shared/utils/error';
import { KnowledgeNoteForm } from '@/features/knowledge/components/KnowledgeNoteForm';
import {
  useDeleteKnowledgeNoteMutation,
  useUpdateKnowledgeNoteMutation,
} from '@/features/knowledge/hooks/useKnowledgeMutations';
import { useKnowledgeNoteQuery } from '@/features/knowledge/hooks/useKnowledgeQueries';
import {
  toKnowledgeNoteFormValues,
  toKnowledgeNoteRequest,
  type KnowledgeNoteFormValues,
} from '@/features/knowledge/schemas/knowledge.schemas';

export function KnowledgeDetailPage() {
  const { noteId } = useParams<{ noteId: string }>();
  const navigate = useNavigate();
  const notification = useNotification();

  const [editOpen, setEditOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);

  const editSubmitRef = useRef<(() => void) | null>(null);

  const noteQuery = useKnowledgeNoteQuery(noteId);
  const updateMutation = useUpdateKnowledgeNoteMutation();
  const deleteMutation = useDeleteKnowledgeNoteMutation();

  const editDefaults = useMemo<KnowledgeNoteFormValues | undefined>(() => {
    if (!noteQuery.data) return undefined;
    return toKnowledgeNoteFormValues(noteQuery.data);
  }, [noteQuery.data]);

  const handleUpdate = useCallback(
    async (values: KnowledgeNoteFormValues) => {
      if (!noteId) return;
      try {
        await updateMutation.mutateAsync({
          noteId,
          payload: toKnowledgeNoteRequest(values),
        });
        notification.success('Knowledge note updated');
        setEditOpen(false);
      } catch (error) {
        notification.error(getErrorMessage(error, 'Failed to update note'));
      }
    },
    [noteId, notification, updateMutation],
  );

  const handleDelete = useCallback(async () => {
    if (!noteId) return;
    try {
      await deleteMutation.mutateAsync(noteId);
      notification.success('Knowledge note deleted');
      navigate('/knowledge');
    } catch (error) {
      notification.error(getErrorMessage(error, 'Failed to delete note'));
    }
  }, [deleteMutation, navigate, noteId, notification]);

  if (!noteId) {
    return (
      <EmptyState
        title="Note not found"
        description="The knowledge note identifier is missing from the URL."
        actionLabel="Back to knowledge"
        onAction={() => navigate('/knowledge')}
      />
    );
  }

  if (noteQuery.isLoading) {
    return <LoadingOverlay open label="Loading note…" />;
  }

  if (noteQuery.isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(noteQuery.error, 'Failed to load knowledge note')}
        onRetry={() => void noteQuery.refetch()}
      />
    );
  }

  const note = noteQuery.data;
  if (!note) {
    return (
      <EmptyState
        title="Note not found"
        description="This knowledge note may have been deleted or you do not have access."
        actionLabel="Back to knowledge"
        onAction={() => navigate('/knowledge')}
      />
    );
  }

  return (
    <>
      <PageHeader
        title={note.title}
        description={note.summary}
        actions={
          <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
            <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/knowledge')}>
              Back
            </Button>
            <Button
              startIcon={<EditOutlinedIcon />}
              variant="outlined"
              onClick={() => setEditOpen(true)}
            >
              Edit
            </Button>
            <Button
              startIcon={<DeleteOutlineIcon />}
              color="error"
              variant="outlined"
              onClick={() => setDeleteOpen(true)}
            >
              Delete
            </Button>
          </Stack>
        }
      />

      <Paper variant="outlined" sx={{ p: 3, mb: 3 }}>
        <Stack
          direction={{ xs: 'column', sm: 'row' }}
          spacing={2}
          divider={<Divider flexItem orientation="vertical" />}
        >
          <Box>
            <Typography variant="caption" color="text.secondary" display="block">
              Category
            </Typography>
            <Typography variant="body2">{note.category?.name ?? '—'}</Typography>
          </Box>
          <Box>
            <Typography variant="caption" color="text.secondary" display="block">
              Created
            </Typography>
            <Typography variant="body2">{formatDate(note.createdAt)}</Typography>
          </Box>
          <Box>
            <Typography variant="caption" color="text.secondary" display="block">
              Updated
            </Typography>
            <Typography variant="body2">{formatDateTime(note.updatedAt)}</Typography>
          </Box>
        </Stack>

        {note.tags.length > 0 ? (
          <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap sx={{ mt: 2 }}>
            {note.tags.map((tag) => (
              <Chip key={tag} label={tag} size="small" />
            ))}
          </Stack>
        ) : null}
      </Paper>

      <Paper variant="outlined" sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>
          Content
        </Typography>
        <MarkdownViewer content={note.content} />
      </Paper>

      <FormDialog
        open={editOpen}
        title="Edit knowledge note"
        submitLabel="Save changes"
        loading={updateMutation.isPending}
        maxWidth="md"
        onClose={() => {
          if (!updateMutation.isPending) setEditOpen(false);
        }}
        onSubmit={() => editSubmitRef.current?.()}
      >
        {editDefaults ? (
          <KnowledgeNoteForm
            key={note.id}
            defaultValues={editDefaults}
            onRegisterSubmit={(submit) => {
              editSubmitRef.current = submit;
            }}
            onSubmit={(values) => void handleUpdate(values)}
          />
        ) : null}
      </FormDialog>

      <ConfirmationDialog
        open={deleteOpen}
        title="Delete knowledge note"
        description={`Delete "${note.title}"? This action cannot be undone.`}
        confirmLabel="Delete"
        danger
        loading={deleteMutation.isPending}
        onConfirm={() => void handleDelete()}
        onCancel={() => {
          if (!deleteMutation.isPending) setDeleteOpen(false);
        }}
      />
    </>
  );
}
