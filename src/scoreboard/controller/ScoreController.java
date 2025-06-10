package scoreboard.controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import scoreboard.model.ScoreModel;
import scoreboard.view.ScoreView;

/**
 * Controller: handles user input, updates model, and refreshes view.
 */
public class ScoreController {
    private final ScoreModel model;
    private final ScoreView view;
    private boolean flipped = false;
    private String redPlayerName = "Red Player";
    private String bluePlayerName = "Blue Player";

    private boolean fullscreen = false;
    private GraphicsDevice gd;

    private boolean matchWon = false;
    private int postWinClickCount = 0;

    public ScoreController(ScoreModel model, ScoreView view) {
        this.model = model;
        this.view = view;
        this.gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        initController();
        refreshView();
    }

    private void initController() {
        view.addGameLeftClickListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { actionRedInc(); view.getFocusTarget().requestFocusInWindow(); }
        });
        view.addGameRightClickListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { actionBlueInc(); view.getFocusTarget().requestFocusInWindow(); }
        });

        InputMap im = view.getRootPaneForKeys().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = view.getRootPaneForKeys().getActionMap();
        im.put(KeyStroke.getKeyStroke("UP"), "redInc");
        im.put(KeyStroke.getKeyStroke("DOWN"), "blueInc");
        im.put(KeyStroke.getKeyStroke("ENTER"), "resetGame");
        im.put(KeyStroke.getKeyStroke("LEFT"), "redDec");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "blueDec");
        im.put(KeyStroke.getKeyStroke("F11"), "toggleFullscreen");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_DOWN_MASK), "toggleFullscreen");

        am.put("redInc", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { actionRedInc(); view.getFocusTarget().requestFocusInWindow(); }
        });
        am.put("blueInc", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { actionBlueInc(); view.getFocusTarget().requestFocusInWindow(); }
        });
        am.put("resetGame", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                model.resetGame();
                view.clearWinnerHighlight();
                matchWon = false;
                postWinClickCount = 0;
                view.getFocusTarget().requestFocusInWindow();
                refreshView();
            }
        });
        am.put("redDec", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { actionRedDec(); view.getFocusTarget().requestFocusInWindow(); }
        });
        am.put("blueDec", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { actionBlueDec(); view.getFocusTarget().requestFocusInWindow(); }
        });
        am.put("toggleFullscreen", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { toggleFullscreen(); }
        });

        view.addComboListener(e -> {
            String sel = view.getComboSelection();
            if ("Reset Sets".equals(sel)) {
                model.resetSets();
                flipped = false;
                view.clearWinnerHighlight();
                matchWon = false;
                postWinClickCount = 0;
                JOptionPane.showMessageDialog(view, "Scores reset.");
            } else {
                int sets = Integer.parseInt(sel.replaceAll("[^0-9]", ""));
                promptForPlayerNames();
                model.setBestOfSets(sets);
                flipped = false;
                view.clearWinnerHighlight();
                matchWon = false;
                postWinClickCount = 0;
                JOptionPane.showMessageDialog(view, "Match format set to " + sel + ".");
            }
            view.getFocusTarget().requestFocusInWindow();
            refreshView();
        });

        view.getFlipButton().addActionListener(e -> {
            flipped = !flipped;
            view.clearWinnerHighlight();
            view.getFocusTarget().requestFocusInWindow();
            refreshView();
        });
    }

    private void promptForPlayerNames() {
        JTextField redField = new JTextField(redPlayerName, 15);
        JTextField blueField = new JTextField(bluePlayerName, 15);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Red Player Name:"));
        panel.add(redField);
        panel.add(new JLabel("Blue Player Name:"));
        panel.add(blueField);

        int result = JOptionPane.showConfirmDialog(view, panel, "Enter Player Names", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            redPlayerName = redField.getText().trim().isEmpty() ? "Red Player" : redField.getText().trim();
            bluePlayerName = blueField.getText().trim().isEmpty() ? "Blue Player" : blueField.getText().trim();
        }
    }

    private void toggleFullscreen() {
        fullscreen = !fullscreen;
        view.dispose();
        view.setUndecorated(fullscreen);
        if (fullscreen) {
            gd.setFullScreenWindow(view);
        } else {
            gd.setFullScreenWindow(null);
            view.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        view.setVisible(true);
    }

    private void actionRedInc() {
        if (handlePostWinClick()) return;
        model.incrementGameLeft();
        postAction();
    }

    private void actionBlueInc() {
        if (handlePostWinClick()) return;
        model.incrementGameRight();
        postAction();
    }

    private void actionRedDec() {
        model.decrementGameLeft();
        refreshView();
    }

    private void actionBlueDec() {
        model.decrementGameRight();
        refreshView();
    }

    private boolean handlePostWinClick() {
        if (matchWon) {
            postWinClickCount++;
            if (postWinClickCount >= 2) {
                model.resetSets();
                flipped = false;
                view.clearWinnerHighlight();
                matchWon = false;
                postWinClickCount = 0;
            }
            refreshView();
            return true; // block increment
        }
        return false;
    }

    private void postAction() {
        if (model.isMatchWon()) {
            matchWon = true;
            postWinClickCount = 0;
            boolean redWins = model.getWinner().equals("Red");
            boolean blueWins = model.getWinner().equals("Blue");
            view.highlightWinner(redWins, blueWins);
        } else if (model.getGameLeft() == 0 && model.getGameRight() == 0) {
            if (view.getFlipCheckBox().isSelected()) {
                flipped = !flipped;
            }
            view.clearWinnerHighlight();
            matchWon = false;
            postWinClickCount = 0;
        }
        refreshView();
    }

    private void refreshView() {
        int leftGame = model.getGameLeft(), rightGame = model.getGameRight();
        int leftSet = model.getSetLeft(), rightSet = model.getSetRight();

        if (!flipped) {
            view.updateGameScores(leftGame, rightGame);
            view.updateSetScores(leftSet, rightSet);
            view.updateGameLabelColors(Color.RED, Color.BLUE);
            view.updatePlayerNames(redPlayerName, bluePlayerName);
        } else {
            view.updateGameScores(rightGame, leftGame);
            view.updateSetScores(rightSet, leftSet);
            view.updateGameLabelColors(Color.BLUE, Color.RED);
            view.updatePlayerNames(bluePlayerName, redPlayerName);
        }

        if (leftGame >= ScoreModel.WIN_GAME_SCORE - 1 && rightGame >= ScoreModel.WIN_GAME_SCORE - 1 && leftGame == rightGame) {
            view.updateInfo("Deuce", new Color(0, 128, 0)); // dark green
        } else if (isMatchPoint(leftGame, rightGame)) {
            view.updateInfo("Match Point", new Color(128, 0, 128));
        } else if (isGamePoint(leftGame, rightGame)) {
            view.updateInfo("Game Point", new Color(128, 0, 128));
        } else {
            view.updateInfo("", Color.BLACK);
        }
    }

    private boolean isGamePoint(int left, int right) {
        int max = Math.max(left, right);
        return max == ScoreModel.WIN_GAME_SCORE - 1 || (max > ScoreModel.WIN_GAME_SCORE - 1 && Math.abs(left - right) == 1);
    }

    private boolean isMatchPoint(int left, int right) {
        int neededSets = model.getBestOfSets() / 2 + 1;
        int max = Math.max(left, right);
        boolean gamePoint = isGamePoint(left, right);
        boolean leftSetOneAway = model.getSetLeft() == neededSets - 1 && left > right;
        boolean rightSetOneAway = model.getSetRight() == neededSets - 1 && right > left;
        return gamePoint && (leftSetOneAway || rightSetOneAway);
    }
}
