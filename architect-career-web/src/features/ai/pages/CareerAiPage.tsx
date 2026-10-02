import { zodResolver } from '@hookform/resolvers/zod';
import { Box, Button, Grid, Stack, TextField, Typography } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { PageHeader } from '@/shared/components';
import { useNotification } from '@/shared/hooks/useNotification';
import {
  AiCard,
  AiResponseViewer,
  AiResultPanel,
  AiStatusChip,
  AiUnavailableBanner,
  SuggestionCard,
} from '@/features/ai/components';
import {
  useAnalyzeInterviewMutation,
  useGenerateCoverLetterMutation,
  useGenerateResumeMutation,
} from '@/features/ai/hooks/useAiMutations';
import { useAiAvailability } from '@/features/ai/hooks/useAiHealth';
import { copyTextToClipboard, downloadTextFile } from '@/features/ai/services/chatSession.service';
import { getCapability } from '@/features/ai/services/aiCapabilities';
import {
  coverLetterSchema,
  interviewAnalysisSchema,
  parseCommaSeparatedList,
  resumeGenerateSchema,
  type CoverLetterFormValues,
  type InterviewAnalysisFormValues,
  type ResumeGenerateFormValues,
} from '@/features/ai/schemas/ai.schemas';

function formatInterviewAnalysisMarkdown(data: {
  summary: string;
  strengths: string[];
  improvements: string[];
  score: number;
}): string {
  const strengths = data.strengths.map((item) => `- ${item}`).join('\n');
  const improvements = data.improvements.map((item) => `- ${item}`).join('\n');
  return `## Summary\n\n${data.summary}\n\n## Score\n\n${data.score}\n\n## Strengths\n\n${strengths}\n\n## Improvements\n\n${improvements}`;
}

