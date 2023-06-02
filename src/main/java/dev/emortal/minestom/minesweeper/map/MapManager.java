package dev.emortal.minestom.minesweeper.map;

import dev.emortal.minestom.minesweeper.board.Board;
import dev.emortal.minestom.minesweeper.board.BoardSettings;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public final class MapManager {
    public static final int FLOOR_HEIGHT = 64;
    public static final Pos SPAWN_POSITION = new Pos(0.5F, FLOOR_HEIGHT + 1, 0.5F, -45F, 0F);

    public @NotNull BoardMap createMap() {
        final Board board = new Board(BoardSettings.DEFAULT);
        return createMap(board);
    }

    public @NotNull BoardMap createMap(@NotNull Board board) {
        final Instance instance = createInstance();
        final BoardMap map = new BoardMap(instance, MapTheme.DEFAULT, board);
        fillBoard(map);
        return map;
    }

    private Instance createInstance() {
        final Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.setGenerator(unit -> {
            unit.modifier().fillHeight(FLOOR_HEIGHT, 65, Block.GRASS_BLOCK);
            unit.modifier().fillHeight(60, 64, Block.DIRT);
        });
        instance.enableAutoChunkLoad(false);

        final int radius = 5;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                instance.loadChunk(x, z).join();
            }
        }

        return instance;
    }

    private void fillBoard(@NotNull BoardMap map) {
        final BoardSettings settings = map.board().getSettings();
        final AbsoluteBlockBatch batch = new AbsoluteBlockBatch();

        boolean alternate = false;
        for (int x = 0; x < settings.length(); x++) {
            for (int z = 0; z < settings.width(); z++) {
                final Block block = alternate ? map.theme().checkerAlternate() : map.theme().checkerMain();
                batch.setBlock(x, FLOOR_HEIGHT, z, block);
                alternate = !alternate;
            }
            alternate = !alternate;
        }

        batch.apply(map.instance(), () -> {});
    }
}
