import java.util.List;
import java.util.ArrayList;

public class Pawn extends GamePiece {
    public Pawn(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public List<List<Position>> getMoveRays() {
        List<List<Position>> legalMoves = new ArrayList<>();
        int direction = (color == PieceColor.WHITE) ? -1 : 1; // White moves up, Black moves down
        if(this.position.row() == (color == PieceColor.WHITE ? 6 : 1)) {
            // If the pawn is in its initial position, it can move two squares forward
            legalMoves.add(generateMove(2 * direction, 0));
        }
        // Forward move
        legalMoves.add(generateMove(direction, 0));

        // Capture moves
        legalMoves.add(generateMove(direction, 1));  // Diagonal right
        legalMoves.add(generateMove(direction, -1)); // Diagonal left

        return legalMoves;
    }

    private List<Position> generateMove(int rowDelta, int colDelta) {
        List<Position> move = new ArrayList<>();
        int newRow = position.row() + rowDelta;
        int newCol = position.col() + colDelta;

        Position newPosition = new Position(newRow, newCol);
        if (newPosition.isWithinBounds()) {
            move.add(newPosition);
        }

        return move;
    }
}