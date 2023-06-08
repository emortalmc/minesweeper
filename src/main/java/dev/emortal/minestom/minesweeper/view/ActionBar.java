package dev.emortal.minestom.minesweeper.view;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

public final class ActionBar {

    private final Instance instance;

    private final int mines;
    private int flags;

    public ActionBar(@NotNull Instance instance, int mines) {
        this.instance = instance;
        this.mines = mines;
    }

    public void incrementFlags() {
        flags++;
        update();
    }

    public void update() {
        // ☠ {mines} MINES | ⚑ {flags} FLAGS
        instance.sendActionBar(Component.text()
                .append(Component.text("☠ ", NamedTextColor.RED))
                .append(Component.text(mines, NamedTextColor.RED))
                .append(Component.text(" MINES", NamedTextColor.RED))
                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                .append(Component.text("⚑ ", NamedTextColor.GREEN))
                .append(Component.text(flags, NamedTextColor.GREEN))
                .append(Component.text(" FLAGS", NamedTextColor.GREEN)));
    }
}
