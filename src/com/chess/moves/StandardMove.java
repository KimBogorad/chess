package com.chess.moves;

import com.chess.board.Board;
import com.chess.board.Position;
import com.chess.enums.PieceType;
import com.chess.pieces.GamePiece;

public class StandardMove extends Move {
    private final GamePiece piece;
    private final Position destination;
    private final Position startPosition;
    
    private GamePiece capturedPiece; 
    private final boolean wasFirstMove; 

    public StandardMove(GamePiece piece, Position destination) {
        this.piece = piece;
        this.destination = destination;
        this.startPosition = piece.getPosition();
        this.wasFirstMove = !piece.hasMoved(); 
    }

    @Override
    public void doExecute(Board board) {
        this.capturedPiece = board.getPieceAt(destination); 
        
        board.setPieceAt(startPosition, null);
        board.setPieceAt(destination, piece);
        piece.setPosition(destination);
        piece.setHasMoved(true); 

        if (piece.getPieceType() == PieceType.PAWN && Math.abs(startPosition.row() - destination.row()) == 2) {
            // If a pawn skipped a step, the step they skipped is now a valid enPassantTarget. Pass that to the board.
            int skippedRow = (startPosition.row() + destination.row()) / 2;
            Position target = new Position(skippedRow, startPosition.col());
            board.setEnPassantTarget(target);
        } // else : already set to null by Move.execute()
    }

    @Override
    public void doUndo(Board board) {
        board.setPieceAt(startPosition, piece);
        board.setPieceAt(destination, capturedPiece);
        piece.setPosition(startPosition);
        
        if (wasFirstMove) {
            piece.setHasMoved(false);
        }
    }
}