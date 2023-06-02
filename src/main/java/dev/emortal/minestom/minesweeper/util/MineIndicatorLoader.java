package dev.emortal.minestom.minesweeper.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;
import net.minestom.server.entity.Player;
import net.minestom.server.map.framebuffers.Graphics2DFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MineIndicatorLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MineIndicatorLoader.class);

    private static final List<MapDataPacket> packets = new CopyOnWriteArrayList<>();

    public static void loadAll() {
        for (int i = 1; i <= 8; i++) {
            final BufferedImage image = loadImage("icons/block/" + i + ".png");
            final MapDataPacket packet = generatePacketFromImage(image, i);
            packets.add(packet);
        }
    }

    public static void registerForPlayer(@NotNull Player player) {
        for (final MapDataPacket packet : packets) {
            player.sendPacket(packet);
        }
    }

    private static MapDataPacket generatePacketFromImage(BufferedImage image, int mapId) {
        final Graphics2DFramebuffer framebuffer = new Graphics2DFramebuffer();
        final Graphics2D renderer = framebuffer.getRenderer();
        renderer.drawImage(image, null, 0, 0);
        return framebuffer.preparePacket(mapId);
    }

    private static BufferedImage loadImage(String path) {
        final URL dataUrl = Thread.currentThread().getContextClassLoader().getResource(path);
        if (dataUrl == null) {
            LOGGER.error("Could not find resource {}", path);
            throw new RuntimeException("Could not find resource " + path);
        }

        try {
            return ImageIO.read(dataUrl);
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
