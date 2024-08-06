package dev.emortal.minestom.minesweeper.map;

import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

final class InstanceCreator {
    private static final int CHUNK_RADIUS = 5;

    private static final DimensionType FULLBRIGHT = DimensionType.builder().ambientLight(1F).build();

    static {
        MinecraftServer.getDimensionTypeRegistry().register("emortalmc:fullbright", FULLBRIGHT);
    }

    public static @NotNull Instance createWorld() {
        DynamicRegistry<DimensionType> dimRegistry = MinecraftServer.getDimensionTypeRegistry();
        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer(dimRegistry.getKey(FULLBRIGHT));

        instance.setGenerator(unit -> {
            unit.modifier().fillHeight(MapManager.FLOOR_HEIGHT, 65, Block.GRASS_BLOCK);
            unit.modifier().fillHeight(60, 64, Block.DIRT);
        });
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
