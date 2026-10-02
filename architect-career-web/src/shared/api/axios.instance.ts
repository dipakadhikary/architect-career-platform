import axios from 'axios';
import { appConfig } from '@/app/config/app.config';
import { attachInterceptors } from './interceptors';

/**
 * Shared Axios client for ACOS backend (`/api/v1/**`).
 * In development, VITE_API_BASE_URL is empty so requests go through the Vite proxy.
 */
export const apiClient = axios.create({
  baseURL: appConfig.api.baseUrl,
  timeout: appConfig.api.timeoutMs,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
});

attachInterceptors(apiClient);

export default apiClient;
