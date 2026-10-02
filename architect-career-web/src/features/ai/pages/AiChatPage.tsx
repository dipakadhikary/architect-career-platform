import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import DownloadOutlinedIcon from '@mui/icons-material/DownloadOutlined';
import RefreshOutlinedIcon from '@mui/icons-material/RefreshOutlined';
import { Box, Button, Grid, Paper, Stack, Typography } from '@mui/material';
import { useCallback, useState } from 'react';
import { PageHeader } from '@/shared/components';
import {
  AiUnavailableBanner,
  ChatInput,
  ChatMessage,
  PromptHistory,
  SuggestionCard,
} from '@/features/ai/components';
import { useAiChat } from '@/features/ai/hooks/useAiChat';

const CHAT_SUGGESTIONS = [
  {
    title: 'Learning guidance',
    description: 'Ask for help prioritizing topics in your learning plan.',
    prompt: 'What should I focus on next in my learning plan based on my current progress?',
  },
  {
    title: 'Interview prep',
    description: 'Practice explaining architecture decisions clearly.',
    prompt:
      'Help me prepare talking points for a system design interview about scalable microservices.',
  },
  {
    title: 'Portfolio review',
    description: 'Get feedback on how to present your projects.',
    prompt: 'How can I better highlight distributed systems experience in my portfolio projects?',
  },
  {
    title: 'Knowledge synthesis',
    description: 'Connect ideas across your notes and domains.',
    prompt: 'What patterns connect my recent knowledge notes about event-driven architecture?',
  },
] as const;

export function AiChatPage() {
  const {
    messages,
    selectedPrompt,
    setSelectedPrompt,
    sendMessage,
    regenerateLast,
    clear,
    exportMarkdown,
    isSending,
    canInvoke,
    unavailableReason,
  } = useAiChat();

  const [prefill, setPrefill] = useState('');

  const handleSuggestion = useCallback(
    (prompt: string) => {
      setPrefill(prompt);
      setSelectedPrompt(prompt);
    },
    [setSelectedPrompt],
  );

  const handlePromptSelect = useCallback(
    (content: string) => {
      setPrefill(content);
      setSelectedPrompt(content);
    },
    [setSelectedPrompt],
  );

  const handleSend = useCallback(
    async (message: string) => {
      setPrefill('');
      setSelectedPrompt('');
      await sendMessage(message);
    },
    [sendMessage, setSelectedPrompt],
  );

  const lastUserMessage = [...messages].reverse().find((message) => message.role === 'user');

  return (
    <Box>
      <PageHeader
        title="AI Chat"
        description="Conversational assistance across ACOS domains."
        actions={
          <Stack direction="row" spacing={1}>
            <Button
              size="small"
              startIcon={<RefreshOutlinedIcon />}
              onClick={() => void regenerateLast()}
              disabled={!lastUserMessage || isSending}
            >
              Regenerate last
            </Button>
            <Button
              size="small"
              startIcon={<DownloadOutlinedIcon />}
              onClick={exportMarkdown}
              disabled={messages.length === 0}
            >
              Export
            </Button>
            <Button
              size="small"
              color="inherit"
              startIcon={<DeleteOutlineIcon />}
              onClick={clear}
              disabled={messages.length === 0}
            >
              Clear
            </Button>
          </Stack>
        }
      />

      {!canInvoke && unavailableReason ? <AiUnavailableBanner message={unavailableReason} /> : null}

      <Grid container spacing={2} sx={{ mb: 2 }}>
        {CHAT_SUGGESTIONS.map((suggestion) => (
          <Grid key={suggestion.title} size={{ xs: 12, sm: 6, md: 3 }}>
            <SuggestionCard
              title={suggestion.title}
              description={suggestion.description}
              actionLabel="Use prompt"
              onAction={() => handleSuggestion(suggestion.prompt)}
              disabled={!canInvoke}
            />
          </Grid>
        ))}
      </Grid>

      <Grid container spacing={2}>
        <Grid size={{ xs: 12, md: 8 }}>
          <Paper
            variant="outlined"
            sx={{ p: 2, mb: 2, minHeight: 360, maxHeight: 520, overflow: 'auto' }}
          >
            {messages.length === 0 ? (
              <Typography variant="body2" color="text.secondary">
                Start a conversation or choose a suggested prompt above.
              </Typography>
            ) : (
              messages.map((message) => <ChatMessage key={message.id} message={message} />)
            )}
          </Paper>
          <ChatInput
            disabled={!canInvoke}
            loading={isSending}
            prefill={prefill || selectedPrompt || undefined}
            onSend={handleSend}
          />
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <PromptHistory messages={messages} onSelect={handlePromptSelect} onClear={clear} />
        </Grid>
      </Grid>
    </Box>
  );
}
