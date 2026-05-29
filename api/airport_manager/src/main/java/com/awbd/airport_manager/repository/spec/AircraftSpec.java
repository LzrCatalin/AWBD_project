package com.awbd.airport_manager.repository.spec;

import com.awbd.airport_manager.dto.filter.AircraftFilter;
import com.awbd.airport_manager.model.Aircraft;
import org.springframework.data.jpa.domain.Specification;

public class AircraftSpec {

    public static Specification<Aircraft> fromFilter(AircraftFilter filter) {
        return Specification.where(likeModel(filter.getModel()));
    }

    private static Specification<Aircraft> likeModel(String value) {
        return (root, query, cb) ->
                value == null || value.isBlank() ? null :
                cb.like(cb.lower(root.get("model")), "%" + value.toLowerCase() + "%");
    }
}
