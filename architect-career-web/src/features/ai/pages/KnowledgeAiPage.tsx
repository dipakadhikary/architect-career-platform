import { zodResolver } from '@hookform/resolvers/zod';
import {
  Box,
  Button,
  Grid,
  List,
  ListItem,
  ListItemText,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { PageHeader } from '@/shared/components';
import { moduleConfig } from '@/app/config/module.config';
import { AiCard, AiResultPanel, AiStatusChip, AiUnavailableBanner } from '@/features/ai/components';
import {
  useKnowledgeSearchMutation,
  useKnowledgeSummarizeMutation,
} from '@/features/ai/hooks/useAiMutations';
import { useAiAvailability } from '@/features/ai/hooks/useAiHealth';
import { getCapability } from '@/features/ai/services/aiCapabilities';
import {
  knowledgeSearchSchema,
  knowledgeSummarizeSchema,
  type KnowledgeSearchFormValues,
  type KnowledgeSummarizeFormValues,
} from '@/features/ai/schemas/ai.schemas';

function formatSummarizeMarkdown(summary: string, keyPoints: string[]): string {
  const points = keyPoints.map((point) => `- ${point}`).join('\n');
  return `## Summary\n\n${summary}\n\n## Key points\n\n${points}`;
}

export function KnowledgeAiPage() {
  const { canInvoke, unavailableReason } = useAiAvailability();
  const searchMutation = useKnowledgeSearchMutation();
  const summarizeMutation = useKnowledgeSummarizeMutation();

  const searchForm = useForm<KnowledgeSearchFormValues>({
    resolver: zodResolver(knowledgeSearchSchema),
    defaultValues: { query: '', limit: moduleConfig.ai.defaultSearchLimit },
  });

  const summarizeForm = useForm<KnowledgeSummarizeFormValues>({
    resolver: zodResolver(knowledgeSummarizeSchema),
    defaultValues: { noteId: '', content: '' },
  });

  const onSearch = (values: KnowledgeSearchFormValues) => {
    searchMutation.mutate({ query: values.query, limit: values.limit });
  };

  const onSummarize = (values: KnowledgeSummarizeFormValues) => {
    summarizeMutation.mutate({
      noteId: values.noteId,
      content: values.content,
    });
  };

  const relatedCapability = getCapability('knowledge.related');
  const insightsCapability = getCapability('knowledge.insights');

  const summarizeContent = summarizeMutation.data
    ? formatSummarizeMarkdown(summarizeMutation.data.summary, summarizeMutation.data.keyPoints)
    : null;

  return (
    <Box>
      <PageHeader
        title="Knowledge AI"
        description="Semantic search, summarization, and knowledge insights."
      />

      {!canInvoke && unavailableReason ? <AiUnavailableBanner message={unavailableReason} /> : null}

      <Grid container spacing={2}>
        <Grid size={{ xs: 12, lg: 6 }}>
          <AiCard title="Semantic Search" description="Find notes by meaning, not just keywords.">
            <Stack component="form" spacing={2} onSubmit={searchForm.handleSubmit(onSearch)}>
              <Controller
                name="query"
                control={searchForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Search query"
                    fullWidth
                    required
                    error={Boolean(searchForm.formState.errors.query)}
                    helperText={searchForm.formState.errors.query?.message}
                  />
                )}
              />
              <Controller
                name="limit"
                control={searchForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Result limit"
                    type="number"
                    fullWidth
                    error={Boolean(searchForm.formState.errors.limit)}
                    helperText={searchForm.formState.errors.limit?.message}
                  />
                )}
              />
              <Button
                type="submit"
                variant="contained"
                disabled={!canInvoke || searchMutation.isPending}
              >
                Search
              </Button>
            </Stack>
            <Box sx={{ mt: 2 }}>
              {searchMutation.isPending ? (
                <AiResultPanel title="Search results" loading />
              ) : searchMutation.isError ? (
                <AiResultPanel
                  title="Search results"
                  error={searchMutation.error.message}
                  onRetry={() => searchForm.handleSubmit(onSearch)()}
                />
              ) : searchMutation.data?.hits.length ? (
                <List dense disablePadding>
                  {searchMutation.data.hits.map((hit) => (
                    <ListItem key={hit.noteId} divider alignItems="flex-start">
                      <ListItemText
                        primary={
                          <Stack direction="row" justifyContent="space-between" spacing={2}>
                            <Typography variant="subtitle2">{hit.title}</Typography>
                            <Typography variant="caption" color="text.secondary">
                              Score {hit.score.toFixed(2)}
                            </Typography>
                          </Stack>
                        }
                        secondary={
                          <>
                            <Typography variant="caption" color="text.secondary" display="block">
                              {hit.noteId}
                            </Typography>
                            <Typography variant="body2" sx={{ mt: 0.5 }}>
                              {hit.snippet}
                            </Typography>
                          </>
                        }
                      />
                    </ListItem>
                  ))}
                </List>
              ) : searchMutation.data ? (
                <Typography variant="body2" color="text.secondary">
                  No matching notes found.
                </Typography>
              ) : (
                <Typography variant="body2" color="text.secondary">
                  Run a search to see matching notes.
                </Typography>
              )}
            </Box>
          </AiCard>
        </Grid>

        <Grid size={{ xs: 12, lg: 6 }}>
          <AiCard
            title="Document Summarization"
            description="Generate concise summaries and key points."
          >
            <Stack component="form" spacing={2} onSubmit={summarizeForm.handleSubmit(onSummarize)}>
              <Controller
                name="noteId"
                control={summarizeForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Note ID"
                    fullWidth
                    required
                    error={Boolean(summarizeForm.formState.errors.noteId)}
                    helperText={summarizeForm.formState.errors.noteId?.message}
                  />
                )}
              />
              <Controller
                name="content"
                control={summarizeForm.control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Note content"
                    fullWidth
                    required
                    multiline
                    minRows={4}
                    error={Boolean(summarizeForm.formState.errors.content)}
                    helperText={summarizeForm.formState.errors.content?.message}
                  />
                )}
              />
              <Button
                type="submit"
                variant="contained"
                disabled={!canInvoke || summarizeMutation.isPending}
              >
                Summarize
              </Button>
            </Stack>
            <Box sx={{ mt: 2 }}>
              <AiResultPanel
                title="Summary"
                content={summarizeContent}
                loading={summarizeMutation.isPending}
                error={summarizeMutation.isError ? summarizeMutation.error.message : null}
                onRetry={() => summarizeForm.handleSubmit(onSummarize)()}
                downloadFilename="knowledge-summary.md"
              />
            </Box>
          </AiCard>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <AiCard
            title={relatedCapability?.title ?? 'Related Notes'}
            description={relatedCapability?.description}
            actions={<AiStatusChip kind="coming_soon" label="Coming soon" />}
          >
            <Typography variant="body2" color="text.secondary">
              Related note discovery will be available when the integration layer ships semantic
              similarity endpoints. No results are shown until the capability is live.
            </Typography>
          </AiCard>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <AiCard
            title={insightsCapability?.title ?? 'Knowledge Insights'}
            description={insightsCapability?.description}
            actions={<AiStatusChip kind="coming_soon" label="Coming soon" />}
          >
            <Typography variant="body2" color="text.secondary">
              Cross-note theme and gap analysis is planned for a future release. Check back when the
              platform reports this capability as available.
            </Typography>
          </AiCard>
        </Grid>
      </Grid>
    </Box>
  );
}
