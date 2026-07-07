import java.util.List;
import java.util.ArrayList;

public class Bishop extends GamePiece {
    public Bishop(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public List<List<Position>> getMoveRays() {
        List<List<Position>> allRays = new ArrayList<>();

        // Bishop-like moves
        allRays.add(generateRay(1, 1));   // Down-Right
        allRays.add(generateRay(1, -1));  // Down-Left
        allRays.add(generateRay(-1, 1));  // Up-Right
        allRays.add(generateRay(-1, -1)); // Up-Left

        return allRays;
    }

    private List<Position> generateRay(int rowDelta, int colDelta) {
        List<Position> ray = new ArrayList<>();
        int currentRow = position.row() + rowDelta;
        int currentCol = position.col() + colDelta;

        while (currentRow >= 0 && currentRow < 8 && currentCol >= 0 && currentCol < 8) {
            Position newPosition = new Position(currentRow, currentCol);
            if (!newPosition.isWithinBounds()) {
                break;
            }
            ray.add(newPosition);
            currentRow += rowDelta;
            currentCol += colDelta;
        }
        return ray;
    }
}
