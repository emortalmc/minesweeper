package dev.emortal.minestom.minesweeper.game;

import dev.emortal.minestom.gamesdk.config.GameCreationInfo;
import dev.emortal.minestom.gamesdk.game.Game;
import dev.emortal.minestom.gamesdk.util.GameWinLoseMessages;
import dev.emortal.minestom.minesweeper.map.BoardMap;
import dev.emortal.minestom.minesweeper.map.MapManager;
import dev.emortal.minestom.minesweeper.util.MineIndicatorLoader;
import dev.emortal.minestom.minesweeper.util.MinesweeperLoseMessages;
import dev.emortal.minestom.minesweeper.view.InteractionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class MinesweeperGame extends Game {

    private final @NotNull BoardMap map;

    private final TeamAllocator teamAllocator = new TeamAllocator();
    private final PlayerDisconnectHandler disconnectHandler = new PlayerDisconnectHandler(this, this.teamAllocator);

    public MinesweeperGame(@NotNull GameCreationInfo creationInfo, @NotNull BoardMap map) {
        super(creationInfo);

        this.map = map;
        new InteractionManager(this, map);

        this.getEventNode().addListener(PlayerBlockBreakEvent.class, event -> event.setCancelled(true));
        this.getEventNode().addListener(PlayerSpawnEvent.class, event -> MineIndicatorLoader.registerForPlayer(event.getPlayer()));
    }

    @Override
    public void start() {
        // This game is already in a started state when it is created, so we don't do anything on start
    }

    @Override
    public void cleanUp() {
        this.map.instance().scheduleNextTick(MinecraftServer.getInstanceManager()::unregisterInstance);
    }

    @Override
    public void onPreJoin(@NotNull Player player) {
        player.setRespawnPoint(MapManager.SPAWN_POSITION);
    }

    @Override
    public void onJoin(@NotNull Player player) {
        player.setAutoViewable(true);
        player.setGameMode(GameMode.CREATIVE);
        this.teamAllocator.allocate(player);
    }

    @Override
    public void onLeave(@NotNull Player player) {
        this.disconnectHandler.onDisconnect(player);
    }

    @Override
    public @NotNull Instance getSpawningInstance(@NotNull Player player) {
        return this.map.instance();
    }

    public void win() {
        Title title = Title.title(
                Component.text("VICTORY!", NamedTextColor.GOLD, TextDecoration.BOLD),
                Component.text(GameWinLoseMessages.randomVictory(), NamedTextColor.GRAY),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(3), Duration.ofSeconds(6))
        );
        for (Player player : this.getPlayers()) {
            player.showTitle(title);
        }

        this.map.instance().scheduler().buildTask(this::finish).delay(TaskSchedule.seconds(8)).schedule();
    }

    public void lose() {
        Title title = Title.title(
                Component.text("DEFEAT!", NamedTextColor.RED, TextDecoration.BOLD),
                Component.text(MinesweeperLoseMessages.random(), NamedTextColor.GRAY),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(4))
        );
        for (Player player : this.getPlayers()) {
            player.showTitle(title);
        }

        this.map.instance().scheduler().buildTask(this::finish).delay(TaskSchedule.seconds(4)).schedule();
    }
}
