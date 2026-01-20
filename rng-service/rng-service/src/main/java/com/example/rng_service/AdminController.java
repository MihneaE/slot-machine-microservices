package com.example.rng_service;

import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Administrative REST Controller for managing the RNG Service.
 * <p>
 * This controller exposes endpoints intended for <b>development, testing, and debugging purposes</b>.
 * It allows administrators or developers to manipulate the internal state of the Random Number Generator,
 * enabling the "Cheat Mode" functionality.
 * <br>
 * <b>Main Endpoint:</b>
 * <ul>
 * <li><b>POST /admin/rng/force</b>: Accepts a list of integers (e.g., [7, 7, 7]) and injects them
 * into the RNG service. The next gRPC request for numbers will return this exact sequence instead of
 * random values. This is crucial for verifying game logic like Jackpots without waiting for random chance.</li>
 * </ul>
 * </p>
 */

@RestController
@RequestMapping("/admin/rng")
public class AdminController {
    private final RngServiceImpl rngService;

    public AdminController(RngServiceImpl rngService) {
        this.rngService = rngService;
    }

    @PostMapping("/force")
    public String forceOutcome(@RequestBody List<Integer> numbers) {
        rngService.setForcedOutcome(numbers);
        return "OK. Urmatorul spin va fi fortat la: " + numbers;
    }
}
