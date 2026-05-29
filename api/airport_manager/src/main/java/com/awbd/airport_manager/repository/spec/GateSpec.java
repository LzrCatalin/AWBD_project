package com.awbd.airport_manager.repository.spec;

import com.awbd.airport_manager.dto.filter.GateFilter;
import com.awbd.airport_manager.model.Gate;
import org.springframework.data.jpa.domain.Specification;

public class GateSpec {

    public static Specification<Gate> fromFilter(GateFilter filter) {
        return Specification
                .where(likeTerminal(filter.getTerminal()))
                .and(likeGateNo(filter.getGateNo()));
    }

    private static Specification<Gate> likeTerminal(String value) {
        return (root, query, cb) ->
                value == null || value.isBlank() ? null :
                cb.like(cb.lower(root.get("terminal")), "%" + value.toLowerCase() + "%");
    }

    private static Specification<Gate> likeGateNo(String value) {
        return (root, query, cb) ->
                value == null || value.isBlank() ? null :
                cb.like(cb.lower(root.get("gateNo")), "%" + value.toLowerCase() + "%");
    }
}
