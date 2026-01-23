package com.example.gateway_service.client;

import com.casino.game.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * gRPC Client Wrapper for the Game Microservice.
 * <p>
 * This component implements the <b>Gateway Pattern</b>. It acts as a proxy, translating external
 * HTTP/WebSocket requests (JSON) into internal gRPC calls (Protobuf).
 * </p>
 *
 *
 *
 * <b>Core Responsibilities:</b>
 * <ul>
 * <li><b>Protocol Translation:</b> Converts high-level user actions into binary gRPC messages.</li>
 * <li><b>Service Discovery:</b> Locates the "game-service" instance via the configured discovery mechanism (e.g., K8s DNS or static config).</li>
 * <li><b>Synchronous Execution:</b> Uses a blocking stub to ensure the frontend gets an immediate confirmation of actions like spins or logins.</li>
 * </ul>
 */

@Service
public class GameClient {

    @GrpcClient("game-service")
    private GameServiceGrpc.GameServiceBlockingStub gameStub;

    public SpinResponse executeSpin(String playerId, long betAmount) {
        SpinRequest request = SpinRequest.newBuilder()
                .setPlayerId(playerId)
                .setBetAmount(betAmount)
                .build();
        return gameStub.executeSpin(request);
    }

    public BalanceResponse getBalance(String playerId) {
        BalanceRequest request = BalanceRequest.newBuilder()
                .setPlayerId(playerId)
                .build();
        return gameStub.getBalance(request);
    }

    public AuthResponse login(String username, String password) {
        AuthRequest request = AuthRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        return gameStub.login(request);
    }

    public AuthResponse register(String username, String password) {
        AuthRequest request = AuthRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        return gameStub.register(request);
    }
}
