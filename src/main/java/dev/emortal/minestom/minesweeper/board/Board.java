package dev.emortal.minestom.minesweeper.board;

import dev.emortal.minestom.minesweeper.util.Direction8;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.NotNull;

public final class Board {

    private final @NotNull BoardSettings settings;
    private final byte[][] grid;

    public Board(@NotNull BoardSettings settings) {
        this.settings = settings;
        this.grid = createEmptyGrid(settings);
    }

    private static byte[][] createEmptyGrid(@NotNull BoardSettings settings) {
        byte[][] grid = new byte[settings.length()][settings.width()];

        for (byte[] row : grid) {
            Arrays.fill(row, SquareType.UNREVEALED);
        }

        return grid;
    }

    public @NotNull BoardSettings getSettings() {
        return this.settings;
    }

    public boolean isMine(int x, int y) {
        return this.get(x, y) == SquareType.MINE;
    }

    public boolean isRevealed(int x, int y) {
        return this.get(x, y) != SquareType.UNREVEALED;
    }

    public byte get(int x, int y) {
        return this.grid[x][y];
    }

    public void set(int x, int y, byte value) {
        this.grid[x][y] = value;
    }

    public void setEmpty(int x, int y) {
        this.set(x, y, SquareType.NOTHING);
    }

    private void addMine(int x, int y) {
        this.set(x, y, SquareType.MINE);
    }

    public byte getMinesAround(int x, int y) {
        byte mines = 0;

        for (Direction8 direction : Direction8.values()) {
            int newX = x + direction.offsetX();
            int newY = y + direction.offsetY();
            if (this.isOutOfBounds(newX, newY)) continue;

            if (this.isMine(newX, newY)) {
                mines++;
            }
        }

        return mines;
    }

    private boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= this.settings.length() || y < 0 || y >= this.settings.width();
    }

    public void populateWithMines(int clickedX, int clickedY) {
        int length = this.settings.length();
        int width = this.settings.width();

        int mines = this.settings.mines();
        while (mines > 0) {
            int x = getRandom(length);
            int y = getRandom(width);

            if (Math.abs(clickedX - x) < 2 && Math.abs(clickedY - y) < 2) {
                continue;
            }

            if (!this.isMine(x, y)) {
                this.addMine(x, y);
                mines--;
            }
        }

        this.set(clickedX, clickedY, SquareType.NOTHING);
    }

    private static int getRandom(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }
}
