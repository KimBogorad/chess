import java.util.List;
import java.util.ArrayList;

public class Queen extends GamePiece {
    public Queen(PieceColor color, Position position) {
        super(color, position);
    }

    @Override
    public List<List<Position>> getMoveRays() {
        List<List<Position>> allRays = new ArrayList<>();

        // Rook-like moves
        allRays.add(generateRay(1, 0));   // Down
        allRays.add(generateRay(-1, 0));  // Up
        allRays.add(generateRay(0, 1));   // Right
        allRays.add(generateRay(0, -1));  // Left

        // Bishop-like moves
        allRays.add(generateRay(1, 1));   // Down-Right
        allRays.add(generateRay(1, -1));  // Down-Left
        allRays.add(generateRay(-1, 1));  // Up-Right
        allRays.add(generateRay(-1, -1)); // Up-Left

        return allRays;
    }
}