package dev.emortal.minestom.minesweeper;

import dev.emortal.minestom.gamesdk.MinestomGameServer;
import dev.emortal.minestom.gamesdk.config.GameSdkConfig;
import dev.emortal.minestom.minesweeper.game.MinesweeperGame;
import dev.emortal.minestom.minesweeper.map.MapManager;
import dev.emortal.minestom.minesweeper.util.MineIndicatorLoader;

public final class Main {
    private static final int MIN_PLAYERS = 1;

    public static void main(String[] args) {
        MineIndicatorLoader.loadAll();

        MinestomGameServer.create(moduleManager -> {
            MapManager mapManager = new MapManager();

            return GameSdkConfig.builder()
                    .minPlayers(MIN_PLAYERS)
                    .finishBehaviour(GameSdkConfig.FinishBehaviour.REQUEUE)
                    .gameCreator(info -> new MinesweeperGame(info, mapManager.createMap()))
                    .build();
        });
    }
}
