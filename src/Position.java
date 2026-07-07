public record Position(int row, int col) {
    // בלוח שחמט סטנדרטי, השורות והעמודות הן בין 0 ל-7
    public boolean isWithinBounds() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}