import type { AxiosInstance, AxiosResponse, RawAxiosRequestConfig } from 'axios';
import { DashboardApi } from '../generated/api';
import { ServiceSupport, type ServiceSupportOptions } from './ServiceSupport';

/**
 * Domain wrapper around the generated DashboardApi.
 */
export class DashboardApiService extends ServiceSupport {
  readonly api: DashboardApi;

  constructor(options: ServiceSupportOptions) {
    super(options);
    this.api = new DashboardApi(options.configuration, undefined, options.axios as AxiosInstance);
  }

  getDashboard(options?: RawAxiosRequestConfig): Promise<AxiosResponse> {
    return this.execute('dashboard.getDashboard', () => this.api.getDashboard(options));
  }
}
