import type { PageParams } from '@/shared/types/pagination';
import { DEFAULT_PAGE_SIZE } from '@/shared/types/pagination';

export function toPageQuery(params: PageParams = {}): Record<string, string | number> {
  return {
    page: params.page ?? 0,
    size: params.size ?? DEFAULT_PAGE_SIZE,
    ...(params.sort ? { sort: params.sort } : {}),
  };
}
