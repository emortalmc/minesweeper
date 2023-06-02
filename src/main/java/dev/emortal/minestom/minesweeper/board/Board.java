package dev.emortal.minestom.minesweeper.board;

import dev.emortal.minestom.minesweeper.util.Direction8;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.NotNull;

public final class Board {

    private final BoardSettings settings;
    private final byte[][] board;

    public Board(@NotNull BoardSettings settings) {
        this.settings = settings;
        this.board = new byte[settings.length()][settings.width()];
        for (final byte[] row : board) {
            Arrays.fill(row, SquareType.UNREVEALED);
        }
    }

    public @NotNull BoardSettings getSettings() {
        return settings;
    }

    public boolean isMine(int x, int y) {
        return get(x, y) == SquareType.MINE;
    }

    public boolean isRevealed(int x, int y) {
        return get(x, y) != SquareType.UNREVEALED;
    }

    public byte get(int x, int y) {
        return board[x][y];
    }

    public void set(int x, int y, byte value) {
        board[x][y] = value;
    }

    public void setEmpty(int x, int y) {
        set(x, y, SquareType.NOTHING);
    }

    private void addMine(int x, int y) {
        board[x][y] = SquareType.MINE;
    }

    public byte getMinesAround(int x, int y) {
        byte mines = 0;

        for (final Direction8 direction : Direction8.values()) {
            final int newX = x + direction.offsetX();
            final int newY = y + direction.offsetY();

            if (newX < 0 || newX >= settings.length() || newY < 0 || newY >= settings.width()) continue;
            if (isMine(newX, newY)) mines++;
        }

        return mines;
    }

    public void populateWithMines(int clickedX, int clickedY) {
        final int length = settings.length();
        final int width = settings.width();

        int mines = settings.mines();
        while (mines > 0) {
            final int x = getRandom(length);
            final int y = getRandom(width);

            if (Math.abs(clickedX - x) < 2 && Math.abs(clickedY - y) < 2) {
                continue;
            }

            if (!isMine(x, y)) {
                addMine(x, y);
                mines--;
            }
        }

        set(clickedX, clickedY, SquareType.NOTHING);
    }

    private static int getRandom(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }
}