export function CareerAiPage() {
  const { canInvoke, unavailableReason } = useAiAvailability();
  const { success, error: notifyError } = useNotification();
  const resumeMutation = useGenerateResumeMutation();
  const interviewMutation = useAnalyzeInterviewMutation();
  const coverLetterMutation = useGenerateCoverLetterMutation();

  const resumeForm = useForm<ResumeGenerateFormValues>({
    resolver: zodResolver(resumeGenerateSchema),
    defaultValues: { targetRole: '', experienceHighlights: '', skills: '' },
  });

  const interviewForm = useForm<InterviewAnalysisFormValues>({
    resolver: zodResolver(interviewAnalysisSchema),
    defaultValues: { transcript: '', jobDescription: '' },
  });

  const coverLetterForm = useForm<CoverLetterFormValues>({
    resolver: zodResolver(coverLetterSchema),
    defaultValues: { targetRole: '', companyName: '', highlights: '' },
  });

  const onResume = (values: ResumeGenerateFormValues) => {
    resumeMutation.mutate({
      targetRole: values.targetRole,
      experienceHighlights: values.experienceHighlights
        .split('\n')
        .map((line) => line.trim())
        .filter(Boolean),
      skills: parseCommaSeparatedList(values.skills),
    });
  };

  const onInterview = (values: InterviewAnalysisFormValues) => {
    interviewMutation.mutate({
      transcript: values.transcript,
      jobDescription: values.jobDescription,
    });
  };

  const onCoverLetter = (values: CoverLetterFormValues) => {
    coverLetterMutation.mutate({
      targetRole: values.targetRole,
      companyName: values.companyName,
      highlights: values.highlights
        .split('\n')
        .map((line) => line.trim())
        .filter(Boolean),
    });
  };

  const handleCopy = async (content: string) => {
    try {
      await copyTextToClipboard(content);
      success('Copied to clipboard');
    } catch {
      notifyError('Unable to copy');
    }
  };

  const mockInterviewCapability = getCapability('career.mock-interview');
  const recommendationCapability = getCapability('career.recommendation');

  const interviewContent = interviewMutation.data
    ? formatInterviewAnalysisMarkdown(interviewMutation.data)
    : null;

  return (
    <Box>
      <PageHeader
        title="Career AI"
        description="Resume generation, interview analysis, and cover letter drafting."
      />

      {!canInvoke && unavailableReason ? <AiUnavailableBanner message={unavailableReason} /> : null}

      <Grid container spacing={2}>
        <Grid size={{ xs: 12, lg: 6 }}>
          <AiCard
            title="Resume Generator"
            description="Draft a role-targeted resume from highlights and skills."
          >
            <Stack component="form" spacing={2} onSubmit={resumeForm.handleSubmit(onResume)}>
              <Controller
                name="targetRole"
                control={resumeForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Target role"
                    fullWidth
                    required
                    error={Boolean(resumeForm.formState.errors.targetRole)}
                    helperText={resumeForm.formState.errors.targetRole?.message}
                  />
                )}
              />
              <Controller
                name="experienceHighlights"
                control={resumeForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Experience highlights (one per line)"
                    fullWidth
                    required
                    multiline
                    minRows={4}
                    error={Boolean(resumeForm.formState.errors.experienceHighlights)}
                    helperText={resumeForm.formState.errors.experienceHighlights?.message}
                  />
                )}
              />
              <Controller
                name="skills"
                control={resumeForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Skills (comma-separated)"
                    fullWidth
                    required
                    multiline
                    minRows={2}
                    error={Boolean(resumeForm.formState.errors.skills)}
                    helperText={resumeForm.formState.errors.skills?.message}
                  />
                )}
              />
              <Button
                type="submit"
                variant="contained"
                disabled={!canInvoke || resumeMutation.isPending}
              >
                Generate resume
              </Button>
            </Stack>
            <Box sx={{ mt: 2 }}>
              {resumeMutation.isPending ? (
                <AiResultPanel title="Resume" loading />
              ) : resumeMutation.isError ? (
                <AiResultPanel
                  title="Resume"
                  error={resumeMutation.error.message}
                  onRetry={() => resumeForm.handleSubmit(onResume)()}
                />
              ) : resumeMutation.data ? (
                <AiResultPanel
                  title="Resume"
                  content={resumeMutation.data.content}
                  downloadFilename={`resume-${resumeMutation.data.format}.md`}
                />
              ) : (
                <Typography variant="body2" color="text.secondary">
                  Generated resume will appear here.
                </Typography>
              )}
            </Box>
          </AiCard>
        </Grid>

        <Grid size={{ xs: 12, lg: 6 }}>
          <AiCard
            title="Cover Letter Generator"
            description="Generate a tailored cover letter for an application."
          >
            <Stack
              component="form"
              spacing={2}
              onSubmit={coverLetterForm.handleSubmit(onCoverLetter)}
            >
              <Controller
                name="targetRole"
                control={coverLetterForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Target role"
                    fullWidth
                    required
                    error={Boolean(coverLetterForm.formState.errors.targetRole)}
                    helperText={coverLetterForm.formState.errors.targetRole?.message}
                  />
                )}
              />
              <Controller
                name="companyName"
                control={coverLetterForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Company name"
                    fullWidth
                    required
                    error={Boolean(coverLetterForm.formState.errors.companyName)}
                    helperText={coverLetterForm.formState.errors.companyName?.message}
                  />
                )}
              />
              <Controller
                name="highlights"
                control={coverLetterForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Highlights (one per line)"
                    fullWidth
                    required
                    multiline
                    minRows={4}
                    error={Boolean(coverLetterForm.formState.errors.highlights)}
                    helperText={coverLetterForm.formState.errors.highlights?.message}
                  />
                )}
              />
              <Button
                type="submit"
                variant="contained"
                disabled={!canInvoke || coverLetterMutation.isPending}
              >
                Generate cover letter
              </Button>
            </Stack>
            <Box sx={{ mt: 2 }}>
              {coverLetterMutation.isPending ? (
                <AiResultPanel title="Cover letter" loading />
              ) : coverLetterMutation.isError ? (
                <AiResultPanel
                  title="Cover letter"
                  error={coverLetterMutation.error.message}
                  onRetry={() => coverLetterForm.handleSubmit(onCoverLetter)()}
                />
              ) : coverLetterMutation.data ? (
                <AiResponseViewer
                  content={coverLetterMutation.data.content}
                  format={coverLetterMutation.data.format}
                  onCopy={() => void handleCopy(coverLetterMutation.data!.content)}
                  onDownload={() =>
                    downloadTextFile(
                      `cover-letter.${coverLetterMutation.data!.format}`,
                      coverLetterMutation.data!.content,
                    )
                  }
                />
              ) : (
                <Typography variant="body2" color="text.secondary">
                  Generated cover letter will appear here.
                </Typography>
              )}
            </Box>
          </AiCard>
        </Grid>

        <Grid size={{ xs: 12 }}>
          <AiCard
            title="Interview Analysis"
            description="Analyze interview transcripts against a job description."
          >
            <Stack component="form" spacing={2} onSubmit={interviewForm.handleSubmit(onInterview)}>
              <Controller
                name="transcript"
                control={interviewForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Interview transcript"
                    fullWidth
                    required
                    multiline
                    minRows={5}
                    error={Boolean(interviewForm.formState.errors.transcript)}
                    helperText={interviewForm.formState.errors.transcript?.message}
                  />
                )}
              />
              <Controller
                name="jobDescription"
                control={interviewForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Job description"
                    fullWidth
                    required
                    multiline
                    minRows={4}
                    error={Boolean(interviewForm.formState.errors.jobDescription)}
                    helperText={interviewForm.formState.errors.jobDescription?.message}
                  />
                )}
              />
              <Button
                type="submit"
                variant="contained"
                disabled={!canInvoke || interviewMutation.isPending}
              >
                Analyze interview
              </Button>
            </Stack>
            <Box sx={{ mt: 2 }}>
              <AiResultPanel
                title="Interview analysis"
                content={interviewContent}
                loading={interviewMutation.isPending}
                error={interviewMutation.isError ? interviewMutation.error.message : null}
                onRetry={() => interviewForm.handleSubmit(onInterview)()}
                downloadFilename="interview-analysis.md"
              />
            </Box>
          </AiCard>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <SuggestionCard
            title={mockInterviewCapability?.title ?? 'Mock Interview'}
            description={
              mockInterviewCapability?.description ??
              'Guided mock interviews with feedback will be available in a future release.'
            }
            actionLabel="Coming soon"
            disabled
          />
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <Stack spacing={1}>
            <SuggestionCard
              title={recommendationCapability?.title ?? 'Career Recommendation'}
              description={
                recommendationCapability?.description ??
                'Career path and next-step recommendations are planned for a future release.'
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
