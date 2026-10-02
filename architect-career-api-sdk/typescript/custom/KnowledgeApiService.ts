import type { AxiosInstance, AxiosResponse, RawAxiosRequestConfig } from 'axios';
import {
  KnowledgeApi,
  type KnowledgeApiCreate8Request,
  type KnowledgeApiDelete8Request,
  type KnowledgeApiGet7Request,
  type KnowledgeApiList8Request,
  type KnowledgeApiSearch1Request,
  type KnowledgeApiUpdate8Request,
} from '../generated/api';
import { ServiceSupport, type ServiceSupportOptions } from './ServiceSupport';

/**
 * Domain wrapper around the generated KnowledgeApi.
 * Adds retry, logging, and stable method names without modifying generated code.
 */
export class KnowledgeApiService extends ServiceSupport {
  readonly api: KnowledgeApi;

  constructor(options: ServiceSupportOptions) {
    super(options);
    this.api = new KnowledgeApi(options.configuration, undefined, options.axios as AxiosInstance);
  }

  createNote(
    request: KnowledgeApiCreate8Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('knowledge.createNote', () => this.api.create8(request, options));
  }

  updateNote(
    request: KnowledgeApiUpdate8Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('knowledge.updateNote', () => this.api.update8(request, options));
  }

  deleteNote(
    request: KnowledgeApiDelete8Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('knowledge.deleteNote', () => this.api.delete8(request, options));
  }

  getNote(
    request: KnowledgeApiGet7Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('knowledge.getNote', () => this.api.get7(request, options));
  }

  listNotes(
    request: KnowledgeApiList8Request = {},
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('knowledge.listNotes', () => this.api.list8(request, options));
  }

  searchNotes(
    request: KnowledgeApiSearch1Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('knowledge.searchNotes', () => this.api.search1(request, options));
  }
}
