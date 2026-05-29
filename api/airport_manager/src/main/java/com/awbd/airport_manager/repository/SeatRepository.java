package com.awbd.airport_manager.repository;

import com.awbd.airport_manager.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID>, JpaSpecificationExecutor<Seat> {
}
