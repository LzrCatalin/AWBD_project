package com.awbd.airport_manager.util.spec;

import com.awbd.airport_manager.util.search.dto.FilterDTO;
import com.awbd.airport_manager.util.search.enums.FilterOperation;
import jakarta.persistence.criteria.*;

import java.util.List;

public class DefaultPredicateBuilder {

    public static <T> Predicate build(Root<T> root, CriteriaBuilder cb, FilterDTO filter) {
        String field = filter.getField();
        Object value = filter.getValue();
        FilterOperation op = filter.getType();

        Path<Object> path = getNestedPath(root, field);

        return switch (op) {
            case EQ -> cb.equal(path, value);
            case NOT -> cb.notEqual(path, value);
            case LIKE -> {
                if (value == null) yield null;
                yield cb.like(path.as(String.class), "%" + value + "%");
            }
            case TRIM_LIKE_IGNORE_CASE -> {
                if (value == null) yield null;
                yield cb.like(cb.lower(path.as(String.class)), "%" + value.toString().trim().toLowerCase() + "%");
            }
            case STARTS_WITH -> {
                if (value == null) yield null;
                yield cb.like(cb.lower(path.as(String.class)), value.toString().toLowerCase() + "%");
            }
            case ENDS_WITH -> {
                if (value == null) yield null;
                yield cb.like(cb.lower(path.as(String.class)), "%" + value.toString().toLowerCase());
            }
            case GT -> {
                if (!(value instanceof Comparable)) yield null;
                yield cb.greaterThan(path.as(Comparable.class), (Comparable) value);
            }
            case LT -> {
                if (!(value instanceof Comparable)) yield null;
                yield cb.lessThan(path.as(Comparable.class), (Comparable) value);
            }
            case GTE -> {
                if (!(value instanceof Comparable)) yield null;
                yield cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) value);
            }
            case LTE -> {
                if (!(value instanceof Comparable)) yield null;
                yield cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) value);
            }
            case IS_NULL -> cb.isNull(path);
            case IS_NOT_NULL -> cb.isNotNull(path);
            case IN -> {
                if (!(value instanceof List)) yield null;
                List<?> values = (List<?>) value;
                yield values.isEmpty() ? null : path.in(values);
            }
            case NOT_IN -> {
                if (!(value instanceof List)) yield null;
                List<?> values = (List<?>) value;
                yield values.isEmpty() ? null : cb.not(path.in(values));
            }
        };
    }

    private static <T> Path<Object> getNestedPath(Root<T> root, String field) {
        String[] parts = field.split("\\.");
        Path<Object> path = root.get(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }
        return path;
    }
}
