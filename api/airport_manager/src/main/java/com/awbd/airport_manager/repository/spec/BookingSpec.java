package com.awbd.airport_manager.repository.spec;

import com.awbd.airport_manager.dto.filter.BookingFilter;
import com.awbd.airport_manager.model.Booking;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class BookingSpec {

    public static Specification<Booking> fromFilter(BookingFilter filter) {
        return Specification
                .where(hasFlightId(filter.getFlightId()))
                .and(hasAccountId(filter.getAccountId()));
    }

    private static Specification<Booking> hasFlightId(UUID flightId) {
        return (root, query, cb) ->
                flightId == null ? null : cb.equal(root.get("flight").get("id"), flightId);
    }

    private static Specification<Booking> hasAccountId(UUID accountId) {
        return (root, query, cb) ->
                accountId == null ? null : cb.equal(root.get("account").get("id"), accountId);
    }
}
