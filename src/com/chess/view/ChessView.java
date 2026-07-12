package com.chess.view;

import com.chess.board.Board;
import com.chess.enums.GameStatus;
import com.chess.enums.PieceColor;
import java.util.function.Consumer;

public interface ChessView {
    void displayBoard(Board board, PieceColor currentPlayer);
    void showMessage(String message);
    void showError(String error);
    void showGameOver(GameStatus status, PieceColor winner);
    void onMoveSubmitted(Consumer<String> moveConsumer);
    void startInputLoop();
}