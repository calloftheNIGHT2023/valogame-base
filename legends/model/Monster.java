package legends.model;

import legends.world.Position;
import legends.world.TileType;

/**
 * Minimal monster definition. Person 1 owns movement/AI (A5, A6) and Person 2
 * will plug in combat stats (B2). Fields are intentionally small; extend as
 * systems evolve.
 */
public class Monster {

    private final String name;
    private final int laneIndex;

    private int level;
    private int maxHp;
    private int hp;
    private int damage;
    private int defense;
    private double dodgeChance;

    private Position position;

    public Monster(String name, int laneIndex) {
        this.name = (name == null || name.trim().isEmpty()) ? "Monster" : name.trim();
        this.laneIndex = laneIndex;
        this.level = 1;
        this.maxHp = 100;
        this.hp = maxHp;
        this.damage = 10;
        this.defense = 0;
        this.dodgeChance = 0.05;
    }

    public String getName() {
        return name;
    }

    public int getLaneIndex() {
        return laneIndex;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = Math.max(1, maxHp);
        this.hp = Math.min(this.hp, this.maxHp);
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(hp, maxHp));
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = Math.max(0, damage);
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = Math.max(0, defense);
    }

    public double getDodgeChance() {
        return dodgeChance;
    }

    public void setDodgeChance(double dodgeChance) {
        this.dodgeChance = Math.min(Math.max(dodgeChance, 0.0), 0.95);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void move(Position newPosition) {
        this.position = newPosition;
    }

    public void applyTerrainBonus(TileType tileType) {
        // TODO(A2): adjust stats per terrain.
    }

    public void restoreFull() {
        this.hp = maxHp;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    @Override
    public String toString() {
        return String.format("%s (Lane %d Lv%d HP %d/%d)", name, laneIndex, level, hp, maxHp);
    }
}
