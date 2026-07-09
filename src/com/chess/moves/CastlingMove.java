package com.chess.moves;

import com.chess.board.Board;
import com.chess.board.Position;
import com.chess.pieces.GamePiece;

public class CastlingMove extends Move {
    private final GamePiece king;
    private final GamePiece rook;
    private final Position kingStart, kingEnd;
    private final Position rookStart, rookEnd;
    
    private final boolean kingWasFirstMove;
    private final boolean rookWasFirstMove;

    public CastlingMove(GamePiece king, Position kingEnd, GamePiece rook, Position rookEnd) {
        this.king = king;
        this.rook = rook;
        this.kingStart = king.getPosition();
        this.rookStart = rook.getPosition();
        this.kingEnd = kingEnd;
        this.rookEnd = rookEnd;
        
        this.kingWasFirstMove = !king.hasMoved();
        this.rookWasFirstMove = !rook.hasMoved();
    }

    @Override
    public void doExecute(Board board) {

        // Move the King
        board.setPieceAt(kingStart, null);
        board.setPieceAt(kingEnd, king);
        king.setPosition(kingEnd);
        king.setHasMoved(true);

        // Move the rook
        board.setPieceAt(rookStart, null);
        board.setPieceAt(rookEnd, rook);
        rook.setPosition(rookEnd);
        rook.setHasMoved(true);
    }

    @Override
    public void doUndo(Board board) {
        board.setPieceAt(kingEnd, null);
        board.setPieceAt(kingStart, king);
        king.setPosition(kingStart);
        if (kingWasFirstMove) king.setHasMoved(false);

        board.setPieceAt(rookEnd, null);
        board.setPieceAt(rookStart, rook);
        rook.setPosition(rookStart);
        if (rookWasFirstMove) rook.setHasMoved(false);
    }
}