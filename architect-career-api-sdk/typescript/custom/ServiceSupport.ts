import type { AxiosInstance } from 'axios';
import type { Configuration } from '../generated/configuration';
import { SdkLogger, type SdkLoggerOptions } from './logging';
import { withRetry, type RetryOptions } from './retry';

export interface ServiceSupportOptions {
  configuration: Configuration;
  axios?: AxiosInstance;
  retry?: RetryOptions;
  logger?: SdkLoggerOptions;
}

/**
 * Shared retry / logging helpers for domain service wrappers.
 * Generated API classes remain untouched.
 */
export abstract class ServiceSupport {
  protected readonly configuration: Configuration;
  protected readonly axios?: AxiosInstance;
  protected readonly retryOptions: RetryOptions;
  protected readonly logger: SdkLogger;

  protected constructor(options: ServiceSupportOptions) {
    this.configuration = options.configuration;
    this.axios = options.axios;
    this.retryOptions = options.retry ?? { retries: 3 };
    this.logger = new SdkLogger(options.logger);
  }

  protected async execute<T>(operationName: string, operation: () => Promise<T>): Promise<T> {
    this.logger.debug(`Calling ${operationName}`);
    try {
      const result = await withRetry(operation, this.retryOptions);
      this.logger.debug(`Completed ${operationName}`);
      return result;
    } catch (error) {
      this.logger.error(`Failed ${operationName}`, {
        name: error instanceof Error ? error.name : 'Error',
        message: error instanceof Error ? error.message : String(error),
      });
      throw error;
    }
  }
}
