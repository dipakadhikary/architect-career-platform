import type { AxiosInstance } from 'axios';
import { Configuration, type ConfigurationParameters } from '../generated/configuration';

export interface AcosClientOptions {
  /** API base path, e.g. https://api.example.com or http://localhost:8080 */
  basePath: string;
  /** Static bearer token, or a resolver invoked per request */
  accessToken?: ConfigurationParameters['accessToken'];
  /** Optional shared Axios instance */
  axios?: AxiosInstance;
  /** Extra headers applied to every request */
  baseOptions?: Configuration['baseOptions'];
}

/**
 * Builds an OpenAPI Generator Configuration with bearer authentication support.
 */
export function createAcosConfiguration(options: AcosClientOptions): Configuration {
  return new Configuration({
    basePath: options.basePath.replace(/\/$/, ''),
    accessToken: options.accessToken,
    baseOptions: options.baseOptions,
  });
}
