package com.awbd.airport_manager.repository;

import com.awbd.airport_manager.model.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, UUID>, JpaSpecificationExecutor<Aircraft> {
}
