package com.chess.board;

import java.util.ArrayList;
import java.util.List;

import com.chess.pieces.*;
import com.chess.enums.PieceColor;

public class Board {
    private GamePiece[][] grid;
    private Position enPassantTarget = null;

    public Board() {
        // initialize the board
        this.grid = new GamePiece[8][8];
        setup();
    }

    public void setup() {
        setupPieces(PieceColor.WHITE);
        setupPieces(PieceColor.BLACK);
    }

    public void setupPieces(PieceColor color) {
        int mainRow = (color == PieceColor.WHITE) ? 7 : 0;
        int pawnRow = (color == PieceColor.WHITE) ? 6 : 1;

        // Place Rooks
        grid[mainRow][0] = new Rook(color, new Position(mainRow, 0));
        grid[mainRow][7] = new Rook(color, new Position(mainRow, 7));

        // Place Knights
        grid[mainRow][1] = new Knight(color, new Position(mainRow, 1));
        grid[mainRow][6] = new Knight(color, new Position(mainRow, 6));

        // Place Bishops
        grid[mainRow][2] = new Bishop(color, new Position(mainRow, 2));
        grid[mainRow][5] = new Bishop(color, new Position(mainRow, 5));

        // Place Queen
        grid[mainRow][3] = new Queen(color, new Position(mainRow, 3));

        // Place King
        grid[mainRow][4] = new King(color, new Position(mainRow, 4));

        // Place Pawns
        for (int col = 0; col < 8; col++) {
            grid[pawnRow][col] = new Pawn(color, new Position(pawnRow, col));
        }
    }

    public void setupPiecesForTestPromotion() {
        // need to have kings to avoid stalemate
        Position whiteKingPos = new Position(7, 4);
        Position blackKingPos = new Position(0, 2);

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        grid[6][4] = new Pawn(PieceColor.WHITE, new Position(6, 4));
    }

    public void setupPiecesForTestEnPassant() {
        // need to have kings to avoid stalemate
        Position whiteKingPos = new Position(7, 4);
        Position blackKingPos = new Position(0, 2);

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Place opposing pawns to test en passant
        grid[4][4] = new Pawn(PieceColor.WHITE, new Position(4, 4));
        grid[1][3] = new Pawn(PieceColor.BLACK, new Position(1,3));
    }

    public void setupPiecesForTestCastling() {
        // Place both kings
        Position whiteKingPos = new Position(7, 4);
        Position blackKingPos = new Position(0, 4);

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Place White Rooks
        grid[7][0] = new Rook(PieceColor.WHITE, new Position(7, 0));
        grid[7][7] = new Rook(PieceColor.WHITE, new Position(7, 7));

        // Place Black Rooks
        grid[0][0] = new Rook(PieceColor.BLACK, new Position(0, 0));
        grid[0][7] = new Rook(PieceColor.BLACK, new Position(0, 7));

        // Place black and white Queens for testing castling under check
        grid[4][5] = new Queen(PieceColor.WHITE, new Position(4, 5));
        grid[4][4] = new Queen(PieceColor.BLACK, new Position(4,4));
    }

    public void setupPiecesForTestMate() {
        // need to have kings to avoid stalemate
        Position whiteKingPos = new Position(7, 4);
        Position blackKingPos = new Position(0, 2);

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Two white rooks for quick line-by-line mate
        grid[3][0] = new Rook(PieceColor.WHITE, new Position(3, 0));
        grid[2][7] = new Rook(PieceColor.WHITE, new Position(2, 7));
    }

    public void setupPiecesForTestStalemate() {
        // need to have kings to avoid stalemate
        Position whiteKingPos = new Position(7, 4);
        Position blackKingPos = new Position(0, 2);

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        
    }

    public GamePiece getPieceAt(Position pos) {
        return grid[pos.row()][pos.col()];
    }

    public void setPieceAt(Position pos, GamePiece piece) {
        grid[pos.row()][pos.col()] = piece;
    }

    // Get valid moves for a piece, considering the current board state (occupied squares)
    public List<Position> getValidMovesForPiece(GamePiece piece) {
        List<Position> validMoves = new ArrayList<>();

        // Handle walk rays
        for (List<Position> ray : piece.getWalkRays()) {
            for (Position pos : ray) {
                GamePiece pieceAtDest = getPieceAt(pos);
                
                if (pieceAtDest == null) {
                    // valid move and need to keep moving
                    validMoves.add(pos);
                } else {
                    // ran into a piece, no more valid moves in this ray
                    break; 
                }
            }
        }

        // Handle capture rays
        for (List<Position> ray : piece.getCaptureRays()) {
            for (Position pos : ray) {
                GamePiece pieceAtDest = getPieceAt(pos);
                
                if (pieceAtDest == null) {
                    // the only legal capture move to a clear square is en passant:
                    if (pos.equals(enPassantTarget)) {
                        validMoves.add(pos);
                        break;
                    } else {
                    // no need to add this position, because we added it as valid walking path,
                    // can keep going to find possible caprture target
                    continue;
                    }
                } else {
                    // found a piece, check if it's an enemy piece
                    if (pieceAtDest.getColor() != piece.getColor()) {
                        // can capture
                        validMoves.add(pos);
                    }
                    // either way, we can't go further in this ray
                    break;
                }
            }
        }

        return validMoves;
    }

    public Position getEnPassantTarget() { return this.enPassantTarget; }

    public void setEnPassantTarget(Position enPassantTarget) { 
        this.enPassantTarget = enPassantTarget;
    }
}