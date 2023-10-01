package dev.emortal.minestom.minesweeper.map;

import dev.emortal.minestom.minesweeper.util.ChunkCopyingChunkLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

final class InstanceCreator {
    private static final int CHUNK_RADIUS = 5;

    private static final DimensionType FULLBRIGHT = DimensionType.builder(NamespaceID.from("emortalmc", "fullbright")).ambientLight(1F).build();

    static {
        MinecraftServer.getDimensionTypeManager().addDimension(FULLBRIGHT);
    }

    static @NotNull Instance createRoot() {
        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer(FULLBRIGHT);

        instance.setGenerator(unit -> {
            unit.modifier().fillHeight(MapManager.FLOOR_HEIGHT, 65, Block.GRASS_BLOCK);
            unit.modifier().fillHeight(60, 64, Block.DIRT);
        });
        instance.enableAutoChunkLoad(false);

        loadAllChunks(instance);

        return instance;
    }

    static @NotNull Instance createCopy(@NotNull Instance rootInstance) {
        InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer(FULLBRIGHT);

        instance.setChunkLoader(new ChunkCopyingChunkLoader(rootInstance));
        instance.enableAutoChunkLoad(false);

        loadAllChunks(instance);

        return instance;
    }

    private static void loadAllChunks(@NotNull Instance instance) {
        // Store the futures so we can use CompletableFuture#allOf
        Set<CompletableFuture<Chunk>> futures = new HashSet<>();

        for (int x = -CHUNK_RADIUS; x <= CHUNK_RADIUS; x++) {
            for (int z = -CHUNK_RADIUS; z <= CHUNK_RADIUS; z++) {
                CompletableFuture<Chunk> future = instance.loadChunk(x, z);
                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

    private InstanceCreator() {
    }
}
