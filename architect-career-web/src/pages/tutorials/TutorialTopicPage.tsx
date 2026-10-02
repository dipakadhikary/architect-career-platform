import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { Link as RouterLink, useNavigate, useParams } from 'react-router-dom';
import AddIcon from '@mui/icons-material/Add';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { Button, Paper, Stack, Typography } from '@mui/material';
import {
  ConfirmationDialog,
  EmptyState,
  ErrorPanel,
  FormDialog,
  LoadingOverlay,
  PageHeader,
} from '@/shared/components';
import { useNotification } from '@/shared/hooks/useNotification';
import { getErrorMessage } from '@/shared/utils/error';
import { TutorialBreadcrumb } from '@/features/tutorials/components/TutorialBreadcrumb';
import {
  TutorialTopicForm,
  flattenTutorialOptions,
} from '@/features/tutorials/components/TutorialTopicForm';
import {
  useCreateTutorialTopicMutation,
  useDeleteTutorialTopicMutation,
  useTutorialTopicQuery,
  useTutorialTreeQuery,
  useUpdateTutorialTopicMutation,
} from '@/features/tutorials/hooks/useTutorialQueries';
import type { TutorialTopicFormValues } from '@/features/tutorials/schemas/tutorial.schemas';

export function TutorialTopicPage() {
  const { '*': splat = '' } = useParams();
  const path = splat.replace(/^\/+|\/+$/g, '');
  const navigate = useNavigate();
  const notification = useNotification();

  const [editOpen, setEditOpen] = useState(false);
  const [childOpen, setChildOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const editSubmitRef = useRef<(() => void) | null>(null);
  const childSubmitRef = useRef<(() => void) | null>(null);

  const topicQuery = useTutorialTopicQuery(path);
  const treeQuery = useTutorialTreeQuery();
  const updateMutation = useUpdateTutorialTopicMutation();
  const createMutation = useCreateTutorialTopicMutation();
  const deleteMutation = useDeleteTutorialTopicMutation();

  useEffect(() => {
    const topic = topicQuery.data;
    if (topic && topic.childCount === 0) {
      navigate(`/tutorials/${topic.path}/concept`, { replace: true });
    }
  }, [navigate, topicQuery.data]);

  const parentOptions = useMemo(
    () => flattenTutorialOptions(treeQuery.data ?? []).filter((option) => option.id !== topicQuery.data?.id),
    [topicQuery.data?.id, treeQuery.data],
  );

  const editDefaults = useMemo<TutorialTopicFormValues | undefined>(() => {
    if (!topicQuery.data) return undefined;
    return {
      title: topicQuery.data.title,
      slug: topicQuery.data.slug,
      parentId: topicQuery.data.parentId,
      sortOrder: topicQuery.data.sortOrder,
    };
  }, [topicQuery.data]);

  const handleUpdate = useCallback(
    async (values: TutorialTopicFormValues) => {
      if (!topicQuery.data) return;
      try {
        const updated = await updateMutation.mutateAsync({
          topicId: topicQuery.data.id,
          payload: {
            title: values.title,
            slug: values.slug?.trim() ? values.slug.trim() : null,
            parentId: values.parentId ?? null,
            sortOrder: values.sortOrder ?? null,
          },
        });
        notification.success('Tutorial topic updated');
        setEditOpen(false);
        if (updated.path !== path) {
          navigate(
            updated.childCount === 0
              ? `/tutorials/${updated.path}/concept`
              : `/tutorials/${updated.path}`,
            { replace: true },
          );
        }
      } catch (error) {
        notification.error(getErrorMessage(error, 'Failed to update topic'));
      }
    },
    [navigate, notification, path, topicQuery.data, updateMutation],
  );

  const handleCreateChild = useCallback(
    async (values: TutorialTopicFormValues) => {
      if (!topicQuery.data) return;
      try {
        const child = await createMutation.mutateAsync({
          title: values.title,
          slug: values.slug?.trim() ? values.slug.trim() : null,
          parentId: topicQuery.data.id,
          sortOrder: values.sortOrder ?? null,
        });
        notification.success('Child topic created');
        setChildOpen(false);
        navigate(`/tutorials/${child.path}/concept`);
      } catch (error) {
        notification.error(getErrorMessage(error, 'Failed to create child topic'));
      }
    },
    [createMutation, navigate, notification, topicQuery.data],
  );

  const handleDelete = useCallback(async () => {
    if (!topicQuery.data) return;
    try {
      await deleteMutation.mutateAsync(topicQuery.data.id);
      notification.success('Tutorial topic deleted');
      navigate('/tutorials');
    } catch (error) {
      notification.error(getErrorMessage(error, 'Failed to delete topic'));
    }
  }, [deleteMutation, navigate, notification, topicQuery.data]);

  if (!path) {
    return (
      <EmptyState
        title="Topic not found"
        description="The tutorial path is missing."
        actionLabel="Back to tutorials"
        onAction={() => navigate('/tutorials')}
      />
    );
  }

  if (topicQuery.isLoading) {
    return <LoadingOverlay open label="Loading topic…" />;
  }

  if (topicQuery.isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(topicQuery.error, 'Failed to load tutorial topic')}
        onRetry={() => void topicQuery.refetch()}
      />
    );
  }

  const topic = topicQuery.data;
  if (!topic) {
    return (
      <EmptyState
        title="Topic not found"
        description="This tutorial topic may have been deleted or you do not have access."
        actionLabel="Back to tutorials"
        onAction={() => navigate('/tutorials')}
      />
    );
  }

  if (topic.childCount === 0) {
    return <LoadingOverlay open label="Opening concept…" />;
  }

  const breadcrumb = topic.breadcrumb ?? [];

  return (
    <>
      <TutorialBreadcrumb items={breadcrumb.slice(0, -1)} leaf={topic.title} />
      <PageHeader
        title={topic.title}
        description={`Stable path: /tutorials/${topic.path}`}
        actions={
          <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
            <Button startIcon={<AddIcon />} variant="outlined" onClick={() => setChildOpen(true)}>
              Add child
            </Button>
            <Button startIcon={<EditOutlinedIcon />} variant="outlined" onClick={() => setEditOpen(true)}>
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

      <Paper variant="outlined" sx={{ p: 3, mb: 2 }}>
        <Stack spacing={2}>
          <Typography color="text.secondary">
            Open learning content for this topic, or create child topics from the actions above.
          </Typography>
          <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
            <Button
              component={RouterLink}
              to={`/tutorials/${topic.path}/concept`}
              variant={topic.hasConcept ? 'contained' : 'outlined'}
            >
              {topic.hasConcept ? 'Open Concept' : 'Create Concept'}
            </Button>
            <Button
              component={RouterLink}
              to={`/tutorials/${topic.path}/questions`}
              variant={topic.hasQuestions ? 'contained' : 'outlined'}
            >
              {topic.hasQuestions ? 'Open Questions & Answers' : 'Add Questions & Answers'}
            </Button>
          </Stack>
        </Stack>
      </Paper>

      <FormDialog
        open={editOpen}
        title="Edit tutorial topic"
        onClose={() => setEditOpen(false)}
        onSubmit={() => editSubmitRef.current?.()}
        submitLabel="Save"
        loading={updateMutation.isPending}
      >
        {editDefaults ? (
          <TutorialTopicForm
            defaultValues={editDefaults}
            parentOptions={parentOptions}
            onSubmit={handleUpdate}
            onRegisterSubmit={(submit) => {
              editSubmitRef.current = submit;
            }}
          />
        ) : null}
      </FormDialog>

      <FormDialog
        open={childOpen}
        title="Create child topic"
        onClose={() => setChildOpen(false)}
        onSubmit={() => childSubmitRef.current?.()}
        submitLabel="Create"
        loading={createMutation.isPending}
      >
        <TutorialTopicForm
          defaultValues={{ title: '', slug: '', parentId: topic.id, sortOrder: null }}
          parentOptions={parentOptions}
          onSubmit={handleCreateChild}
          onRegisterSubmit={(submit) => {
            childSubmitRef.current = submit;
          }}
        />
      </FormDialog>

      <ConfirmationDialog
        open={deleteOpen}
        title="Delete tutorial topic"
        description="This deletes the topic, its descendants, and all concept/Q&A content."
        onConfirm={() => void handleDelete()}
        onCancel={() => setDeleteOpen(false)}
      />
    </>
  );
}
