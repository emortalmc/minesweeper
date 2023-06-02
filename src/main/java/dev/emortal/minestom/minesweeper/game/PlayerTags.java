package dev.emortal.minestom.minesweeper.game;

import dev.emortal.minestom.minesweeper.util.TeamColor;
import net.minestom.server.tag.Tag;

public final class PlayerTags {
    public static final Tag<TeamColor> COLOR = Tag.Byte("minesweeper:color").map(TeamColor::fromId, color -> (byte) color.ordinal());
}
