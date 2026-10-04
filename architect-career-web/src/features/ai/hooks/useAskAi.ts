import { useCallback, useEffect, useRef, useState } from 'react';
import { aiApi } from '../api/ai.api';
import { getAiUserMessage } from '../services/aiAvailability';
import type {
  AssistantConversation,
  AssistantConversationMessage,
  ChatMessage,
  ChatRole,
} from '../types/ai.types';

type LocalMessage = ChatMessage & { idempotencyKey?: string };

function turn(
  role: ChatRole,
  content: string,
  status: ChatMessage['status'],
  idempotencyKey?: string,
): LocalMessage {
  return {
    id: crypto.randomUUID(),
    role,
    content,
    createdAt: new Date().toISOString(),
    status,
    idempotencyKey,
  };
}

function mapMessage(message: AssistantConversationMessage, idempotencyKey?: string): LocalMessage {
  const role: ChatRole = message.role === 'USER' ? 'user' : 'assistant';
  const status: ChatMessage['status'] =
    message.status === 'FAILED' ? 'error' : message.status === 'COMPLETED' ? 'complete' : 'pending';
  return {
    id: message.id,
    role,
    content: message.content,
    createdAt: message.createdAt,
    status,
    grounded: message.grounded,
    sources: message.sources,
    idempotencyKey,
  };
}

