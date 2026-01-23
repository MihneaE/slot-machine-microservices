package com.example.data_service.repository;

import com.example.data_service.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data Access Object (DAO) for the {@link Player} entity.
 * <p>
 * Provides abstract methods for standard CRUD operations on the "players" table.
 * Used by the service layer to validate credentials and update wallet balances.
 * </p>
 */

public interface PlayerRepository extends JpaRepository<Player, String> {
}
