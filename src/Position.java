public class Position {
    private int x;
    private int y;

    // ----- Constructors -----

    public Position(int x, int y) {
        this.x = x;
        this.y= y;
    }

    // ----- Getters and Setters

    public int GetX() { return this.x; }
    public int GetY() { return this.y; }

    // THROWS IllegalArgumentException!
    public void SetX(int x) {
        if(x >= 0 && x <= 8) 
            this.x = x;
        else
            throw new IllegalArgumentException("new x position is out of bounds.");
    }

    // THROWS IllegalArgumentException!
    public void SetY(int y) {
        if(y >= 0 && y <= 8) 
            this.y = y;
        else
            throw new IllegalArgumentException("new y position is out of bounds.");
    }

    // THROWS IllegalArgumentException!
    public void UpdatePosition(int x, int y) {
        SetX(x);
        SetY(y);
    }
    public void UpdatePosition(Position new_pos) {
        SetX(new_pos.x);
        SetY(new_pos.y);
    }

    // ----- Methods -----
    // return the difference between the old and new x coordinates.
    public int DistanceX(Position new_pos) {
        return this.x - new_pos.x;
    }
    // return the difference between the old and new y coordinates.
    public int DistanceY(Position new_pos) {
        return this.y - new_pos.y;
    }
    // return the difference between the old and new coordinates.
    public Position GetGap(Position new_pos) {
        return new Position(DistanceX(new_pos), DistanceY(new_pos));
    }
}
