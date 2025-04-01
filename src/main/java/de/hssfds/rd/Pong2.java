package de.hssfds.rd;

import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.raymath.Vector2;

import static com.raylib.java.core.input.Keyboard.*;

public class Pong2 {

    public static void main(String[] args) {
        // Initialization
        //---------------------------------------------------------
        final int screenWidth = 800;
        final int screenHeight = 450;
        Raylib rlj = new Raylib();

        rlj.core.InitWindow(screenWidth, screenHeight, "Pong Game");

        // Ball initialization
        Vector2 ballPosition = new Vector2((float) rlj.core.GetScreenWidth() / 2,
                (float) rlj.core.GetScreenHeight() / 2);
        Vector2 ballSpeed = new Vector2(5.0f, 4.0f);
        int ballRadius = 10;

        // Paddle initialization
        final int paddleWidth = 15;
        final int paddleHeight = 80;
        final float paddleSpeed = 6.0f;

        // Left paddle (Player 1)
        Vector2 leftPaddlePosition = new Vector2(50, (float) screenHeight / 2 - paddleHeight / 2);

        // Right paddle (Player 2)
        Vector2 rightPaddlePosition = new Vector2(screenWidth - 50 - paddleWidth,
                (float) screenHeight / 2 - paddleHeight / 2);

        // Score variables
        int player1Score = 0;
        int player2Score = 0;

        boolean pause = false;
        boolean gameOver = false;
        int framesCounter = 0;
        final int winScore = 5; // Score needed to win the game

        rlj.core.SetTargetFPS(60);               // Set our game to run at 60 frames-per-second
        //----------------------------------------------------------

        // Main game loop
        while (!rlj.core.WindowShouldClose()) {    // Detect window close button or ESC key

            // Update
            //-----------------------------------------------------
            // Toggle pause
            if (rlj.core.IsKeyPressed(KEY_SPACE)) {
                pause = !pause;
            }

            // Reset game
            if (rlj.core.IsKeyPressed(KEY_R) || gameOver && rlj.core.IsKeyPressed(KEY_ENTER)) {
                // Reset ball position
                ballPosition.x = screenWidth / 2;
                ballPosition.y = screenHeight / 2;

                // Reset paddle positions
                leftPaddlePosition.y = screenHeight / 2 - paddleHeight / 2;
                rightPaddlePosition.y = screenHeight / 2 - paddleHeight / 2;

                // Reset game state
                player1Score = 0;
                player2Score = 0;
                gameOver = false;

                // Randomize ball direction
                if (rlj.core.GetRandomValue(0, 1) == 0) {
                    ballSpeed.x = 5.0f;
                } else {
                    ballSpeed.x = -5.0f;
                }
                ballSpeed.y = (float) (rlj.core.GetRandomValue(-3, 3) + rlj.core.GetRandomValue(2, 5));
            }

            if (!pause && !gameOver) {
                // Move paddles
                // Left paddle (Player 1) - W and S keys
                if (rlj.core.IsKeyDown(KEY_W)) {
                    leftPaddlePosition.y -= paddleSpeed;
                }
                if (rlj.core.IsKeyDown(KEY_S)) {
                    leftPaddlePosition.y += paddleSpeed;
                }

                // Right paddle (Player 2) - Up and Down arrow keys
                if (rlj.core.IsKeyDown(KEY_UP)) {
                    rightPaddlePosition.y -= paddleSpeed;
                }
                if (rlj.core.IsKeyDown(KEY_DOWN)) {
                    rightPaddlePosition.y += paddleSpeed;
                }

                // Make sure paddles don't go out of the screen
                if (leftPaddlePosition.y < 0) leftPaddlePosition.y = 0;
                if (leftPaddlePosition.y > screenHeight - paddleHeight) leftPaddlePosition.y = screenHeight - paddleHeight;
                if (rightPaddlePosition.y < 0) rightPaddlePosition.y = 0;
                if (rightPaddlePosition.y > screenHeight - paddleHeight) rightPaddlePosition.y = screenHeight - paddleHeight;

                // Update ball position
                ballPosition.x += ballSpeed.x;
                ballPosition.y += ballSpeed.y;

                // Check walls collision for bouncing (top and bottom)
                if ((ballPosition.y >= (screenHeight - ballRadius)) || (ballPosition.y <= ballRadius)) {
                    ballSpeed.y *= -1.0f;
                }

                // Left paddle collision
                if (ballPosition.x - ballRadius <= leftPaddlePosition.x + paddleWidth &&
                        ballPosition.y >= leftPaddlePosition.y &&
                        ballPosition.y <= leftPaddlePosition.y + paddleHeight &&
                        ballSpeed.x < 0) {

                    ballSpeed.x *= -1.1f; // Increase speed slightly with each hit

                    // Add some variation to the y speed based on where the ball hit the paddle
                    float hitPosition = (ballPosition.y - leftPaddlePosition.y) / paddleHeight;
                    ballSpeed.y = (hitPosition - 0.5f) * 10.0f;
                }

                // Right paddle collision
                if (ballPosition.x + ballRadius >= rightPaddlePosition.x &&
                        ballPosition.y >= rightPaddlePosition.y &&
                        ballPosition.y <= rightPaddlePosition.y + paddleHeight &&
                        ballSpeed.x > 0) {

                    ballSpeed.x *= -1.1f; // Increase speed slightly with each hit

                    // Add some variation to the y speed based on where the ball hit the paddle
                    float hitPosition = (ballPosition.y - rightPaddlePosition.y) / paddleHeight;
                    ballSpeed.y = (hitPosition - 0.5f) * 10.0f;
                }

                // Check scoring (ball goes beyond left or right edge)
                if (ballPosition.x < 0) {
                    // Player 2 scores
                    player2Score++;

                    // Reset ball position
                    ballPosition.x = screenWidth / 2;
                    ballPosition.y = screenHeight / 2;

                    // Reset ball speed (going towards the scoring player)
                    ballSpeed.x = 5.0f;
                    ballSpeed.y = (float) rlj.core.GetRandomValue(-3, 3);
                }

                if (ballPosition.x > screenWidth) {
                    // Player 1 scores
                    player1Score++;

                    // Reset ball position
                    ballPosition.x = screenWidth / 2;
                    ballPosition.y = screenHeight / 2;

                    // Reset ball speed (going towards the scoring player)
                    ballSpeed.x = -5.0f;
                    ballSpeed.y = (float) rlj.core.GetRandomValue(-3, 3);
                }

                // Check for win condition
                if (player1Score >= winScore || player2Score >= winScore) {
                    gameOver = true;
                }
            } else {
                framesCounter++;
            }
            //-----------------------------------------------------

            // Draw
            //-----------------------------------------------------
            rlj.core.BeginDrawing();

            rlj.core.ClearBackground(Color.BLACK);

            if (!gameOver) {
                // Draw ball
                rlj.shapes.DrawCircleV(ballPosition, ballRadius, Color.WHITE);

                // Draw paddles
                rlj.shapes.DrawRectangleV(leftPaddlePosition, new Vector2(paddleWidth, paddleHeight), Color.WHITE);
                rlj.shapes.DrawRectangleV(rightPaddlePosition, new Vector2(paddleWidth, paddleHeight), Color.WHITE);

                // Draw center line
                for (int i = 0; i < screenHeight; i += 10) {
                    rlj.shapes.DrawRectangle(screenWidth / 2 - 2, i, 4, 5, Color.WHITE);
                }

                // Draw scores
                rlj.text.DrawText(String.valueOf(player1Score), screenWidth / 4, 20, 40, Color.WHITE);
                rlj.text.DrawText(String.valueOf(player2Score), 3 * screenWidth / 4, 20, 40, Color.WHITE);

                // Draw controls
                rlj.text.DrawText("PLAYER 1: W/S", 10, screenHeight - 40, 20, Color.LIGHTGRAY);
                rlj.text.DrawText("PLAYER 2: UP/DOWN", screenWidth - 220, screenHeight - 40, 20, Color.LIGHTGRAY);
                rlj.text.DrawText("PRESS SPACE to PAUSE", 10, screenHeight - 20, 20, Color.LIGHTGRAY);
                rlj.text.DrawText("PRESS R to RESET", screenWidth - 220, screenHeight - 20, 20, Color.LIGHTGRAY);
            } else {
                // Game over screen
                String winnerText = (player1Score >= winScore) ? "PLAYER 1 WINS!" : "PLAYER 2 WINS!";
                rlj.text.DrawText(winnerText, screenWidth / 2 - rlj.text.MeasureText(winnerText, 40) / 2,
                        screenHeight / 2 - 40, 40, Color.WHITE);
                rlj.text.DrawText("PRESS ENTER TO PLAY AGAIN", screenWidth / 2 -
                        rlj.text.MeasureText("PRESS ENTER TO PLAY AGAIN", 20) / 2, screenHeight / 2 + 20, 20, Color.WHITE);
            }

            // On pause, we draw a blinking message
            if (pause && ((framesCounter / 30) % 2) == 0) {
                rlj.text.DrawText("PAUSED", screenWidth / 2 - rlj.text.MeasureText("PAUSED", 30) / 2,
                        screenHeight / 2, 30, Color.WHITE);
            }

            rlj.text.DrawFPS(10, 10);

            rlj.core.EndDrawing();
            //-----------------------------------------------------
        }

        // De-Initialization
        rlj.core.CloseWindow();
    }
}