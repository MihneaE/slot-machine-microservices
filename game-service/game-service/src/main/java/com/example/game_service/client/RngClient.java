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
 * This component acts as a bridge between the Game Service and the remote RNG Service.
 * It abstracts the complexity of the gRPC network calls using a synchronous (blocking) stub.
 * <br>
 * <b>Main Responsibility:</b>
 * <ul>
 * <li><b>getNumbers(int count)</b>: Sends a request to the RNG service to generate a specific list
 * of random integers (e.g., [7, 2, 9]). These numbers determine the outcome of the slot machine spin.</li>
 * </ul>
 * </p>
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
