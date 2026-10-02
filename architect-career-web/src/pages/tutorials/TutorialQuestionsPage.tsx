import { useCallback, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import AddIcon from '@mui/icons-material/Add';
import { Button } from '@mui/material';
import {
  EmptyState,
  ErrorPanel,
  FormDialog,
  LoadingOverlay,
  PageHeader,
} from '@/shared/components';
import { useNotification } from '@/shared/hooks/useNotification';
import { getErrorMessage } from '@/shared/utils/error';
import { TutorialBreadcrumb } from '@/features/tutorials/components/TutorialBreadcrumb';
import { TutorialQuestionForm } from '@/features/tutorials/components/TutorialQuestionForm';
import { TutorialQuestionList } from '@/features/tutorials/components/TutorialQuestionList';
import {
  useCreateTutorialQuestionMutation,
  useTutorialQuestionsQuery,
  useTutorialTopicQuery,
} from '@/features/tutorials/hooks/useTutorialQueries';
import type { TutorialQuestionFormValues } from '@/features/tutorials/schemas/tutorial.schemas';

export function TutorialQuestionsPage() {
  const { '*': splat = '' } = useParams();
  const raw = splat.replace(/^\/+|\/+$/g, '');
  const path = raw.replace(/\/questions$/i, '');
  const navigate = useNavigate();
  const notification = useNotification();
  const [createOpen, setCreateOpen] = useState(false);
  const createSubmitRef = useRef<(() => void) | null>(null);

  const topicQuery = useTutorialTopicQuery(path);
  const questionsQuery = useTutorialQuestionsQuery(path);
  const createMutation = useCreateTutorialQuestionMutation();

  const handleCreate = useCallback(
    async (values: TutorialQuestionFormValues) => {
      if (!topicQuery.data) return;
      try {
        await createMutation.mutateAsync({
          topicId: topicQuery.data.id,
          question: values.question,
          answer: values.answer,
        });
        notification.success('Question created');
        setCreateOpen(false);
        await questionsQuery.refetch();
        await topicQuery.refetch();
      } catch (error) {
        notification.error(getErrorMessage(error, 'Failed to create question'));
      }
    },
    [createMutation, notification, questionsQuery, topicQuery],
  );

  if (!path) {
    return (
      <EmptyState
        title="Questions not found"
        actionLabel="Back to tutorials"
        onAction={() => navigate('/tutorials')}
      />
    );
  }

  if (topicQuery.isLoading || questionsQuery.isLoading) {
    return <LoadingOverlay open label="Loading questions…" />;
  }

  if (topicQuery.isError) {
    return (
      <ErrorPanel
        message={getErrorMessage(topicQuery.error, 'Failed to load tutorial topic')}
        onRetry={() => void topicQuery.refetch()}
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

  const topic = topicQuery.data;
  const page = questionsQuery.data;
  if (!topic || !page) {
    return (
      <EmptyState
        title="Topic not found"
        actionLabel="Back to tutorials"
        onAction={() => navigate('/tutorials')}
      />
    );
  }

  return (
    <>
      <TutorialBreadcrumb items={page.breadcrumb} leaf="Questions & Answers" />
      <PageHeader
        title={`${topic.title} — Questions & Answers`}
        actions={
          <Button startIcon={<AddIcon />} variant="contained" onClick={() => setCreateOpen(true)}>
            Add question
          </Button>
        }
      />

      <TutorialQuestionList questions={page.questions} />

      <FormDialog
        open={createOpen}
        title="Add question"
        onClose={() => setCreateOpen(false)}
        onSubmit={() => createSubmitRef.current?.()}
        submitLabel="Create"
        loading={createMutation.isPending}
      >
        <TutorialQuestionForm
          onSubmit={handleCreate}
          onRegisterSubmit={(submit) => {
            createSubmitRef.current = submit;
          }}
        />
      </FormDialog>
    </>
  );
}
