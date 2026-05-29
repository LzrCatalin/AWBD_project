package com.awbd.airport_manager.util.pagination;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

public class PagedResponse<T> implements Serializable {

    private List<T> items;
    private Long totalItems;
    private Long totalPages;

    public PagedResponse() {
    }

    public PagedResponse(Page<T> page) {
        this.items = page.getContent();
        this.totalItems = page.getTotalElements();
        this.totalPages = (long) page.getTotalPages();
    }

    public PagedResponse(List<T> items, Long totalItems, Long totalPages) {
        this.items = items;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }

    public List<T> getItems() { return items; }
    public void setItems(List<T> items) { this.items = items; }

    public Long getTotalItems() { return totalItems; }
    public void setTotalItems(Long totalItems) { this.totalItems = totalItems; }

    public Long getTotalPages() { return totalPages; }
    public void setTotalPages(Long totalPages) { this.totalPages = totalPages; }
}
