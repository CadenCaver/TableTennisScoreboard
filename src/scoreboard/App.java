package scoreboard;

import javax.swing.*;
import scoreboard.model.ScoreModel;
import scoreboard.view.ScoreView;
import scoreboard.controller.ScoreController;

/**
 * Entry point: initializes MVC components.
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ScoreModel model = new ScoreModel(3);
            ScoreView view = new ScoreView();
            new ScoreController(model, view);
        });
    }
}
