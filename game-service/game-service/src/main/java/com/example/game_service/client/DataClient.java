package com.example.game_service.client;

import com.casino.data.grpc.*;
import com.casino.game.grpc.AuthRequest;
import com.casino.game.grpc.AuthResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import java.util.List;
import com.casino.data.grpc.DataServiceGrpc;
import com.casino.data.grpc.PersistenceRequest;
import com.casino.data.grpc.PersistenceResponse;
import com.casino.data.grpc.PlayerRequest;
import com.casino.data.grpc.PlayerResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * gRPC Client Wrapper for the Data Microservice.
 * <p>
 * This class serves as the <b>Data Access Layer</b> for the Game Service. It abstracts the low-level
 * gRPC calls to the {@code data-service}, ensuring that the core game logic remains decoupled from
 * database implementation details.
 * </p>
 *
 *
 *
 * <b>Key Responsibilities:</b>
 * <ul>
 * <li><b>Protocol Adaptation (Adapter Pattern)</b>: Manually maps objects between the Game Protocol
 * (used by the Gateway) and the Data Protocol (used by the Database Service), specifically for
 * {@code AuthRequest} and {@code AuthResponse}.</li>
 * <li><b>State Retrieval</b>: Fetches player balances via {@code getPlayer}.</li>
 * <li><b>Transactional Persistence</b>: Delegates the atomic "bet + win + history" transaction to
 * the Data Service via {@code saveSpin}.</li>
 * </ul>
 */

@Service
public class DataClient {
    @GrpcClient("data-service")
    private DataServiceGrpc.DataServiceBlockingStub dataStub;

    public PlayerResponse getPlayer(String playerId) {
        return dataStub.getPlayer(PlayerRequest.newBuilder().setPlayerId(playerId).build());
    }

    public com.casino.game.grpc.AuthResponse login(com.casino.game.grpc.AuthRequest gameRequest) {

        com.casino.data.grpc.AuthRequest dataRequest = com.casino.data.grpc.AuthRequest.newBuilder()
                .setUsername(gameRequest.getUsername())
                .setPassword(gameRequest.getPassword())
                .build();

        com.casino.data.grpc.AuthResponse dataResponse = dataStub.login(dataRequest);

        return com.casino.game.grpc.AuthResponse.newBuilder()
                .setSuccess(dataResponse.getSuccess())
                .setMessage(dataResponse.getMessage())
                .setPlayerId(dataResponse.getPlayerId())
                .build();
    }

    public com.casino.game.grpc.AuthResponse register(com.casino.game.grpc.AuthRequest gameRequest) {

        com.casino.data.grpc.AuthRequest dataRequest = com.casino.data.grpc.AuthRequest.newBuilder()
                .setUsername(gameRequest.getUsername())
                .setPassword(gameRequest.getPassword())
                .build();

        com.casino.data.grpc.AuthResponse dataResponse = dataStub.register(dataRequest);

        return com.casino.game.grpc.AuthResponse.newBuilder()
                .setSuccess(dataResponse.getSuccess())
                .setMessage(dataResponse.getMessage())
                .setPlayerId(dataResponse.getPlayerId())
                .build();
    }

    public PersistenceResponse saveSpin(String playerId, long bet, long win, List<Integer> outcome, String spinId) {
        PersistenceRequest request = PersistenceRequest.newBuilder()
                .setPlayerId(playerId)
                .setBetAmount(bet)
                .setWinAmount(win)
                .addAllOutcome(outcome)
                .setSpinId(spinId)
                .build();
        return dataStub.processSpin(request);
    }
}
