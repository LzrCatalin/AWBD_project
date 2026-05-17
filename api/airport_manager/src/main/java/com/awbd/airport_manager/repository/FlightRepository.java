package com.awbd.airport_manager.repository;

import com.awbd.airport_manager.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FlightRepository extends JpaRepository<Flight, UUID> {
}
