package com.awbd.airport_manager.util.search.dto;

import com.awbd.airport_manager.util.pagination.PaginationDTO;
import com.awbd.airport_manager.util.search.enums.FilterOperation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class SearchDTO {

    private List<FilterDTO> filters = new ArrayList<>();
    private List<SorterDTO> sorters = new ArrayList<>();
    private PaginationDTO pagination = new PaginationDTO();

    public SearchDTO() {
    }

    public List<FilterDTO> getFilters() { return filters; }
    public void setFilters(List<FilterDTO> filters) { this.filters = filters; }

    public List<SorterDTO> getSorters() { return sorters; }
    public void setSorters(List<SorterDTO> sorters) { this.sorters = sorters; }

    public PaginationDTO getPagination() { return pagination; }
    public void setPagination(PaginationDTO pagination) { this.pagination = pagination; }

    public void addFilter(String field, FilterOperation type, Object value) {
        if (filters == null) filters = new ArrayList<>();
        filters.add(new FilterDTO(field, type, value));
    }

    public Sort buildSort() {
        if (sorters == null || sorters.isEmpty()) return Sort.unsorted();
        List<Sort.Order> orders = sorters.stream()
                .filter(s -> s.getField() != null && s.getDirection() != null)
                .map(s -> new Sort.Order(s.getDirection(), s.getField()))
                .toList();
        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }

    public Pageable buildPageable() {
        Sort sort = buildSort();
        if (pagination.isAll()) return Pageable.unpaged(sort);
        return PageRequest.of(pagination.getPage(), pagination.getPageSize(), sort);
    }
}
