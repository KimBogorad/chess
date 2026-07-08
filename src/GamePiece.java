import java.util.ArrayList;
import java.util.List;

public abstract class GamePiece {
    protected PieceColor color; // Enum: WHITE, BLACK
    protected Position position;

    public GamePiece(PieceColor color, Position position) {
        this.color = color;
        this.position = position;
    }

    public PieceColor getColor() {
        return color;
    }

    public abstract List<List<Position>> getMoveRays();

    // For the Pawn
    public List<List<Position>> getWalkRays() {
        return getMoveRays();
    }

    public List<List<Position>> getCaptureRays() {
        return getMoveRays();
    }

    //----- Protected Helper Methods -----

    // helper method for stepping pieces (King, Knight, Pawn)
    protected List<Position> generateMove(int rowDelta, int colDelta) {
        List<Position> move = new ArrayList<>();
        int newRow = position.row() + rowDelta;
        int newCol = position.col() + colDelta;

        if(newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
            Position newPosition = new Position(newRow, newCol);
            move.add(newPosition);
        }

        return move;
    } 

    // helper method for sliding pieces (Queen, Rook, Bishop, Pawn)
    protected List<Position> generateRay(int rowDelta, int colDelta) {
        List<Position> ray = new ArrayList<>();
        int currentRow = position.row() + rowDelta;
        int currentCol = position.col() + colDelta;

        while (currentRow >= 0 && currentRow < 8 && currentCol >= 0 && currentCol < 8) {
            Position newPosition = new Position(currentRow, currentCol);
            ray.add(newPosition);
            currentRow += rowDelta;
            currentCol += colDelta;
        }
        return ray;
    }
}