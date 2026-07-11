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
        //setupPieces(PieceColor.WHITE);
        //setupPieces(PieceColor.BLACK);
        //setupPiecesForTestPromotion();
        //setupPiecesForTestEnPassant();
        //setupPiecesForTestCastling();
        //setupPiecesForTestMate();
        //setupPiecesForTestStalemate();
        //setupPiecesForTestEnPassantPin();
        //setupPiecesForTestMovementAlongPin();
        //setupPiecesForTestDoubleCheck();
        //setupPiecesForTestCompoundPromotion();
        setupPiecesForTestDisambiguationVsPin();
        //setupPiecesForTestInsufficientMaterial();
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

    public void setupPiecesForTestEnPassantPin() {
        // Position both Kings
        Position whiteKingPos = new Position(3, 0); // a5
        Position blackKingPos = new Position(0, 4); // e8

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Setup the en passant pin scenario on the 5th rank
        Position whitePawn = new Position(3, 1); // b5
        Position blackPawn = new Position(1, 2); // c7 (will move to c5)
        Position blackRook = new Position(3, 7); // h5 (aimed at the white king on a5)

        grid[whitePawn.row()][whitePawn.col()] = new Pawn(PieceColor.WHITE, whitePawn);
        grid[blackPawn.row()][blackPawn.col()] = new Pawn(PieceColor.BLACK, blackPawn);
        grid[blackRook.row()][blackRook.col()] = new Rook(PieceColor.BLACK, blackRook);
    }

    public void setupPiecesForTestMovementAlongPin() {
        // Position both Kings
        Position whiteKingPos = new Position(7, 4); // e1
        Position blackKingPos = new Position(0, 0); // a8

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Place White Rook pinned to the White King by a Black Rook on the E file
        Position whiteRook = new Position(5, 4); // e3
        Position blackRook = new Position(0, 4); // e8

        grid[whiteRook.row()][whiteRook.col()] = new Rook(PieceColor.WHITE, whiteRook);
        grid[blackRook.row()][blackRook.col()] = new Rook(PieceColor.BLACK, blackRook);
    }

    public void setupPiecesForTestDoubleCheck() {
        // Position both Kings
        Position whiteKingPos = new Position(7, 4); // e1
        Position blackKingPos = new Position(0, 0); // a8

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Place two Black pieces directly attacking the White King
        Position blackRook = new Position(0, 4); // e8 (vertical check)
        Position blackKnight = new Position(5, 5); // f3 (knight check)

        grid[blackRook.row()][blackRook.col()] = new Rook(PieceColor.BLACK, blackRook);
        grid[blackKnight.row()][blackKnight.col()] = new Knight(PieceColor.BLACK, blackKnight);

        // Place a white rook to attempt (and hopefully fail) to take one of the checking pieces
        Position whiteRook = new Position(0, 7);

        grid[whiteRook.row()][whiteRook.col()] = new Rook(PieceColor.WHITE, whiteRook);
    }

    public void setupPiecesForTestCompoundPromotion() {
        // Position both Kings
        Position whiteKingPos = new Position(7, 7); // h1
        Position blackKingPos = new Position(0, 4); // e8

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Place White pawn ready to promote and capture, delivering check
        Position whitePawn = new Position(1, 4); // e7
        Position blackRook = new Position(0, 3); // d8

        grid[whitePawn.row()][whitePawn.col()] = new Pawn(PieceColor.WHITE, whitePawn);
        grid[blackRook.row()][blackRook.col()] = new Rook(PieceColor.BLACK, blackRook);
    }

    public void setupPiecesForTestDisambiguationVsPin() {
        // White King on a3 (row 5, col 0)
        Position whiteKingPos = new Position(5, 0); 
        Position blackKingPos = new Position(0, 0);

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Knight A on b3 (row 5, col 1) - PINNED!
        // Knight B on f5 (row 3, col 5) - FREE!
        // Both can geometrically jump to d4 (row 4, col 3).
        Position whiteKnightA = new Position(5, 1); 
        Position whiteKnightB = new Position(3, 5); 
        
        // Black Rook on h3 (row 5, col 7) pinning Knight A to the King on a3
        Position blackRook = new Position(5, 7); 

        grid[whiteKnightA.row()][whiteKnightA.col()] = new Knight(PieceColor.WHITE, whiteKnightA);
        grid[whiteKnightB.row()][whiteKnightB.col()] = new Knight(PieceColor.WHITE, whiteKnightB);
        grid[blackRook.row()][blackRook.col()] = new Rook(PieceColor.BLACK, blackRook);
    }

    public void setupPiecesForTestInsufficientMaterial() {
        // Position both Kings
        Position whiteKingPos = new Position(7, 4);
        Position blackKingPos = new Position(0, 4);

        grid[whiteKingPos.row()][whiteKingPos.col()] = new King(PieceColor.WHITE, whiteKingPos);
        grid[blackKingPos.row()][blackKingPos.col()] = new King(PieceColor.BLACK, blackKingPos);

        // Place just one knight for White (King + Knight vs King = Draw)
        Position whiteKnight = new Position(4, 4);

        grid[whiteKnight.row()][whiteKnight.col()] = new Knight(PieceColor.WHITE, whiteKnight);
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