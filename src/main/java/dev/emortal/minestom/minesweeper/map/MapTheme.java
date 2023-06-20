package dev.emortal.minestom.minesweeper.map;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.GlowItemFrameMeta;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.MapMeta;
import org.jetbrains.annotations.NotNull;

public record MapTheme(@NotNull Block checkerMain, @NotNull Block checkerAlternate, @NotNull Block safe, @NotNull Block mine,
                       @NotNull RevealedSquarePlacer revealedSquarePlacer) {
    public static final MapTheme DEFAULT = new MapTheme(Block.LIME_CONCRETE, Block.LIME_CONCRETE_POWDER, Block.STONE, Block.TNT, RevealedSquarePlacer.DEFAULT);

    @FunctionalInterface
    public interface RevealedSquarePlacer {
        RevealedSquarePlacer DEFAULT = (map, x, y, surroundingCount) -> {
            if (surroundingCount < 1) return;
            final Instance instance = map.instance();

            final Entity entity = new Entity(EntityType.GLOW_ITEM_FRAME);
            entity.setNoGravity(true);

            final GlowItemFrameMeta meta = (GlowItemFrameMeta) entity.getEntityMeta();
            meta.setOrientation(ItemFrameMeta.Orientation.UP);
            meta.setItem(ItemStack.builder(Material.FILLED_MAP)
                    .meta(MapMeta.class, data -> data.mapId(surroundingCount))
                    .build());

            entity.setInstance(instance, new Pos(x, MapManager.FLOOR_HEIGHT + 1, y, 0F, -90F));
        };

        void revealSquare(@NotNull BoardMap map, int x, int y, int surroundingCount);
    }
}
