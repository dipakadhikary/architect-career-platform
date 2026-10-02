import { create } from 'zustand';

export type NotificationSeverity = 'success' | 'info' | 'warning' | 'error';

export interface NotificationPayload {
  message: string;
  severity?: NotificationSeverity;
  autoHideDuration?: number;
}

interface NotificationState {
  open: boolean;
  message: string;
  severity: NotificationSeverity;
  autoHideDuration: number;
  notify: (payload: NotificationPayload) => void;
  close: () => void;
}

export const useNotificationStore = create<NotificationState>((set) => ({
  open: false,
  message: '',
  severity: 'info',
  autoHideDuration: 5000,

  notify: ({ message, severity = 'info', autoHideDuration = 5000 }) => {
    set({ open: true, message, severity, autoHideDuration });
  },

  close: () => {
    set({ open: false });
  },
}));
