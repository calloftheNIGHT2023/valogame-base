package legends.controller;

import controller.HeroController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import legends.ai.MonsterAI;
import legends.ai.MonsterFactory;
import legends.ai.MonsterSpawner;
import legends.config.LegendsConfig;
import legends.view.LegendsBoardRenderer;
import legends.world.Direction;
import legends.world.LegendsBoard;
import legends.world.LegendsTile;
import legends.world.Position;
import model.entity.Dragon;
import model.entity.Exoskeleton;
import model.entity.Hero;
import model.entity.Monster;
import model.entity.Spirit;
import util.GameDataParser;
import view.Colors;

/**
 * Minimal playable loop that exercises the Person 1 features of the Legends of Valor assignment.
 */
public class LegendsOfValorController {
    private final Scanner scanner;
    private final LegendsBoard board;
    private final LegendsBoardRenderer renderer;
    private final HeroController heroController;
    private final MonsterAI monsterAI;
    private final MonsterSpawner monsterSpawner;
    private final Random random;

    private final List<Hero> heroRoster = new ArrayList<>();
    private final List<Hero> activeHeroes = new ArrayList<>();
    private final List<Monster> monsterTemplates = new ArrayList<>();

    private int roundNumber = 0;

    public LegendsOfValorController() {
        this(new Scanner(System.in), LegendsConfig.DEFAULT_RANDOM_SEED);
    }

    public LegendsOfValorController(Scanner scanner, long seed) {
        this.scanner = Objects.requireNonNull(scanner, "scanner");
        this.random = (seed == LegendsConfig.DEFAULT_RANDOM_SEED) ? new Random() : new Random(seed);
        this.board = new LegendsBoard(seed);
        this.renderer = new LegendsBoardRenderer();
        this.heroController = new HeroController();
        this.monsterAI = new MonsterAI(heroController, random);
        this.monsterSpawner = new MonsterSpawner();
    }

    public void start() {
        System.out.println(Colors.CYAN + "\n=== LEGENDS OF VALOR (Prototype) ===" + Colors.RESET);
        System.out.println("Person 1 focus: board generation, terrain effects, monster AI & spawning.\n");
        try {
            loadGameData();
        } catch (IOException ex) {
            System.err.println("Failed to load game data: " + ex.getMessage());
            return;
        }
        if (!selectHeroes()) {
            return;
        }
        positionHeroes();
        gameLoop();
    }

    private void loadGameData() throws IOException {
        heroRoster.clear();
        heroRoster.addAll(GameDataParser.parseHeroes("Warriors.txt", "Warrior"));
        heroRoster.addAll(GameDataParser.parseHeroes("Sorcerers.txt", "Sorcerer"));
        heroRoster.addAll(GameDataParser.parseHeroes("Paladins.txt", "Paladin"));

        monsterTemplates.clear();
        monsterTemplates.addAll(GameDataParser.parseMonsters("Dragons.txt", "Dragon"));
        monsterTemplates.addAll(GameDataParser.parseMonsters("Exoskeletons.txt", "Exoskeleton"));
        monsterTemplates.addAll(GameDataParser.parseMonsters("Spirits.txt", "Spirit"));
    }

