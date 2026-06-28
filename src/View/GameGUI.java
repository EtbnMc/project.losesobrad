package View;
import Model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Controller.GameController;

public class GameGUI extends JFrame {
    private static final Color BG_DARK     = new Color(18, 18, 18);
    private static final Color BG_PANEL    = new Color(30, 30, 30);
    private static final Color COLOR_HERO  = new Color(0, 200, 255);
    private static final Color COLOR_ENEMY = new Color(220, 50, 50);
    private static final Color COLOR_BOSS  = new Color(180, 60, 220);
    private static final Color COLOR_WALL = new Color(101, 67, 33);;
    private static final Color COLOR_EMPTY = new Color(55, 55, 55);
    private static final Color COLOR_TEXT  = new Color(220, 220, 220);
    private static final Color COLOR_BTN   = new Color(60, 60, 60);
    private static final Color COLOR_CHEST = new Color(255, 200, 0);
    private static final Color COLOR_BTN_H = new Color(90, 90, 90);
    private static final Color COLOR_DOOR = new Color(255, 105, 180);
    private static final int   CELL_SIZE   = 38;

    private final GameController controller;

    private JPanel    mapPanel;
    private JTextArea logArea;
    private JLabel    heroStatusLabel;

    public GameGUI(GameController controller) {
        this.controller = controller;
        controller.setGUI(this);          // el controller necesita referencia a la GUI
        buildWindow();
    }


