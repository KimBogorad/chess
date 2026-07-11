package com.chess.parser;

import java.util.Set;
import java.util.EnumSet;
import com.chess.board.Position;
import com.chess.enums.CastlingType;
import com.chess.enums.PieceType;
import com.chess.enums.MoveFlag;

// DTO for parsed chess move
public record ParsedIntent(
        PieceType pieceType,
        Position destination,
        Set<MoveFlag> flags,
        PieceType promotionPiece,
        Character fileDisambiguation,
        Integer rankDisambiguation,
        CastlingType castlingType
) {
    // Constructor for standard moves
    public ParsedIntent(PieceType pieceType, Position destination) {
        this(pieceType, destination, EnumSet.noneOf(MoveFlag.class), null, null, null, CastlingType.NONE);
    }

    // Constructor for castling moves
    public ParsedIntent(CastlingType castlingType) {
        this(null, null, EnumSet.noneOf(MoveFlag.class), null, null, null, castlingType);
    }

    public boolean isCastle() {
        return castlingType != CastlingType.NONE;
    }

    public boolean isKingsideCastle() {
        return castlingType == CastlingType.KINGSIDE;
    }

    @Override
    public String toString() {
        String toString = "Parsed Intent: {";
        toString += "Piece Type: " + pieceType; 
        toString += "\nDestination: " + destination;
        toString += "\nFlags: " + flags;
        toString += "\nPromotion Piece: " + promotionPiece;
        toString += "\nRank Disambiguation: " + rankDisambiguation;
        toString += "\nFile Disambiguation: " + fileDisambiguation;
        toString += "\nCastling Type: " + castlingType + "}";

        return toString;
    }
}