    private boolean selectHeroes() {
        System.out.println("Select exactly " + board.getLaneCount() + " heroes (one per lane).");
        if (heroRoster.isEmpty()) {
            System.out.println("Hero roster empty.");
            return false;
        }
        for (int i = 0; i < heroRoster.size(); i++) {
            Hero hero = heroRoster.get(i);
            System.out.printf(Locale.US, "%2d) %-18s Lvl %-2d %s%n", i + 1, hero.getName(), hero.getLevel(), hero.getClass().getSimpleName());
        }
        activeHeroes.clear();
        while (activeHeroes.size() < board.getLaneCount()) {
            System.out.print("Enter hero number for lane " + (activeHeroes.size() + 1) + ": ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            int index;
            try {
                index = Integer.parseInt(line);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input.");
                continue;
            }
            if (index < 1 || index > heroRoster.size()) {
                System.out.println("Number out of range.");
                continue;
            }
            Hero chosen = heroRoster.get(index - 1);
            if (activeHeroes.contains(chosen)) {
                System.out.println("Hero already selected.");
                continue;
            }
            activeHeroes.add(chosen);
        }
        board.registerHeroes(activeHeroes);
        return true;
    }

    private void positionHeroes() {
        for (int lane = 0; lane < activeHeroes.size(); lane++) {
            Hero hero = activeHeroes.get(lane);
            Position start = board.getHeroNexusEntry(lane);
            board.addHero(hero, start);
        }
    }

    private void gameLoop() {
        boolean running = true;
        while (running && !activeHeroes.isEmpty()) {
            roundNumber++;
            System.out.println(Colors.YELLOW + "\n-- Round " + roundNumber + " --" + Colors.RESET);
            renderer.render(board);
            printHeroStatus();
            running = handleHeroPhase();
            if (!running) {
                break;
            }
            List<String> spawns = monsterSpawner.onRoundStart(board, buildFactory(), activeHeroes);
            for (String s : spawns) {
                System.out.println(Colors.GREEN + s + Colors.RESET);
            }
            List<String> monsterLogs = monsterAI.performRound(board);
            for (String log : monsterLogs) {
                System.out.println(Colors.RED + log + Colors.RESET);
            }
            pruneDefeatedHeroes();
        }
        System.out.println(Colors.CYAN + "\nLegends of Valor session ended." + Colors.RESET);
    }

    private void printHeroStatus() {
        for (Hero hero : activeHeroes) {
            Optional<Integer> id = board.getHeroId(hero);
            System.out.printf(Locale.US, "H%s %-18s HP:%-6.1f Lvl:%d%n",
                id.map(Object::toString).orElse("?"), hero.getName(), hero.getHp(), hero.getLevel());
        }
    }

    private boolean handleHeroPhase() {
        while (true) {
            System.out.print("Command (move <id> <N|S|E|W> | clear <row> <col> | end | quit): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase(Locale.US);
            switch (command) {
                case "move":
                    if (parts.length < 3) {
                        System.out.println("Usage: move <id> <direction>");
                        break;
                    }
                    Hero hero = resolveHero(parts[1]);
                    if (hero == null) {
                        break;
                    }
                    Direction direction = parseDirection(parts[2]);
                    if (direction == null) {
                        System.out.println("Invalid direction.");
                        break;
                    }
                    if (!board.moveHero(hero, direction)) {
                        System.out.println("Cannot move hero in that direction.");
                    }
                    renderer.render(board);
                    printHeroStatus();
                    break;
                case "clear":
                    if (parts.length < 3) {
                        System.out.println("Usage: clear <row> <col>");
                        break;
                    }
                    try {
                        int row = Integer.parseInt(parts[1]);
                        int col = Integer.parseInt(parts[2]);
                        clearObstacle(row, col);
                    } catch (NumberFormatException ex) {
                        System.out.println("Row/column must be numbers.");
                    }
                    break;
                case "end":
                    return true;
                case "quit":
                    return false;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    private void clearObstacle(int row, int col) {
        Position position = new Position(row, col);
        if (!board.isInside(position)) {
            System.out.println("Position out of bounds.");
            return;
        }
        LegendsTile tile = board.getTile(position);
        if (!tile.getTerrain().isObstacle()) {
            System.out.println("No obstacle at that location.");
            return;
        }
        board.clearObstacle(position);
        System.out.println("Obstacle cleared. Terrain is now plain.");
    }

    private Hero resolveHero(String idToken) {
        int id;
        try {
            id = Integer.parseInt(idToken);
        } catch (NumberFormatException ex) {
            System.out.println("Hero id must be numeric.");
            return null;
        }
        for (Hero hero : activeHeroes) {
            if (board.getHeroId(hero).map(val -> val == id).orElse(false)) {
                return hero;
            }
        }
        System.out.println("Hero with id " + id + " not found.");
        return null;
    }

    private Direction parseDirection(String token) {
        switch (token.toUpperCase(Locale.US)) {
            case "N":
            case "UP":
                return Direction.NORTH;
            case "S":
            case "DOWN":
                return Direction.SOUTH;
            case "E":
            case "RIGHT":
                return Direction.EAST;
            case "W":
            case "LEFT":
                return Direction.WEST;
            default:
                return null;
        }
    }

    private MonsterFactory buildFactory() {
        return (lane, level) -> {
            if (monsterTemplates.isEmpty()) {
                return null;
            }
            Monster template = monsterTemplates.get(random.nextInt(monsterTemplates.size()));
            Monster monster = cloneMonster(template);
            monster.scaleStats(level);
            return monster;
        };
    }

    private Monster cloneMonster(Monster template) {
        if (template instanceof Dragon) {
            return new Dragon(template.getName(), template.getLevel(), template.getBaseDamage(), template.getDefense(), template.getDodgeChance());
        }
        if (template instanceof Exoskeleton) {
            return new Exoskeleton(template.getName(), template.getLevel(), template.getBaseDamage(), template.getDefense(), template.getDodgeChance());
        }
        if (template instanceof Spirit) {
            return new Spirit(template.getName(), template.getLevel(), template.getBaseDamage(), template.getDefense(), template.getDodgeChance());
        }
        throw new IllegalArgumentException("Unsupported monster type: " + template.getClass());
    }

    private void pruneDefeatedHeroes() {
        activeHeroes.removeIf(hero -> hero.isFainted() || !board.getHeroId(hero).isPresent());
    }
}
