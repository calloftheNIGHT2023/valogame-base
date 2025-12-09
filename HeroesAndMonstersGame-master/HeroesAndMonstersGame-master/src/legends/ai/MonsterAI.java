package legends.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import controller.HeroController;
import legends.config.LegendsConfig;
import legends.world.Direction;
import legends.world.LegendsBoard;
import legends.world.Position;
import model.entity.Hero;
import model.entity.Monster;
import model.item.Armor;

/**
 * Encapsulates the monster behavior rules (movement, attacking, targeting).
 */
public class MonsterAI {
    private final HeroController heroController;
    private final Random random;

    public MonsterAI(HeroController heroController) {
        this(heroController, new Random());
    }

    public MonsterAI(HeroController heroController, Random random) {
        this.heroController = Objects.requireNonNull(heroController, "heroController");
        this.random = Objects.requireNonNull(random, "random");
    }

    public List<String> performRound(LegendsBoard board) {
        List<String> logs = new ArrayList<>();
        for (int lane = 0; lane < board.getLaneCount(); lane++) {
            List<Monster> monsters = board.monstersInLane(lane);
            monsters.sort(Comparator.comparingInt(m -> board.getMonsterPositions().get(m).getRow()).reversed());
            for (Monster monster : monsters) {
                String action = takeTurn(board, monster);
                if (action != null && !action.isEmpty()) {
                    logs.add(action);
                }
            }
        }
        return logs;
    }

    public String takeTurn(LegendsBoard board, Monster monster) {
        Position position = board.getMonsterPositions().get(monster);
        if (position == null) {
            return null;
        }
        List<Hero> heroesInRange = board.heroesInRange(position, LegendsConfig.MONSTER_ATTACK_RANGE);
        if (!heroesInRange.isEmpty()) {
            Hero target = selectTarget(heroesInRange);
            return resolveAttack(board, monster, target);
        }
        boolean moved = attemptAdvance(board, monster, position);
        return moved ? monster.getName() + " advances." : monster.getName() + " waits.";
    }

    private Hero selectTarget(List<Hero> heroes) {
        return heroes.stream()
            .min(Comparator.comparingDouble(Hero::getHp))
            .orElse(heroes.get(0));
    }

    private String resolveAttack(LegendsBoard board, Monster monster, Hero hero) {
        double dodgeChance = heroController.calculateDodgeChance(hero);
        if (random.nextDouble() < dodgeChance) {
            return monster.getName() + " attacks " + hero.getName() + " but misses.";
        }
        double defense = Optional.ofNullable(hero.getEquippedArmor())
            .map(Armor::getDamageReduction)
            .orElse(0.0);
        double rawDamage = Math.max(0, monster.getBaseDamage() - defense);
        hero.takeDamage(rawDamage);
        if (hero.isFainted()) {
            board.removeHero(hero);
        }
        return monster.getName() + " hits " + hero.getName() + " for " + String.format("%.1f", rawDamage) + " damage.";
    }

    private boolean attemptAdvance(LegendsBoard board, Monster monster, Position position) {
        Position next = position.translate(Direction.SOUTH.deltaRow(), Direction.SOUTH.deltaCol());
        if (!board.isInside(next)) {
            return false;
        }
        if (board.getTile(next).getHeroOccupant() != null) {
            // Respect the "cannot pass through a hero" rule by stopping in place.
            return false;
        }
        if (board.getTile(next).getMonsterOccupant() != null) {
            return false;
        }
        return board.moveMonster(monster, Direction.SOUTH);
    }
}
