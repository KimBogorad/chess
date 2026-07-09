package com.chess.view;

import com.chess.board.Board;
import com.chess.enums.GameStatus;
import com.chess.enums.PieceColor;

public interface ChessView {
    void displayBoard(Board board);
    void showMessage(String message);
    void showError(String error);
    void showGameOver(GameStatus status, PieceColor winner);
    String promptForMove(PieceColor currentPlayer);
}