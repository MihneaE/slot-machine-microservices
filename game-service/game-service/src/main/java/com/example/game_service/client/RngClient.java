package com.example.game_service.client;

import com.casino.rng.grpc.RngRequest;
import com.casino.rng.grpc.RngResponse;
import com.casino.rng.grpc.RngServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * gRPC Client Wrapper for the RNG (Random Number Generator) Microservice.
 * <p>
 * This component acts as a synchronous bridge to the external RNG Service.
 * It uses a blocking gRPC stub, meaning the Game Service thread will wait
 * until the RNG service responds.
 * </p>
 *
 *
 *
 * <b>Main Responsibility:</b>
 * <ul>
 * <li><b>getNumbers(int count)</b>: Requests a specific quantity of random integers
 * (e.g., 15 numbers for a 5x3 grid). These numbers are the raw data used to determine
 * the visual outcome of the spin.</li>
 * </ul>
 */

@Service
public class RngClient {

    @GrpcClient("rng-service")
    private RngServiceGrpc.RngServiceBlockingStub rngStub;

    public List<Integer> getNumbers(int count) {
        RngRequest request = RngRequest.newBuilder().setCount(count).build();
        RngResponse response = rngStub.generateNumbers(request);
        return response.getNumbersList();
    }
}
