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
 * Unlike a traditional REST Controller, this class handles messages sent via the STOMP protocol.
 * It serves as the entry point for frontend actions (like spinning the wheel) and orchestrates the response flow.
 * <br>
 * <b>Workflow:</b>
 * <ol>
 * <li>Client sends a message to <code>/app/spin</code>.</li>
 * <li>Controller receives the payload, parses it, and calls the <b>GameClient</b> (gRPC).</li>
 * <li>The result is automatically broadcast to subscribers of <code>/topic/spin-result</code> via the <code>@SendTo</code> annotation.</li>
 * </ol>
 * </p>
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
                "finalBalance", response.getFinalBalance()
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
