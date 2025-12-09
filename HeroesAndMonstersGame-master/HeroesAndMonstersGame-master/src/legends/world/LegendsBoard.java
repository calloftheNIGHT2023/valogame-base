package legends.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import legends.config.LegendsConfig;
import legends.world.buff.TerrainBuffManager;
import model.entity.Hero;
import model.entity.Monster;

/**
 * Responsible for constructing and maintaining the Legends of Valor board state.
 * The board enforces lane restrictions, terrain assignments, nexus placement,
 * obstacle rules, hero/monster occupancy, and terrain buff activation.
 */
public class LegendsBoard {
    private final int size;
    private final LegendsTile[][] grid;
    private final Random random;
    private final TerrainBuffManager buffManager = new TerrainBuffManager();
    private final Map<Hero, Position> heroPositions = new LinkedHashMap<>();
    private final Map<Monster, Position> monsterPositions = new LinkedHashMap<>();
    private final Map<Hero, Integer> heroIds = new LinkedHashMap<>();
    private final Map<Monster, Integer> monsterIds = new LinkedHashMap<>();
    private final int[][] laneColumns;
    private int monsterIdCounter = 1;

    public LegendsBoard() {
        this(LegendsConfig.DEFAULT_RANDOM_SEED);
    }

    public LegendsBoard(long seed) {
        this.size = LegendsConfig.BOARD_SIZE;
        this.random = (seed == LegendsConfig.DEFAULT_RANDOM_SEED) ? new Random() : new Random(seed);
        this.grid = new LegendsTile[size][size];
        this.laneColumns = computeLaneColumns();
        initialiseStructure();
        assignLaneTerrains();
    }

    private int[][] computeLaneColumns() {
        int[][] columns = new int[LegendsConfig.LANES][LegendsConfig.LANE_WIDTH];
        int laneIndex = 0;
        int slot = 0;
        for (int col = 0; col < size; col++) {
            if (isInaccessibleColumn(col)) {
                continue;
            }
            columns[laneIndex][slot] = col;
            slot++;
            if (slot == LegendsConfig.LANE_WIDTH) {
                slot = 0;
                laneIndex++;
            }
        }
        return columns;
    }

