package View;
import Model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Controller.GameController;

public class GameGUI extends JFrame {
    private static final Color backgColor     = new Color(18, 18, 18);
    private static final Color panelColor    = new Color(30, 30, 30);
    private static final Color heroColor  = new Color(0, 200, 255);
    private static final Color enemyColor = new Color(220, 50, 50);
    private static final Color bossColor  = new Color(180, 60, 220);
    private static final Color wallColor = new Color(101, 67, 33);;
    private static final Color emptyColor = new Color(55, 55, 55);
    private static final Color textColor  = new Color(220, 220, 220);
    private static final Color buttonColor   = new Color(60, 60, 60);
    private static final Color chestColor = new Color(255, 200, 0);
    private static final Color buttonColorH = new Color(90, 90, 90);
    private static final Color doorColor = new Color(255, 105, 180);
    private static final int   sizeCell   = 38;

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
        setTitle("Minecraft 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setResizable(false);
        getContentPane().setBackground(backgColor);
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
        top.setBackground(panelColor);

        heroStatusLabel = new JLabel("HERO — HP: ???");
        heroStatusLabel.setForeground(heroColor);
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
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };
        mapPanel.setBackground(backgColor);
        mapPanel.setPreferredSize(new Dimension(15 * sizeCell, 15 * sizeCell));
        return mapPanel;
    }

    private JPanel buildSidePanel() {
        JPanel side = new JPanel(new BorderLayout(0, 6));
        side.setBackground(backgColor);
        side.setPreferredSize(new Dimension(220, 15 * sizeCell));
        side.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel logTitle = new JLabel("LOG");
        logTitle.setForeground(textColor);
        logTitle.setFont(new Font("Monospaced", Font.BOLD, 13));
        side.add(logTitle, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(22, 22, 22));
        logArea.setForeground(textColor);
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
        btns.setBackground(panelColor);

        String[][] defs = {{"↑ W", "W"}, {"← A", "A"}, {"↓ S", "S"}, {"→ D", "D"}};
        for (String[] def : defs) {
            JButton btn = makeBtn(def[0]);
            String cmd = def[1];
            btn.addActionListener(e -> controller.handleKey(cmd.charAt(0)));
            btns.add(btn);
        }

        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(panelColor);
        wrapper.add(btns, BorderLayout.CENTER);

        JPanel saveLoad = new JPanel(new GridLayout(1, 2, 4, 0));
        saveLoad.setBackground(panelColor);
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
        btn.setBackground(buttonColor);
        btn.setForeground(textColor);
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(buttonColorH); }
            public void mouseExited (MouseEvent e) { btn.setBackground(buttonColor);   }
        });
        return btn;
    }

    private void setupKeyboard() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
        
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
                int px = j * sizeCell;
                int py = i * sizeCell;
                char cell = grid[i][j];

                Color bg;

                if
                (cell == 'H') bg = heroColor;
                else if
                (cell == 'E') bg = enemyColor;
                else if
                (cell == 'B') bg = bossColor;
                else if
                (cell == Map.Wall) bg = wallColor;
                else if
                (cell == 'C')
                    bg = chestColor;
                else if
                (cell == Map.DoorClosedSy || cell == Map.DoorOpenSy) bg = doorColor;
                else
                    bg = emptyColor;

                g.setColor(bg);
                g.fillRoundRect(px + 2, py + 2, sizeCell - 4, sizeCell - 4, 6, 6);

                if (cell != '.' && cell != Map.Wall) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Monospaced", Font.BOLD, 16));
                    FontMetrics fm = g.getFontMetrics();
                    String s = String.valueOf(cell);
                    g.drawString(s,
                            px + (sizeCell - fm.stringWidth(s)) / 2,
                            py + (sizeCell + fm.getAscent()) / 2 - 2);
                }

                g.setColor(bg);
                g.fillRoundRect(px + 2, py + 2, sizeCell - 4, sizeCell - 4, 6, 6);

                if (cell != '.' && cell != '—') {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Monospaced", Font.BOLD, 16));
                    FontMetrics fm = g.getFontMetrics();
                    String s = String.valueOf(cell);
                    g.drawString(s,
                            px + (sizeCell - fm.stringWidth(s)) / 2,
                            py + (sizeCell + fm.getAscent()) / 2 - 2);
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
        panel.setBackground(panelColor);
        JLabel h = new JLabel(heroInfo); h.setForeground(heroColor);  h.setFont(new Font("Monospaced", Font.BOLD, 13));
        JLabel e = new JLabel(enemyInfo); e.setForeground(enemyColor); e.setFont(new Font("Monospaced", Font.BOLD, 13));
        panel.add(h);
        panel.add(e);

        UIManager.put("OptionPane.background", backgColor);
        UIManager.put("Panel.background", panelColor);
        UIManager.put("OptionPane.messageForeground", textColor);

             int result = JOptionPane.showOptionDialog(
                this, panel, " Battle",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        return (result < 0) ? 1 : result + 1;   // esto le dará 1=atk 2=heal 3=run
    }

 public void showMessage(String title, String msg) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.PLAIN_MESSAGE);
    }
}
