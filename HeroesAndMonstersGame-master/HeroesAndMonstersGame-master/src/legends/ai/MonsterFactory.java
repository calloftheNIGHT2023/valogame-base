package legends.ai;

import model.entity.Monster;

/**
 * Abstraction that provides appropriately levelled monster instances for spawning.
 */
public interface MonsterFactory {
    Monster createMonsterForLane(int laneIndex, int level);
}
