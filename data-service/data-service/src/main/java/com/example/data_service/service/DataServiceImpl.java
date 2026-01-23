package com.example.data_service.service;

import com.casino.data.grpc.*;
import com.example.data_service.entity.Player;
import com.example.data_service.entity.Spin;
import com.example.data_service.repository.PlayerRepository;
import com.example.data_service.repository.SpinRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * gRPC Server implementation for the Data Service.
 * <p>
 * This class extends the generated {@code DataServiceImplBase} and encapsulates the core business logic
 * for database interactions using Spring Data JPA. It serves as the system of record, ensuring
 * <b>ACID compliance</b> for all financial operations.
 * </p>
 * *
 * * <br>
 * <b>Key Functionalities:</b>
 * <ul>
 * <li><b>Authentication</b>: Handles {@code login} (credential verification) and {@code register} (account creation with initial balance).</li>
 * <li><b>getPlayer</b>: Retrieves a player's balance or creates a default account if needed.</li>
 * <li><b>processSpin</b>: Handles the financial transaction for a game round atomically via {@code @Transactional}. It performs validation,
 * deducts bets, adds winnings, and logs the transaction.</li>
 * <li><b>Idempotency</b>: Prevents double-spending by checking if a specific Spin ID has already been processed.</li>
 * </ul>
 */

@GrpcService
public class DataServiceImpl extends DataServiceGrpc.DataServiceImplBase {

    private final PlayerRepository playerRepo;
    private final SpinRepository spinRepo;

    public DataServiceImpl(PlayerRepository playerRepo, SpinRepository spinRepo) {
        this.playerRepo = playerRepo;
        this.spinRepo = spinRepo;
    }

    @Override
    public void getPlayer(PlayerRequest request, StreamObserver<PlayerResponse> responseObserver) {

        Player player = playerRepo.findById(request.getPlayerId())
                .orElseGet(() -> playerRepo.save(new Player(request.getPlayerId(), request.getPlayerId(), 10000L))); // 100.00 credite

        PlayerResponse response = PlayerResponse.newBuilder()
                .setPlayerId(player.getId())
                .setBalance(player.getBalance())
                .setExists(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void processSpin(PersistenceRequest request, StreamObserver<PersistenceResponse> responseObserver) {
        String spinId = request.getSpinId();

        if (spinRepo.existsById(spinId)) {
            Player p = playerRepo.findById(request.getPlayerId()).orElseThrow();

            responseObserver.onNext(PersistenceResponse.newBuilder()
                    .setSuccess(true)
                    .setNewBalance(p.getBalance())
                    .build());
            responseObserver.onCompleted();
            return;
        }

        Optional<Player> playerOpt = playerRepo.findById(request.getPlayerId());
        if (playerOpt.isEmpty()) {
            responseObserver.onNext(PersistenceResponse.newBuilder().setSuccess(false).setErrorMessage("Player not found").build());
            responseObserver.onCompleted();
            return;
        }
        Player player = playerOpt.get();

        if (player.getBalance() < request.getBetAmount()) {
            responseObserver.onNext(PersistenceResponse.newBuilder().setSuccess(false).setErrorMessage("Insufficient funds").build());
            responseObserver.onCompleted();
            return;
        }

        long newBalance = player.getBalance() - request.getBetAmount() + request.getWinAmount();
        player.setBalance(newBalance);
        playerRepo.save(player);

        Spin spin = new Spin(
                spinId,
                player.getId(),
                request.getBetAmount(),
                request.getWinAmount(),
                request.getOutcomeList().toString(),
                LocalDateTime.now()
        );
        spinRepo.save(spin);

        responseObserver.onNext(PersistenceResponse.newBuilder()
                .setSuccess(true)
                .setNewBalance(newBalance)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void register(AuthRequest request, StreamObserver<AuthResponse> responseObserver) {
        if (playerRepo.existsById(request.getUsername())) {
            responseObserver.onNext(AuthResponse.newBuilder().setSuccess(false).setMessage("Username already exists").build());
        } else {

            playerRepo.save(new Player(request.getUsername(), request.getPassword(), 1000L));
            responseObserver.onNext(AuthResponse.newBuilder().setSuccess(true).setMessage("Registered successfully").build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void login(AuthRequest request, StreamObserver<AuthResponse> responseObserver) {
        var playerOpt = playerRepo.findById(request.getUsername());

        if (playerOpt.isPresent() && playerOpt.get().getPassword().equals(request.getPassword())) {
            responseObserver.onNext(AuthResponse.newBuilder()
                    .setSuccess(true)
                    .setPlayerId(playerOpt.get().getId())
                    .setMessage("Login successful")
                    .build());
        } else {
            responseObserver.onNext(AuthResponse.newBuilder().setSuccess(false).setMessage("Invalid credentials").build());
        }
        responseObserver.onCompleted();
    }
}
