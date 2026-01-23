package com.example.gateway_service.controller;

import com.casino.game.grpc.AuthResponse;
import com.example.gateway_service.client.GameClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * REST Controller for handling User Authentication.
 * <p>
 * Serves as the HTTP entry point for user sessions. Unlike the gameplay actions which require
 * a persistent connection, authentication is stateless and uses standard REST POST methods.
 * </p>
 *
 *
 *
 * <b>Endpoints:</b>
 * <ul>
 * <li><b>POST /auth/login</b>: Validates credentials and returns a Player ID.</li>
 * <li><b>POST /auth/register</b>: Creates a new account with a default balance.</li>
 * </ul>
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
