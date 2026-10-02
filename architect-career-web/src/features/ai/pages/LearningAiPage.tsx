import { zodResolver } from '@hookform/resolvers/zod';
import {
  Box,
  Button,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { PageHeader } from '@/shared/components';
import { moduleConfig } from '@/app/config/module.config';
import {
  AiCard,
  AiResultPanel,
  AiStatusChip,
  AiUnavailableBanner,
  RecommendationCard,
  SuggestionCard,
} from '@/features/ai/components';
import {
  useEvaluateProgressMutation,
  useGenerateQuizMutation,
  useRecommendNextTopicMutation,
} from '@/features/ai/hooks/useAiMutations';
import { useAiAvailability } from '@/features/ai/hooks/useAiHealth';
import { getCapability } from '@/features/ai/services/aiCapabilities';
import {
  evaluateProgressSchema,
  learningQuizSchema,
  parseCommaSeparatedList,
  parseCommaSeparatedNumbers,
  recommendNextTopicSchema,
  type EvaluateProgressFormValues,
  type LearningQuizFormValues,
  type RecommendNextTopicFormValues,
} from '@/features/ai/schemas/ai.schemas';

export function LearningAiPage() {
  const { canInvoke, unavailableReason } = useAiAvailability();
  const quizMutation = useGenerateQuizMutation();
  const nextTopicMutation = useRecommendNextTopicMutation();
  const progressMutation = useEvaluateProgressMutation();

  const quizForm = useForm<LearningQuizFormValues>({
    resolver: zodResolver(learningQuizSchema),
    defaultValues: {
      topic: '',
      difficulty: 'INTERMEDIATE',
      questionCount: moduleConfig.ai.defaultQuizQuestions,
    },
  });

  const nextTopicForm = useForm<RecommendNextTopicFormValues>({
    resolver: zodResolver(recommendNextTopicSchema),
    defaultValues: { planId: '', completedTopics: '', goals: '' },
  });

  const progressForm = useForm<EvaluateProgressFormValues>({
    resolver: zodResolver(evaluateProgressSchema),
    defaultValues: { planId: '', completedTopics: '', quizScores: '' },
  });

  const onQuiz = (values: LearningQuizFormValues) => {
    quizMutation.mutate({
      topic: values.topic,
      difficulty: values.difficulty,
      questionCount: values.questionCount,
    });
  };

  const onNextTopic = (values: RecommendNextTopicFormValues) => {
    nextTopicMutation.mutate({
      planId: values.planId,
      completedTopics: parseCommaSeparatedList(values.completedTopics),
      goals: parseCommaSeparatedList(values.goals),
    });
  };

  const onProgress = (values: EvaluateProgressFormValues) => {
    progressMutation.mutate({
      planId: values.planId,
      completedTopics: parseCommaSeparatedList(values.completedTopics),
      quizScores: parseCommaSeparatedNumbers(values.quizScores),
    });
  };

  const recommendCapability = getCapability('learning.recommend');
  const weakTopicsCapability = getCapability('learning.weak-topics');

  return (
    <Box>
      <PageHeader
        title="Learning AI"
        description="Generate quizzes, evaluate progress, and get learning recommendations."
      />

      {!canInvoke && unavailableReason ? <AiUnavailableBanner message={unavailableReason} /> : null}

      <Grid container spacing={2}>
        <Grid size={{ xs: 12, lg: 6 }}>
          <AiCard title="Generate Quiz" description="Create practice quizzes for a learning topic.">
            <Stack component="form" spacing={2} onSubmit={quizForm.handleSubmit(onQuiz)}>
              <Controller
                name="topic"
                control={quizForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Topic"
                    fullWidth
                    required
                    error={Boolean(quizForm.formState.errors.topic)}
                    helperText={quizForm.formState.errors.topic?.message}
                  />
                )}
              />
              <Controller
                name="difficulty"
                control={quizForm.control}
                render={({ field }) => (
                  <FormControl fullWidth>
                    <InputLabel id="quiz-difficulty-label">Difficulty</InputLabel>
                    <Select {...field} labelId="quiz-difficulty-label" label="Difficulty">
                      <MenuItem value="BEGINNER">Beginner</MenuItem>
                      <MenuItem value="INTERMEDIATE">Intermediate</MenuItem>
                      <MenuItem value="ADVANCED">Advanced</MenuItem>
                    </Select>
                  </FormControl>
                )}
              />
              <Controller
                name="questionCount"
                control={quizForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Question count"
                    type="number"
                    fullWidth
                    error={Boolean(quizForm.formState.errors.questionCount)}
                    helperText={quizForm.formState.errors.questionCount?.message}
                  />
                )}
              />
              <Button
                type="submit"
                variant="contained"
                disabled={!canInvoke || quizMutation.isPending}
              >
                Generate quiz
              </Button>
            </Stack>
            <Box sx={{ mt: 2 }}>
              <AiResultPanel
                title="Quiz"
                loading={quizMutation.isPending}
                error={quizMutation.isError ? quizMutation.error.message : null}
                onRetry={() => quizForm.handleSubmit(onQuiz)()}
              >
                {quizMutation.data?.questions.map((question, index) => (
                  <Box key={`${question.prompt}-${index}`} sx={{ mb: 2 }}>
                    <Typography variant="subtitle2" gutterBottom>
                      {index + 1}. {question.prompt}
                    </Typography>
                    <Stack spacing={0.5} sx={{ mb: 1 }}>
                      {question.choices.map((choice) => (
                        <Typography key={choice} variant="body2" color="text.secondary">
                          • {choice}
                        </Typography>
                      ))}
                    </Stack>
                    <Typography variant="caption" color="primary">
                      Answer: {question.correctAnswer}
                    </Typography>
                    {question.explanation ? (
                      <Typography variant="body2" sx={{ mt: 0.5 }}>
                        {question.explanation}
                      </Typography>
                    ) : null}
                  </Box>
                ))}
              </AiResultPanel>
            </Box>
          </AiCard>
        </Grid>

        <Grid size={{ xs: 12, lg: 6 }}>
          <AiCard
            title="Next Topic Recommendation"
            description="Suggest the best next topic in your learning plan."
          >
            <Stack component="form" spacing={2} onSubmit={nextTopicForm.handleSubmit(onNextTopic)}>
              <Controller
                name="planId"
                control={nextTopicForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Plan ID"
                    fullWidth
                    required
                    error={Boolean(nextTopicForm.formState.errors.planId)}
                    helperText={nextTopicForm.formState.errors.planId?.message}
                  />
                )}
              />
              <Controller
                name="completedTopics"
                control={nextTopicForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Completed topics (comma-separated)"
                    fullWidth
                    multiline
                    minRows={2}
                  />
                )}
              />
              <Controller
                name="goals"
                control={nextTopicForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Goals (comma-separated)"
                    fullWidth
                    multiline
                    minRows={2}
                  />
                )}
              />
              <Button
                type="submit"
                variant="contained"
                disabled={!canInvoke || nextTopicMutation.isPending}
              >
                Recommend next topic
              </Button>
            </Stack>
            <Box sx={{ mt: 2 }}>
              {nextTopicMutation.isPending ? (
                <AiResultPanel title="Recommendation" loading />
              ) : nextTopicMutation.isError ? (
                <AiResultPanel
                  title="Recommendation"
                  error={nextTopicMutation.error.message}
                  onRetry={() => nextTopicForm.handleSubmit(onNextTopic)()}
                />
              ) : nextTopicMutation.data ? (
                <RecommendationCard
                  title={nextTopicMutation.data.topic}
                  rationale={nextTopicMutation.data.rationale}
                  related={nextTopicMutation.data.relatedTopics}
                />
              ) : (
                <Typography variant="body2" color="text.secondary">
                  Submit a plan to receive a next-topic recommendation.
                </Typography>
              )}
            </Box>
          </AiCard>
        </Grid>

        <Grid size={{ xs: 12 }}>
          <AiCard
            title="Progress Evaluation"
            description="Evaluate learning progress with strengths and focus areas."
          >
            <Stack component="form" spacing={2} onSubmit={progressForm.handleSubmit(onProgress)}>
              <Grid container spacing={2}>
                <Grid size={{ xs: 12, md: 4 }}>
                  <Controller
                    name="planId"
                    control={progressForm.control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Plan ID"
                        fullWidth
                        required
                        error={Boolean(progressForm.formState.errors.planId)}
                        helperText={progressForm.formState.errors.planId?.message}
                      />
                    )}
                  />
                </Grid>
                <Grid size={{ xs: 12, md: 4 }}>
                  <Controller
                    name="completedTopics"
                    control={progressForm.control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Completed topics (comma-separated)"
                        fullWidth
                        multiline
                        minRows={2}
                      />
                    )}
                  />
                </Grid>
                <Grid size={{ xs: 12, md: 4 }}>
                  <Controller
                    name="quizScores"
                    control={progressForm.control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Quiz scores (comma-separated numbers)"
                        fullWidth
                        multiline
                        minRows={2}
                      />
                    )}
                  />
                </Grid>
              </Grid>
              <Button
                type="submit"
                variant="contained"
                disabled={!canInvoke || progressMutation.isPending}
              >
                Evaluate progress
              </Button>
            </Stack>
            <Box sx={{ mt: 2 }}>
              <AiResultPanel
                title="Progress evaluation"
                loading={progressMutation.isPending}
                error={progressMutation.isError ? progressMutation.error.message : null}
                onRetry={() => progressForm.handleSubmit(onProgress)()}
              >
                {progressMutation.data ? (
                  <Stack spacing={1.5}>
                    <Typography variant="body1">
                      Progress: {progressMutation.data.progressPercent}%
                    </Typography>
                    <Typography variant="body2">{progressMutation.data.summary}</Typography>
                    {progressMutation.data.strengths.length > 0 ? (
                      <Box>
                        <Typography variant="subtitle2">Strengths</Typography>
                        {progressMutation.data.strengths.map((item) => (
                          <Typography key={item} variant="body2">
                            • {item}
                          </Typography>
                        ))}
                      </Box>
                    ) : null}
                    {progressMutation.data.focusAreas.length > 0 ? (
                      <Box>
                        <Typography variant="subtitle2">Focus areas</Typography>
                        {progressMutation.data.focusAreas.map((item) => (
                          <Typography key={item} variant="body2">
                            • {item}
                          </Typography>
                        ))}
                      </Box>
                    ) : null}
                  </Stack>
                ) : null}
              </AiResultPanel>
            </Box>
          </AiCard>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <SuggestionCard
            title={recommendCapability?.title ?? 'Learning Recommendation'}
            description={
              recommendCapability?.description ??
              'Personalized learning recommendations will be available in a future release.'
            }
            actionLabel="Coming soon"
            disabled
          />
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <Stack spacing={1}>
            <SuggestionCard
              title={weakTopicsCapability?.title ?? 'Weak Topic Detection'}
              description={
                weakTopicsCapability?.description ??
                'Identify topics that need more practice once the capability ships.'
              }
              actionLabel="Coming soon"
              disabled
            />
            <AiStatusChip kind="coming_soon" label="Coming soon" />
          </Stack>
        </Grid>
      </Grid>
    </Box>
  );
}
