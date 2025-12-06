package legends.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import legends.items.Armor;
import legends.items.Item;
import legends.items.Weapon;
import legends.world.Position;
import legends.world.TileType;

public class Hero {

    public enum Archetype {
        WARRIOR,
        SORCERER,
        PALADIN
    }

    private final String name;
    private final Archetype archetype;
    private Position position;

    private int level;
    private int experience;

    private int maxHp;
    private int hp;

    private int maxMp;
    private int mp;

    private int strength;
    private int dexterity;
    private int agility;

    private int gold;

    private int healthPotions;
    private int manaPotions;

    private Weapon equippedWeapon;
    private Armor equippedArmor;

    private final List<Item> inventory = new ArrayList<>();

    public Hero(String name,
                Archetype archetype,
                int level,
                int maxHp,
                int maxMp,
                int strength,
                int dexterity,
                int agility,
                int gold) {
        if (name == null || name.trim().isEmpty()) {
            this.name = "Hero";
        } else {
            this.name = name.trim();
        }
        this.archetype = Objects.requireNonNull(archetype);
        this.level = level;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxMp = maxMp;
        this.mp = maxMp;
        this.strength = strength;
        this.dexterity = dexterity;
        this.agility = agility;
        this.gold = gold;
        this.healthPotions = 0;
        this.manaPotions = 0;
    }

    public static Hero createDefault(String name, Archetype archetype) {
        // Baseline stats chosen as placeholders until B1 finalises formulas.
        int level = 1;
        int maxHp = 100;
        int maxMp;
        int strength;
        int dexterity;
        int agility;
        switch (archetype) {
            case SORCERER:
                maxMp = 120;
                strength = 12;
                dexterity = 22;
                agility = 18;
                break;
            case PALADIN:
                maxMp = 100;
                strength = 18;
                dexterity = 18;
                agility = 12;
                break;
            case WARRIOR:
            default:
                maxMp = 80;
                strength = 20;
                dexterity = 10;
                agility = 16;
                break;
        }
        int gold = 300;
        return new Hero(name, archetype, level, maxHp, maxMp, strength, dexterity, agility, gold);
    }

    public String getName() {
        return name;
    }

    public Archetype getArchetype() {
        return archetype;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getMp() {
        return mp;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public int getStrength() {
        return strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getAgility() {
        return agility;
    }

    public int getGold() {
        return gold;
    }

    public Position getPosition() {
        return position;
    }

    public void moveTo(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void receiveDamage(int rawDamage) {
        int damage = Math.max(0, rawDamage);
        hp = Math.max(0, hp - damage);
    }

    public void restoreFullResources() {
        hp = maxHp;
        mp = maxMp;
    }

    public void regenerateForNewRound() {
        // TODO(B3): Confirm regen rates per design (currently no-op).
    }

    public void gainExperience(int amount) {
        if (amount <= 0) {
            return;
        }
        experience += amount;
        // TODO(B4): Implement level up thresholds and stat scaling.
    }

    public void gainExp(int amount) {
        gainExperience(amount);
    }

    public void addGold(int amount) {
        if (amount > 0) {
            gold += amount;
        }
    }

    public void gainGold(int amount) {
        addGold(amount);
    }

    public boolean spendGold(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (gold < amount) {
            return false;
        }
        gold -= amount;
        return true;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public void equipArmor(Armor armor) {
        this.equippedArmor = armor;
    }

    public void addItem(Item item) {
        if (item != null) {
            inventory.add(item);
        }
    }

    public List<Item> getInventory() {
        return Collections.unmodifiableList(inventory);
    }

    public void clearInventory() {
        inventory.clear();
    }

    public int getEffectiveStrength(TileType tile) {
        // TODO(A2/B4): Combine terrain buffs and equipment bonuses.
        return strength;
    }

    public int getEffectiveDexterity(TileType tile) {
        return dexterity;
    }

    public int getEffectiveAgility(TileType tile) {
        return agility;
    }

    public void consumeMana(int amount) {
        if (amount <= 0) {
            return;
        }
        mp = Math.max(0, mp - amount);
    }

    public void restoreMana(int amount) {
        if (amount <= 0) {
            return;
        }
        mp = Math.min(maxMp, mp + amount);
    }

    public void heal(int amount) {
        if (amount <= 0) {
            return;
        }
        hp = Math.min(maxHp, hp + amount);
    }

    public void takeDamage(int amount) {
        receiveDamage(amount);
    }

    public double getDodgeChance() {
        return Math.min(0.5, agility * 0.002);
    }

    public void addHealthPotion() {
        healthPotions++;
    }

    public void addManaPotion() {
        manaPotions++;
    }

    public void reviveAtHalf() {
        hp = Math.max(1, maxHp / 2);
    }

    @Override
    public String toString() {
        return String.format("%s (Lv%d %s) HP %d/%d MP %d/%d", name, level, archetype,
                hp, maxHp, mp, maxMp);
    }
}
