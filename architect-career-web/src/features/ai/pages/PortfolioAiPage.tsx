import { zodResolver } from '@hookform/resolvers/zod';
import { Box, Button, Chip, Grid, Stack, TextField, Typography } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { PageHeader } from '@/shared/components';
import {
  AiCard,
  AiResultPanel,
  AiStatusChip,
  AiUnavailableBanner,
  RecommendationCard,
  SuggestionCard,
} from '@/features/ai/components';
import {
  usePortfolioReviewMutation,
  useSkillGapMutation,
} from '@/features/ai/hooks/useAiMutations';
import { useAiAvailability } from '@/features/ai/hooks/useAiHealth';
import { getCapability } from '@/features/ai/services/aiCapabilities';
import {
  parseCommaSeparatedList,
  portfolioReviewSchema,
  skillGapSchema,
  type PortfolioReviewFormValues,
  type SkillGapFormValues,
} from '@/features/ai/schemas/ai.schemas';

function formatPortfolioReviewMarkdown(data: {
  summary: string;
  strengths: string[];
  improvements: string[];
  score: number;
}): string {
  const strengths = data.strengths.map((item) => `- ${item}`).join('\n');
  const improvements = data.improvements.map((item) => `- ${item}`).join('\n');
  return `## Summary\n\n${data.summary}\n\n## Score\n\n${data.score}\n\n## Strengths\n\n${strengths}\n\n## Improvements\n\n${improvements}`;
}

