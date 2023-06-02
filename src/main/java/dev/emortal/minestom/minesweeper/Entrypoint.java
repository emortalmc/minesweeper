package dev.emortal.minestom.minesweeper;

import dev.emortal.minestom.core.MinestomServer;
import dev.emortal.minestom.gamesdk.GameSdkModule;

public final class Entrypoint {
    public static void main(String[] args) {
        new MinestomServer.Builder()
                .commonModules()
                .module(GameSdkModule.class, GameSdkModule::new)
                .module(MinesweeperModule.class, MinesweeperModule::new)
                .build();
    }
}
