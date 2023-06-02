package dev.emortal.minestom.minesweeper.game;

import dev.emortal.minestom.minesweeper.board.BoardSettings;
import dev.emortal.minestom.minesweeper.map.BoardMap;
import dev.emortal.minestom.minesweeper.map.MapManager;
import dev.emortal.minestom.minesweeper.util.Vec2;
import java.util.List;
import java.util.function.Supplier;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

public final class BlockUpdater {

    private final BoardMap map;
    private final ViewManager viewManager;

    public BlockUpdater(@NotNull BoardMap map, @NotNull ViewManager viewManager) {
        this.map = map;
        this.viewManager = viewManager;
    }

    public void updateBlocks(@NotNull List<Vec2> toUpdate, @NotNull Player player) {
        final Instance instance = map.instance();
        instance.scheduler().submitTask(new Supplier<>() {
            int i = 0;

            @Override
            public TaskSchedule get() {
                final float pitchOffset = ((float) i) / 50F / 5F;
                instance.playSound(Sound.sound(SoundEvent.ENTITY_ITEM_PICKUP, Sound.Source.MASTER, 0.5F, 0.7F + pitchOffset), Sound.Emitter.self());

                for (int j = 0; j < 5; j++) {
                    final Vec2 pos = toUpdate.get(i);
                    viewManager.reveal(pos.x(), pos.y());

                    i++;
                    if (i >= toUpdate.size()) return TaskSchedule.stop();
                }

                return TaskSchedule.nextTick();
            }
        });
    }

    public void revealMines(int clickedX, int clickedY) {
        final BoardSettings settings = map.board().getSettings();
        final AbsoluteBlockBatch batch = new AbsoluteBlockBatch();

        for (int x = 0; x < settings.length(); x++) {
            for (int y = 0; y < settings.width(); y++) {
                if (!map.board().isMine(x, y)) continue;
                batch.setBlock(x, MapManager.FLOOR_HEIGHT, y, map.theme().mine());
            }
        }

        batch.apply(map.instance(), () -> map.instance().setBlock(clickedX, MapManager.FLOOR_HEIGHT, clickedY, map.theme().mine()));
    }
}
