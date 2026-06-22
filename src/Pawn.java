public class Pawn extends GamePiece {

    public Pawn(Cell cell, boolean color) {
        super(cell, color);
    }

    @Override
    public boolean IsLegalMove(Cell new_cell) {
        Position current_pos = this.pos.GetPosition();
        Position new_pos = new_cell.GetPosition();
        int x_diff = current_pos.DistanceX(new_pos);
        int y_diff = current_pos.DistanceY(new_pos);
        // if Pawn ate enemy GamePiece:
        if(new_cell.IsOccupied() && (x_diff == 1 || x_diff == -1) && (y_diff == 1)) return true;
        // else: if made any illegal move
        if (this.pos.IsOccupied() || (x_diff != 0) || (y_diff > 2)
            || (y_diff == 2 && current_pos.GetY() != 2)) return false;
        return true;
    }
}