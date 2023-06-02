package dev.emortal.minestom.minesweeper.game;

import dev.emortal.minestom.minesweeper.board.Board;
import dev.emortal.minestom.minesweeper.board.BoardSettings;
import dev.emortal.minestom.minesweeper.map.BoardMap;
import dev.emortal.minestom.minesweeper.map.MapManager;
import dev.emortal.minestom.minesweeper.util.Direction8;
import dev.emortal.minestom.minesweeper.util.Vec2;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

public final class ViewManager {

    private final BoardMap map;

    public ViewManager(@NotNull BoardMap map) {
        this.map = map;
    }

    public boolean isRevealed(int x, int y) {
        return map.board().isRevealed(x, y);
    }

    public void reveal(int x, int y) {
        final boolean mine = map.board().isMine(x, y);
        if (mine) return;

        map.instance().setBlock(x, MapManager.FLOOR_HEIGHT, y, map.theme().safe());
        final int mines = map.board().get(x, y);
        if (mines > 0) placeMap(x, y, mines);
    }

    public int getUnrevealed() {
        final BoardSettings settings = map.board().getSettings();

        int unrevealed = 0;
        for (int x = 0; x < settings.length(); x++) {
            for (int y = 0; y < settings.width(); y++) {
                if (!map.board().isRevealed(x, y)) unrevealed++;
            }
        }

        return unrevealed;
    }

    private void placeMap(int x, int y, int mines) {
        map.theme().revealedSquarePlacer().revealSquare(map, new Vec(x, MapManager.FLOOR_HEIGHT + 1, y), mines);
    }

    public List<Vec2> revealAroundStart(int x, int y) {
        final List<Vec2> changed = new ArrayList<>();
        changed.add(new Vec2(x, y));
        revealAround(changed, x, y);
        return changed;
    }

    public List<Vec2> revealAround(int x, int y) {
        final List<Vec2> changed = new ArrayList<>();
        revealAround(changed, x, y);
        return changed;
    }

    private void revealAround(List<Vec2> changed, int x, int y) {
        final Board board = map.board();
        for (final Direction8 direction : Direction8.values()) {
            final int newX = x + direction.offsetX();
            final int newY = y + direction.offsetY();

            if (isOutsideBoard(newX, newY)) continue;
            if (isRevealed(newX, newY)) continue;

            if (board.get(x, y) > 0) continue;
            board.setEmpty(newX, newY);
            changed.add(new Vec2(newX, newY));

            final byte minesAround = board.getMinesAround(newX, newY);
            if (minesAround > 0) {
                if (board.get(x, y) > 0) continue;
                board.set(newX, newY, minesAround);
                changed.add(new Vec2(newX, newY));
                continue;
            }

            revealAround(changed, newX, newY);
        }
    }

    private boolean isOutsideBoard(int x, int y) {
        final BoardSettings settings = map.board().getSettings();
        return x < 0 || x >= settings.length() || y < 0 || y >= settings.width();
    }
}
