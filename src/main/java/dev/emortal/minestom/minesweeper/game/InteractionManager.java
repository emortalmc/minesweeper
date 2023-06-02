package dev.emortal.minestom.minesweeper.game;

import dev.emortal.minestom.minesweeper.board.Board;
import dev.emortal.minestom.minesweeper.board.BoardSettings;
import dev.emortal.minestom.minesweeper.map.BoardMap;
import dev.emortal.minestom.minesweeper.map.MapManager;
import dev.emortal.minestom.minesweeper.util.Vec2;
import java.util.List;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

public final class InteractionManager {

    private final MinesweeperGame game;
    private final BoardMap map;
    private final ViewManager viewManager;
    private final BlockUpdater blockUpdater;
    private boolean firstClick = true;

    public InteractionManager(@NotNull MinesweeperGame game, @NotNull BoardMap map) {
        this.game = game;
        this.map = map;
        this.viewManager = new ViewManager(map);
        this.blockUpdater = new BlockUpdater(map, viewManager);

        game.getEventNode().addListener(PlayerBlockBreakEvent.class, this::onClick);
    }

    public void onClick(@NotNull PlayerBlockBreakEvent event) {
        final Player player = event.getPlayer();
        final int x = event.getBlockPosition().blockX();
        final int y = event.getBlockPosition().blockY();
        final int z = event.getBlockPosition().blockZ();

        if (y != MapManager.FLOOR_HEIGHT || isOutsideBoard(x, z)) return;
        if (event.getInstance().getBlock(x, y + 1, z).name().endsWith("carpet")) return;
        if (!player.getItemInMainHand().isAir()) return;

        final Board board = map.board();
        if (board.isMine(x, z)) {
            // TODO: Handle game over
            return;
        }

        final byte minesAround = board.getMinesAround(x, z);
        if (board.get(x, z) != minesAround) {
            board.set(x, z, minesAround);
            viewManager.reveal(x, z);
            playRevealSound(player);
        }

        final List<Vec2> blocksToChange;
        if (firstClick) {
            firstClick = false;
            board.populateWithMines(x, z);
            blocksToChange = viewManager.revealAroundStart(x, z);
        } else {
            blocksToChange = viewManager.revealAround(x, z);
        }

        if (viewManager.getUnrevealed() <= 0) {
            // TODO: Handle victory
            return;
        }

        if (blocksToChange.isEmpty()) return;
        blockUpdater.updateBlocks(blocksToChange, player);
    }

    private boolean isOutsideBoard(int x, int y) {
        final BoardSettings settings = map.board().getSettings();
        return x < 0 || x >= settings.length() || y < 0 || y >= settings.width();
    }

    private void playRevealSound(Player player) {
        player.playSound(Sound.sound(SoundEvent.ENTITY_ITEM_PICKUP, Sound.Source.MASTER, 0.5F, 0.7F), Sound.Emitter.self());
    }
}
