package dev.emortal.minestom.minesweeper.game;

import dev.emortal.minestom.minesweeper.map.BoardMap;
import dev.emortal.minestom.minesweeper.util.Vec2;
import java.util.List;
import java.util.function.Supplier;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
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
}
