package com.chess.pieces;

import java.util.List;
import java.util.ArrayList;
import com.chess.enums.*;
import com.chess.board.Position;

public class Knight extends GamePiece {
    public Knight(PieceColor color, Position position) {
        super(color, position, PieceType.KNIGHT);
    }

    @Override
    public List<List<Position>> getMoveRays() {
    
        List<List<Position>> legalMoves = new ArrayList<>();
        legalMoves.add(generateMove(2, 1));
        legalMoves.add(generateMove(2, -1));
        legalMoves.add(generateMove(-2, 1));
        legalMoves.add(generateMove(-2, -1));
        legalMoves.add(generateMove(1, 2));
        legalMoves.add(generateMove(1, -2));
        legalMoves.add(generateMove(-1, 2));
        legalMoves.add(generateMove(-1, -2));

        return legalMoves;
    }
}