export class Pagination {
  page = 0;
  pageSize = 10;
  all = false;

  constructor(init?: Partial<Pagination>) {
    Object.assign(this, init);
  }
}
