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

    // מתודה אבסטרקטית שכל כלי מממש בעצמו
    public abstract List<List<Position>> getMoveRays();
}