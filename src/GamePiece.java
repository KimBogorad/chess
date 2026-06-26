public abstract class GamePiece {
    protected boolean color;
    protected boolean is_alive;

    public GamePiece(boolean color) {
        this.color = color;
        this.is_alive = true;
    }
}