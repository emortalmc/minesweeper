package dev.emortal.minestom.minesweeper;

import dev.emortal.minestom.gamesdk.MinestomGameServer;
import dev.emortal.minestom.gamesdk.config.GameSdkConfig;
import dev.emortal.minestom.minesweeper.board.Board;
import dev.emortal.minestom.minesweeper.game.MinesweeperGame;
import dev.emortal.minestom.minesweeper.map.MapManager;

public final class Main {
    private static final int MIN_PLAYERS = 1;

    void main() {
        MinestomGameServer.create(moduleManager -> {
            MapManager mapManager = new MapManager();
            mapManager.registerDimensions();

            return GameSdkConfig.builder()
                    .minPlayers(MIN_PLAYERS)
                    .finishBehaviour(GameSdkConfig.FinishBehaviour.REQUEUE)
                    .gameCreator(info -> {
                        Board board = mapManager.createMap();
                        return new MinesweeperGame(info, board);
                    })
                    .build();
        });
    }
}
