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
        //setupPiecesForTestPromotion();
        //setupPiecesForTestEnPassant();
        //setupPiecesForTestCastling();
        //setupPiecesForTestMate();
        //setupPiecesForTestStalemate();
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

        // Place pawn to promote
        Position pawn = new Position(6,4);

        grid[pawn.row()][pawn.col()] = new Pawn(PieceColor.WHITE, pawn);
    }

    public void setupPiecesForTestEnPassant() {
        // need to have kings to avoid stalemate
        Position whiteKingPos = new Position(7, 4);
        Position blackKingPos = new Position(0, 2);

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Place opposing pawns to test en passant
        Position whitePawn = new Position(4,4);
        Position blackPawn = new Position(1, 3);

        grid[whitePawn.row()][whitePawn.col()] = new Pawn(PieceColor.WHITE, whitePawn);
        grid[blackPawn.row()][blackPawn.col()] = new Pawn(PieceColor.BLACK, blackPawn);
    }

    public void setupPiecesForTestCastling() {
        // Place both kings
        Position whiteKingPos = new Position(7, 4);
        Position blackKingPos = new Position(0, 4);

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Place White Rooks

        Position whiteRookA = new Position(7, 0);
        Position whiteRookB = new Position(7,7);

        grid[whiteRookA.row()][whiteRookA.col()] = new Rook(PieceColor.WHITE, whiteRookA);
        grid[whiteRookB.row()][whiteRookB.col()] = new Rook(PieceColor.WHITE, whiteRookB);

        // Place Black Rooks
        Position blackRookA = new Position(0, 0);
        Position blackRookB = new Position(0,7);

        grid[blackRookA.row()][blackRookA.col()] = new Rook(PieceColor.BLACK, blackRookA);
        grid[blackRookB.row()][blackRookB.col()] = new Rook(PieceColor.BLACK, blackRookB);

        // Place black and white Queens for testing castling under check
        Position whiteQueen = new Position(4, 5);
        Position blackQueen = new Position(4,4);

        grid[whiteQueen.row()][whiteQueen.col()] = new Queen(PieceColor.WHITE, whiteQueen);
        grid[blackQueen.row()][blackQueen.col()] = new Queen(PieceColor.BLACK, blackQueen);
    }

    public void setupPiecesForTestMate() {
        // need to have kings to avoid stalemate
        Position whiteKingPos = new Position(7, 4);
        Position blackKingPos = new Position(0, 2);

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Two white rooks for quick line-by-line mate
        Position rookA = new Position(3, 0);
        Position rookB = new Position(2, 7);
        
        grid[rookA.row()][rookA.col()] = new Rook(PieceColor.WHITE, rookA);
        grid[rookB.row()][rookB.col()] = new Rook(PieceColor.WHITE, rookB);
    }

    public void setupPiecesForTestStalemate() {
        // Position both Kings
        Position whiteKingPos = new Position(7, 4);
        Position blackKingPos = new Position(0, 0);

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Position 2 white rooks to create stalemate
        Position rookA = new Position(7, 2);
        Position rookB = new Position(1, 7);

        grid[rookA.row()][rookA.col()] = new Rook(PieceColor.WHITE, rookA);
        grid[rookB.row()][rookB.col()] = new Rook(PieceColor.WHITE, rookB);
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