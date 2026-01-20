package com.example.game_service.service;

import com.casino.data.grpc.PersistenceResponse;
import com.casino.data.grpc.PlayerResponse;
import com.casino.game.grpc.*;
import com.example.game_service.client.DataClient;
import com.example.game_service.client.RngClient;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import java.util.List;
import java.util.UUID;

/**
 * Core Business Logic implementation for the Game Microservice.
 * <p>
 * This class acts as the "Orchestrator" of the entire system. It coordinates the flow between
 * the RNG Service (for luck) and the Data Service (for persistence).
 * <br>
 * <b>Workflow for `executeSpin`:</b>
 * <ol>
 * <li>Validates the bet amount (must be positive).</li>
 * <li>Calls <b>RngClient</b> to get 3 random numbers.</li>
 * <li>Calculates winnings based on the game rules:
 * <ul>
 * <li><b>3 Identical Numbers</b>: 10x Multiplier (Jackpot).</li>
 * <li><b>2 Identical Numbers</b>: 2x Multiplier (Small Win).</li>
 * <li><b>No Matches</b>: Loss (0 payout).</li>
 * </ul>
 * </li>
 * <li>Generates a unique <b>Spin ID (UUID)</b> for transaction tracking.</li>
 * <li>Calls <b>DataClient</b> to persist the transaction and update the balance.</li>
 * <li>Returns the final result to the Gateway.</li>
 * </ol>
 * </p>
 */

@GrpcService
public class GameServiceImpl extends GameServiceGrpc.GameServiceImplBase {
    private final RngClient rngClient;
    private final DataClient dataClient;

    public GameServiceImpl(RngClient rngClient, DataClient dataClient) {
        this.rngClient = rngClient;
        this.dataClient = dataClient;
    }

    @Override
    public void getBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        PlayerResponse dataResponse = dataClient.getPlayer(request.getPlayerId());

        BalanceResponse response = BalanceResponse.newBuilder()
                .setBalance(dataResponse.getBalance())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void executeSpin(SpinRequest request, StreamObserver<SpinResponse> responseObserver) {
        if (request.getBetAmount() <= 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Bet amount must be positive").asRuntimeException());
            return;
        }

        List<Integer> numbers = rngClient.getNumbers(3);

        long winAmount = calculatePayout(numbers, request.getBetAmount());

        String spinId = UUID.randomUUID().toString();

        PersistenceResponse persistence = dataClient.saveSpin(
                request.getPlayerId(),
                request.getBetAmount(),
                winAmount,
                numbers,
                spinId
        );

        if (!persistence.getSuccess()) {
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription(persistence.getErrorMessage()).asRuntimeException());
            return;
        }

        SpinResponse response = SpinResponse.newBuilder()
                .setSpinId(spinId)
                .addAllNumbers(numbers)
                .setWinAmount(winAmount)
                .setFinalBalance(persistence.getNewBalance())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private long calculatePayout(List<Integer> numbers, long betAmount) {
        if (numbers.size() != 3) return 0;

        int n1 = numbers.get(0);
        int n2 = numbers.get(1);
        int n3 = numbers.get(2);

        if (n1 == n2 && n2 == n3) {
            return betAmount * 10;
        } else if (n1 == n2 || n2 == n3 || n1 == n3) {
            return betAmount * 2;
        } else {

            return 0;
        }
    }
}
