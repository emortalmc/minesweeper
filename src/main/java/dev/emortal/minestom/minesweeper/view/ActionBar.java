package dev.emortal.minestom.minesweeper.view;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class ActionBar {

    private final @NotNull Instance instance;
    private final long startTime;

    private int flags;

    public ActionBar(@NotNull Instance instance) {
        this.instance = instance;
        this.startTime = System.currentTimeMillis();

        // Keep action bar shown
        this.instance.scheduler()
                .buildTask(this::update)
                .repeat(TaskSchedule.tick(20))
                .schedule();
    }

    public void incrementFlags() {
        this.flags++;
        this.update();
    }

    public void decrementFlags() {
        this.flags--;
        this.update();
    }

    public void update() {
        long now = System.currentTimeMillis();
        Duration duration = Duration.ofMillis(now - this.startTime);

        // ☠ {mines} MINES | ⚑ {flags} FLAGS | ⌚ 1m 23s
        this.instance.sendActionBar(Component.text()
                .append(Component.text("⚑ ", NamedTextColor.GREEN))
                .append(Component.text(this.flags, NamedTextColor.GREEN))
                .append(Component.text(" FLAGS", NamedTextColor.GREEN))
                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                .append(Component.text("⌚ ", NamedTextColor.AQUA))
                .append(Component.text(this.formatDuration(duration), NamedTextColor.AQUA)));
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
