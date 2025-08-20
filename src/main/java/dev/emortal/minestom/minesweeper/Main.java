package dev.emortal.minestom.minesweeper;

import java.io.IOException;
import java.nio.file.Paths;

import com.google.common.io.Files;

import dev.emortal.api.model.gamedata.V1MinesweeperSave;
import dev.emortal.minestom.gamesdk.MinestomGameServer;
import dev.emortal.minestom.gamesdk.config.GameSdkConfig;
import dev.emortal.minestom.minesweeper.board.Board;
import dev.emortal.minestom.minesweeper.game.MinesweeperGame;
import dev.emortal.minestom.minesweeper.map.MapManager;

public final class Main {
    private static final int MIN_PLAYERS = 1;

    public static void main(String[] args) {
        MinestomGameServer.create(moduleManager -> {
            MapManager mapManager = new MapManager();
            mapManager.registerDimensions();

            return GameSdkConfig.builder()
                    .minPlayers(MIN_PLAYERS)
                    .finishBehaviour(GameSdkConfig.FinishBehaviour.REQUEUE)
                    .gameCreator(info -> {
                        try {
                            Board board = mapManager.createMap(Files.toByteArray(Paths.get("test").toFile()));
                            return new MinesweeperGame(info, board);
                        } catch (IOException e) {
                            Board board = mapManager.createMap();
                            return new MinesweeperGame(info, board);
                        }
                    })
                    .build();
        });
    }
}
