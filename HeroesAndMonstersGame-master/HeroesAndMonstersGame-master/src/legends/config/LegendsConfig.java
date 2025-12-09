package legends.config;

/**
 * Central configuration for the Legends of Valor ruleset.
 * Encapsulates the deterministic board layout, terrain distribution,
 * monster spawning cadence, and combat/effect multipliers that are
 * required by Person 1 of the assignment.
 */
public final class LegendsConfig {
    private LegendsConfig() {
    }

    public static final int BOARD_SIZE = 8;
    public static final int LANES = 3;
    public static final int LANE_WIDTH = 2;

    public static final int[] INACCESSIBLE_COLUMNS = {2, 5};

    public static final double PLAIN_RATIO = 0.40;
    public static final double BUSH_RATIO = 0.15;
    public static final double CAVE_RATIO = 0.15;
    public static final double KOULOU_RATIO = 0.15;
    public static final double OBSTACLE_RATIO = 0.15;

    public static final double BUSH_DEXTERITY_MULTIPLIER = 1.10;
    public static final double CAVE_AGILITY_MULTIPLIER = 1.10;
    public static final double KOULOU_STRENGTH_MULTIPLIER = 1.10;

    public static final int MONSTER_SPAWN_INTERVAL = 8;
    public static final int MONSTER_ATTACK_RANGE = 1;

    public static final long DEFAULT_RANDOM_SEED = -1L;
}
