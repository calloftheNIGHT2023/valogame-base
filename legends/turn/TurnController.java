package legends.turn;

import legends.GameContext;
import legends.combat.CombatResolver;
import legends.io.IO;
import legends.monster.MonsterManager;

/**
 * Skeleton for the round/turn flow (C1). Person 3 owns the full implementation.
 * The class currently exposes a single {@link #run()} method that will control
 * the high-level loop once the remaining subsystems are ready.
 */
public class TurnController {

    private final GameContext context;
    private final IO io;
    private final CombatResolver combatResolver;

    public TurnController(GameContext context, IO io, CombatResolver combatResolver) {
        this.context = context;
        this.io = io;
        this.combatResolver = combatResolver;
    }

    public void run() {
        // Implement the full round structure.
        io.println("Turn controller not yet implemented. Ready for Person 3.");
    }

    protected MonsterManager monsters() {
        return context.getMonsterManager();
    }

    protected CombatResolver combat() {
        return combatResolver;
    }
}

