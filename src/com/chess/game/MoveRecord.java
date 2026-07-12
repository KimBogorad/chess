package com.chess.game;

import com.chess.board.Position;
import com.chess.enums.PieceColor;
import com.chess.pieces.GamePiece;

public class MoveRecord {
    private final PieceColor player;
    private final GamePiece movedPiece;
    private final Position from;
    private final Position to;
    private final GamePiece capturedPiece;
    private final String san;

    public MoveRecord(PieceColor player, GamePiece movedPiece, Position from, Position to, GamePiece capturedPiece, String san) {
        this.player = player;
        this.movedPiece = movedPiece;
        this.from = from;
        this.to = to;
        this.capturedPiece = capturedPiece;
        this.san = san;
    }

    public String getSan() {
        return san;
    }

    public PieceColor getPlayer() {
        return player;
    }
}