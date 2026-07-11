package com.chess.board;

public record Position(int row, int col) {
    // Chess board bounds for both rows and columns are between 1 and 8 (0 and 7)
    public boolean isWithinBounds() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}   