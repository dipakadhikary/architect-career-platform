import { useCallback, useState } from 'react';
import { aiApi } from '../api/ai.api';
import { getAiUserMessage } from '../services/aiAvailability';
import type { ChatMessage, ChatRole } from '../types/ai.types';

function turn(role: ChatRole, content: string, status: ChatMessage['status']): ChatMessage {
  return {
    id: crypto.randomUUID(),
    role,
    content,
    createdAt: new Date().toISOString(),
    status,
  };
}

function requestMessages(messages: ChatMessage[]) {
  return messages
    .filter(
      (message) =>
        message.status === 'complete' && (message.role === 'user' || message.role === 'assistant'),
    )
    .map((message) => ({ role: message.role, content: message.content }));
}

export function useAskAi() {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [asking, setAsking] = useState(false);

  const send = useCallback(async (history: ChatMessage[], pendingId: string) => {
    setAsking(true);
    try {
      const response = await aiApi.ask({ messages: requestMessages(history) });
      setMessages((current) =>
        current.map((message) =>
          message.id === pendingId
            ? { ...message, content: response.answer, status: 'complete' }
            : message,
        ),
      );
    } catch (error) {
      const message = getAiUserMessage(error, 'ACOS AI could not answer. Please try again.');
      setMessages((current) =>
        current.map((item) =>
          item.id === pendingId ? { ...item, content: message, status: 'error' } : item,
        ),
      );
    } finally {
      setAsking(false);
    }
  }, []);

  const ask = useCallback(
    async (question: string) => {
      const trimmed = question.trim();
      if (!trimmed || asking) return;
      const userMessage = turn('user', trimmed, 'complete');
      const pending = turn('assistant', '', 'pending');
      const history = [
        ...messages.filter((message) => message.status === 'complete'),
        userMessage,
      ];
      setMessages((current) => [...current, userMessage, pending]);
      await send(history, pending.id);
    },
    [asking, messages, send],
  );

  const retry = useCallback(async () => {
    if (asking) return;
    const withoutFailed = messages.filter(
      (message) => !(message.role === 'assistant' && message.status === 'error'),
    );
    const lastUser = [...withoutFailed].reverse().find((message) => message.role === 'user');
    if (!lastUser) return;
    const pending = turn('assistant', '', 'pending');
    const history = withoutFailed.filter((message) => message.status === 'complete');
    setMessages([...withoutFailed, pending]);
    await send(history, pending.id);
  }, [asking, messages, send]);

  const failed = messages.at(-1)?.status === 'error';

  return { messages, asking, ask, retry, failed };
}
