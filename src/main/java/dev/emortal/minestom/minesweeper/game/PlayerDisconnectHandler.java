package dev.emortal.minestom.minesweeper.game;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerDisconnectHandler {

    private final MinesweeperGame game;
    private final TeamAllocator teamAllocator;

    public PlayerDisconnectHandler(@NotNull MinesweeperGame game, @NotNull TeamAllocator teamAllocator) {
        this.game = game;
        this.teamAllocator = teamAllocator;
    }

    public void onDisconnect(@NotNull Player left) {
        game.getPlayers().remove(left);
        removeFromTeam(left);

        sendQuitMessage(left);
        playQuitSound();

        if (game.getPlayers().size() == 0) game.finish();
    }

    private void removeFromTeam(Player left) {
        teamAllocator.deallocate(left);
        left.removeTag(PlayerTags.COLOR);

        final Team team = left.getTeam();
        if (team != null) MinecraftServer.getTeamManager().deleteTeam(team);
    }

    private void sendQuitMessage(Player left) {
        final Component message = Component.text()
                .append(Component.text("QUIT", NamedTextColor.RED, TextDecoration.BOLD))
                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                .append(Component.text(left.getUsername(), NamedTextColor.RED))
                .build();
        game.getAudience().sendMessage(message);
    }

    private void playQuitSound() {
        final Sound sound = Sound.sound(SoundEvent.ENTITY_ITEM_PICKUP, Sound.Source.MASTER, 1F, 0.5F);
        game.getAudience().playSound(sound);
    }
}
