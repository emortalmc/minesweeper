package dev.emortal.minestom.minesweeper.map;

import dev.emortal.minestom.minesweeper.board.Board;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

public record BoardMap(@NotNull Instance instance, @NotNull MapTheme theme, @NotNull Board board) {
}
