# 🎰 Microservices Casino Slot Platform

A high-performance, distributed casino gaming platform built with **Java Spring Boot** and **gRPC**, featuring a 5x3 Video Slot game with real-time banking and cryptographically secure RNG.

![Game Screenshot](assets/game-screenshot.png)

## 🚀 Key Features

* **Microservices Architecture:** 4 distinct services (Gateway, Game, Data, RNG) communicating via fast gRPC protobufs.
* **5x3 Video Slot Engine:** Complex game logic with 5 paylines, wild symbols, and scatter bonuses.
* **Real-Time Gameplay:** WebSocket (STOMP) integration for instant spin results and balance updates.
* **ACID Transactions:** Financial data is handled transactionally in PostgreSQL to prevent double-spending or balance drift.
* **Fair Play (RTP):** Theoretical Return to Player is calibrated to **~96%** (Industry Standard), verified via Monte Carlo simulation tests.
* **Secure RNG:** Uses `SecureRandom` for entropy, with a "Cheat Mode" for admin testing/debugging.

## 🎥 Gameplay Demo

Watch the full gameplay flow including Login, Spinning, Winning, and Paytable inspection:

<video src="assets/game_video.mp4" controls="controls" width="100%"></video>

*(If the video doesn't play, file is located at `assets/game_video.mp4`)*

## 🛠 Tech Stack

* **Backend:** Java 17, Spring Boot 3.x
* **Communication:** gRPC (Inter-service), WebSocket/STOMP (Frontend-Backend), REST (Auth)
* **Database:** PostgreSQL
* **Frontend:** React (integrated via Gateway), HTML5, CSS3
* **DevOps:** Docker, Docker Compose

## 🏗 Architecture

The system follows a Hexagonal/Microservices architecture pattern:

![Architecture Diagram](assets/architecture-diagram.png)

1.  **Gateway Service:** Handles HTTP Auth & WebSockets. Translates frontend JSON to backend gRPC.
2.  **Game Service:** The "Brain". Orchestrates the spin, calculates winnings based on the math model.
3.  **Data Service:** The "Vault". Manages player accounts, balances, and transaction history (Audit Log).
4.  **RNG Service:** The "Dice". Provides cryptographically secure random numbers.

## 🎲 Math Model & RTP

The game operates on a **5-Reel x 3-Row** grid.
* **Total Paylines:** 5 (Horizontal + Diagonals)
* **Return to Player (RTP):** Calculated at **96.67%** based on 1 Million simulated spins.

**RTP Formula:**
> RTP = (Total Winnings / Total Bet Amount) * 100

*The RTP simulation test is included in the test suite (`RtpSimulationTest.java`).*

## ⚙️ Installation & Running

### Prerequisites
* Docker & Docker Compose
* Java 17 JDK (optional, for local dev)

### Quick Start (Docker)
1.  Clone the repository.
2.  Run the entire platform:
    ```bash
    docker-compose up --build
    ```
3.  Access the game at: `http://localhost:8080`

### Admin / Debugging
* **Force Win (Cheat Mode):** POST to `/admin/rng/force` with a specific matrix (e.g., `[7,7,7...]`) to test Jackpot animations.

## 📸 Screenshots

| Login Screen | Winning Spin | Paytable Info |
| :---: | :---: | :---: |
| ![Login](assets/login.png) | ![Win](assets/win.png) | ![Info](assets/info.png) |
