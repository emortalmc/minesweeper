package dev.emortal.minestom.minesweeper.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public enum TeamColor {

    BLACK(NamedTextColor.BLACK, Block.BLACK_CARPET),
    DARK_BLUE(NamedTextColor.DARK_BLUE, Block.BLUE_CARPET),
    DARK_GREEN(NamedTextColor.DARK_GREEN, Block.GREEN_CARPET),
    DARK_PURPLE(NamedTextColor.DARK_PURPLE, Block.PURPLE_CARPET),
    GOLD(NamedTextColor.GOLD, Block.ORANGE_CARPET),
    GRAY(NamedTextColor.GRAY, Block.LIGHT_GRAY_CARPET),
    DARK_GRAY(NamedTextColor.DARK_GRAY, Block.GRAY_CARPET),
    BLUE(NamedTextColor.BLUE, Block.CYAN_CARPET),
    GREEN(NamedTextColor.GREEN, Block.LIME_CARPET),
    AQUA(NamedTextColor.AQUA, Block.LIGHT_BLUE_CARPET),
    RED(NamedTextColor.RED, Block.RED_CARPET),
    LIGHT_PURPLE(NamedTextColor.LIGHT_PURPLE, Block.MAGENTA_CARPET),
    YELLOW(NamedTextColor.YELLOW, Block.YELLOW_CARPET),
    WHITE(NamedTextColor.WHITE, Block.WHITE_CARPET);

    private static final TeamColor[] VALUES = values();

    public static @NotNull TeamColor fromId(byte id) {
        return VALUES[id];
    }

    private final NamedTextColor color;
    private final Block carpet;

    TeamColor(NamedTextColor color, Block carpet) {
        this.color = color;
        this.carpet = carpet;
    }

    public @NotNull NamedTextColor color() {
        return color;
    }

    public @NotNull Block carpet() {
        return carpet;
    }
}
