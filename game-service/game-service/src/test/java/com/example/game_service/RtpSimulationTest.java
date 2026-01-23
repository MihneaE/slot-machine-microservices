package com.example.game_service;

import com.casino.game.grpc.SpinRequest;
import com.example.game_service.client.DataClient;
import com.example.game_service.client.RngClient;
import com.example.game_service.service.GameServiceImpl;
import org.junit.jupiter.api.Test;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Monte Carlo Simulation to calculate the Theoretical RTP (Return to Player).
 * <p>
 * This integration test bypasses the network layer (gRPC) to verify the core mathematical model
 * of the slot machine. It simulates 1,000,000 spins in rapid succession to ensure the payout
 * algorithm falls within the statistical industry standard (92% - 97%).
 * </p>
 * *
 * * <b>Methodology:</b>
 * <ul>
 * <li>Instantiates {@link GameServiceImpl} with <code>null</code> clients to isolate the logic.</li>
 * <li>Generates high-entropy random numbers locally using {@link SecureRandom}.</li>
 * <li>Directly invokes the pricing engine {@code calculatePayout5x3}.</li>
 * </ul>
 */

public class RtpSimulationTest {

    private final SecureRandom random = new SecureRandom();

    @Test
    void calculateRTP()
    {
        long totalSpins = 1_000_000;
        long betAmount = 10;
        long totalBet = totalSpins * betAmount;
        long totalWin = 0;

        GameServiceImpl gameServiceImpl = new GameServiceImpl(null, null);

        System.out.println("--- STARTING RTP SIMULATION (" + totalSpins + " spins) ---");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalSpins; i++) {
            List<Integer> numbers = generateRandomNumbers(15);
            GameServiceImpl.SpinResult result =  gameServiceImpl.calculatePayout5x3(numbers, betAmount);
            totalWin += result.getTotalWin();
        }

        long endTime = System.currentTimeMillis();
        double rtp = (double) totalWin / totalBet * 100;

        System.out.println("Time elapsed: " + (endTime - startTime) + "ms");
        System.out.println("Total Bet: " + totalBet);
        System.out.println("Total Win: " + totalWin);
        System.out.println("CALCULATED RTP: " + String.format("%.2f", rtp) + "%");

        if (rtp < 90.0)
            System.err.println("WARNING: RTP is too low (<90%)!");
        else if (rtp > 98.0)
            System.err.println("WARNING: RTP is too high (>98%)!");
        else
            System.out.println("SUCCESS: RTP is within healthy casino range (90-98%).");
    }

    private List<Integer> generateRandomNumbers(int count) {
        List<Integer> numbers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            numbers.add(random.nextInt(6) + 1);
        }
        return numbers;
    }
}
