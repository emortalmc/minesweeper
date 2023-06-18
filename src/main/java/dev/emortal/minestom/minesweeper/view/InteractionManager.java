package dev.emortal.minestom.minesweeper.view;

import dev.emortal.minestom.minesweeper.board.Board;
import dev.emortal.minestom.minesweeper.board.BoardSettings;
import dev.emortal.minestom.minesweeper.game.BlockUpdater;
import dev.emortal.minestom.minesweeper.game.MinesweeperGame;
import dev.emortal.minestom.minesweeper.game.PlayerTags;
import dev.emortal.minestom.minesweeper.map.BoardMap;
import dev.emortal.minestom.minesweeper.map.MapManager;
import dev.emortal.minestom.minesweeper.util.Vec2;
import java.util.List;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

public final class InteractionManager {

    private final MinesweeperGame game;
    private final BoardMap map;
    private final ViewManager viewManager;
    private final BlockUpdater blockUpdater;
    private final ActionBar actionBar;

    private boolean firstClick = true;
    private boolean finished;

    public InteractionManager(@NotNull MinesweeperGame game, @NotNull BoardMap map) {
        this.game = game;
        this.map = map;
        this.viewManager = new ViewManager(map);
        this.blockUpdater = new BlockUpdater(map, viewManager);
        this.actionBar = new ActionBar(map.instance(), map.board().getSettings().mines());

        game.getEventNode().addListener(PlayerBlockBreakEvent.class, this::onBreak);
        game.getEventNode().addListener(PlayerBlockInteractEvent.class, this::onClick);
    }

    public void onBreak(@NotNull PlayerBlockBreakEvent event) {
        if (finished) {
            event.setCancelled(true);
            return;
        }

        final Player player = event.getPlayer();
        final int x = event.getBlockPosition().blockX();
        final int y = event.getBlockPosition().blockY();
        final int z = event.getBlockPosition().blockZ();

        if (isOutsideBoard(x, y, z)) return;
        if (event.getInstance().getBlock(x, y + 1, z).name().endsWith("carpet")) return;
        if (!player.getItemInMainHand().isAir()) return;

        final Board board = map.board();
        if (board.isMine(x, z)) {
            loseGame(player, x, z);
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
            playWinSounds(player);
            game.win();
            finished = true;
            return;
        }

        if (blocksToChange.isEmpty()) return;
        blockUpdater.updateBlocks(blocksToChange);
        actionBar.update();
    }

    private void loseGame(@NotNull Player player, int x, int z) {
        for (Player gamePlayer : this.game.getPlayers()) {
            playMineSound(gamePlayer);
            // todo send loss messages
        }

        blockUpdater.revealMines(x, z);
        game.lose();
        finished = true;
    }

    public void onClick(@NotNull PlayerBlockInteractEvent event) {
        if (finished) {
            event.setCancelled(true);
            return;
        }

        final Player player = event.getPlayer();
        final Instance instance = event.getInstance();
        final Block block = event.getBlock();

        final Point pos = event.getBlockPosition();
        final int x = pos.blockX();
        final int y = pos.blockY();
        final int z = pos.blockZ();

        if (event.getHand() != Player.Hand.MAIN) return;

        if (block.name().endsWith("carpet")) {
            instance.setBlock(pos, Block.AIR);
            player.playSound(Sound.sound(SoundEvent.ENTITY_ITEM_PICKUP, Sound.Source.MASTER, 0.6F, 1.8F));
            return;
        }

        if (isInvalidFlag(instance, x, y, z)) {
            event.setCancelled(true);
            return;
        }

        instance.setBlock(x, y + 1, z, player.getTag(PlayerTags.COLOR).carpet());
        player.playSound(Sound.sound(SoundEvent.ENTITY_ENDER_DRAGON_FLAP, Sound.Source.MASTER, 0.6F, 2F));
        actionBar.incrementFlags();
    }

    private boolean isInvalidFlag(@NotNull Instance instance, int x, int y, int z) {
        return isOutsideBoard(x, y, z) || // Out of bounds
                hasCarpetAbove(instance, x, y, z) || // Somehow clicked a block with carpet on top, cannot flag
                (viewManager.isRevealed(x, z) && !map.board().isMine(x, z)); // Is revealed and not a mine
    }

    private boolean hasCarpetAbove(@NotNull Instance instance, int x, int y, int z) {
        return instance.getBlock(x, y + 1, z).name().endsWith("carpet");
    }

    private boolean isOutsideBoard(int x, int y, int z) {
        final BoardSettings settings = map.board().getSettings();
        return y != MapManager.FLOOR_HEIGHT || x < 0 || x >= settings.length() || z < 0 || z >= settings.width();
    }

    private void playRevealSound(Player player) {
        player.playSound(Sound.sound(SoundEvent.ENTITY_ITEM_PICKUP, Sound.Source.MASTER, 0.5F, 0.7F), Sound.Emitter.self());
    }

    private void playMineSound(Player player) {
        player.playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.MASTER, 4F, 0.84F), Sound.Emitter.self());
    }

    private void playWinSounds(Player player) {
        player.playSound(Sound.sound(SoundEvent.ENTITY_VILLAGER_CELEBRATE, Sound.Source.MASTER, 1F, 1F), Sound.Emitter.self());
        player.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.MASTER, 1F, 1F), Sound.Emitter.self());
    }
}
