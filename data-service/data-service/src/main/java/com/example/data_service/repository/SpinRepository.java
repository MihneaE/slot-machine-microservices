package com.example.data_service.repository;

import com.example.data_service.entity.Spin;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Spin} persistence.
 * <p>
 * Extends <b>JpaRepository</b> and handles operations on the "spins" table.
 * Its main responsibilities are saving the game history (audit log) and performing existence checks
 * on spin IDs to support idempotency (preventing duplicate transaction processing).
 * </p>
 */

public interface SpinRepository extends JpaRepository<Spin, String> {
}
