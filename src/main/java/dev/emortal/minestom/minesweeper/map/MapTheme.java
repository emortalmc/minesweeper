package dev.emortal.minestom.minesweeper.map;

import dev.emortal.minestom.minesweeper.util.NoTickEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.GlowItemFrameMeta;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

public record MapTheme(@NotNull Block checkerMain, @NotNull Block checkerAlternate, @NotNull Block safe, @NotNull Block mine,
                       @NotNull RevealedSquarePlacer revealedSquarePlacer) {
    public static final @NotNull MapTheme DEFAULT = new MapTheme(Block.LIME_CONCRETE, Block.LIME_CONCRETE_POWDER, Block.STONE, Block.TNT,
            RevealedSquarePlacer.DISPLAY_ENTITY);

    private static final RGBLike[] COLORS = new RGBLike[]{
            new Color(0, 0, 0), // 0
            new Color(2, 2, 254), // 1
            new Color(64, 116, 64),
            new Color(254, 2, 2),
            new Color(1, 1, 132),
            new Color(132, 0, 0),
            new Color(3, 131, 133),
            new Color(132, 2, 132),
            new Color(117, 117, 117),
    };

    @FunctionalInterface
    public interface RevealedSquarePlacer {
        @NotNull RevealedSquarePlacer DEFAULT = (map, x, y, surroundingCount) -> {
            if (surroundingCount < 1) return;
            Instance instance = map.instance();

            Entity entity = new Entity(EntityType.GLOW_ITEM_FRAME);
            entity.setNoGravity(true);

            GlowItemFrameMeta meta = (GlowItemFrameMeta) entity.getEntityMeta();
            meta.setOrientation(ItemFrameMeta.Orientation.UP);
            meta.setItem(ItemStack.builder(Material.FILLED_MAP)
                    .set(ItemComponent.MAP_ID, surroundingCount)
                    .build());

            entity.setInstance(instance, new Pos(x, MapManager.FLOOR_HEIGHT + 1.0, y, 0F, -90F));
        };

        @NotNull RevealedSquarePlacer DISPLAY_ENTITY = (map, x, y, surroundingCount) -> {
            if (surroundingCount < 1) return;
            Instance instance = map.instance();

            double margin = 0.1;
            Entity background = new NoTickEntity(EntityType.BLOCK_DISPLAY);
            background.editEntityMeta(BlockDisplayMeta.class, meta -> {
                meta.setBlockState(Block.WHITE_CONCRETE);
                meta.setScale(new Vec(1.0 - margin, 0.01, 1.0 - margin));
            });
            background.setInstance(instance, new Pos(x + margin / 2, MapManager.FLOOR_HEIGHT + 1.0, y + margin / 2));

            Entity number = new NoTickEntity(EntityType.TEXT_DISPLAY);
            number.editEntityMeta(TextDisplayMeta.class, meta -> {
                meta.setText(Component.text(surroundingCount, TextColor.color(COLORS[surroundingCount])));
                meta.setBackgroundColor(0);
                meta.setScale(new Vec(3));
            });
            number.setInstance(instance, new Pos(x + 0.47, MapManager.FLOOR_HEIGHT + 1.05, y + 1, 0F, -90F));
        };

        void revealSquare(@NotNull BoardMap map, int x, int y, int surroundingCount);
    }
}
