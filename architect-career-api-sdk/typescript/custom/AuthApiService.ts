import type { AxiosInstance, AxiosResponse, RawAxiosRequestConfig } from 'axios';
import {
  AuthenticationApi,
  type AuthenticationApiLoginRequest,
  type AuthenticationApiLogoutRequest,
  type AuthenticationApiRefreshRequest,
  type AuthenticationApiRegisterRequest,
} from '../generated/api';
import { ServiceSupport, type ServiceSupportOptions } from './ServiceSupport';

/**
 * Domain wrapper around the generated AuthenticationApi.
 */
export class AuthApiService extends ServiceSupport {
  readonly api: AuthenticationApi;

  constructor(options: ServiceSupportOptions) {
    super(options);
    this.api = new AuthenticationApi(
      options.configuration,
      undefined,
      options.axios as AxiosInstance,
    );
  }

  login(
    request: AuthenticationApiLoginRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('auth.login', () => this.api.login(request, options));
  }

  register(
    request: AuthenticationApiRegisterRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('auth.register', () => this.api.register(request, options));
  }

  refresh(
    request: AuthenticationApiRefreshRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('auth.refresh', () => this.api.refresh(request, options));
  }

  logout(
    request: AuthenticationApiLogoutRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('auth.logout', () => this.api.logout(request, options));
  }

  getCurrentUser(options?: RawAxiosRequestConfig): Promise<AxiosResponse> {
    return this.execute('auth.getCurrentUser', () => this.api.getCurrentUser(options));
  }
}
