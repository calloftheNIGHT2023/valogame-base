package legends.world;

/**
 * Structural overlay for a tile - determines whether it acts as a nexus
 * or is inaccessible regardless of the underlying terrain enrichments.
 */
public enum TileOverlay {
    NONE,
    HERO_NEXUS,
    MONSTER_NEXUS,
    INACCESSIBLE;

    public boolean isNexus() {
        return this == HERO_NEXUS || this == MONSTER_NEXUS;
    }
}
