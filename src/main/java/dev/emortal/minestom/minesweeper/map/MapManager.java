package dev.emortal.minestom.minesweeper.map;

import dev.emortal.minestom.minesweeper.board.Board;
import dev.emortal.minestom.minesweeper.board.BoardSettings;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class MapManager {
    public static final int FLOOR_HEIGHT = 64;
    public static final Pos SPAWN_POSITION = new Pos(0.5F, FLOOR_HEIGHT + 1, 0.5F, -45F, 0F);
    private static final DimensionType FULLBRIGHT = DimensionType.builder(NamespaceID.from("emortalmc", "fullbright")).ambientLight(1F).build();

    static {
        MinecraftServer.getDimensionTypeManager().addDimension(FULLBRIGHT);
    }

    public @NotNull BoardMap createMap() {
        Board board = new Board(BoardSettings.DEFAULT);
        return this.createMap(board);
    }

    public @NotNull BoardMap createMap(@NotNull Board board) {
        Instance instance = this.createInstance();
        BoardMap map = new BoardMap(instance, MapTheme.DEFAULT, board);
        this.fillBoard(map);
        return map;
    }

    private Instance createInstance() {
        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer(FULLBRIGHT);
        instance.setGenerator(unit -> {
            unit.modifier().fillHeight(FLOOR_HEIGHT, 65, Block.GRASS_BLOCK);
            unit.modifier().fillHeight(60, 64, Block.DIRT);
        });
        instance.enableAutoChunkLoad(false);

        // Store the futures so we can use CompletableFuture#allOf
        Set<CompletableFuture<Chunk>> futures = new HashSet<>();

        int radius = 5;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                CompletableFuture<Chunk> future = instance.loadChunk(x, z);
                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        return instance;
    }

    private void fillBoard(@NotNull BoardMap map) {
        BoardSettings settings = map.board().getSettings();
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
