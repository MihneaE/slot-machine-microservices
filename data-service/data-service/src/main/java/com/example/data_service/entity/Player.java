package com.example.data_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA Entity representing a user's financial profile.
 * <p>
 * This class maps to the <b>"players"</b> table and acts as the single source of truth for
 * a user's current funds. It contains:
 * <ul>
 * <li><b>id</b>: The unique identifier for the player (e.g., username or UUID).</li>
 * <li><b>balance</b>: The current wallet balance, updated transactionally during gameplay.</li>
 * </ul>
 * </p>
 */

@Entity
@Table(name = "players")
public class Player {

    @Id
    private String id;
    private long balance;

    public Player() {
    }

    public Player(String id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
