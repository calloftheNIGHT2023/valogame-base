package legends.monster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import legends.model.Monster;
import legends.world.Position;
import legends.world.WorldMap;

/**
 * Shared place-holder for the upcoming monster systems 
 */
public class MonsterManager {

    private final List<Monster> activeMonsters = new ArrayList<>();
    private final WorldMap worldMap;

    public MonsterManager(WorldMap worldMap) {
        this.worldMap = Objects.requireNonNull(worldMap);
    }

    public List<Monster> getActiveMonsters() {
        return Collections.unmodifiableList(activeMonsters);
    }

    public void clearAll() {
        activeMonsters.clear();
    }

    public void addMonster(Monster monster) {
        activeMonsters.add(Objects.requireNonNull(monster));
    }

    public void removeMonster(Monster monster) {
        activeMonsters.remove(monster);
    }

    /**
     * Stub entry-point for Person 1 to plug in spawning logic (A6).
     */
    public void spawnWaveIfNeeded(int round, int highestHeroLevel) {
        // TODO(A6): Implement actual spawn timing and monster selection.
    }

    /**
     * Placeholder hook for the monster AI system (A5). The method is expected to
     * decide whether the monster should attack or move south, respecting the
     * lane rules and board occupants.
     */
    public void takeTurn(Monster monster) {
        // TODO(A5): Implement monster turn logic.
    }

    public boolean isNexusOccupied(Position position) {
        for (Monster monster : activeMonsters) {
            if (position.equals(monster.getPosition())) {
                return true;
            }
        }
        return false;
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }
}

