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
import java.util.Locale;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

public final class InteractionManager {

    private final @NotNull MinesweeperGame game;
    private final @NotNull BoardMap map;
    private final @NotNull ViewManager viewManager;
    private final @NotNull BlockUpdater blockUpdater;
    private final @NotNull ActionBar actionBar;

    private boolean firstClick = true;
    private boolean finished;

    public InteractionManager(@NotNull MinesweeperGame game, @NotNull BoardMap map) {
        this.game = game;
        this.map = map;
        this.viewManager = new ViewManager(map);
        this.blockUpdater = new BlockUpdater(map, this.viewManager);
        this.actionBar = new ActionBar(map.instance(), map.board().getSettings().mines());

        game.getEventNode().addListener(PlayerBlockBreakEvent.class, this::onBreak);
        game.getEventNode().addListener(PlayerBlockInteractEvent.class, this::onClick);
    }

    public void onBreak(@NotNull PlayerBlockBreakEvent event) {
        if (this.finished) {
            event.setCancelled(true);
            return;
        }

        Instance instance = event.getInstance();
        Player player = event.getPlayer();

        Point pos = event.getBlockPosition();
        int x = pos.blockX();
        int y = pos.blockY();
        int z = pos.blockZ();

        if (this.isOutsideBoard(x, y, z)) return;
        if (this.hasCarpetAbove(instance, x, y, z)) return;
        if (!player.getItemInMainHand().isAir()) return;

        Board board = this.map.board();
        if (board.isMine(x, z)) {
            this.loseGame(player, x, z);
            return;
        }

        this.revealMinesOnBoard(player, board, x, z);
        List<Vec2> blocksToChange = this.revealBlocks(board, x, z);

        if (this.viewManager.getUnrevealed() <= 0) {
            this.win(player);
            return;
        }

        if (blocksToChange.isEmpty()) return;
        this.blockUpdater.updateBlocks(blocksToChange);
        this.actionBar.update();
    }

    public void onClick(@NotNull PlayerBlockInteractEvent event) {
        if (this.finished) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        Instance instance = event.getInstance();
        Block block = event.getBlock();

        Point pos = event.getBlockPosition();
        int x = pos.blockX();
        int y = pos.blockY();
        int z = pos.blockZ();

        if (event.getHand() != Player.Hand.MAIN) return;

        if (this.isCarpet(block)) {
            instance.setBlock(pos, Block.AIR);
            player.playSound(Sound.sound(SoundEvent.ENTITY_ITEM_PICKUP, Sound.Source.MASTER, 0.6F, 1.8F));
            return;
        }

        if (this.isInvalidFlag(instance, x, y, z)) {
            event.setCancelled(true);
            return;
        }

        instance.setBlock(x, y + 1, z, player.getTag(PlayerTags.COLOR).carpet());
        player.playSound(Sound.sound(SoundEvent.ENTITY_ENDER_DRAGON_FLAP, Sound.Source.MASTER, 0.6F, 2F));
        this.actionBar.incrementFlags();
    }

    private boolean isCarpet(@NotNull Block block) {
        return block.name().toLowerCase(Locale.ROOT).endsWith("carpet");
    }

    private void revealMinesOnBoard(@NotNull Player player, @NotNull Board board, int x, int y) {
        byte minesAround = board.getMinesAround(x, y);
        if (board.get(x, y) == minesAround) return;

        board.set(x, y, minesAround);
        this.viewManager.reveal(x, y);
        this.playRevealSound(player);
    }

    private @NotNull List<Vec2> revealBlocks(@NotNull Board board, int x, int y) {
        if (this.firstClick) {
            this.firstClick = false;
            board.populateWithMines(x, y);
            return this.viewManager.revealAroundStart(x, y);
        } else {
            return this.viewManager.revealAround(x, y);
        }
    }

    private void win(@NotNull Player player) {
        this.playWinSounds(player);
        this.game.win();
        this.finished = true;
    }

    private void loseGame(@NotNull Player player, int x, int z) {
        Component lossMessage = Component.text()
                .append(Component.text(player.getUsername(), NamedTextColor.RED))
                .append(Component.text(" clicked a bomb :\\", NamedTextColor.GRAY))
                .build();

        for (Player gamePlayer : this.game.getPlayers()) {
            this.playMineSound(gamePlayer);
            gamePlayer.sendMessage(lossMessage);
        }

        this.playMineSound(player);
        this.blockUpdater.revealMines(x, z);
        this.game.lose();
        this.finished = true;
    }

    private boolean isInvalidFlag(@NotNull Instance instance, int x, int y, int z) {
        return this.isOutsideBoard(x, y, z) || // Out of bounds
                this.hasCarpetAbove(instance, x, y, z) || // Somehow clicked a block with carpet on top, cannot flag
                (this.viewManager.isRevealed(x, z) && !this.map.board().isMine(x, z)); // Is revealed and not a mine
    }

    private boolean hasCarpetAbove(@NotNull Instance instance, int x, int y, int z) {
        return this.isCarpet(instance.getBlock(x, y + 1, z));
    }

    private boolean isOutsideBoard(int x, int y, int z) {
        BoardSettings settings = this.map.board().getSettings();
        return y != MapManager.FLOOR_HEIGHT || x < 0 || x >= settings.length() || z < 0 || z >= settings.width();
    }

    private void playRevealSound(@NotNull Player player) {
        player.playSound(Sound.sound(SoundEvent.ENTITY_ITEM_PICKUP, Sound.Source.MASTER, 0.5F, 0.7F), Sound.Emitter.self());
    }

    private void playMineSound(@NotNull Player player) {
        player.playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.MASTER, 4F, 0.84F), Sound.Emitter.self());
    }

    private void playWinSounds(@NotNull Player player) {
        player.playSound(Sound.sound(SoundEvent.ENTITY_VILLAGER_CELEBRATE, Sound.Source.MASTER, 1F, 1F), Sound.Emitter.self());
        player.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.MASTER, 1F, 1F), Sound.Emitter.self());
    }
}
