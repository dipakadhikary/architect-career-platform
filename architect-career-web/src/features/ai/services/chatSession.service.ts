import { moduleConfig } from '@/app/config/module.config';
import { storage } from '@/shared/utils/storage';
import type { ChatMessage, ChatSession } from '../types/ai.types';

const { chatStorageKey, chatMaxMessages } = moduleConfig.ai;

function createId(): string {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID();
  }
  return `ai-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 8)}`;
}

export function createEmptyChatSession(): ChatSession {
  const now = new Date().toISOString();
  return {
    id: createId(),
    title: 'New conversation',
    messages: [],
    updatedAt: now,
  };
}

export function loadChatSession(): ChatSession {
  const stored = storage.getJson<ChatSession>(chatStorageKey);
  if (stored?.id && Array.isArray(stored.messages)) {
    return stored;
  }
  return createEmptyChatSession();
}

export function saveChatSession(session: ChatSession): void {
  const trimmed: ChatSession = {
    ...session,
    messages: session.messages.slice(-chatMaxMessages),
    updatedAt: new Date().toISOString(),
  };
  storage.setJson(chatStorageKey, trimmed);
}

export function clearChatSession(): ChatSession {
  const empty = createEmptyChatSession();
  storage.remove(chatStorageKey);
  return empty;
}

export function createChatMessage(
  role: ChatMessage['role'],
  content: string,
  status: ChatMessage['status'] = 'complete',
): ChatMessage {
  return {
    id: createId(),
    role,
    content,
    createdAt: new Date().toISOString(),
    status,
    streaming: status === 'streaming' || status === 'pending',
  };
}

export function exportChatAsMarkdown(session: ChatSession): string {
  const lines = [`# ${session.title}`, '', `Updated: ${session.updatedAt}`, ''];
  for (const message of session.messages) {
    const label =
      message.role === 'user' ? 'You' : message.role === 'assistant' ? 'Assistant' : 'System';
    lines.push(`## ${label}`, '', message.content, '');
  }
  return lines.join('\n');
}

export function downloadTextFile(filename: string, content: string, mime = 'text/markdown'): void {
  const blob = new Blob([content], { type: `${mime};charset=utf-8` });
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = filename;
  anchor.click();
  URL.revokeObjectURL(url);
}

export async function copyTextToClipboard(text: string): Promise<void> {
  await navigator.clipboard.writeText(text);
}
