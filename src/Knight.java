import java.util.List;
import java.util.ArrayList;

public class Knight extends GamePiece {
    public Knight(PieceColor color, Position position) {
        super(color, position);
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
