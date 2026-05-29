package com.awbd.airport_manager.util.spec;

import com.awbd.airport_manager.util.search.dto.FilterDTO;
import com.awbd.airport_manager.util.search.dto.SearchDTO;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class QuerySpecification<T> implements Specification<T> {

    private final SearchDTO searchDTO;

    public QuerySpecification(SearchDTO searchDTO) {
        this.searchDTO = searchDTO;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (searchDTO == null || searchDTO.getFilters() == null || searchDTO.getFilters().isEmpty()) {
            return cb.conjunction();
        }

        List<Predicate> predicates = new ArrayList<>();

        for (FilterDTO filter : searchDTO.getFilters()) {
            if (filter.getField() == null || filter.getField().isBlank()) continue;
            try {
                Predicate predicate = DefaultPredicateBuilder.build(root, cb, filter);
                if (predicate != null) predicates.add(predicate);
            } catch (Exception ignored) {
            }
        }

        return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
    }
}