export function PortfolioAiPage() {
  const { canInvoke, unavailableReason } = useAiAvailability();
  const reviewMutation = usePortfolioReviewMutation();
  const skillGapMutation = useSkillGapMutation();

  const reviewForm = useForm<PortfolioReviewFormValues>({
    resolver: zodResolver(portfolioReviewSchema),
    defaultValues: { targetRole: '', projectIds: '' },
  });

  const skillGapForm = useForm<SkillGapFormValues>({
    resolver: zodResolver(skillGapSchema),
    defaultValues: { targetRole: '', currentSkills: '', projectTechnologies: '' },
  });

  const onReview = (values: PortfolioReviewFormValues) => {
    reviewMutation.mutate({
      targetRole: values.targetRole,
      projectIds: parseCommaSeparatedList(values.projectIds),
    });
  };

  const onSkillGap = (values: SkillGapFormValues) => {
    skillGapMutation.mutate({
      targetRole: values.targetRole,
      currentSkills: parseCommaSeparatedList(values.currentSkills),
      projectTechnologies: parseCommaSeparatedList(values.projectTechnologies),
    });
  };

  const technologyCapability = getCapability('portfolio.technology');
  const projectSummaryCapability = getCapability('portfolio.project-summary');
  const profileReviewCapability = getCapability('portfolio.profile-review');

  const reviewContent = reviewMutation.data
    ? formatPortfolioReviewMarkdown(reviewMutation.data)
    : null;

  return (
    <Box>
      <PageHeader
        title="Portfolio AI"
        description="Review projects, analyze skill gaps, and strengthen your professional profile."
      />

      {!canInvoke && unavailableReason ? <AiUnavailableBanner message={unavailableReason} /> : null}

      <Grid container spacing={2}>
        <Grid size={{ xs: 12, lg: 6 }}>
          <AiCard
            title="Portfolio Review"
            description="Review projects for a target role with scored feedback."
          >
            <Stack component="form" spacing={2} onSubmit={reviewForm.handleSubmit(onReview)}>
              <Controller
                name="targetRole"
                control={reviewForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Target role"
                    fullWidth
                    required
                    error={Boolean(reviewForm.formState.errors.targetRole)}
                    helperText={reviewForm.formState.errors.targetRole?.message}
                  />
                )}
              />
              <Controller
                name="projectIds"
                control={reviewForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Project IDs (comma-separated UUIDs)"
                    fullWidth
                    required
                    multiline
                    minRows={2}
                    error={Boolean(reviewForm.formState.errors.projectIds)}
                    helperText={reviewForm.formState.errors.projectIds?.message}
                  />
                )}
              />
              <Button
                type="submit"
                variant="contained"
                disabled={!canInvoke || reviewMutation.isPending}
              >
                Review portfolio
              </Button>
            </Stack>
            <Box sx={{ mt: 2 }}>
              <AiResultPanel
                title="Portfolio review"
                content={reviewContent}
                loading={reviewMutation.isPending}
                error={reviewMutation.isError ? reviewMutation.error.message : null}
                onRetry={() => reviewForm.handleSubmit(onReview)()}
                downloadFilename="portfolio-review.md"
              />
            </Box>
          </AiCard>
        </Grid>

        <Grid size={{ xs: 12, lg: 6 }}>
          <AiCard
            title="Skill Gap Analysis"
            description="Compare current skills to a target role and close gaps."
          >
            <Stack component="form" spacing={2} onSubmit={skillGapForm.handleSubmit(onSkillGap)}>
              <Controller
                name="targetRole"
                control={skillGapForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Target role"
                    fullWidth
                    required
                    error={Boolean(skillGapForm.formState.errors.targetRole)}
                    helperText={skillGapForm.formState.errors.targetRole?.message}
                  />
                )}
              />
              <Controller
                name="currentSkills"
                control={skillGapForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Current skills (comma-separated)"
                    fullWidth
                    required
                    multiline
                    minRows={2}
                    error={Boolean(skillGapForm.formState.errors.currentSkills)}
                    helperText={skillGapForm.formState.errors.currentSkills?.message}
                  />
                )}
              />
              <Controller
                name="projectTechnologies"
                control={skillGapForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Project technologies (comma-separated)"
                    fullWidth
                    required
                    multiline
                    minRows={2}
                    error={Boolean(skillGapForm.formState.errors.projectTechnologies)}
                    helperText={skillGapForm.formState.errors.projectTechnologies?.message}
                  />
                )}
              />
              <Button
                type="submit"
                variant="contained"
                disabled={!canInvoke || skillGapMutation.isPending}
              >
                Analyze skill gap
              </Button>
            </Stack>
            <Box sx={{ mt: 2 }}>
              {skillGapMutation.isPending ? (
                <AiResultPanel title="Skill gap analysis" loading />
              ) : skillGapMutation.isError ? (
                <AiResultPanel
                  title="Skill gap analysis"
                  error={skillGapMutation.error.message}
                  onRetry={() => skillGapForm.handleSubmit(onSkillGap)()}
                />
              ) : skillGapMutation.data ? (
                <Stack spacing={2}>
                  <RecommendationCard
                    title={`Skill gaps for ${skillGapForm.getValues('targetRole')}`}
                    rationale={skillGapMutation.data.summary}
                  />
                  {skillGapMutation.data.missingSkills.length > 0 ? (
                    <Box>
                      <Typography variant="subtitle2" gutterBottom>
                        Missing skills
                      </Typography>
                      <Stack direction="row" flexWrap="wrap" gap={0.75}>
                        {skillGapMutation.data.missingSkills.map((skill) => (
                          <Chip
                            key={skill}
                            size="small"
                            label={skill}
                            color="warning"
                            variant="outlined"
                          />
                        ))}
                      </Stack>
                    </Box>
                  ) : null}
                  {skillGapMutation.data.recommendedActions.length > 0 ? (
                    <Box>
                      <Typography variant="subtitle2" gutterBottom>
                        Recommended actions
                      </Typography>
                      {skillGapMutation.data.recommendedActions.map((action) => (
                        <Typography key={action} variant="body2">
                          • {action}
                        </Typography>
                      ))}
                    </Box>
                  ) : null}
                </Stack>
              ) : (
                <Typography variant="body2" color="text.secondary">
                  Submit your skills to receive a gap analysis.
                </Typography>
              )}
            </Box>
          </AiCard>
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <SuggestionCard
            title={technologyCapability?.title ?? 'Technology Recommendation'}
            description={
              technologyCapability?.description ??
              'Technology recommendations to strengthen your portfolio are coming soon.'
            }
            actionLabel="Coming soon"
            disabled
          />
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <SuggestionCard
            title={projectSummaryCapability?.title ?? 'Project Summary'}
            description={
              projectSummaryCapability?.description ??
              'Professional project summaries will be available in a future release.'
            }
            actionLabel="Coming soon"
            disabled
          />
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <Stack spacing={1}>
            <SuggestionCard
              title={profileReviewCapability?.title ?? 'Professional Profile Review'}
              description={
                profileReviewCapability?.description ??
                'Overall profile narrative review is planned for a future release.'
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
