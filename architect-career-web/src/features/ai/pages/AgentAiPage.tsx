import { useState } from 'react';
import { Box, Button, Stack, TextField, Typography } from '@mui/material';
import { MarkdownViewer, PageHeader } from '@/shared/components';
import { aiApi } from '@/features/ai/api/ai.api';
import { AiUnavailableBanner } from '@/features/ai/components';
import { useAiAvailability } from '@/features/ai/hooks/useAiHealth';
import { getAiUserMessage } from '@/features/ai/services/aiAvailability';
import type { AgentExecution } from '@/features/ai/types/ai.types';

export function AgentAiPage() {
  const { canInvoke, unavailableReason } = useAiAvailability();
  const [goal, setGoal] = useState('');
  const [execution, setExecution] = useState<AgentExecution | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [pending, setPending] = useState(false);

  const run = async () => {
    setPending(true);
    setError(null);
    try {
      setExecution(await aiApi.executeAgent(goal));
    } catch (caught) {
      setError(getAiUserMessage(caught));
    } finally {
      setPending(false);
    }
  };

  const decide = async (decision: 'APPROVE' | 'REJECT') => {
    if (!execution) return;
    setPending(true);
    setError(null);
    try {
      setExecution(await aiApi.decideAgent(execution.executionId, decision));
    } catch (caught) {
      setError(getAiUserMessage(caught));
    } finally {
      setPending(false);
    }
  };

  return (
    <Stack spacing={2}>
      <PageHeader
        title="Analyze with AI"
        description="Give the agent a goal. It can search your ACOS knowledge and conversations. It cannot change or publish content."
      />
      {!canInvoke && unavailableReason ? <AiUnavailableBanner message={unavailableReason} /> : null}
      <TextField
        label="Goal"
        value={goal}
        onChange={(event) => setGoal(event.target.value)}
        fullWidth
        required
        multiline
        minRows={2}
      />
      <Box>
        <Button
          variant="contained"
          disabled={!canInvoke || pending || !goal.trim()}
          onClick={() => void run()}
        >
          Analyze with AI
        </Button>
      </Box>
      {error ? (
        <Typography color="error" variant="body2">
          {error}
        </Typography>
      ) : null}
      {execution?.steps.map((step) => (
        <Typography key={step.stepId} variant="body2">
          {step.label}
        </Typography>
      ))}
      {execution?.approvalRequired && execution.status === 'WAITING_FOR_APPROVAL' ? (
        <Stack spacing={1} sx={{ border: 1, borderColor: 'divider', p: 2 }}>
          <Typography variant="subtitle1">Approval required</Typography>
          <Typography variant="body2">{execution.proposedAction}</Typography>
          <Stack direction="row" spacing={1}>
            <Button variant="contained" disabled={pending} onClick={() => void decide('APPROVE')}>
              Approve
            </Button>
            <Button variant="outlined" disabled={pending} onClick={() => void decide('REJECT')}>
              Reject
            </Button>
          </Stack>
        </Stack>
      ) : null}
      {execution?.answer ? <MarkdownViewer content={execution.answer} /> : null}
      {execution?.sources.map((source) =>
        source.url.startsWith('/') ? (
          <Typography key={source.chunkId} component="a" href={source.url} variant="body2">
            {source.title}
          </Typography>
        ) : (
          <Typography key={source.chunkId} variant="body2">
            {source.title}
          </Typography>
        ),
      )}
    </Stack>
  );
}
