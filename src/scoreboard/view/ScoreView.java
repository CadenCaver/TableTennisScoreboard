package scoreboard.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;

/**
 * View: renders the scoreboard GUI and exposes UI events.
 */
public class ScoreView extends JFrame {
    private final JLabel lblGameLeft, lblGameRight;
    private final JLabel lblSetLeft, lblSetSep, lblSetRight;
    private final JLabel lblInfo;
    private final JLabel lblRedName, lblBlueName;
    private final JComboBox<String> comboSets;
    private final JCheckBox flipCheckBox;
    private final JButton flipButton;

    private final JPanel panelLeftGame, panelRightGame;
    private final JPanel panelSetScore;

    public ScoreView() {
        super("Table Tennis Scoreboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set score panel
        lblSetLeft  = new JLabel("0", SwingConstants.CENTER);
        lblSetLeft.setFont(new Font("Arial", Font.BOLD, 144));
        lblSetSep   = new JLabel(" - ", SwingConstants.CENTER);
        lblSetSep.setFont(new Font("Arial", Font.BOLD, 144));
        lblSetRight = new JLabel("0", SwingConstants.CENTER);
        lblSetRight.setFont(new Font("Arial", Font.BOLD, 144));
        panelSetScore = new JPanel(new GridBagLayout());
        JPanel innerSetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        innerSetPanel.add(lblSetLeft);
        innerSetPanel.add(lblSetSep);
        innerSetPanel.add(lblSetRight);
        panelSetScore.add(innerSetPanel);

        // Top bar with combo and flip controls
        comboSets     = new JComboBox<>(new String[]{"Best of 3", "Best of 5", "Best of 7", "Reset Sets"});
        flipCheckBox  = new JCheckBox("Flip Sides After Each Game");
        flipButton    = new JButton("Flip Now");

        // Game score labels and panels (init before northPanel)
        lblGameLeft   = createGameLabel(Color.RED);
        lblGameRight  = createGameLabel(Color.BLUE);
        panelLeftGame  = wrap(lblGameLeft);
        panelRightGame = wrap(lblGameRight);

        // Flip controls panel
        JPanel flipControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        flipControls.add(flipCheckBox);
        flipControls.add(flipButton);

        // Top panel: controls row and centered set score row
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        // Row 1: flip controls and sets combo
        JPanel controlsRow = new JPanel(new BorderLayout());
        controlsRow.add(flipControls, BorderLayout.WEST);
        controlsRow.add(comboSets,    BorderLayout.EAST);
        controlsRow.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        northPanel.add(controlsRow);

        // Row 2: centered set score
        JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        scoreRow.add(lblSetLeft);
        scoreRow.add(lblSetSep);
        scoreRow.add(lblSetRight);
        scoreRow.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        northPanel.add(scoreRow);

        add(northPanel, BorderLayout.NORTH);

        // Info label between set and game scores
        lblInfo = new JLabel("", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 120));
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.add(lblInfo);

        // Game score panels
        JPanel gamePanel = new JPanel(new GridLayout(1, 2));
        gamePanel.add(panelLeftGame);
        gamePanel.add(panelRightGame);

        // Player name labels
        lblRedName  = new JLabel("Red Player", SwingConstants.CENTER);
        lblRedName.setFont(new Font("Arial", Font.BOLD, 48));
        lblBlueName = new JLabel("Blue Player", SwingConstants.CENTER);
        lblBlueName.setFont(new Font("Arial", Font.BOLD, 48));
        JPanel namesPanel = new JPanel(new GridLayout(1, 2));
        namesPanel.add(lblRedName);
        namesPanel.add(lblBlueName);

        // Center layout
        JPanel center = new JPanel(new BorderLayout());
        center.add(infoPanel,  BorderLayout.NORTH);
        center.add(gamePanel,  BorderLayout.CENTER);
        center.add(namesPanel, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        // Focus settings
        getContentPane().setFocusable(true);
        getContentPane().requestFocusInWindow();

        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private JLabel createGameLabel(Color color) {
        JLabel lbl = new JLabel("0", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 400));
        lbl.setForeground(color);
        return lbl;
    }

    private JPanel wrap(JLabel label) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(label);
        panel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 10));
        return panel;
    }

    public void addGameLeftClickListener(MouseListener ml) {
        lblGameLeft.addMouseListener(ml);
    }

    public void addGameRightClickListener(MouseListener ml) {
        lblGameRight.addMouseListener(ml);
    }

    public void addComboListener(ActionListener al) {
        comboSets.addActionListener(al);
    }

    public JCheckBox getFlipCheckBox() {
        return flipCheckBox;
    }

    public JButton getFlipButton() {
        return flipButton;
    }

    public JRootPane getRootPaneForKeys() {
        return this.getRootPane();
    }

    public String getComboSelection() {
        return (String) comboSets.getSelectedItem();
    }

    public void showWinner(String message) {
        // no popup anymore
    }

    public void updateGameScores(int left, int right) {
        lblGameLeft.setText(String.valueOf(left));
        lblGameRight.setText(String.valueOf(right));
    }

    public void updateSetScores(int left, int right) {
        lblSetLeft.setText(String.valueOf(left));
        lblSetRight.setText(String.valueOf(right));
    }

    public void updateInfo(String text, Color color) {
        lblInfo.setText(text);
        lblInfo.setForeground(color);
    }

    public void updateGameLabelColors(Color leftColor, Color rightColor) {
        lblGameLeft.setForeground(leftColor);
        lblGameRight.setForeground(rightColor);
    }

    public void updatePlayerNames(String redName, String blueName) {
        lblRedName.setText(redName);
        lblBlueName.setText(blueName);
    }

    public void highlightWinner(boolean redWins, boolean blueWins) {
        Border goldBorder = BorderFactory.createLineBorder(Color.ORANGE, 20);
        Border transparentBorder = BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 10);
        panelLeftGame.setBorder(redWins ? goldBorder : transparentBorder);
        panelRightGame.setBorder(blueWins ? goldBorder : transparentBorder);
    }

    public void clearWinnerHighlight() {
        Border transparentBorder = BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 10);
        panelLeftGame.setBorder(transparentBorder);
        panelRightGame.setBorder(transparentBorder);
    }

    public JComponent getFocusTarget() {
        return (JComponent) getContentPane();
    }
}
