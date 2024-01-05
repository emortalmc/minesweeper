package dev.emortal.minestom.minesweeper.game;

import dev.emortal.minestom.minesweeper.board.BoardDimensions;
import dev.emortal.minestom.minesweeper.map.BoardMap;
import dev.emortal.minestom.minesweeper.map.MapManager;
import dev.emortal.minestom.minesweeper.util.Vec2;
import dev.emortal.minestom.minesweeper.view.ViewManager;
import java.util.List;
import java.util.function.Supplier;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

public final class BlockUpdater {

    private final @NotNull MinesweeperGame game;
    private final @NotNull BoardMap map;
    private final @NotNull ViewManager viewManager;

    public BlockUpdater(@NotNull MinesweeperGame game, @NotNull BoardMap map, @NotNull ViewManager viewManager) {
        this.game = game;
        this.map = map;
        this.viewManager = viewManager;
    }

    public void updateBlocks(@NotNull List<Vec2> toUpdate) {
        this.map.instance().scheduler().submitTask(new BlockUpdateTask(this.viewManager, this.map.instance(), toUpdate));
    }

    public void revealMines(int clickedX, int clickedY) {
        BoardDimensions settings = this.map.dimensions();
        AbsoluteBlockBatch batch = new AbsoluteBlockBatch();

        for (int x = 0; x < settings.length(); x++) {
            for (int y = 0; y < settings.width(); y++) {
                if (!this.game.getBoard().isMine(x, y)) continue;
                batch.setBlock(x, MapManager.FLOOR_HEIGHT, y, this.map.theme().mine());
            }
        }

        batch.apply(this.map.instance(), () -> this.map.instance().setBlock(clickedX, MapManager.FLOOR_HEIGHT, clickedY, this.map.theme().mine()));
    }

    private static final class BlockUpdateTask implements Supplier<TaskSchedule> {

        private final @NotNull ViewManager viewManager;
        private final @NotNull Instance instance;
        private final @NotNull List<Vec2> toUpdate;

        private int i = 0;

        BlockUpdateTask(@NotNull ViewManager viewManager, @NotNull Instance instance, @NotNull List<Vec2> toUpdate) {
            this.viewManager = viewManager;
            this.instance = instance;
            this.toUpdate = toUpdate;
        }

        @Override
        public @NotNull TaskSchedule get() {
            this.playUpdateSound();

            for (int j = 0; j < 5; j++) {
                Vec2 pos = this.toUpdate.get(this.i);
                this.viewManager.reveal(pos.x(), pos.y());

                this.i++;
                if (this.i >= this.toUpdate.size()) return TaskSchedule.stop();
            }

            return TaskSchedule.nextTick();
        }

        private void playUpdateSound() {
            float pitchOffset = this.i / 50F / 5F;
            this.instance.playSound(Sound.sound(SoundEvent.ENTITY_ITEM_PICKUP, Sound.Source.MASTER, 0.5F, 0.7F + pitchOffset), Sound.Emitter.self());
        }
    }
}
