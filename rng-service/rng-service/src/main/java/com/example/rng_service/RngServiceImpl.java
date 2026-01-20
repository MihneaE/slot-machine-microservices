package com.example.rng_service;

import com.casino.rng.grpc.RngRequest;
import com.casino.rng.grpc.RngResponse;
import com.casino.rng.grpc.RngServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * gRPC Service Implementation for Random Number Generation.
 * <p>
 * This class is the core "Dice Roller" of the casino platform. It extends the generated gRPC base class
 * to serve random numbers to the Game Service.
 * <br>
 * <b>Key Features:</b>
 * <ul>
 * <li><b>Standard Generation</b>: Uses {@code java.util.Random} to generate unbiased random integers (0-9) for the slot reels.</li>
 * <li><b>Deterministic Override (Cheat Mode)</b>: Utilizes an {@link AtomicReference} to hold a "forced outcome".
 * If a forced outcome is set (via the AdminController), the service returns that specific sequence once
 * and then immediately clears it, reverting to random generation. This is thread-safe.</li>
 * </ul>
 * </p>
 */

@GrpcService
public class RngServiceImpl extends RngServiceGrpc.RngServiceImplBase {
    private final Random random = new Random();
    private final AtomicReference<List<Integer>> forcedOutcome = new AtomicReference<>();

    @Override
    public void generateNumbers(RngRequest request, StreamObserver<RngResponse> responseObserver) {
        List<Integer> numbers = new ArrayList<>();
        int count = request.getCount();

        List<Integer> forced = forcedOutcome.getAndSet(null);

        if (forced != null && forced.size() == count) {
            numbers.addAll(forced);
            System.out.println("RNG: Generating FORCED outcome: " + numbers);
        } else {

            for (int i = 0; i < count; i++) {
                numbers.add(random.nextInt(10));
            }
            System.out.println("RNG: Generating RANDOM outcome: " + numbers);
        }

        RngResponse response = RngResponse.newBuilder()
                .addAllNumbers(numbers)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void setForcedOutcome(List<Integer> outcome) {
        this.forcedOutcome.set(outcome);
    }
}
