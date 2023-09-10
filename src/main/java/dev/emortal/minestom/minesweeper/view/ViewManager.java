package dev.emortal.minestom.minesweeper.view;

import dev.emortal.minestom.minesweeper.board.Board;
import dev.emortal.minestom.minesweeper.board.BoardSettings;
import dev.emortal.minestom.minesweeper.map.BoardMap;
import dev.emortal.minestom.minesweeper.map.MapManager;
import dev.emortal.minestom.minesweeper.util.Direction8;
import dev.emortal.minestom.minesweeper.util.Vec2;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class ViewManager {

    private final @NotNull BoardMap map;

    public ViewManager(@NotNull BoardMap map) {
        this.map = map;
    }

    public boolean isRevealed(int x, int y) {
        return this.map.board().isRevealed(x, y);
    }

    public void reveal(int x, int y) {
        boolean mine = this.map.board().isMine(x, y);
        if (mine) return;

        this.map.instance().setBlock(x, MapManager.FLOOR_HEIGHT, y, this.map.theme().safe());

        int mines = this.map.board().get(x, y);
        if (mines > 0) {
            this.placeMap(x, y, mines);
        }
    }

    public int getUnrevealed() {
        BoardSettings settings = this.map.board().getSettings();

        int unrevealed = 0;
        for (int x = 0; x < settings.length(); x++) {
            for (int y = 0; y < settings.width(); y++) {
                if (!this.map.board().isRevealed(x, y)) unrevealed++;
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
        Board board = this.map.board();
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
        BoardSettings settings = this.map.board().getSettings();
        return x < 0 || x >= settings.length() || y < 0 || y >= settings.width();
    }
}
