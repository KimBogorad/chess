package com.chess.pieces;

import java.util.List;
import java.util.ArrayList;
import com.chess.enums.PieceColor;
import com.chess.board.Position;

public class King extends GamePiece {
    public King(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public List<List<Position>> getMoveRays() {
        List<List<Position>> legalMoves = new ArrayList<>();
        legalMoves.add(generateMove(1, 0));   // Down
        legalMoves.add(generateMove(-1, 0));  // Up
        legalMoves.add(generateMove(0, 1));   // Right
        legalMoves.add(generateMove(0, -1));  // Left
        legalMoves.add(generateMove(1, 1));   // Down-Right
        legalMoves.add(generateMove(1, -1));  // Down-Left
        legalMoves.add(generateMove(-1, 1));  // Up-Right
        legalMoves.add(generateMove(-1, -1)); // Up-Left

        return legalMoves;
    }
}
