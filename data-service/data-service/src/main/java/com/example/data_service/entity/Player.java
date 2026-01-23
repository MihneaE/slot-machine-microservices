package com.example.data_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA Entity representing the immutable audit record of a single game round.
 * <p>
 * Maps to the <b>"spins"</b> table. This entity is created once per transaction
 * and is never updated, serving as a permanent history of the game outcome.
 * </p>
 * <ul>
 * <li><b>id</b>: UUID provided by the Game Service (used as an idempotency key).</li>
 * <li><b>playerId</b>: Foreign key reference to the Player.</li>
 * <li><b>betAmount</b>: Wagered amount.</li>
 * <li><b>winAmount</b>: Payout amount.</li>
 * <li><b>outcome</b>: Serialized representation of the slot result (e.g., "[7, 7, 7]").</li>
 * <li><b>timestamp</b>: Creation time of the record.</li>
 * </ul>
 */

@Entity
@Table(name = "players")
public class Player {

    @Id
    private String id;
    private String password;
    private long balance;

    public Player() {
    }

    public Player(String id, String password, long balance) {
        this.id = id;
        this.password = password;
        this.balance = balance;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getBalance() {
        return balance;
    }

    public String getPassword() {
        return password;
    }
}
