package com.chess.moves;

import com.chess.board.Board;
import com.chess.board.Position;
import com.chess.pieces.GamePiece;

public class PromotionMove extends Move {
    private final GamePiece pawn;
    private final Position destination;
    private final Position startPosition;
    private final GamePiece promotedPiece; 
    
    private GamePiece capturedPiece;

    public PromotionMove(GamePiece pawn, Position destination, GamePiece promotedPiece) {
        this.pawn = pawn;
        this.destination = destination;
        this.startPosition = pawn.getPosition();
        this.promotedPiece = promotedPiece;
    }

    @Override
    public void doExecute(Board board) {
        this.capturedPiece = board.getPieceAt(destination);
        
        board.setPieceAt(startPosition, null);
        
        // Place the promoted piece at destination instead of the pawn
        board.setPieceAt(destination, promotedPiece);
    }

    @Override
    public void doUndo(Board board) {
        board.setPieceAt(destination, capturedPiece);
        board.setPieceAt(startPosition, pawn);
        pawn.setPosition(startPosition);
    }
}