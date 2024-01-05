package dev.emortal.minestom.minesweeper.board;

import org.jetbrains.annotations.NotNull;

public record BoardDimensions(int length, int width) {
    public static final @NotNull BoardDimensions DEFAULT = new BoardDimensions(30, 30);
}
