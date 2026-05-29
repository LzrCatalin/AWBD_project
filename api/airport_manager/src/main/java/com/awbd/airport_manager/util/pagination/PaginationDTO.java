package com.awbd.airport_manager.util.pagination;

import jakarta.validation.constraints.Min;

public class PaginationDTO {

    @Min(0)
    private int page = 0;

    @Min(1)
    private int pageSize = 10;

    private boolean all;

    public PaginationDTO() {
    }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public boolean isAll() { return all; }
    public void setAll(boolean all) { this.all = all; }
}
