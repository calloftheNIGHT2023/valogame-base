package legends.combat;

import java.util.Objects;

import legends.model.Hero;
import legends.model.Monster;

/**
 * Placeholder for Person 2's combat logic (B2, B3, B4, B5). The class will
 * eventually orchestrate attack calculations, spell resolution, damage
 * application, and validity checks. For now it only exposes a handful of hook
 * methods to help wire the upcoming implementation into the game loop.
 */
public class CombatResolver {

    public enum AttackResult {
        HIT,
        MISS,
        INVALID
    }

    public AttackResult heroAttack(Hero hero, Monster target) {
        Objects.requireNonNull(hero);
        Objects.requireNonNull(target);
        // TODO(B2): Replace with real calculation and board-aware range checks.
        return AttackResult.INVALID;
    }

    public AttackResult monsterAttack(Monster monster, Hero target) {
        Objects.requireNonNull(monster);
        Objects.requireNonNull(target);
        // TODO(B2): Implement monster attack parity with hero combat rules.
        return AttackResult.INVALID;
    }

    public void handleHeroRespawn(Hero hero) {
        Objects.requireNonNull(hero);
        // TODO(B3): Perform respawn logic and stat restoration.
    }
}
