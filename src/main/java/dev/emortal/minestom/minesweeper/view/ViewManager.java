package dev.emortal.minestom.minesweeper.view;

import dev.emortal.minestom.minesweeper.board.Board;
import dev.emortal.minestom.minesweeper.board.BoardDimensions;
import dev.emortal.minestom.minesweeper.game.MinesweeperGame;
import dev.emortal.minestom.minesweeper.map.BoardMap;
import dev.emortal.minestom.minesweeper.map.MapManager;
import dev.emortal.minestom.minesweeper.util.Direction8;
import dev.emortal.minestom.minesweeper.util.Vec2;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class ViewManager {

    private final @NotNull MinesweeperGame game;
    private final @NotNull BoardMap map;

    public ViewManager(@NotNull MinesweeperGame game, @NotNull BoardMap map) {
        this.game = game;
        this.map = map;
    }

    public boolean isRevealed(int x, int y) {
        return this.game.getBoard().isRevealed(x, y);
    }

    public void reveal(int x, int y) {
        boolean mine = this.game.getBoard().isMine(x, y);
        if (mine) return;

        this.map.instance().setBlock(x, MapManager.FLOOR_HEIGHT, y, this.map.theme().safe());

        int mines = this.game.getBoard().get(x, y);
        if (mines > 0) {
            this.placeMap(x, y, mines);
        }
    }

    public int getUnrevealed() {
        BoardDimensions dimensions = this.map.dimensions();

        int unrevealed = 0;
        for (int x = 0; x < dimensions.length(); x++) {
            for (int y = 0; y < dimensions.width(); y++) {
                if (!this.game.getBoard().isRevealed(x, y)) unrevealed++;
            }
        }

        return unrevealed;
    }

    private void placeMap(int x, int y, int mines) {
        this.map.theme().revealedSquarePlacer().revealSquare(this.map, x, y, mines);
    }

    public @NotNull List<Vec2> revealAroundStart(int x, int y) {
        List<Vec2> changed = new ArrayList<>();
        changed.add(new Vec2(x, y));
        this.revealAround(changed, x, y);
        return changed;
    }

    public @NotNull List<Vec2> revealAround(int x, int y) {
        List<Vec2> changed = new ArrayList<>();
        this.revealAround(changed, x, y);
        return changed;
    }

    private void revealAround(@NotNull List<Vec2> changed, int x, int y) {
        Board board = this.game.getBoard();
        for (Direction8 direction : Direction8.values()) {
            int newX = x + direction.offsetX();
            int newY = y + direction.offsetY();

            if (this.isOutsideBoard(newX, newY)) continue;
            if (this.isRevealed(newX, newY)) continue;

            if (board.get(x, y) > 0) continue;
            board.setEmpty(newX, newY);
            changed.add(new Vec2(newX, newY));

            byte minesAround = board.getMinesAround(newX, newY);
            if (minesAround > 0) {
                if (board.get(x, y) > 0) continue;
                board.set(newX, newY, minesAround);
                changed.add(new Vec2(newX, newY));
                continue;
            }

            this.revealAround(changed, newX, newY);
        }
    }

    private boolean isOutsideBoard(int x, int y) {
        BoardDimensions dimensions = this.map.dimensions();
        return x < 0 || x >= dimensions.length() || y < 0 || y >= dimensions.width();
    }
}
