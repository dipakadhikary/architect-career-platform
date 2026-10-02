import { useCallback, useMemo, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import AddIcon from '@mui/icons-material/Add';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { Box, Button, Paper, Stack, Typography } from '@mui/material';
import {
  ErrorPanel,
  FormDialog,
  LoadingOverlay,
  MarkdownViewer,
  PageHeader,
  EmptyState,
} from '@/shared/components';
import { useNotification } from '@/shared/hooks/useNotification';
import { getErrorMessage } from '@/shared/utils/error';
import { TutorialBreadcrumb } from '@/features/tutorials/components/TutorialBreadcrumb';
import { TutorialConceptForm } from '@/features/tutorials/components/TutorialConceptForm';
import { TutorialQuestionForm } from '@/features/tutorials/components/TutorialQuestionForm';
import { TutorialQuestionList } from '@/features/tutorials/components/TutorialQuestionList';
import {
  useCreateTutorialQuestionMutation,
  useTutorialConceptQuery,
  useTutorialQuestionsQuery,
  useTutorialTopicQuery,
  useUpsertTutorialConceptMutation,
} from '@/features/tutorials/hooks/useTutorialQueries';
import type {
  TutorialConceptFormValues,
  TutorialQuestionFormValues,
} from '@/features/tutorials/schemas/tutorial.schemas';

export function TutorialConceptPage() {
  const { '*': splat = '' } = useParams();
  const raw = splat.replace(/^\/+|\/+$/g, '');
  const path = raw.replace(/\/concept$/i, '');
  const navigate = useNavigate();
  const notification = useNotification();
  const [editConceptOpen, setEditConceptOpen] = useState(false);
  const [addQuestionOpen, setAddQuestionOpen] = useState(false);
  const conceptSubmitRef = useRef<(() => void) | null>(null);
  const questionSubmitRef = useRef<(() => void) | null>(null);

  const topicQuery = useTutorialTopicQuery(path);
  const conceptQuery = useTutorialConceptQuery(
    path,
    Boolean(path) && Boolean(topicQuery.data?.hasConcept),
  );
  const questionsQuery = useTutorialQuestionsQuery(path, Boolean(path));
  const upsertMutation = useUpsertTutorialConceptMutation();
  const createQuestionMutation = useCreateTutorialQuestionMutation();

  const conceptDefaults = useMemo<TutorialConceptFormValues>(
    () => ({ content: conceptQuery.data?.content ?? '' }),
    [conceptQuery.data?.content],
  );

  const handleSaveConcept = useCallback(
    async (values: TutorialConceptFormValues) => {
      if (!topicQuery.data) return;
      try {
        await upsertMutation.mutateAsync({ topicId: topicQuery.data.id, content: values.content });
        notification.success('Concept saved');
        setEditConceptOpen(false);
        await conceptQuery.refetch();
        await topicQuery.refetch();
      } catch (error) {
        notification.error(getErrorMessage(error, 'Failed to save concept'));
      }
    },
    [conceptQuery, notification, topicQuery, upsertMutation],
  );

  const handleCreateQuestion = useCallback(
    async (values: TutorialQuestionFormValues) => {
      if (!topicQuery.data) return;
      try {
        await createQuestionMutation.mutateAsync({
          topicId: topicQuery.data.id,
          question: values.question,
          answer: values.answer,
        });
        notification.success('Question created');
        setAddQuestionOpen(false);
        await questionsQuery.refetch();
        await topicQuery.refetch();
      } catch (error) {
        notification.error(getErrorMessage(error, 'Failed to create question'));
      }
    },
    [createQuestionMutation, notification, questionsQuery, topicQuery],
  );

  if (!path) {
    return (
      <EmptyState
        title="Tutorial not found"
        actionLabel="Back to tutorials"
        onAction={() => navigate('/tutorials')}
      />
    );
  }

  if (
    topicQuery.isLoading ||
    questionsQuery.isLoading ||
    (topicQuery.data?.hasConcept && conceptQuery.isLoading)
  ) {
    return <LoadingOverlay open label="Loading tutorial…" />;
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
        actionLabel="Back to tutorials"
        onAction={() => navigate('/tutorials')}
      />
    );
  }

  if (topic.hasConcept && conceptQuery.isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(conceptQuery.error, 'Failed to load concept')}
        onRetry={() => void conceptQuery.refetch()}
      />
    );
  }

  if (questionsQuery.isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(questionsQuery.error, 'Failed to load questions')}
        onRetry={() => void questionsQuery.refetch()}
      />
    );
  }

  const breadcrumb =
    conceptQuery.data?.breadcrumb ??
    questionsQuery.data?.breadcrumb ?? [
      { id: topic.id, title: topic.title, slug: topic.slug, path: topic.path },
    ];
  const questions = questionsQuery.data?.questions ?? [];

  return (
    <>
      <TutorialBreadcrumb items={breadcrumb.slice(0, -1)} leaf={topic.title} />
      <PageHeader
        title={topic.title}
        actions={
          <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
            <Button
              startIcon={<EditOutlinedIcon />}
              variant="outlined"
              onClick={() => setEditConceptOpen(true)}
            >
              {topic.hasConcept ? 'Edit' : 'Add content'}
            </Button>
            <Button
              startIcon={<AddIcon />}
              variant="contained"
              onClick={() => setAddQuestionOpen(true)}
            >
              Add question
            </Button>
          </Stack>
        }
      />

      <Stack spacing={3}>
        <Box>
          {!topic.hasConcept ? (
            <Typography color="text.secondary">
              No concept content yet. Use Add content to write Markdown for this topic.
            </Typography>
          ) : (
            <Paper variant="outlined" sx={{ p: 3 }}>
              <MarkdownViewer content={conceptQuery.data?.content ?? ''} withSyntaxHighlight />
            </Paper>
          )}
        </Box>

        <Box>
          <Typography variant="h6" gutterBottom>
            Questions & Answers
          </Typography>
          <TutorialQuestionList questions={questions} />
        </Box>
      </Stack>

      <FormDialog
        open={editConceptOpen}
        title={topic.hasConcept ? 'Edit concept' : 'Add concept'}
        onClose={() => setEditConceptOpen(false)}
        onSubmit={() => conceptSubmitRef.current?.()}
        submitLabel="Save"
        loading={upsertMutation.isPending}
      >
        <TutorialConceptForm
          defaultValues={conceptDefaults}
          onSubmit={handleSaveConcept}
          onRegisterSubmit={(submit) => {
            conceptSubmitRef.current = submit;
          }}
        />
      </FormDialog>

      <FormDialog
        open={addQuestionOpen}
        title="Add question"
        onClose={() => setAddQuestionOpen(false)}
        onSubmit={() => questionSubmitRef.current?.()}
        submitLabel="Create"
        loading={createQuestionMutation.isPending}
      >
        <TutorialQuestionForm
          onSubmit={handleCreateQuestion}
          onRegisterSubmit={(submit) => {
            questionSubmitRef.current = submit;
          }}
        />
      </FormDialog>
    </>
  );
}
