package legends.world;

/**
 * Terrain flavors with optional combat buffs as described in the spec.
 */
public enum TerrainType {
    PLAIN('P'),
    BUSH('B'),
    CAVE('C'),
    KOULOU('K'),
    OBSTACLE('O');

    private final char symbol;

    TerrainType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public boolean grantsBuff() {
        return this == BUSH || this == CAVE || this == KOULOU;
    }

    public boolean isObstacle() {
        return this == OBSTACLE;
    }
}
