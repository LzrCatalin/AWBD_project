package com.awbd.airport_manager.util.search.dto;

import com.awbd.airport_manager.util.search.enums.FilterOperation;
import com.awbd.airport_manager.util.search.enums.OperatorEnum;

public class FilterDTO {

    private String field;
    private FilterOperation type;
    private Object value;
    private OperatorEnum operator = OperatorEnum.AND;

    public FilterDTO() {
    }

    public FilterDTO(String field, FilterOperation type, Object value) {
        this.field = field;
        this.type = type;
        this.value = value;
    }

    public FilterDTO(String field, FilterOperation type, Object value, OperatorEnum operator) {
        this.field = field;
        this.type = type;
        this.value = value;
        this.operator = operator;
    }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public FilterOperation getType() { return type; }
    public void setType(FilterOperation type) { this.type = type; }

    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }

    public OperatorEnum getOperator() { return operator; }
    public void setOperator(OperatorEnum operator) { this.operator = operator; }
}
