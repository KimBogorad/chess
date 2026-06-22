public abstract class GamePiece {
    protected boolean color;
    protected boolean is_alive;
    protected Cell pos;

    public GamePiece(Cell cell, boolean color) {
        this.pos = cell;
        this.color = color;
        this.is_alive = true;
    }

    public boolean IsLegalMove(Cell new_cell) {
        return true;
    }

    public void SetPosition(Cell new_cell) {
        if(IsLegalMove(new_cell)) this.pos = new_cell;
    }
}