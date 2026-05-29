package com.awbd.airport_manager.repository.spec;

import com.awbd.airport_manager.dto.filter.FlightFilter;
import com.awbd.airport_manager.model.Flight;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class FlightSpec {

    public static Specification<Flight> fromFilter(FlightFilter filter) {
        return Specification
                .where(likeCity("departureCity", filter.getDepartureCity()))
                .and(likeCity("arrivalCity", filter.getArrivalCity()))
                .and(departureAfter(filter.getDepartureAfter()))
                .and(departureBefore(filter.getDepartureBefore()));
    }

    private static Specification<Flight> likeCity(String field, String value) {
        return (root, query, cb) ->
                value == null || value.isBlank() ? null :
                cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }

    private static Specification<Flight> departureAfter(LocalDateTime value) {
        return (root, query, cb) ->
                value == null ? null : cb.greaterThanOrEqualTo(root.get("departureTime"), value);
    }

    private static Specification<Flight> departureBefore(LocalDateTime value) {
        return (root, query, cb) ->
                value == null ? null : cb.lessThanOrEqualTo(root.get("departureTime"), value);
    }
}
