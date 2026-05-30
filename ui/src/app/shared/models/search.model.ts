import { Pagination } from './pagination.model';

export type FilterOperation =
  | 'EQ'
  | 'NOT'
  | 'LT'
  | 'GT'
  | 'LTE'
  | 'GTE'
  | 'LIKE'
  | 'TRIM_LIKE_IGNORE_CASE'
  | 'STARTS_WITH'
  | 'ENDS_WITH'
  | 'IN'
  | 'NOT_IN'
  | 'IS_NULL'
  | 'IS_NOT_NULL';

export type SortDirection = 'ASC' | 'DESC';

export interface FilterDTO {
  field: string;
  type: FilterOperation;
  value?: unknown;
}

export interface SorterDTO {
  field: string;
  direction: SortDirection;
}

export class SearchDTO {
  filters: FilterDTO[] = [];
  sorters: SorterDTO[] = [];
  pagination: Pagination = new Pagination();

  constructor(init?: Partial<SearchDTO>) {
    Object.assign(this, init);
  }
}
