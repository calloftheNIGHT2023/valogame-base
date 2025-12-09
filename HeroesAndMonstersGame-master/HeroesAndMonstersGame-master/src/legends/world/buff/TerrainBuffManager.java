package legends.world.buff;

import java.util.HashMap;
import java.util.Map;

import legends.config.LegendsConfig;
import legends.world.TerrainType;
import model.entity.Hero;

/**
 * Applies and reverts terrain specific stat buffs for heroes.
 * The manager keeps a per-hero snapshot to ensure temporary bonuses are reverted
 * when the entity leaves the tile.
 */
public class TerrainBuffManager {
    private final Map<Hero, HeroSnapshot> heroSnapshots = new HashMap<>();

    public void applyBuff(Hero hero, TerrainType terrain) {
        removeBuff(hero); // Reset any previous state first
        if (!terrain.grantsBuff()) {
            return;
        }
        HeroSnapshot snapshot = new HeroSnapshot(hero.getStrength(), hero.getAgility(), hero.getDexterity());
        heroSnapshots.put(hero, snapshot);

        switch (terrain) {
            case BUSH:
                hero.setDexterity(snapshot.dexterity * LegendsConfig.BUSH_DEXTERITY_MULTIPLIER);
                break;
            case CAVE:
                hero.setAgility(snapshot.agility * LegendsConfig.CAVE_AGILITY_MULTIPLIER);
                break;
            case KOULOU:
                hero.setStrength(snapshot.strength * LegendsConfig.KOULOU_STRENGTH_MULTIPLIER);
                break;
            default:
                break;
        }
    }

    public void removeBuff(Hero hero) {
        HeroSnapshot snapshot = heroSnapshots.remove(hero);
        if (snapshot != null) {
            hero.setStrength(snapshot.strength);
            hero.setAgility(snapshot.agility);
            hero.setDexterity(snapshot.dexterity);
        }
    }

    private static final class HeroSnapshot {
        private final double strength;
        private final double agility;
        private final double dexterity;

        private HeroSnapshot(double strength, double agility, double dexterity) {
            this.strength = strength;
            this.agility = agility;
            this.dexterity = dexterity;
        }
    }
}
