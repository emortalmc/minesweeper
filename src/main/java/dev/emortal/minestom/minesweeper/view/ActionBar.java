package dev.emortal.minestom.minesweeper.view;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import dev.emortal.minestom.minesweeper.board.Board;

import java.time.Duration;

public final class ActionBar {

    private final @NotNull Board board;
    private final @NotNull Instance instance;
    private final long startTime;

    public ActionBar(@NotNull Board board) {
        this.board = board;
        this.instance = board.getInstance();
        this.startTime = System.currentTimeMillis() - this.board.duration.toMillis();

        // Keep action bar shown
        this.instance.scheduler().buildTask(this::update).repeat(TaskSchedule.tick(20)).schedule();
    }

    public void incrementLives() {
        if (this.board.lives < 3) {
            this.board.lives++;
            this.update();
        }
    }

    public short decrementLives() {
        this.board.lives--;
        this.update();
        return this.board.lives;
    }

    public short getLives() {
        return this.board.lives;
    }

    public void incrementFlags() {
        this.board.flags++;
        this.update();
    }

    public void decrementFlags() {
        this.board.flags--;
        this.update();
    }

    public void update() {
        long now = System.currentTimeMillis();
        this.board.duration = Duration.ofMillis(now - this.startTime);

        // ☠ {mines} MINES | ⚑ {flags} FLAGS | ⌚ 1m 23s
        this.instance.sendActionBar(Component.text().append(Component.text("⚑ ", NamedTextColor.GREEN))
                .append(Component.text(this.board.flags, NamedTextColor.GREEN))
                .append(Component.text(" FLAGS", NamedTextColor.GREEN))
                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                .append(Component.text("♥ ", NamedTextColor.RED)).append(Component.text(this.board.lives, NamedTextColor.RED))
                .append(Component.text(" LIVES", NamedTextColor.RED))
                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                .append(Component.text("⌚ ", NamedTextColor.AQUA))
                .append(Component.text(this.formatDuration(this.board.duration), NamedTextColor.AQUA)));
    }

    private @NotNull String formatDuration(@NotNull Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        if (hours == 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        }
    }
}
