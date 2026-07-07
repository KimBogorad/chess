import java.util.ArrayList;
import java.util.List;

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

    private List<Position> generateRay(int rowDelta, int colDelta) {
        List<Position> ray = new ArrayList<>();
        int currentRow = position.row() + rowDelta;
        int currentCol = position.col() + colDelta;

        while (currentRow >= 0 && currentRow < 8 && currentCol >= 0 && currentCol < 8) {
            currentRow += rowDelta;
            currentCol += colDelta;
            Position newPosition = new Position(currentRow, currentCol);
            if (!newPosition.isWithinBounds()) {
                break;
            }
            ray.add(newPosition);
        }
        return ray;
    } 
}