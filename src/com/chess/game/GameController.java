package com.chess.game; // או chess.controller, תלוי איך סידרת

import com.chess.board.Board;
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
        view.displayBoard(game.getBoard());

        while (game.getGameStatus() == GameStatus.ACTIVE) {
            // 1. Using view for any output related issues to support modularity and separation of concerns
            String input = view.promptForMove(game.getCurrentPlayer());

            try {
                // 2. Using parser to parse input into a playable move
                ParsedIntent intent = parser.parse(input);

                // 3. Send the move to Game engine - check move validity and move if possible
                boolean success = game.playTurn(intent);

                if (success) {
                    // 4. Display updated board after successful move
                    view.displayBoard(game.getBoard());
                    
                    // 5. After every move, check if game is over for any reason
                    if (game.getGameStatus() != GameStatus.ACTIVE) {
                        PieceColor winner = (game.getCurrentPlayer() == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
                        view.showGameOver(game.getGameStatus(), winner);
                    }
                } else {
                    view.showError("Illegal move! Either geometrically impossible or leaves King in check. Try again.");
                }

            } catch (IllegalArgumentException e) {
                view.showError("Invalid input: " + e.getMessage() + ". Please try again.");
            }
        }
    }
}