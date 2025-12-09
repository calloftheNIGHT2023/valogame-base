package legends.view;

import java.util.Optional;

import legends.world.LegendsBoard;
import legends.world.LegendsTile;
import legends.world.Position;
import model.entity.Hero;
import model.entity.Monster;

/**
 * Responsible for terminal rendering of the Legends of Valor board.
 */
public class LegendsBoardRenderer {
    private static final int CELL_WIDTH = 6;

    public void render(LegendsBoard board) {
        System.out.print(renderToString(board));
    }

    public String renderToString(LegendsBoard board) {
        StringBuilder sb = new StringBuilder();
        String horizontal = buildHorizontal(board.getSize());
        for (int row = 0; row < board.getSize(); row++) {
            sb.append(horizontal).append(System.lineSeparator());
            for (int col = 0; col < board.getSize(); col++) {
                Position position = new Position(row, col);
                sb.append("|");
                sb.append(formatCell(board, position));
            }
            sb.append("|").append(System.lineSeparator());
        }
        sb.append(horizontal).append(System.lineSeparator());
        return sb.toString();
    }

    private String formatCell(LegendsBoard board, Position position) {
        LegendsTile tile = board.getTile(position);
        Hero hero = tile.getHeroOccupant();
        Monster monster = tile.getMonsterOccupant();
        char baseSymbol = deriveBaseSymbol(tile);

        String content;
        if (hero != null && monster != null) {
            String heroToken = heroToken(board, hero);
            String monsterToken = monsterToken(board, monster);
            content = heroToken + "/" + monsterToken;
        } else if (hero != null) {
            String heroToken = heroToken(board, hero);
            content = heroToken + "-" + baseSymbol;
        } else if (monster != null) {
            String monsterToken = monsterToken(board, monster);
            content = monsterToken + "-" + baseSymbol;
        } else {
            content = " " + baseSymbol + " ";
        }
        return pad(content, CELL_WIDTH);
    }

    private String heroToken(LegendsBoard board, Hero hero) {
        Optional<Integer> id = board.getHeroId(hero);
        return "H" + id.orElse(0);
    }

    private String monsterToken(LegendsBoard board, Monster monster) {
        Optional<Integer> id = board.getMonsterId(monster);
        return "M" + id.orElse(0);
    }

    private char deriveBaseSymbol(LegendsTile tile) {
        switch (tile.getOverlay()) {
            case HERO_NEXUS:
            case MONSTER_NEXUS:
                return 'N';
            case INACCESSIBLE:
                return 'I';
            default:
                return tile.getTerrain().getSymbol();
        }
    }

    private String pad(String value, int width) {
        if (value.length() >= width) {
            return value.substring(0, width);
        }
        StringBuilder sb = new StringBuilder(value);
        while (sb.length() < width) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private String buildHorizontal(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append('+');
            for (int j = 0; j < CELL_WIDTH; j++) {
                sb.append('-');
            }
        }
        sb.append('+');
        return sb.toString();
    }
}
