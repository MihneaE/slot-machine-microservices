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
 * This class manages all persistence-related communication. The Game Service never accesses
 * the database directly; instead, it delegates all read/write operations to the Data Service
 * through this client.
 * <br>
 * <b>Key Functions:</b>
 * <ul>
 * <li><b>Authentication Mapping</b>: Converts {@code AuthRequest} objects from the Game protocol (received from Gateway)
 * to the Data protocol (sent to Database) for {@code login} and {@code register}.</li>
 * <li><b>getPlayer</b>: Retrieves player details and current wallet balance.</li>
 * <li><b>saveSpin</b>: Sends a transactional request to persist the spin result, update the player's balance
 * (deduct bet, add win), and log the game history.</li>
 * </ul>
 * </p>
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
