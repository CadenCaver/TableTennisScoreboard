package scoreboard.model;

/**
 * Model: holds game and set scores and match logic.
 */
public class ScoreModel {
    public static final int WIN_GAME_SCORE = 11;
    private int gameLeft = 0, gameRight = 0;
    private int setLeft = 0, setRight = 0;
    private int bestOfSets;

    public ScoreModel(int bestOfSets) {
        this.bestOfSets = bestOfSets;
        resetSets();
    }

    public void setBestOfSets(int bestOfSets) {
        this.bestOfSets = bestOfSets;
        resetSets();
    }

    public int getGameLeft() { return gameLeft; }
    public int getGameRight() { return gameRight; }
    public int getSetLeft() { return setLeft; }
    public int getSetRight() { return setRight; }
    public int getBestOfSets() { return bestOfSets; }

    public void incrementGameLeft() {
        gameLeft++;
        checkGameEnd();
    }

    public void incrementGameRight() {
        gameRight++;
        checkGameEnd();
    }

    public void decrementGameLeft() {
        if (gameLeft > 0) gameLeft--;
    }

    public void decrementGameRight() {
        if (gameRight > 0) gameRight--;
    }

    private void checkGameEnd() {
        if ((gameLeft >= WIN_GAME_SCORE || gameRight >= WIN_GAME_SCORE)
                && Math.abs(gameLeft - gameRight) >= 2) {
            if (gameLeft > gameRight) setLeft++;
            else setRight++;
            resetGame();
        }
    }

    public void resetGame() {
        gameLeft = 0;
        gameRight = 0;
    }

    public void resetSets() {
        setLeft = 0;
        setRight = 0;
        resetGame();
    }

    public boolean isMatchWon() {
        int needed = bestOfSets / 2 + 1;
        return setLeft >= needed || setRight >= needed;
    }

    public String getWinner() {
        int needed = bestOfSets / 2 + 1;
        if (setLeft >= needed) return "Red";
        if (setRight >= needed) return "Blue";
        return null;
    }
}
