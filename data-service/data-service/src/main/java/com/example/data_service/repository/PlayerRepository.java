package com.example.data_service.repository;

import com.example.data_service.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Player} persistence.
 * <p>
 * Extends <b>JpaRepository</b> to provide standard CRUD (Create, Read, Update, Delete) operations
 * on the "players" table without requiring manual SQL queries. It is primarily used by the
 * {@code DataServiceImpl} to retrieve and update user balances.
 * </p>
 */

public interface PlayerRepository extends JpaRepository<Player, String> {
}
