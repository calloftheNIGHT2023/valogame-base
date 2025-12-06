package legends.items;

import legends.model.Hero;
import legends.model.Monster;
import legends.io.IO;

/**
 * Placeholder consumable definition，等待 Person 2 根据新规则完善具体效果。
 */
public class Consumable implements Item {

    public enum ConsumeType {
        HEAL,
        RESTORE_MP,
        BUFF,
        DEBUFF
    }

    private final String name;
    private final int level;
    private final int price;
    private final ConsumeType type;
    private final int potency;
    private final boolean usableInBattle;

    public Consumable(String name, int level, int price, ConsumeType type, int potency, boolean usableInBattle) {
        this.name = name;
        this.level = level;
        this.price = price;
        this.type = type;
        this.potency = potency;
        this.usableInBattle = usableInBattle;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public ConsumeType getType() {
        return type;
    }

    public int getPotency() {
        return potency;
    }

    public boolean isUsableInBattle() {
        return usableInBattle;
    }

    /**
     * 留给 Person 2 实现的具体使用逻辑。
     */
    public boolean apply(Hero user, Monster target, IO io) {
        io.println("TODO: Consumable.apply 尚未实现，等待战斗/状态系统。");
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s (Lv %d %s potency=%d price=%d)", name, level, type, potency, price);
    }
}
