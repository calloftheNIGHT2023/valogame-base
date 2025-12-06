package legends;

import java.util.Objects;

import legends.model.Hero;
import legends.monster.MonsterManager;
import legends.party.Party;
import legends.world.WorldMap;

/**
 * Shared state object passed between the different subsystems of the new game
 * implementation. 
 */
public final class GameContext {

    private final WorldMap worldMap;
    private final Party party;
    private final MonsterManager monsterManager;

    /** Tracks how many full rounds (hero + monster turn) have elapsed. */
    private int roundNumber;

    public GameContext(WorldMap worldMap, Party party, MonsterManager monsterManager) {
        this.worldMap = Objects.requireNonNull(worldMap);
        this.party = Objects.requireNonNull(party);
        this.monsterManager = Objects.requireNonNull(monsterManager);
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public Party getParty() {
        return party;
    }

    public MonsterManager getMonsterManager() {
        return monsterManager;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void incrementRound() {
        roundNumber++;
    }

    /** Convenience access for single-party games. */
    public Hero getHero(int index) {
        return party.getHero(index);
    }
}

