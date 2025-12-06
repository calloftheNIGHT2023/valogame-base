package legends;

import java.util.Arrays;
import java.util.Random;

import legends.combat.CombatResolver;
import legends.io.IO;
import legends.io.ConsoleIO;
import legends.model.Hero;
import legends.model.Hero.Archetype;
import legends.monster.MonsterManager;
import legends.party.Party;
import legends.turn.TurnController;
import legends.world.Position;
import legends.world.WorldMap;

/**
 * 精简版游戏启动骨架，供三位同学在其基础上继续填充逻辑。
 */
public class LegendsGame {

    private final IO io;
    private final Random rng;

    public LegendsGame() {
        this(new ConsoleIO(), new Random());
    }

    public LegendsGame(IO io, Random rng) {
        this.io = io;
        this.rng = rng == null ? new Random() : rng;
    }

    public void run() {
        io.println("=== Legends: Reborn Prototype ===");
        io.println("当前仅保留框架，请在各自模块上扩展实现。");

        WorldMap worldMap = WorldMap.create(rng);
        Party party = bootstrapParty();
        MonsterManager monsterManager = new MonsterManager(worldMap);

        GameContext context = new GameContext(worldMap, party, monsterManager);
        CombatResolver combatResolver = new CombatResolver();
        TurnController controller = new TurnController(context, io, combatResolver);

        Position heroNexus = worldMap.getHeroNexusTiles().isEmpty()
                ? new Position(WorldMap.ROWS - 1, 0)
                : worldMap.getHeroNexusTiles().get(0);
        party.resetPositions(heroNexus);

        controller.run();
    }

    private Party bootstrapParty() {
        Hero hero1 = Hero.createDefault("Hero1", Archetype.WARRIOR);
        Hero hero2 = Hero.createDefault("Hero2", Archetype.SORCERER);
        Hero hero3 = Hero.createDefault("Hero3", Archetype.PALADIN);
        return new Party(Arrays.asList(hero1, hero2, hero3));
    }
}
