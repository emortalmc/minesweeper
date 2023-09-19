package dev.emortal.minestom.minesweeper.game;

import dev.emortal.minestom.minesweeper.util.TeamColor;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public final class PlayerTags {
    public static final @NotNull Tag<TeamColor> COLOR = Tag.Byte("minesweeper:color").map(TeamColor::fromId, color -> (byte) color.ordinal());

    private PlayerTags() {
    }
}
