package com.chess.pieces;

import java.util.ArrayList;
import java.util.List;
import com.chess.enums.PieceColor;
import com.chess.board.Position;

public class Rook extends GamePiece {

    public Rook(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public List<List<Position>> getMoveRays() {
        List<List<Position>> allRays = new ArrayList<>();

        allRays.add(generateRay(1,0));
        allRays.add(generateRay(-1,0));
        allRays.add(generateRay(0,1));
        allRays.add(generateRay(0,-1));

        return allRays;
    }
}