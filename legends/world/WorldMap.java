package legends.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import legends.io.IO;

/**
 * Simplified 8×8 lane board. Person 1 is expected to evolve this
 * implementation to satisfy requirements (A1–A4).
 */
public class WorldMap {

    public static final int ROWS = 8;
    public static final int COLS = 8;
    public static final int LANES = 3;

    private final TileType[][] tiles = new TileType[ROWS][COLS];
    private final List<Position> heroNexusTiles = new ArrayList<>();
    private final List<Position> monsterNexusTiles = new ArrayList<>();

    private WorldMap() {
    }

    public static WorldMap create(Random rng) {
        WorldMap map = new WorldMap();
        map.initialiseBaseLayout();
        map.distributeTerrain(rng == null ? new Random() : rng);
        return map;
    }

    private void initialiseBaseLayout() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                tiles[r][c] = TileType.PLAIN;
            }
        }

        for (int r = 0; r < ROWS; r++) {
            tiles[r][2] = TileType.INACCESSIBLE;
            tiles[r][5] = TileType.INACCESSIBLE;
        }

        for (int c = 0; c < COLS; c++) {
            if (!isLaneDivider(c)) {
                tiles[0][c] = TileType.MONSTER_NEXUS;
                tiles[ROWS - 1][c] = TileType.HERO_NEXUS;
                monsterNexusTiles.add(new Position(0, c));
                heroNexusTiles.add(new Position(ROWS - 1, c));
            }
        }
    }

    private void distributeTerrain(Random rng) {
        // TODO(A1/A2/A3): implement proper distribution rules.
    }

    public TileType getTile(int row, int col) {
        return tiles[row][col];
    }

    public void setTile(int row, int col, TileType tileType) {
        tiles[row][col] = Objects.requireNonNull(tileType);
    }

    public boolean isInBounds(Position position) {
        int r = position.getRow();
        int c = position.getCol();
        return r >= 0 && r < ROWS && c >= 0 && c < COLS;
    }

    public boolean isLaneDivider(int col) {
        return col == 2 || col == 5;
    }

    public int laneFor(Position pos) {
        int col = pos.getCol();
        if (col <= 1) {
            return 0;
        }
        if (col <= 4) {
            return 1;
        }
        return 2;
    }

    public List<Position> getHeroNexusTiles() {
        return Collections.unmodifiableList(heroNexusTiles);
    }

    public List<Position> getMonsterNexusTiles() {
        return Collections.unmodifiableList(monsterNexusTiles);
    }

    public TileType[][] snapshotTiles() {
        TileType[][] copy = new TileType[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            System.arraycopy(tiles[r], 0, copy[r], 0, COLS);
        }
        return copy;
    }

    public void render(IO io, Map<Position, Overlay> overlays) {
        io.println("=== Board Preview ===");
        for (int r = 0; r < ROWS; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < COLS; c++) {
                Position pos = new Position(r, c);
                Overlay overlay = overlays.get(pos);
                String symbol = symbolFor(tiles[r][c]);
                if (overlay != null) {
                    symbol = overlay.render(symbol);
                }
                sb.append('[').append(symbol).append(']');
            }
            io.println(sb.toString());
        }
        io.println("=====================");
    }

    private String symbolFor(TileType tile) {
        switch (tile) {
            case HERO_NEXUS:
                return "HN";
            case MONSTER_NEXUS:
                return "MN";
            case INACCESSIBLE:
                return "XX";
            case BUSH:
                return "B";
            case CAVE:
                return "C";
            case KOULOU:
                return "K";
            case OBSTACLE:
                return "OB";
            case PLAIN:
            default:
                return "  ";
        }
    }

    public Map<TileType, Integer> countTilesByType() {
        Map<TileType, Integer> counts = new EnumMap<>(TileType.class);
        for (TileType tile : TileType.values()) {
            counts.put(tile, 0);
        }
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                TileType type = tiles[r][c];
                counts.put(type, counts.get(type) + 1);
            }
        }
        return counts;
    }

    public static final class Overlay {
        private final String heroSymbol;
        private final List<String> monsterSymbols;

        public Overlay(String heroSymbol, List<String> monsterSymbols) {
            this.heroSymbol = heroSymbol;
            this.monsterSymbols = monsterSymbols == null
                    ? new ArrayList<>()
                    : new ArrayList<>(monsterSymbols);
        }

        public String render(String base) {
            if (heroSymbol != null && !heroSymbol.isEmpty()) {
                if (!monsterSymbols.isEmpty()) {
                    return heroSymbol.charAt(0) + monsterSymbols.get(0);
                }
                return heroSymbol;
            }
            if (!monsterSymbols.isEmpty()) {
                return monsterSymbols.get(0);
            }
            return base;
        }
    }
}
