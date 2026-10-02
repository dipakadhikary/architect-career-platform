import type { AxiosInstance, AxiosResponse, RawAxiosRequestConfig } from 'axios';
import { AIIntegrationApi } from '../generated/api';
import { ServiceSupport, type ServiceSupportOptions } from './ServiceSupport';

/**
 * Domain wrapper around the generated AI Integration API.
 */
export class AiIntegrationApiService extends ServiceSupport {
  readonly api: AIIntegrationApi;

  constructor(options: ServiceSupportOptions) {
    super(options);
    this.api = new AIIntegrationApi(
      options.configuration,
      undefined,
      options.axios as AxiosInstance,
    );
  }

  getHealth(options?: RawAxiosRequestConfig): Promise<AxiosResponse> {
    return this.execute('ai.getHealth', () => this.api.getHealth(options));
  }
}
