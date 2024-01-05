package dev.emortal.minestom.minesweeper.board;

import dev.emortal.minestom.minesweeper.util.Direction8;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.NotNull;

public final class Board {
    public static final int DEFAULT_MINES = 170;

    private final @NotNull BoardDimensions dimensions;
    private final byte[][] grid;

    private int mines;
    private boolean minesPopulated;

    public Board(@NotNull BoardDimensions settings) {
        this.dimensions = settings;
        this.mines = DEFAULT_MINES;
        this.grid = createEmptyGrid(settings);
    }

    private static byte[][] createEmptyGrid(@NotNull BoardDimensions dimensions) {
        byte[][] grid = new byte[dimensions.length()][dimensions.width()];

        for (byte[] row : grid) {
            Arrays.fill(row, SquareType.UNREVEALED);
        }

        return grid;
    }

    public int getMines() {
        return this.mines;
    }

    public boolean setMines(int mines) {
        if (this.minesPopulated) return false;

        this.mines = mines;
        return true;
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
        return x < 0 || x >= this.dimensions.length() || y < 0 || y >= this.dimensions.width();
    }

    public void populateWithMines(int clickedX, int clickedY) {
        this.minesPopulated = true;

        int length = this.dimensions.length();
        int width = this.dimensions.width();

        int mines = this.mines;
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
