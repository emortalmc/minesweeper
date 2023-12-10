package dev.emortal.minestom.minesweeper.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class MinesweeperLoseMessages {

    private static final List<String> MESSAGES = List.of(
            "stepped on a bomb",
            "sabotaged the game",
            "was thinking too fast",
            "misplaced their flag",
            "is the boomer",
            "had skill issue"
    );

    public static @NotNull String random() {
        int length = MESSAGES.size();
        int randomIndex = ThreadLocalRandom.current().nextInt(length);
        return MESSAGES.get(randomIndex);
    }

    private MinesweeperLoseMessages() {
    }
}
