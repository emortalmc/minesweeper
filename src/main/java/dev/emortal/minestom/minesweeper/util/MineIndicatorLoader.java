package dev.emortal.minestom.minesweeper.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;
import net.minestom.server.entity.Player;
import net.minestom.server.map.framebuffers.Graphics2DFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import org.jetbrains.annotations.NotNull;

public final class MineIndicatorLoader {
    private static final List<MapDataPacket> packets = new CopyOnWriteArrayList<>();

    public static void loadAll() {
        for (int i = 1; i <= 8; i++) {
            BufferedImage image = loadImage("icons/block/" + i + ".png");
            MapDataPacket packet = generatePacketFromImage(image, i);
            packets.add(packet);
        }
    }

    public static void registerForPlayer(@NotNull Player player) {
        for (MapDataPacket packet : packets) {
            player.sendPacket(packet);
        }
    }

    private static @NotNull MapDataPacket generatePacketFromImage(@NotNull BufferedImage image, int mapId) {
        Graphics2DFramebuffer framebuffer = new Graphics2DFramebuffer();
        Graphics2D renderer = framebuffer.getRenderer();
        renderer.drawImage(image, null, 0, 0);
        return framebuffer.preparePacket(mapId);
    }

    private static @NotNull BufferedImage loadImage(@NotNull String path) {
        URL dataUrl = Thread.currentThread().getContextClassLoader().getResource(path);
        if (dataUrl == null) throw new RuntimeException("Could not find resource " + path);

        try {
            return ImageIO.read(dataUrl);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private MineIndicatorLoader() {
    }
}
