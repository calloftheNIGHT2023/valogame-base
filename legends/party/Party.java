package legends.party;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import legends.model.Hero;
import legends.world.Position;

/**
 * Minimal hero party container. Person 2 / Person 3 will extend this with
 * inventory management and action validation (B1, B5, C3).
 */
public class Party {

    private final List<Hero> heroes = new ArrayList<>();

    public Party(List<Hero> initialHeroes) {
        if (initialHeroes != null) {
            for (Hero hero : initialHeroes) {
                addHero(hero);
            }
        }
    }

    public void addHero(Hero hero) {
        if (hero != null) {
            heroes.add(hero);
        }
    }

    public List<Hero> getHeroes() {
        return Collections.unmodifiableList(heroes);
    }

    public Hero getHero(int index) {
        if (index < 0 || index >= heroes.size()) {
            return null;
        }
        return heroes.get(index);
    }

    public int highestLevel() {
        int max = 1;
        for (Hero hero : heroes) {
            max = Math.max(max, hero.getLevel());
        }
        return max;
    }

    public void resetPositions(Position start) {
        Objects.requireNonNull(start, "start");
        for (int i = 0; i < heroes.size(); i++) {
            heroes.get(i).moveTo(start);
        }
    }

    public void moveHero(int index, Position newPosition) {
        if (index < 0 || index >= heroes.size()) {
            return;
        }
        heroes.get(index).moveTo(newPosition);
    }

    public boolean allDefeated() {
        for (Hero hero : heroes) {
            if (hero.isAlive()) {
                return false;
            }
        }
        return true;
    }
}
