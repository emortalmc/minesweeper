package dev.emortal.minestom.minesweeper.map;

import dev.emortal.minestom.minesweeper.board.Board;
import dev.emortal.minestom.minesweeper.board.BoardReader;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.ShadowColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.attribute.EnvironmentAttribute;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public final class MapManager {
    public static final int FLOOR_HEIGHT = 64;
    public static final Pos SPAWN_POSITION = new Pos(0.5F, FLOOR_HEIGHT + 1, 0.5F, -45F, 0F);

    public void registerDimensions() {
        DimensionType overworld = MinecraftServer.getDimensionTypeRegistry().get(DimensionType.OVERWORLD);

        DimensionType dimensionTypeFB = DimensionType.builder()
                .timelines(overworld.timelines())
                .setAttribute(EnvironmentAttribute.CLOUD_COLOR, ShadowColor.fromHexString("#ccffffff"))
                .setAttribute(EnvironmentAttribute.FOG_COLOR, new Color(0xc0d8ff))
                .setAttribute(EnvironmentAttribute.SKY_COLOR, new Color(0x78a7ff))
                .ambientLight(1f)
                .build();

        MinecraftServer.getDimensionTypeRegistry().register("emortalmc:fullbright", dimensionTypeFB);
    }

    private Instance createInstance(MapTheme theme) {
        DynamicRegistry<DimensionType> dimRegistry = MinecraftServer.getDimensionTypeRegistry();
        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer(dimRegistry.getKey(Key.key("emortalmc:fullbright")));

        instance.setGenerator(unit -> {
            unit.modifier().fillHeight(60, 64, Block.SMOOTH_QUARTZ);

            for (int x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                    int chunkX = unit.absoluteStart().chunkX();
                    int chunkZ = unit.absoluteStart().chunkZ();

                    boolean alternate = (x + z) % 2 == 0;
                    boolean alternateChunk = (chunkX + chunkZ) % 2 == 0;
                    Block block = alternate ? alternateChunk ? theme.unrevealedAlt() : theme.unrevealedAlt3() : alternateChunk ? theme.unrevealed() : theme.unrevealedAlt2();
                    unit.modifier().setBlock(unit.absoluteStart().add(x, 0, z).withY(MapManager.FLOOR_HEIGHT), block);
                }
            }
        });

        return instance;
    }

    public @NotNull Board createMap() {
        MapTheme theme = MapTheme.DEFAULT;
        Instance instance = createInstance(theme);

        long seed = ThreadLocalRandom.current().nextLong();

        return new Board(seed, instance, theme);
    }

    public @NotNull Board createMap(byte[] data) {
        MapTheme theme = MapTheme.DEFAULT;
        Instance instance = createInstance(theme);

        return BoardReader.read(data, instance);
    }

}