    private void initialiseStructure() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                TileOverlay overlay = resolveOverlay(row, col);
                TerrainType baseTerrain = TerrainType.PLAIN;
                grid[row][col] = new LegendsTile(baseTerrain, overlay);
            }
        }
    }

    private void assignLaneTerrains() {
        List<Position> candidates = new ArrayList<>();
        for (int row = 1; row < size - 1; row++) {
            for (int col = 0; col < size; col++) {
                if (isInaccessibleColumn(col)) {
                    continue;
                }
                LegendsTile tile = grid[row][col];
                if (tile.getOverlay() == TileOverlay.NONE) {
                    candidates.add(new Position(row, col));
                }
            }
        }
        List<TerrainType> distribution = buildTerrainDistribution(candidates.size());
        Collections.shuffle(distribution, random);
        for (int i = 0; i < candidates.size(); i++) {
            Position pos = candidates.get(i);
            TerrainType type = distribution.get(i);
            grid[pos.getRow()][pos.getCol()] = new LegendsTile(type, TileOverlay.NONE);
        }
    }

    private List<TerrainType> buildTerrainDistribution(int tiles) {
        if (tiles <= 0) {
            return Collections.emptyList();
        }
        int plain = Math.max(1, (int) Math.round(tiles * LegendsConfig.PLAIN_RATIO));
        int bush = Math.max(1, (int) Math.round(tiles * LegendsConfig.BUSH_RATIO));
        int cave = Math.max(1, (int) Math.round(tiles * LegendsConfig.CAVE_RATIO));
        int koulou = Math.max(1, (int) Math.round(tiles * LegendsConfig.KOULOU_RATIO));
        int obstacle = Math.max(1, (int) Math.round(tiles * LegendsConfig.OBSTACLE_RATIO));

        int assigned = plain + bush + cave + koulou + obstacle;
        if (assigned != tiles) {
            plain += (tiles - assigned);
            if (plain <= 0) {
                plain = 1;
            }
        }

        List<TerrainType> result = new ArrayList<>(tiles);
        addRepeated(result, TerrainType.PLAIN, plain);
        addRepeated(result, TerrainType.BUSH, bush);
        addRepeated(result, TerrainType.CAVE, cave);
        addRepeated(result, TerrainType.KOULOU, koulou);
        addRepeated(result, TerrainType.OBSTACLE, obstacle);
        return result;
    }

    private void addRepeated(List<TerrainType> list, TerrainType type, int count) {
        for (int i = 0; i < count; i++) {
            list.add(type);
        }
    }

    private TileOverlay resolveOverlay(int row, int col) {
        if (row == 0) {
            return TileOverlay.MONSTER_NEXUS;
        }
        if (row == size - 1) {
            return TileOverlay.HERO_NEXUS;
        }
        if (isInaccessibleColumn(col)) {
            return TileOverlay.INACCESSIBLE;
        }
        return TileOverlay.NONE;
    }

    private boolean isInaccessibleColumn(int col) {
        for (int blocked : LegendsConfig.INACCESSIBLE_COLUMNS) {
            if (col == blocked) {
                return true;
            }
        }
        return false;
    }

    public int getSize() {
        return size;
    }

    public LegendsTile getTile(Position position) {
        Objects.requireNonNull(position, "position");
        if (!isInside(position)) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
        return grid[position.getRow()][position.getCol()];
    }

    public boolean isInside(Position position) {
        return position.getRow() >= 0 && position.getRow() < size
            && position.getCol() >= 0 && position.getCol() < size;
    }

    public void registerHeroes(List<Hero> heroes) {
        heroIds.clear();
        int id = 1;
        for (Hero hero : heroes) {
            heroIds.put(hero, id++);
        }
    }

    public void addHero(Hero hero, Position position) {
        placeHero(hero, position, true);
    }

    public boolean moveHero(Hero hero, Direction direction) {
        Position current = heroPositions.get(hero);
        if (current == null) {
            throw new IllegalStateException("Hero not on board: " + hero.getName());
        }
        Position target = current.translate(direction.deltaRow(), direction.deltaCol());
        return placeHero(hero, target, false);
    }

    private boolean placeHero(Hero hero, Position target, boolean initial) {
        if (!isInside(target)) {
            return false;
        }
        LegendsTile tile = getTile(target);
        if (!tile.isHeroPassable()) {
            return false;
        }
        if (!initial && tile.getHeroOccupant() != null && tile.getHeroOccupant() != hero) {
            return false;
        }

        Position previous = heroPositions.get(hero);
        if (previous != null) {
            LegendsTile previousTile = getTile(previous);
            previousTile.setHeroOccupant(null);
            buffManager.removeBuff(hero);
        }
        tile.setHeroOccupant(hero);
        heroPositions.put(hero, target);
        buffManager.applyBuff(hero, tile.getTerrain());
        return true;
    }

    public void removeHero(Hero hero) {
        Position pos = heroPositions.remove(hero);
        if (pos != null) {
            LegendsTile tile = getTile(pos);
            tile.setHeroOccupant(null);
            buffManager.removeBuff(hero);
        }
        heroIds.remove(hero);
    }

    public void addMonster(Monster monster, Position position) {
        placeMonster(monster, position, true);
    }

    public boolean moveMonster(Monster monster, Direction direction) {
        Position current = monsterPositions.get(monster);
        if (current == null) {
            throw new IllegalStateException("Monster not on board: " + monster.getName());
        }
        Position target = current.translate(direction.deltaRow(), direction.deltaCol());
        return placeMonster(monster, target, false);
    }

    private boolean placeMonster(Monster monster, Position target, boolean initial) {
        if (!isInside(target)) {
            return false;
        }
        LegendsTile tile = getTile(target);
        if (!tile.isMonsterPassable()) {
            return false;
        }
        if (tile.getMonsterOccupant() != null && tile.getMonsterOccupant() != monster) {
            return false;
        }
        if (!initial && tile.getHeroOccupant() != null && !Objects.equals(monsterPositions.get(monster), target)) {
            // A hero blocks forward progression - sharing is allowed but we do not skip heroes
            // by moving beyond them in a single turn.
        }

        Position previous = monsterPositions.get(monster);
        if (previous != null) {
            LegendsTile previousTile = getTile(previous);
            previousTile.setMonsterOccupant(null);
        } else {
            assignMonsterId(monster);
        }
        tile.setMonsterOccupant(monster);
        monsterPositions.put(monster, target);
        return true;
    }

    private void assignMonsterId(Monster monster) {
        monsterIds.putIfAbsent(monster, monsterIdCounter++);
    }

    public void removeMonster(Monster monster) {
        Position pos = monsterPositions.remove(monster);
        if (pos != null) {
            LegendsTile tile = getTile(pos);
            tile.setMonsterOccupant(null);
        }
        monsterIds.remove(monster);
    }

    public Map<Hero, Position> getHeroPositions() {
        return Collections.unmodifiableMap(heroPositions);
    }

    public Map<Monster, Position> getMonsterPositions() {
        return Collections.unmodifiableMap(monsterPositions);
    }

    public Optional<Integer> getHeroId(Hero hero) {
        return Optional.ofNullable(heroIds.get(hero));
    }

    public Optional<Integer> getMonsterId(Monster monster) {
        return Optional.ofNullable(monsterIds.get(monster));
    }

    public int laneIndex(Position position) {
        for (int lane = 0; lane < laneColumns.length; lane++) {
            for (int col : laneColumns[lane]) {
                if (col == position.getCol()) {
                    return lane;
                }
            }
        }
        return -1;
    }

    public int[][] getLaneColumns() {
        int[][] copy = new int[laneColumns.length][];
        for (int i = 0; i < laneColumns.length; i++) {
            copy[i] = laneColumns[i].clone();
        }
        return copy;
    }

    public int getLaneCount() {
        return laneColumns.length;
    }

    public Position getMonsterNexusEntry(int laneIndex) {
        validateLaneIndex(laneIndex);
        return new Position(0, laneColumns[laneIndex][0]);
    }

    public Position getHeroNexusEntry(int laneIndex) {
        validateLaneIndex(laneIndex);
        return new Position(size - 1, laneColumns[laneIndex][0]);
    }

    public List<Hero> heroesInRange(Position from, int range) {
        int lane = laneIndex(from);
        if (lane < 0) {
            return Collections.emptyList();
        }
        List<Hero> inRange = new ArrayList<>();
        for (Map.Entry<Hero, Position> entry : heroPositions.entrySet()) {
            Position pos = entry.getValue();
            if (laneIndex(pos) != lane) {
                continue;
            }
            int delta = Math.abs(pos.getRow() - from.getRow()) + Math.abs(pos.getCol() - from.getCol());
            if (delta <= range) {
                inRange.add(entry.getKey());
            }
        }
        return inRange;
    }

    public void clearObstacle(Position position) {
        LegendsTile tile = getTile(position);
        tile.clearObstacle();
    }

    private void validateLaneIndex(int laneIndex) {
        if (laneIndex < 0 || laneIndex >= laneColumns.length) {
            throw new IllegalArgumentException("Lane index out of bounds: " + laneIndex);
        }
    }

    public List<Monster> monstersInLane(int laneIndex) {
        validateLaneIndex(laneIndex);
        Set<Integer> laneColumnSet = new java.util.HashSet<>();
        for (int col : laneColumns[laneIndex]) {
            laneColumnSet.add(col);
        }
        return monsterPositions.entrySet().stream()
            .filter(e -> laneColumnSet.contains(e.getValue().getCol()))
            .sorted((a, b) -> Integer.compare(b.getValue().getRow(), a.getValue().getRow()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    public boolean isNexus(Position position) {
        return getTile(position).getOverlay().isNexus();
    }
}
