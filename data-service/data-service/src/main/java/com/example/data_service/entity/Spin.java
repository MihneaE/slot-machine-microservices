package com.example.data_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * JPA Entity representing the immutable record of a single game round (Spin).
 * <p>
 * This class maps to the <b>"spins"</b> table in the database and serves as an audit log for all
 * transactions. Each instance contains:
 * <ul>
 * <li><b>id</b>: Unique transaction ID (UUID) provided by the Game Service (idempotency key).</li>
 * <li><b>playerId</b>: The ID of the user who initiated the spin.</li>
 * <li><b>betAmount</b>: The amount wagered in this round.</li>
 * <li><b>winAmount</b>: The amount won (0 if lost).</li>
 * <li><b>outcome</b>: The visual result of the slots (e.g., "[7, 7, 7]").</li>
 * <li><b>timestamp</b>: The exact time when the transaction occurred.</li>
 * </ul>
 * </p>
 */

@Entity
@Table(name = "spins")
public class Spin {
    @Id
    private String id;
    private String playerId;
    private long betAmount;
    private long winAmount;
    private String outcome;
    private LocalDateTime timestamp;

    public Spin() {
    }

    public Spin(String id, String playerId, long betAmount, long winAmount, String outcome, LocalDateTime timestamp) {
        this.id = id;
        this.playerId = playerId;
        this.betAmount = betAmount;
        this.winAmount = winAmount;
        this.outcome = outcome;
        this.timestamp = timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBetAmount(long betAmount) {
        this.betAmount = betAmount;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setWinAmount(long winAmount) {
        this.winAmount = winAmount;
    }

    public String getId() {
        return id;
    }

    public long getBetAmount() {
        return betAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public long getWinAmount() {
        return winAmount;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getPlayerId() {
        return playerId;
    }
}