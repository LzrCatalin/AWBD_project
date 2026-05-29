package com.awbd.airport_manager.util.search.dto;

import org.springframework.data.domain.Sort;

public class SorterDTO {

    private String field;
    private Sort.Direction direction = Sort.Direction.ASC;

    public SorterDTO() {
    }

    public SorterDTO(String field, Sort.Direction direction) {
        this.field = field;
        this.direction = direction;
    }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public Sort.Direction getDirection() { return direction; }
    public void setDirection(Sort.Direction direction) { this.direction = direction; }
}
