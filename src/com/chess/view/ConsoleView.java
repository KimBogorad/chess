package com.chess.view;

import com.chess.board.Board;
import com.chess.board.Position;
import com.chess.enums.GameStatus;
import com.chess.enums.PieceColor;
import com.chess.pieces.GamePiece;

import java.util.Scanner;

public class ConsoleView implements ChessView {
    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayBoard(Board board) {
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_WHITE_PIECE = "\u001B[97m"; 
        final String ANSI_BLACK_PIECE = "\u001B[34m"; 
        final String ANSI_BG_WHITE_SQUARE = "\u001B[47m"; 
        final String ANSI_BG_BLACK_SQUARE = "\u001B[100m"; 

        System.out.println("\n   a  b  c  d  e  f  g  h"); 

        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " "); 

            for (int col = 0; col < 8; col++) {
                GamePiece piece = board.getPieceAt(new Position(row, col));
                boolean isWhiteSquare = (row + col) % 2 == 0;
                String bgCode = isWhiteSquare ? ANSI_BG_WHITE_SQUARE : ANSI_BG_BLACK_SQUARE;

                if (piece == null) {
                    System.out.print(bgCode + "   " + ANSI_RESET);
                } else {
                    String colorCode = (piece.getColor() == PieceColor.WHITE) ? ANSI_WHITE_PIECE : ANSI_BLACK_PIECE;
                    String pieceSymbol = getPieceSymbol(piece);
                    System.out.print(bgCode + colorCode + " " + pieceSymbol + " " + ANSI_RESET);
                }
            }
            System.out.println(" " + (8 - row)); 
        }
        System.out.println("   a  b  c  d  e  f  g  h\n");
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void showError(String error) {
        System.out.println("Error: " + error);
    }

    @Override
    public void showGameOver(GameStatus status, PieceColor winner) {
        System.out.println("\n==================================");
        if (status == GameStatus.MATE) {
            System.out.println("CHECKMATE! Congratulations to " + winner + "!");
        } else if (status == GameStatus.STALEMATE) {
            System.out.println("STALEMATE! The game ends in a draw.");
        }
        System.out.println("==================================\n");
    }

    @Override
    public String promptForMove(PieceColor currentPlayer) {
        System.out.print(currentPlayer + "'s turn. Enter move (e.g., e4, Nf3, O-O): ");
        return scanner.nextLine().trim();
    }

    // Helper method to help draw the board
    private String getPieceSymbol(GamePiece piece) {
        return switch (piece.getPieceType()) {
            case PAWN -> "P";
            case KNIGHT -> "N";
            case BISHOP -> "B";
            case ROOK -> "R";
            case QUEEN -> "Q";
            case KING -> "K";
        };
    }
}