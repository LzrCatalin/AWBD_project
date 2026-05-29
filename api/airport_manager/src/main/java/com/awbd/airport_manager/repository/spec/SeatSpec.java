package com.awbd.airport_manager.repository.spec;

import com.awbd.airport_manager.dto.filter.SeatFilter;
import com.awbd.airport_manager.model.Seat;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class SeatSpec {

    public static Specification<Seat> fromFilter(SeatFilter filter) {
        return Specification
                .where(hasFlightId(filter.getFlightId()))
                .and(isAvailable(filter.getAvailable()))
                .and(hasType(filter.getType()));
    }

    private static Specification<Seat> hasFlightId(UUID flightId) {
        return (root, query, cb) ->
                flightId == null ? null : cb.equal(root.get("flight").get("id"), flightId);
    }

    private static Specification<Seat> isAvailable(Boolean available) {
        return (root, query, cb) ->
                available == null ? null : cb.equal(root.get("available"), available);
    }

    private static Specification<Seat> hasType(String type) {
        return (root, query, cb) ->
                type == null || type.isBlank() ? null :
                cb.like(cb.lower(root.get("type")), "%" + type.toLowerCase() + "%");
    }
}
