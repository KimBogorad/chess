import java.util.List;
import java.util.ArrayList;

public abstract class GamePiece {
    protected boolean color;

    public GamePiece(boolean color) {
        this.color = color;
    }

    public boolean getColor() {
        return color;
    }
}