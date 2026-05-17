package com.awbd.airport_manager.repository;

import com.awbd.airport_manager.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AircraftRepository extends JpaRepository<Account, UUID> {
}
