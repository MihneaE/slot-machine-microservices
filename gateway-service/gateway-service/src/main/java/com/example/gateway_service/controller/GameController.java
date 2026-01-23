package com.example.gateway_service.controller;

import com.casino.game.grpc.BalanceResponse;
import com.casino.game.grpc.SpinResponse;
import com.example.gateway_service.client.GameClient;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import java.util.Map;

/**
 * WebSocket Controller handling real-time game interactions.
 * <p>
 * Manages the bi-directional communication channel using the <b>STOMP protocol</b>.
 * This allows for low-latency interactions crucial for the gaming experience.
 * </p>
 *
 *
 *
 * <b>Workflow:</b>
 * <ol>
 * <li><b>Input:</b> Client sends a JSON payload to <code>/app/spin</code>.</li>
 * <li><b>Processing:</b> Controller parses the payload and invokes the gRPC client.</li>
 * <li><b>Output:</b> The gRPC response (including spin result, balance, and <b>winning lines</b>)
 * is broadcast to <code>/topic/spin-result</code>.</li>
 * </ol>
 */

@Controller
public class GameController {
    private final GameClient gameClient;

    public GameController(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @MessageMapping("/spin")
    @SendTo("/topic/spin-result")
    public Map<String, Object> spin(Map<String, Object> payload) {
        String playerId = (String) payload.get("playerId");

        long betAmount = 0;
        if (payload.get("betAmount") instanceof Integer) {
            betAmount = ((Integer) payload.get("betAmount")).longValue();
        } else {
            betAmount = ((Long) payload.get("betAmount"));
        }

        SpinResponse response = gameClient.executeSpin(playerId, betAmount);

        return Map.of(
                "spinId", response.getSpinId(),
                "numbers", response.getNumbersList(),
                "winAmount", response.getWinAmount(),
                "finalBalance", response.getFinalBalance(),
                "winningLines", response.getWinningLinesList()
        );
    }

    @MessageMapping("/balance")
    @SendTo("/topic/balance-update")
    public Map<String, Object> getBalance(Map<String, String> payload) {
        String playerId = payload.get("playerId");

        BalanceResponse response = gameClient.getBalance(playerId);

        return Map.of(
                "playerId", playerId,
                "balance", response.getBalance()
        );
    }
}