export function useAskAi() {
  const [conversations, setConversations] = useState<AssistantConversation[]>([]);
  const [activeId, setActiveId] = useState<string | null>(null);
  const [messages, setMessages] = useState<LocalMessage[]>([]);
  const [asking, setAsking] = useState(false);
  const [creating, setCreating] = useState(false);
  const [loadingList, setLoadingList] = useState(true);
  const [loadingHistory, setLoadingHistory] = useState(false);
  const [renaming, setRenaming] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [listError, setListError] = useState<string | null>(null);
  const creatingRef = useRef(false);

  const refreshList = useCallback(async () => {
    const page = await aiApi.listConversations();
    setConversations(page.content);
    return page.content;
  }, []);

  useEffect(() => {
    let cancelled = false;
    void (async () => {
      try {
        const page = await aiApi.listConversations();
        if (!cancelled) {
          setConversations(page.content);
          setListError(null);
        }
      } catch (error) {
        if (!cancelled) {
          setListError(getAiUserMessage(error, 'Conversations could not be loaded.'));
        }
      } finally {
        if (!cancelled) setLoadingList(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  const openConversation = useCallback(async (conversationId: string) => {
    setLoadingHistory(true);
    setActiveId(conversationId);
    try {
      const detail = await aiApi.getConversation(conversationId);
      setMessages(detail.messages.map((message) => mapMessage(message)));
      setConversations((current) =>
        current.map((item) =>
          item.id === detail.id ? { ...item, title: detail.title, updatedAt: detail.updatedAt } : item,
        ),
      );
    } catch (error) {
      setMessages([
        turn(
          'assistant',
          getAiUserMessage(error, 'This conversation could not be opened.'),
          'error',
        ),
      ]);
    } finally {
      setLoadingHistory(false);
    }
  }, []);

  const startNew = useCallback(async () => {
    if (creatingRef.current) return;
    creatingRef.current = true;
    setCreating(true);
    try {
      const created = await aiApi.createConversation();
      setConversations((current) => [created, ...current.filter((item) => item.id !== created.id)]);
      setActiveId(created.id);
      setMessages([]);
    } catch (error) {
      setListError(getAiUserMessage(error, 'A new conversation could not be started.'));
    } finally {
      creatingRef.current = false;
      setCreating(false);
    }
  }, []);

  const ensureConversation = useCallback(async () => {
    if (activeId) return activeId;
    if (creatingRef.current) return null;
    creatingRef.current = true;
    setCreating(true);
    try {
      const created = await aiApi.createConversation();
      setConversations((current) => [created, ...current.filter((item) => item.id !== created.id)]);
      setActiveId(created.id);
      setMessages([]);
      return created.id;
    } finally {
      creatingRef.current = false;
      setCreating(false);
    }
  }, [activeId]);

  const sendTurn = useCallback(
    async (conversationId: string, content: string, idempotencyKey: string, pendingId: string) => {
      try {
        const sent = await aiApi.sendConversationMessage(conversationId, content, idempotencyKey);
        setMessages((current) => {
          const kept = current.filter(
            (message) => message.id !== pendingId && message.idempotencyKey !== idempotencyKey,
          );
          return [
            ...kept,
            mapMessage(sent.userMessage, idempotencyKey),
            mapMessage(sent.assistantMessage),
          ];
        });
        const listed = await refreshList();
        const updated = listed.find((item) => item.id === conversationId);
        if (updated) {
          setConversations((current) =>
            [updated, ...current.filter((item) => item.id !== conversationId)],
          );
        }
      } catch (error) {
        const message = getAiUserMessage(error, "Sorry, I couldn't generate a response right now. Please try again.");
        setMessages((current) =>
          current.map((item) =>
            item.id === pendingId ? { ...item, content: message, status: 'error' } : item,
          ),
        );
      }
    },
    [refreshList],
  );

  const ask = useCallback(
    async (question: string) => {
      const trimmed = question.trim();
      if (!trimmed || asking) return;
      setAsking(true);
      try {
        const conversationId = await ensureConversation();
        if (!conversationId) return;
        const idempotencyKey = crypto.randomUUID();
        const userMessage = turn('user', trimmed, 'complete', idempotencyKey);
        const pending = turn('assistant', '', 'pending');
        setMessages((current) => [...current, userMessage, pending]);
        await sendTurn(conversationId, trimmed, idempotencyKey, pending.id);
      } catch (error) {
        setMessages((current) => [
          ...current,
          turn('assistant', getAiUserMessage(error, 'A conversation could not be started.'), 'error'),
        ]);
      } finally {
        setAsking(false);
      }
    },
    [asking, ensureConversation, sendTurn],
  );

  const retry = useCallback(async () => {
    if (asking || !activeId) return;
    const failed = [...messages].reverse().find((message) => message.status === 'error');
    const lastUser = [...messages].reverse().find((message) => message.role === 'user');
    if (!failed || !lastUser?.idempotencyKey) return;
    setAsking(true);
    const pending = turn('assistant', '', 'pending');
    setMessages((current) => [
      ...current.filter((message) => message.id !== failed.id),
      pending,
    ]);
    try {
      await sendTurn(activeId, lastUser.content, lastUser.idempotencyKey, pending.id);
    } finally {
      setAsking(false);
    }
  }, [activeId, asking, messages, sendTurn]);

  const rename = useCallback(
    async (conversationId: string, title: string) => {
      const trimmed = title.trim();
      if (!trimmed) return;
      setRenaming(true);
      try {
        const updated = await aiApi.renameConversation(conversationId, trimmed);
        setConversations((current) =>
          current.map((item) => (item.id === updated.id ? updated : item)),
        );
      } finally {
        setRenaming(false);
      }
    },
    [],
  );

  const remove = useCallback(
    async (conversationId: string) => {
      setDeleting(true);
      try {
        await aiApi.deleteConversation(conversationId);
        setConversations((current) => current.filter((item) => item.id !== conversationId));
        if (activeId === conversationId) {
          setActiveId(null);
          setMessages([]);
        }
      } finally {
        setDeleting(false);
      }
    },
    [activeId],
  );

  const failed = messages.at(-1)?.status === 'error';

  return {
    conversations,
    activeId,
    messages,
    asking,
    creating,
    loadingList,
    loadingHistory,
    renaming,
    deleting,
    listError,
    failed,
    ask,
    retry,
    startNew,
    openConversation,
    rename,
    remove,
  };
}
