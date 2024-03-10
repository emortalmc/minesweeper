package dev.emortal.minestom.minesweeper;

import dev.emortal.minestom.gamesdk.MinestomGameServer;
import dev.emortal.minestom.gamesdk.config.GameSdkConfig;
import dev.emortal.minestom.minesweeper.game.MinesweeperGame;
import dev.emortal.minestom.minesweeper.map.MapManager;
import dev.emortal.minestom.minesweeper.util.MineIndicatorLoader;

public final class Main {
    private static final int MIN_PLAYERS = 1;
    private static final int MAX_GAMES = 10;

    public static void main(String[] args) {
        MineIndicatorLoader.loadAll();

        MinestomGameServer.create(() -> {
            MapManager mapManager = new MapManager();

            return GameSdkConfig.builder()
                    .minPlayers(MIN_PLAYERS)
                    .maxGames(MAX_GAMES)
                    .finishBehaviour(GameSdkConfig.FinishBehaviour.REQUEUE)
                    .gameCreator(info -> new MinesweeperGame(info, mapManager.createMap()))
                    .build();
        });
    }
}
