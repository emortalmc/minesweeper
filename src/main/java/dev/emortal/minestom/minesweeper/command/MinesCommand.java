package dev.emortal.minestom.minesweeper.command;

import dev.emortal.minestom.gamesdk.game.GameProvider;
import dev.emortal.minestom.minesweeper.board.BoardDimensions;
import dev.emortal.minestom.minesweeper.game.MinesweeperGame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MinesCommand extends Command {

    public MinesCommand(GameProvider gameProvider) {
        super("mines");

        this.setCondition(Conditions::playerOnly);

        ArgumentInteger minesArg = new ArgumentInteger("mines");
        minesArg.between(1, BoardDimensions.DEFAULT.length() * BoardDimensions.DEFAULT.width() - 1)
                .setDefaultValue(sender -> this.getDefaultValue(sender, gameProvider));

        this.addSyntax((sender, context) -> {
            MinesweeperGame game = (MinesweeperGame) gameProvider.findGame((Player) sender);
            int mineCount = context.get(minesArg);

            if (game == null) {
                sender.sendMessage(Component.text("You are not in a game!", NamedTextColor.RED));
                return;
            }

            boolean success = game.setMines(mineCount);
            if (success) {
                sender.sendMessage(Component.text("Set mines to " + mineCount, NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("You must change the mine count before the game starts.", NamedTextColor.RED));
            }
        }, minesArg);
    }

    private int getDefaultValue(@NotNull CommandSender sender, @NotNull GameProvider gameProvider) {
        if (!(sender instanceof Player player)) return 0;

        MinesweeperGame game = (MinesweeperGame) gameProvider.findGame(player);
        if (game == null) return 0;

        return game.getBoard().getMines();
    }
}
