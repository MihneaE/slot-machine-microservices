package com.example.data_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA Entity representing a user's financial and authentication profile.
 * <p>
 * This class maps to the <b>"players"</b> table and acts as the single source of truth for
 * a user's account status. It contains:
 * <ul>
 * <li><b>id</b>: The unique identifier for the player (acts as the <b>username</b>).</li>
 * <li><b>password</b>: The user's credentials (stored as plain text for this demo, usually hashed).</li>
 * <li><b>balance</b>: The current wallet balance, updated transactionally during gameplay.</li>
 * </ul>
 * </p>
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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getId() { return id; }
    public long getBalance() { return balance; }
    public void setBalance(long balance) { this.balance = balance; }
}
