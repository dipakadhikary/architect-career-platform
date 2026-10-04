import { Button, Stack } from '@mui/material';
import { PageHeader } from '@/shared/components';
import { ChatInput, ChatMessage } from '@/features/ai/components';
import { useAskAi } from '@/features/ai/hooks/useAskAi';
import { useAiAvailability } from '@/features/ai/hooks/useAiHealth';

export function AskAiPage() {
  const { canInvoke } = useAiAvailability();
  const { messages, asking, ask, retry, failed } = useAskAi();

  return (
    <Stack spacing={2}>
      <PageHeader
        title="Ask ACOS AI"
        description="Ask a technical question. When ACOS knowledge is relevant, the answer includes sources that open the original note or tutorial."
      />
      <Stack>
        {messages.map((message) => (
          <ChatMessage key={message.id} message={message} assistantLabel="ACOS AI" />
        ))}
      </Stack>
      {failed ? (
        <Button variant="outlined" onClick={() => void retry()} disabled={asking}>
          Retry
        </Button>
      ) : null}
      <ChatInput
        submitLabel="Ask"
        placeholder="Explain the Factory Design Pattern"
        loading={asking}
        disabled={!canInvoke}
        onSend={(question) => void ask(question)}
      />
    </Stack>
  );
}
