package legends.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import legends.config.LegendsConfig;
import legends.world.LegendsBoard;
import legends.world.LegendsTile;
import legends.world.Position;
import model.entity.Hero;
import model.entity.Monster;

/**
 * Handles the "spawn 3 monsters per lane every N rounds" requirement.
 */
public class MonsterSpawner {
    private final int interval;
    private int roundCounter;

    public MonsterSpawner() {
        this(LegendsConfig.MONSTER_SPAWN_INTERVAL);
    }

    public MonsterSpawner(int interval) {
        if (interval <= 0) {
            throw new IllegalArgumentException("Interval must be positive");
        }
        this.interval = interval;
        this.roundCounter = 0;
    }

    public List<String> onRoundStart(LegendsBoard board, MonsterFactory factory, List<Hero> heroes) {
        Objects.requireNonNull(board, "board");
        Objects.requireNonNull(factory, "factory");
        Objects.requireNonNull(heroes, "heroes");
        roundCounter++;
        if (roundCounter % interval != 0) {
            return Collections.emptyList();
        }
        int level = heroes.stream().mapToInt(Hero::getLevel).max().orElse(1);
        return spawnWave(board, factory, level);
    }

    private List<String> spawnWave(LegendsBoard board, MonsterFactory factory, int level) {
        List<String> logs = new ArrayList<>();
        for (int lane = 0; lane < board.getLaneCount(); lane++) {
            Position spawnPos = findSpawnSlot(board, lane);
            if (spawnPos == null) {
                logs.add("Lane " + lane + " spawn blocked.");
                continue;
            }
            Monster monster = factory.createMonsterForLane(lane, level);
            if (monster == null) {
                logs.add("Factory produced null monster for lane " + lane);
                continue;
            }
            board.addMonster(monster, spawnPos);
            logs.add(monster.getName() + " emerges in lane " + lane);
        }
        return logs;
    }

    private Position findSpawnSlot(LegendsBoard board, int lane) {
        int[][] laneColumns = board.getLaneColumns();
        for (int col : laneColumns[lane]) {
            Position pos = new Position(0, col);
            LegendsTile tile = board.getTile(pos);
            if (tile.getMonsterOccupant() == null) {
                return pos;
            }
        }
        return null;
    }
}
