package dev.emortal.minestom.minesweeper.game;

import dev.emortal.minestom.minesweeper.board.Board;
import dev.emortal.minestom.minesweeper.board.BoardSettings;
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
    private boolean firstClick = true;

    public InteractionManager(@NotNull MinesweeperGame game, @NotNull BoardMap map) {
        this.game = game;
        this.map = map;
        this.viewManager = new ViewManager(map);
        this.blockUpdater = new BlockUpdater(map, viewManager);

        game.getEventNode().addListener(PlayerBlockBreakEvent.class, this::onBreak);
        game.getEventNode().addListener(PlayerBlockInteractEvent.class, this::onClick);
    }

    public void onBreak(@NotNull PlayerBlockBreakEvent event) {
        final Player player = event.getPlayer();
        final int x = event.getBlockPosition().blockX();
        final int y = event.getBlockPosition().blockY();
        final int z = event.getBlockPosition().blockZ();

        if (y != MapManager.FLOOR_HEIGHT || isOutsideBoard(x, z)) return;
        if (event.getInstance().getBlock(x, y + 1, z).name().endsWith("carpet")) return;
        if (!player.getItemInMainHand().isAir()) return;

        final Board board = map.board();
        if (board.isMine(x, z)) {
            playMineSound(player);
            blockUpdater.revealMines(x, z);
            game.lose();
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
            return;
        }

        if (blocksToChange.isEmpty()) return;
        blockUpdater.updateBlocks(blocksToChange, player);
    }

    public void onClick(@NotNull PlayerBlockInteractEvent event) {
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

        if (y != MapManager.FLOOR_HEIGHT || isOutsideBoard(x, z)) {
            event.setCancelled(true);
            return;
        }

        if (instance.getBlock(x, y + 1, z).name().endsWith("carpet")) {
            event.setCancelled(true);
            return;
        }

        if (viewManager.isRevealed(x, z) && !map.board().isMine(x, z)) {
            event.setCancelled(true);
            return;
        }

        instance.setBlock(x, y + 1, z, player.getTag(PlayerTags.COLOR).carpet());
        player.playSound(Sound.sound(SoundEvent.ENTITY_ENDER_DRAGON_FLAP, Sound.Source.MASTER, 0.6F, 2F));
    }

    private boolean isOutsideBoard(int x, int y) {
        final BoardSettings settings = map.board().getSettings();
        return x < 0 || x >= settings.length() || y < 0 || y >= settings.width();
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
