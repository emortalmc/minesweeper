package dev.emortal.minestom.minesweeper.board;

import org.jetbrains.annotations.NotNull;

public record BoardSettings(int length, int width, int mines) {
    public static final @NotNull BoardSettings DEFAULT = new BoardSettings(30, 30, 170);
}
