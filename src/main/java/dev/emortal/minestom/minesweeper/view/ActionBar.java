package dev.emortal.minestom.minesweeper.view;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class ActionBar {

    private final Instance instance;
    private final int mines;
    private int flags;
    private final long startTime;

    public ActionBar(@NotNull Instance instance, int mines) {
        this.instance = instance;
        this.mines = mines;

        this.startTime = System.currentTimeMillis();

        // Keep action bar shown
        instance.scheduler().buildTask(this::update).repeat(TaskSchedule.tick(20)).schedule();
    }

    public void incrementFlags() {
        flags++;
        update();
    }

    public void update() {
        Duration duration = Duration.ofMillis(System.currentTimeMillis() - startTime);

        // ☠ {mines} MINES | ⚑ {flags} FLAGS | ⌚ 1m 23s
        instance.sendActionBar(
                Component.text()
                        .append(Component.text("☠ ", NamedTextColor.RED))
                        .append(Component.text(mines, NamedTextColor.RED))
                        .append(Component.text(" MINES", NamedTextColor.RED))
                        .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("⚑ ", NamedTextColor.GREEN))
                        .append(Component.text(flags, NamedTextColor.GREEN))
                        .append(Component.text(" FLAGS", NamedTextColor.GREEN))
                        .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("⌚ ", NamedTextColor.AQUA))
                        .append(Component.text(formatDuration(duration), NamedTextColor.AQUA))
        );
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long mins = duration.toMinutesPart();
        long secs = duration.toSecondsPart();

        if (hours == 0) {
            return String.format("%dm %ds", mins, secs);
        } else {
            return String.format("%dh %dm %ds", hours, mins, secs);
        }
    }
}
