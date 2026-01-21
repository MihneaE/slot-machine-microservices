package com.example.gateway_service.client;

import com.casino.game.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * gRPC Client Wrapper for the Game Microservice.
 * <p>
 * This component acts as a proxy between the API Gateway and the internal Game Service.
 * Since the Gateway exposes WebSockets (or REST) to the frontend, but the internal services talk gRPC,
 * this client is responsible for translating the external requests into gRPC calls.
 * <br>
 * <b>Main Functions:</b>
 * <ul>
 * <li><b>Authentication</b>: Forwards {@code login} and {@code register} requests to the Game Service.</li>
 * <li><b>executeSpin</b>: Forwards a spin request to the Game Service and waits for the result (numbers, win amount).</li>
 * <li><b>getBalance</b>: Queries the Game Service (which queries Data Service) for the player's current funds.</li>
 * </ul>
 * </p>
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