    private void buildWindow() {
        setTitle("MINECRAFT 2");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(8, 8));

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildMapPanel(),  BorderLayout.CENTER);
        add(buildSidePanel(), BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setupKeyboard();
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        top.setBackground(BG_PANEL);

        heroStatusLabel = new JLabel("HERO — HP: ???");
        heroStatusLabel.setForeground(COLOR_HERO);
        heroStatusLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        top.add(heroStatusLabel);

        top.add(Box.createHorizontalStrut(30));

        String[] keys = {"W↑","A←","S↓","D→","G=Save","C=Load","Q=Quit"};
        for (String k : keys) {
            JLabel lbl = new JLabel(k);
            lbl.setForeground(new Color(160, 160, 160));
            lbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
            top.add(lbl);
        }
        return top;
    }

    private JPanel buildMapPanel() {
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };
        mapPanel.setBackground(BG_DARK);
        mapPanel.setPreferredSize(new Dimension(15 * CELL_SIZE, 15 * CELL_SIZE));
        return mapPanel;
    }

    private JPanel buildSidePanel() {
        JPanel side = new JPanel(new BorderLayout(0, 6));
        side.setBackground(BG_PANEL);
        side.setPreferredSize(new Dimension(220, 15 * CELL_SIZE));
        side.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel logTitle = new JLabel("LOG");
        logTitle.setForeground(COLOR_TEXT);
        logTitle.setFont(new Font("Monospaced", Font.BOLD, 13));
        side.add(logTitle, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(22, 22, 22));
        logArea.setForeground(COLOR_TEXT);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        side.add(scroll, BorderLayout.CENTER);

        side.add(buildButtons(), BorderLayout.SOUTH);
        return side;
    }

    private JPanel buildButtons() {
        JPanel btns = new JPanel(new GridLayout(2, 2, 4, 4));
        btns.setBackground(BG_PANEL);

        String[][] defs = {{"↑ W", "W"}, {"← A", "A"}, {"↓ S", "S"}, {"→ D", "D"}};
        for (String[] def : defs) {
            JButton btn = makeBtn(def[0]);
            String cmd = def[1];
            btn.addActionListener(e -> controller.handleKey(cmd.charAt(0)));
            btns.add(btn);
        }

        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(BG_PANEL);
        wrapper.add(btns, BorderLayout.CENTER);

        JPanel saveLoad = new JPanel(new GridLayout(1, 2, 4, 0));
        saveLoad.setBackground(BG_PANEL);
        JButton saveBtn = makeBtn(" Save (G)");
        JButton loadBtn = makeBtn(" Load (C)");
        saveBtn.addActionListener(e -> controller.handleKey('G'));
        loadBtn.addActionListener(e -> controller.handleKey('C'));
        saveLoad.add(saveBtn);
        saveLoad.add(loadBtn);
        wrapper.add(saveLoad, BorderLayout.SOUTH);

        return wrapper;
    }

    private JButton makeBtn(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(COLOR_BTN);
        btn.setForeground(COLOR_TEXT);
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(COLOR_BTN_H); }
            public void mouseExited (MouseEvent e) { btn.setBackground(COLOR_BTN);   }
        });
        return btn;
    }

    private void setupKeyboard() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char c = Character.toUpperCase(e.getKeyChar());
                if ("WASDGCQ".indexOf(c) >= 0) {
                    controller.handleKey(c);
                }
            }
        });
    }

    // mapa dibujado

    private void drawMap(Graphics g) {
        char[][] grid = controller.getMap().getGrid();
        int rows = grid.length;
        int cols = grid[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int px = j * CELL_SIZE;
                int py = i * CELL_SIZE;
                char cell = grid[i][j];

                Color bg;

                if
                (cell == 'H') bg = COLOR_HERO;
                else if
                (cell == 'E') bg = COLOR_ENEMY;
                else if
                (cell == 'B') bg = COLOR_BOSS;
                else if
                (cell == Map.Wall) bg = COLOR_WALL;
                else if
                (cell == 'C')
                    bg = COLOR_CHEST;
                else if
                (cell == Map.DoorClosedSy || cell == Map.DoorOpenSy) bg = COLOR_DOOR;
                else
                    bg = COLOR_EMPTY;

                g.setColor(bg);
                g.fillRoundRect(px + 2, py + 2, CELL_SIZE - 4, CELL_SIZE - 4, 6, 6);

                if (cell != '.' && cell != Map.Wall) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Monospaced", Font.BOLD, 16));
                    FontMetrics fm = g.getFontMetrics();
                    String s = String.valueOf(cell);
                    g.drawString(s,
                            px + (CELL_SIZE - fm.stringWidth(s)) / 2,
                            py + (CELL_SIZE + fm.getAscent()) / 2 - 2);
                }

                g.setColor(bg);
                g.fillRoundRect(px + 2, py + 2, CELL_SIZE - 4, CELL_SIZE - 4, 6, 6);

                if (cell != '.' && cell != '—') {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Monospaced", Font.BOLD, 16));
                    FontMetrics fm = g.getFontMetrics();
                    String s = String.valueOf(cell);
                    g.drawString(s,
                            px + (CELL_SIZE - fm.stringWidth(s)) / 2,
                            py + (CELL_SIZE + fm.getAscent()) / 2 - 2);
                }
            }
        }
    }

    //  métodos públicos que llama el controller

    public void refreshMap() {
        mapPanel.repaint();
    }

    public void updateHeroStatus(String text) {
        heroStatusLabel.setText(text);
    }

    public void log(String message) {
        logArea.append(message + " ");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public int showBattleMenu(String heroInfo, String enemyInfo, boolean canRun) {
        String[] options = canRun
                ? new String[]{"Attack", " Heal Potion", " Run"}
                : new String[]{" Attack", " Heal Potion", " Run (locked)"};

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
        panel.setBackground(BG_PANEL);
        JLabel h = new JLabel(heroInfo); h.setForeground(COLOR_HERO);  h.setFont(new Font("Monospaced", Font.BOLD, 13));
        JLabel e = new JLabel(enemyInfo); e.setForeground(COLOR_ENEMY); e.setFont(new Font("Monospaced", Font.BOLD, 13));
        panel.add(h);
        panel.add(e);

        UIManager.put("OptionPane.background", BG_PANEL);
        UIManager.put("Panel.background", BG_PANEL);
        UIManager.put("OptionPane.messageForeground", COLOR_TEXT);

        int result = JOptionPane.showOptionDialog(
                this, panel, " BATTLE",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        return (result < 0) ? 1 : result + 1;   // esto le dará 1=atk 2=heal 3=run
    }

    public void showMessage(String title, String msg) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.PLAIN_MESSAGE);
    }
}
