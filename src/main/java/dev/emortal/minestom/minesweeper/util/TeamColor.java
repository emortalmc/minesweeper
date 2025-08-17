package dev.emortal.minestom.minesweeper.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public enum TeamColor {

    // Ordinal is used by storage so changing order will affect stored data.
    RED(NamedTextColor.RED, Block.RED_CARPET),
    ORANGE(NamedTextColor.GOLD, Block.ORANGE_CARPET),
    CYAN(NamedTextColor.DARK_AQUA, Block.CYAN_CARPET),
    BLUE(NamedTextColor.BLUE, Block.BLUE_CARPET),
    PINK(NamedTextColor.LIGHT_PURPLE, Block.PINK_CARPET),
    PURPLE(NamedTextColor.DARK_PURPLE, Block.PURPLE_CARPET),
    AQUA(NamedTextColor.AQUA, Block.CYAN_CARPET),
    YELLOW(NamedTextColor.YELLOW, Block.YELLOW_CARPET),
    DARK_AQUA(NamedTextColor.DARK_AQUA, Block.CYAN_CARPET),
    LIGHT_GRAY(NamedTextColor.GRAY, Block.LIGHT_GRAY_CARPET);

    private static final TeamColor[] VALUES = values();

    public static @NotNull TeamColor fromId(byte id) {
        return VALUES[id];
    }

    private final @NotNull NamedTextColor color;
    private final @NotNull Block carpet;

    TeamColor(@NotNull NamedTextColor color, @NotNull Block carpet) {
        this.color = color;
        this.carpet = carpet;
    }

    public @NotNull NamedTextColor color() {
        return this.color;
    }

    public @NotNull Block carpet() {
        return this.carpet;
    }
}
