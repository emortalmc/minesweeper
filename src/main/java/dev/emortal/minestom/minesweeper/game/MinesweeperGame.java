package dev.emortal.minestom.minesweeper.game;

import dev.emortal.api.kurushimi.KurushimiMinestomUtils;
import dev.emortal.minestom.core.Environment;
import dev.emortal.minestom.gamesdk.GameSdkModule;
import dev.emortal.minestom.gamesdk.config.GameCreationInfo;
import dev.emortal.minestom.gamesdk.game.Game;
import dev.emortal.minestom.minesweeper.map.BoardMap;
import dev.emortal.minestom.minesweeper.map.MapManager;
import dev.emortal.minestom.minesweeper.util.MineIndicatorLoader;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MinesweeperGame extends Game {
    private static final Logger LOGGER = LoggerFactory.getLogger(MinesweeperGame.class);

    private final EventNode<Event> eventNode;
    private final BoardMap map;
    private final InteractionManager interactionManager;
    private final TeamAllocator teamAllocator = new TeamAllocator();

    public MinesweeperGame(@NotNull GameCreationInfo creationInfo, @NotNull EventNode<Event> gameEventNode, @NotNull BoardMap map) {
        super(creationInfo, gameEventNode);

        this.map = map;
        this.eventNode = EventNode.event(UUID.randomUUID().toString(), EventFilter.ALL, event -> {
            if (event instanceof PlayerEvent playerEvent) {
                if (!isValidPlayerForGame(playerEvent.getPlayer())) return false;
            }
            if (event instanceof InstanceEvent instanceEvent) {
                return instanceEvent.getInstance() == map.instance();
            }
            return true;
        });
        gameEventNode.addChild(this.eventNode);

        this.interactionManager = new InteractionManager(this, map);
        this.eventNode.addListener(PlayerBlockBreakEvent.class, event -> event.setCancelled(true));

        this.eventNode.addListener(PlayerSpawnEvent.class, event -> MineIndicatorLoader.registerForPlayer(event.getPlayer()));
    }

    private boolean isValidPlayerForGame(@NotNull Player player) {
        return getGameCreationInfo().playerIds().contains(player.getUuid());
    }

    @Override
    public void onPlayerLogin(@NotNull PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        if (!getGameCreationInfo().playerIds().contains(player.getUuid())) {
            player.kick("Unexpected join (" + Environment.getHostname() + ")");
            LOGGER.info("Unexpected join for player {}", player.getUuid());
            return;
        }

        player.setRespawnPoint(MapManager.SPAWN_POSITION);
        event.setSpawningInstance(map.instance());
        players.add(player);

        player.setAutoViewable(true);
        player.setGameMode(GameMode.CREATIVE);
        teamAllocator.allocate(player);
    }

    @Override
    public void start() {
    }

    @Override
    public void cancel() {
    }

    public void lose() {
        final Title title = Title.title(Component.text("You lost!", NamedTextColor.RED), Component.text("Do better next time.", NamedTextColor.RED));
        for (final Player player : players) {
            player.showTitle(title);
        }

        map.instance().scheduler().buildTask(this::sendBackToLobby).delay(TaskSchedule.seconds(6)).schedule();
        sendBackToLobby();
    }

    private void sendBackToLobby() {
        KurushimiMinestomUtils.sendToLobby(players, this::removeGame, this::removeGame);
    }

    private void removeGame() {
        GameSdkModule.getGameManager().removeGame(this);
        cleanUp();
    }

    private void cleanUp() {
        for (final Player player : players) {
            player.kick(Component.text("The game ended but we weren't able to connect you to a lobby. Please reconnect.", NamedTextColor.RED));
        }
        MinecraftServer.getInstanceManager().unregisterInstance(map.instance());
    }

    public @NotNull EventNode<Event> getEventNode() {
        return eventNode;
    }
}
