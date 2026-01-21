package com.example.gateway_service.controller;

import com.casino.game.grpc.AuthResponse;
import com.example.gateway_service.client.GameClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * REST Controller for handling User Authentication.
 * <p>
 * This controller serves as the entry point for HTTP authentication requests from the frontend.
 * Unlike the WebSocket controller (used for gameplay), this uses standard REST endpoints for
 * operations that happen before the game starts.
 * <br>
 * <b>Endpoints:</b>
 * <ul>
 * <li><b>POST /auth/login</b>: Authenticates a user and returns their Player ID.</li>
 * <li><b>POST /auth/register</b>: Creates a new user account with an initial balance.</li>
 * </ul>
 * All requests are forwarded to the {@link GameClient} via gRPC.
 * </p>
 */

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final GameClient gameClient;

    public AuthController(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> creds) {
        AuthResponse res = gameClient.login(creds.get("username"), creds.get("password"));

        return Map.of(
                "success", res.getSuccess(),
                "message", res.getMessage(),
                "playerId", res.getPlayerId()
        );
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> creds) {
        AuthResponse res = gameClient.register(creds.get("username"), creds.get("password"));

        return Map.of(
                "success", res.getSuccess(),
                "message", res.getMessage()
        );
    }
}
