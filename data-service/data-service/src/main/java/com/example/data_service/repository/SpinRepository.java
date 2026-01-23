package com.example.data_service.repository;

import com.example.data_service.entity.Spin;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data Access Object (DAO) for the {@link Spin} entity.
 * <p>
 * Handles the persistence of game audit logs. Crucial for performing
 * existence checks on Spin IDs to ensure transaction idempotency.
 * </p>
 */

public interface SpinRepository extends JpaRepository<Spin, String> {
}
