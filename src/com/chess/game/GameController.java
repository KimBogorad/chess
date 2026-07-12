package com.chess.game;

import com.chess.enums.GameStatus;
import com.chess.enums.PieceColor;
import com.chess.parser.ParsedIntent;
import com.chess.parser.Parser;
import com.chess.view.ChessView;

public class GameController {
    private final Game game;
    private final ChessView view;
    private final Parser parser;

    // Dependency Injection 
    public GameController(Game game, ChessView view, Parser parser) {
        this.game = game;
        this.view = view;
        this.parser = parser;
    }

    public void start() {
        view.showMessage("Welcome to Java Chess!");

        view.displayBoard(game.getBoard(), game.getCurrentPlayer());

        view.onMoveSubmitted(this::handleUserMove);

        view.startInputLoop();
    }

    private void handleUserMove(String input) {
        if (game.getGameStatus() != GameStatus.ACTIVE) {
            return;
        }
        try {
                // 1. Using parser to parse input into a playable move
                ParsedIntent intent = parser.parse(input);

                // 2. Send the move to Game engine - check move validity and move if possible
                game.playTurn(intent);

            
                // 3. Display updated board after successful move
                view.displayBoard(game.getBoard(), game.getCurrentPlayer());
                
                // 4. After every move, check if game is over for any reason
                if (game.getGameStatus() != GameStatus.ACTIVE) {
                    PieceColor winner = (game.getCurrentPlayer() == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
                    view.showGameOver(game.getGameStatus(), winner);
                }
                

            } catch (IllegalArgumentException e) {
                view.showError("Invalid input: " + e.getMessage() + " Please try again.");
            }
    }
}