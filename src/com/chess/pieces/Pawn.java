package com.chess.pieces;

import java.util.List;
import java.util.ArrayList;
import com.chess.enums.*;
import com.chess.board.Position;

public class Pawn extends GamePiece {

    private boolean hasMoved = false;

    public Pawn(PieceColor color, Position position) {
        super(color, position, PieceType.PAWN);
    }

    @Override 
    public List<List<Position>> getMoveRays() {
        List<List<Position>> allRays = new ArrayList<>();
        allRays.addAll(getWalkRays());
        allRays.addAll(getCaptureRays());
        return allRays;
    }
    
    @Override
    public List<List<Position>> getWalkRays() {
        int direction = (color == PieceColor.WHITE) ? -1 : 1; 
        List<List<Position>> walkRays = new ArrayList<>();
        List<Position> forwardRay = new ArrayList<>();

        int nextRow = position.row() + direction;
        int col = position.col();

        if (nextRow >= 0 && nextRow < 8) {
            forwardRay.add(new Position(nextRow, col)); // first step
            int doubleStepRow = nextRow + direction;
            if (!hasMoved && doubleStepRow >= 0 && doubleStepRow < 8) {
                forwardRay.add(new Position(doubleStepRow, col)); // second step
            }
        }

        walkRays.add(forwardRay);
        return walkRays;
    }

    @Override
    public List<List<Position>> getCaptureRays() {
        int direction = (color == PieceColor.WHITE) ? -1 : 1; // White moves up, Black moves down
        List<List<Position>> captureRays = new ArrayList<>();
        captureRays.add(generateMove(direction, 1));  // Diagonal right
        captureRays.add(generateMove(direction, -1)); // Diagonal left

        return captureRays;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}