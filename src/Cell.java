public class Cell {
    private final boolean WHITE = true;
    private final boolean BLACK = false;

    private boolean color;
    private Position pos;
    private GamePiece occupant;

    // ----- Constructors -----

    public Cell(int x, int y) {
        this.color = BlackOrWhite(x, y);
        this.pos = new Position(x, y);
        this.occupant = null;
    }

    // ----- Getters and Setters -----

    public boolean GetColor() { return this.color; }
    public Position GetPosition() { return this.pos; }
    public GamePiece GetOccupant() { return this.occupant; }

    public void SetPosition(Position new_pos) {
        this.pos.UpdatePosition(new_pos);
    }
    public void SetOccupant(GamePiece new_occupant) {
        this.occupant = new_occupant;
    }

    public boolean IsOccupied() { return this.occupant == null; }

    // ----- Helper Methods -----

    private boolean BlackOrWhite(int x, int y) {
        if((x+y)%2 == 0)
            return BLACK;
        return WHITE;
    }
}