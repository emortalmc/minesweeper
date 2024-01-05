package dev.emortal.minestom.minesweeper.map;

import dev.emortal.minestom.minesweeper.board.BoardDimensions;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public final class MapManager {
    public static final int FLOOR_HEIGHT = 64;
    public static final Pos SPAWN_POSITION = new Pos(0.5F, FLOOR_HEIGHT + 1, 0.5F, -45F, 0F);

    private final @NotNull Instance rootInstance = InstanceCreator.createRoot();

    public @NotNull BoardMap createMap() {
        return this.createMap(BoardDimensions.DEFAULT);
    }

    public @NotNull BoardMap createMap(@NotNull BoardDimensions settings) {
        Instance instance = InstanceCreator.createCopy(this.rootInstance);
        BoardMap map = new BoardMap(instance, MapTheme.DEFAULT, settings);
        this.fillBoard(map);
        return map;
    }

    private void fillBoard(@NotNull BoardMap map) {
        BoardDimensions settings = map.dimensions();
        AbsoluteBlockBatch batch = new AbsoluteBlockBatch();

        boolean alternate = false;
        for (int x = 0; x < settings.length(); x++) {
            for (int z = 0; z < settings.width(); z++) {
                Block block = alternate ? map.theme().checkerAlternate() : map.theme().checkerMain();
                batch.setBlock(x, FLOOR_HEIGHT, z, block);
                alternate = !alternate;
            }
            alternate = !alternate;
        }

        batch.apply(map.instance(), () -> {});
    }
}
