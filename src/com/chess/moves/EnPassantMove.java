package com.chess.moves;

import com.chess.board.Board;
import com.chess.board.Position;
import com.chess.pieces.GamePiece;

public class EnPassantMove extends Move {
    private final GamePiece pawn;
    private final Position destination;
    private final Position startPosition;
    
    private final GamePiece capturedPawn;
    private final Position capturedPawnPosition;

    public EnPassantMove(GamePiece pawn, Position destination, GamePiece capturedPawn) {
        this.pawn = pawn;
        this.destination = destination;
        this.startPosition = pawn.getPosition();
        this.capturedPawn = capturedPawn;
        this.capturedPawnPosition = capturedPawn.getPosition(); // Captured pawn won't be in destination
    }

    @Override
    public void doExecute(Board board) {
        // Move capturing pawn
        board.setPieceAt(startPosition, null);
        board.setPieceAt(destination, pawn);
        pawn.setPosition(destination);
        
        // Remove captured pawn
        board.setPieceAt(capturedPawnPosition, null);
    }

    @Override
    public void doUndo(Board board) {
        board.setPieceAt(destination, null);
        board.setPieceAt(startPosition, pawn);
        pawn.setPosition(startPosition);
        
        board.setPieceAt(capturedPawnPosition, capturedPawn);
    }
}