package me.wolf.wskywars.game;

import me.wolf.wskywars.arena.Arena;

public class Game {

    private final Arena arena;
    private GameState gameState;

    public Game(final Arena arena) {
        this.arena = arena;
        this.gameState = GameState.READY;
    }

    public Arena getArena() {
        return arena;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
