export class PagedResponse<T> {
  items: T[] = [];
  totalItems: number = 0;
  totalPages: number = 0;

  constructor(init?: Partial<PagedResponse<T>>) {
    Object.assign(this, init);
  }
}
