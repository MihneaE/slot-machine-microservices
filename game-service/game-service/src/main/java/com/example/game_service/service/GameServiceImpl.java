package com.example.game_service.service;

import com.casino.data.grpc.PersistenceResponse;
import com.casino.data.grpc.PlayerResponse;
import com.casino.game.grpc.*;
import com.example.game_service.client.DataClient;
import com.example.game_service.client.RngClient;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Core Business Logic implementation for the Game Microservice.
 * <p>
 * This class acts as the "Orchestrator" of the entire system. It coordinates the flow between
 * the RNG Service (for randomness) and the Data Service (for persistence/ACID transactions).
 * </p>
 *
 *
 *
 * <b>Key Responsibilities:</b>
 * <ul>
 * <li><b>Auth Orchestration</b>: Pass-through for login/register requests to the Data Service.</li>
 * <li><b>Game Logic (5x3 Slot)</b>: Executes spins by fetching 15 random numbers, mapping them to a grid,
 * evaluating 5 distinct paylines, and calculating complex payouts based on symbol multipliers.</li>
 * </ul>
 * <br>
 * <b>Workflow for `executeSpin`:</b>
 * <ol>
 * <li>Validates the bet amount (must be positive).</li>
 * <li>Calls <b>RngClient</b> to get 15 random numbers (filling a 5-column x 3-row grid).</li>
 * <li>Evaluates winning lines using {@code calculatePayout5x3}.</li>
 * <li>Generates a unique <b>Spin ID (UUID)</b> for idempotency.</li>
 * <li>Calls <b>DataClient</b> to persist the transaction (deduct bet, add win, log history).</li>
 * <li>Returns the full result (numbers, balance, winning lines) to the Gateway.</li>
 * </ol>
 */

@GrpcService
public class GameServiceImpl extends GameServiceGrpc.GameServiceImplBase {
    private final RngClient rngClient;
    private final DataClient dataClient;

    public GameServiceImpl(RngClient rngClient, DataClient dataClient) {
        this.rngClient = rngClient;
        this.dataClient = dataClient;
    }

    public static class SpinResult {
        long totalWin;
        List<Integer> winningLines;

        public SpinResult(long totalWin, List<Integer> winningLines) {
            this.totalWin = totalWin;
            this.winningLines = winningLines;
        }

        public long getTotalWin() {
            return totalWin;
        }

        public List<Integer> getWinningLines() {
            return winningLines;
        }
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
    public void register(AuthRequest request, StreamObserver<AuthResponse> responseObserver) {
        AuthResponse response = dataClient.register(request);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void login(AuthRequest request, StreamObserver<AuthResponse> responseObserver) {
        AuthResponse response = dataClient.login(request);

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

        List<Integer> numbers = rngClient.getNumbers(15);

        SpinResult result = calculatePayout5x3(numbers, request.getBetAmount());

        String spinId = UUID.randomUUID().toString();

        PersistenceResponse persistence = dataClient.saveSpin(
                request.getPlayerId(),
                request.getBetAmount(),
                result.totalWin,
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
                .setWinAmount(result.totalWin)
                .setFinalBalance(persistence.getNewBalance())
                .addAllWinningLines(result.winningLines)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public SpinResult calculatePayout5x3(List<Integer> numbers, long totalBet) {
        if (numbers.size() != 15) return new SpinResult(0, new ArrayList<>());

        long totalWin = 0;

        List<Integer> winningLinesIndices = new ArrayList<>();

        long betPerLine = Math.max(1, totalBet / 5);

        int[][] paylines = {
                {5, 6, 7, 8, 9},
                {0, 1, 2, 3, 4},
                {10, 11, 12, 13, 14},
                {0, 6, 12, 8, 4},
                {10, 6, 2, 8, 14}
        };

        for (int i = 0; i < paylines.length; i++) {
            long win = checkLineWin(numbers, paylines[i], betPerLine);

            if (win > 0) {
                totalWin += win;
                winningLinesIndices.add(i);
            }
        }

        return new SpinResult(totalWin, winningLinesIndices);
    }

    private long checkLineWin(List<Integer> grid, int[] lineIndices, long betPerLine) {
        int firstSymbol = grid.get(lineIndices[0]);
        int matchCount = 1;

        for (int i = 1; i < lineIndices.length; i++) {
            if (grid.get(lineIndices[i]) == firstSymbol) {
                matchCount++;
            } else {
                break;
            }
        }

        if (firstSymbol == 1 && matchCount >= 2) {
            return getMultiplier(firstSymbol, matchCount) * betPerLine;
        }

        if (matchCount >= 3) {
            return getMultiplier(firstSymbol, matchCount) * betPerLine;
        }

        return 0;
    }

    private int getMultiplier(int symbolId, int count)
    {
        switch (symbolId)
        {
            case 1:
                if (count == 2) return 1;
                if (count == 3) return 5;
                if (count == 4) return 20;
                if (count == 5) return 50;
                break;

            case 2:
            case 3:
            case 6:
                if (count == 3) return 5;
                if (count == 4) return 20;
                if (count == 5) return 100;
                break;

            case 4:
                if (count == 3) return 15;
                if (count == 4) return 50;
                if (count == 5) return 250;
                break;

            case 5:
                if (count == 3) return 50;
                if (count == 4) return 200;
                if (count == 5) return 2500;
                break;

            default: return 0;
        }

        return 0;
    }
}
