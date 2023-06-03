package dev.emortal.minestom.minesweeper.board;

public record BoardSettings(int length, int width, int mines) {
    public static final BoardSettings DEFAULT = new BoardSettings(30, 30, 10);

    public int size() {
        return length * width;
    }
}
