package com.chess.moves;

import com.chess.board.Board;
import com.chess.board.Position;

public abstract class Move {
    protected Position previousEnPassantTarget;

    public void execute(Board board) {
        // Maintain the previous en passant target position for undo purposes
        this.previousEnPassantTarget = board.getEnPassantTarget();
        
        board.setEnPassantTarget(null);
        
        // we doExecute after setting to null so that a pawn that takes two steps 
        // in a standard move will be able to set it correctly without being overridden.
        doExecute(board);
    }

    public void undo(Board board) {
        doUndo(board);
        
        board.setEnPassantTarget(previousEnPassantTarget);
    }

    protected abstract void doExecute(Board board);
    protected abstract void doUndo(Board board);
}