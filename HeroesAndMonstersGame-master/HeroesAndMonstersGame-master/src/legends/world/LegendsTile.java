package legends.world;

import model.entity.Hero;
import model.entity.Monster;

/**
 * Represents an individual cell on the Legends of Valor board.
 * Supports a single hero and a single monster occupant at a time.
 */
public class LegendsTile {
    private TerrainType terrain;
    private final TileOverlay overlay;
    private Hero heroOccupant;
    private Monster monsterOccupant;

    public LegendsTile(TerrainType terrain, TileOverlay overlay) {
        this.terrain = terrain;
        this.overlay = overlay;
    }

    public TerrainType getTerrain() {
        return terrain;
    }

    public TileOverlay getOverlay() {
        return overlay;
    }

    public boolean isHeroPassable() {
        return overlay != TileOverlay.INACCESSIBLE && !terrain.isObstacle();
    }

    public boolean isMonsterPassable() {
        return overlay != TileOverlay.INACCESSIBLE && !terrain.isObstacle();
    }

    public void clearObstacle() {
        if (terrain.isObstacle()) {
            terrain = TerrainType.PLAIN;
        }
    }

    public Hero getHeroOccupant() {
        return heroOccupant;
    }

    public Monster getMonsterOccupant() {
        return monsterOccupant;
    }

    public void setHeroOccupant(Hero hero) {
        this.heroOccupant = hero;
    }

    public void setMonsterOccupant(Monster monster) {
        this.monsterOccupant = monster;
    }

    public boolean isEmpty() {
        return heroOccupant == null && monsterOccupant == null;
    }
}
