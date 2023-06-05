package dev.emortal.minestom.minesweeper;

import dev.emortal.api.modules.Module;
import dev.emortal.api.modules.ModuleData;
import dev.emortal.api.modules.ModuleEnvironment;
import dev.emortal.minestom.core.module.permissions.PermissionModule;
import dev.emortal.minestom.gamesdk.GameSdkModule;
import dev.emortal.minestom.gamesdk.config.GameSdkConfig;
import dev.emortal.minestom.minesweeper.game.MinesweeperGame;
import dev.emortal.minestom.minesweeper.map.MapManager;
import dev.emortal.minestom.minesweeper.util.MineIndicatorLoader;
import org.jetbrains.annotations.NotNull;

@ModuleData(name = "minesweeper", softDependencies = {GameSdkModule.class, PermissionModule.class}, required = false)
public final class MinesweeperModule extends Module {
    public static final int MIN_PLAYERS = 1;

    protected MinesweeperModule(@NotNull ModuleEnvironment environment) {
        super(environment);

        final MapManager mapManager = new MapManager();
        MineIndicatorLoader.loadAll();

        GameSdkModule.init(
                new GameSdkConfig.Builder()
                        .minPlayers(MIN_PLAYERS)
                        .gameSupplier((info, eventNode) -> new MinesweeperGame(info, eventNode, mapManager.createMap()))
                        .maxGames(5)
                        .build()
        );
    }

    @Override
    public boolean onLoad() {
        return false;
    }

    @Override
    public void onUnload() {
    }
}
