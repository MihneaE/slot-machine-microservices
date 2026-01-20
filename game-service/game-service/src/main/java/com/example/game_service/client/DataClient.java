package com.example.game_service.client;

import com.casino.data.grpc.*;
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
