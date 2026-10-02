import { useCallback, useState } from 'react';
import {
  clearChatSession,
  createChatMessage,
  downloadTextFile,
  exportChatAsMarkdown,
  loadChatSession,
  saveChatSession,
} from '../services/chatSession.service';
import type { ChatMessage, ChatSession } from '../types/ai.types';
import { useChatCompletionMutation } from './useAiMutations';
import { useAiAvailability } from './useAiHealth';
import { getAiUserMessage } from '../services/aiAvailability';

/**
 * Streaming-ready chat session hook.
 * Currently waits for a full completion response; message statuses support future streaming.
 */
export function useAiChat() {
  const [session, setSession] = useState<ChatSession>(() => loadChatSession());
  const [selectedPrompt, setSelectedPrompt] = useState('');
  const completion = useChatCompletionMutation();
  const { canInvoke, unavailableReason } = useAiAvailability();

  const persist = useCallback((next: ChatSession) => {
    saveChatSession(next);
    setSession(next);
  }, []);

  const sendMessage = useCallback(
    async (content: string) => {
      const userMessage = createChatMessage('user', content, 'complete');
      const pendingAssistant = createChatMessage('assistant', '', 'pending');

      const withUser: ChatSession = {
        ...session,
        title: session.messages.length === 0 ? content.slice(0, 48) : session.title,
        messages: [...session.messages, userMessage, pendingAssistant],
        updatedAt: new Date().toISOString(),
      };
      persist(withUser);

      if (!canInvoke) {
        const failed: ChatSession = {
          ...withUser,
          messages: withUser.messages.map((message) =>
            message.id === pendingAssistant.id
              ? {
                  ...message,
                  status: 'error',
                  content: unavailableReason ?? 'AI Platform is currently unavailable.',
                  streaming: false,
                }
              : message,
          ),
        };
        persist(failed);
        return;
      }

      try {
        const history = withUser.messages
          .filter((message) => message.status === 'complete')
          .map((message) => ({ role: message.role, content: message.content }));

        // Mark streaming-ready before awaiting full response (no stream yet).
        persist({
          ...withUser,
          messages: withUser.messages.map((message) =>
            message.id === pendingAssistant.id
              ? { ...message, status: 'streaming', streaming: true }
              : message,
          ),
        });

        const result = await completion.mutateAsync({
          message: content,
          conversationId: session.id,
          history,
        });

        const completed: ChatSession = {
          ...withUser,
          id: result.conversationId || withUser.id,
          messages: withUser.messages.map((message) =>
            message.id === pendingAssistant.id
              ? {
                  ...message,
                  status: 'complete',
                  content: result.message,
                  streaming: false,
                }
              : message,
          ),
          updatedAt: new Date().toISOString(),
        };
        persist(completed);
      } catch (error) {
        const failed: ChatSession = {
          ...withUser,
          messages: withUser.messages.map((message) =>
            message.id === pendingAssistant.id
              ? {
                  ...message,
                  status: 'error',
                  content: getAiUserMessage(error),
                  streaming: false,
                }
              : message,
          ),
        };
        persist(failed);
      }
    },
    [canInvoke, completion, persist, session, unavailableReason],
  );

  const regenerateLast = useCallback(async () => {
    const lastUser = [...session.messages].reverse().find((message) => message.role === 'user');
    if (!lastUser) return;

    const withoutLastAssistant: ChatSession = {
      ...session,
      messages: session.messages.filter(
        (message, index, all) => !(index === all.length - 1 && message.role === 'assistant'),
      ),
    };
    persist(withoutLastAssistant);
    await sendMessage(lastUser.content);
  }, [persist, sendMessage, session]);

  const clear = useCallback(() => {
    const empty = clearChatSession();
    setSession(empty);
    setSelectedPrompt('');
  }, []);

  const exportMarkdown = useCallback(() => {
    const markdown = exportChatAsMarkdown(session);
    downloadTextFile(`acos-chat-${session.id}.md`, markdown);
  }, [session]);

  return {
    session,
    messages: session.messages as ChatMessage[],
    selectedPrompt,
    setSelectedPrompt,
    sendMessage,
    regenerateLast,
    clear,
    exportMarkdown,
    isSending: completion.isPending,
    canInvoke,
    unavailableReason,
  };
}
