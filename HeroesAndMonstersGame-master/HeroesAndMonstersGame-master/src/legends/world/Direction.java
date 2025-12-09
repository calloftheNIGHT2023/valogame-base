package legends.world;

/**
 * Cardinal directions used for hero and monster navigation on the grid.
 */
public enum Direction {
    NORTH(-1, 0),
    SOUTH(1, 0),
    EAST(0, 1),
    WEST(0, -1),
    STAY(0, 0);

    private final int dRow;
    private final int dCol;

    Direction(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    public int deltaRow() {
        return dRow;
    }

    public int deltaCol() {
        return dCol;
    }
}
