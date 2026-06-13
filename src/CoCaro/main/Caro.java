package CoCaro.main;
import CoCaro.sound.SoundManager;
import CoCaro.logic.AI;
import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import CoCaro.logic.GameLogic;

public class Caro extends JFrame {

    private CardLayout layout;
    private JPanel mainPanel;

    private JButton[][] board;

    private int boardSize = 20;
    private int winCondition = 5;
    private char currentPlayer = 'X';
    private boolean gameActive = true;
    private boolean vsAI = false;

    // *** FIX CHÍNH: flag chặn người dùng click khi AI đang tính ***
    private boolean aiThinking = false;

    private int difficulty = 2;

    private JLabel status;
    private JLabel scoreLabel;
    private int xScore = 0;
    private int oScore = 0;

    // SOUND
    private boolean soundOn = true;
    private JButton soundBtn;
    private Clip bgMusic;

    // COLOR
    private final Color bgDark     = new Color(20, 20, 20);
    private final Color panelDark  = new Color(35, 35, 35);
    private final Color neonBlue   = new Color(0, 180, 255);
    private final Color neonRed    = new Color(255, 70, 70);
    private final Color neonYellow = new Color(255, 220, 0);
    private final Color btnColor   = new Color(60, 120, 200);
    private final Color exitColor  = new Color(200, 60, 60);

    public Caro() {
        setTitle("Caro 20x20");
        setSize(900, 960);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        layout = new CardLayout();
        mainPanel = new JPanel(layout);

        mainPanel.add(menuUI(),       "menu");
        mainPanel.add(difficultyUI(), "difficulty");
        mainPanel.add(gameUI(),       "game");

        add(mainPanel);
    }

    // ── BUTTON helpers ──────────────────────────────────────────────────────────

    private JButton createBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBackground(btnColor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.addActionListener(e -> { if (soundOn) SoundManager.play("click.wav"); });
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { b.setBackground(neonBlue); }
            public void mouseExited (java.awt.event.MouseEvent evt) { b.setBackground(btnColor); }
        });
        return b;
    }

    private JButton createBackButton() {
        JButton back = new JButton("←");
        back.setFont(new Font("Segoe UI", Font.BOLD, 22));
        back.setForeground(neonBlue);
        back.setBackground(bgDark);
        back.setBorderPainted(false);
        back.setFocusPainted(false);
        back.setContentAreaFilled(true);
        back.setOpaque(true);
        back.setPreferredSize(new Dimension(60, 40));
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> { if (soundOn) SoundManager.play("click.wav"); });
        return back;
    }

    /**
     * Tạo nút bo tròn nhỏ dùng cho thanh điều khiển trên cùng của Menu
     * (nút Bật/tắt âm thanh bên trái và nút Thoát game bên phải).
     *
     * @param text  nội dung hiển thị trên nút (icon/emoji hoặc chữ ngắn)
     * @param bg    màu nền của nút
     */
    private JButton createTopBarButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110, 40));
        b.addActionListener(e -> { if (soundOn) SoundManager.play("click.wav"); });
        return b;
    }

    // ── MENU ────────────────────────────────────────────────────────────────────

    /**
     * UC-08: Quay lại menu chính / Màn hình menu chính.
     * UC-07: Bật/tắt âm thanh (nút điều khiển bên trái thanh top bar).
     *
     * Bước 1: Tạo thanh top bar gồm 2 nút:
     *         - Bên trái : nút Bật/tắt âm thanh (Sound ON/OFF)
     *         - Bên phải : nút Thoát game (Exit)
     * Bước 2: Tạo phần nội dung chính của menu (Title + 2 nút chọn chế độ chơi).
     * Bước 3: Gộp top bar + nội dung chính vào 1 panel theo BorderLayout.
     */
    private JPanel menuUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bgDark);

        // ── Bước 1: Thanh top bar (trái: Sound, phải: Exit) ─────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(bgDark);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        // Nút Bật/tắt âm thanh - đặt bên trái (UC-07)
        // Quy ước hiển thị: đang ở trạng thái ON thì nút ghi "Bật: âm thanh",
        // đang ở trạng thái OFF thì nút ghi "Tắt: âm thanh".
        JButton soundToggleBtn = createTopBarButton(
                soundOn ? "Bật: âm thanh" : "Tắt: âm thanh", btnColor);
        soundToggleBtn.addActionListener(e -> {
            // UC-07 - Bước: đảo trạng thái soundOn và cập nhật giao diện nút
            soundOn = !soundOn;
            soundToggleBtn.setText(soundOn ? "Bật: âm thanh" : "Tắt: âm thanh");

            // Nếu đang có nhạc nền thì bật/tắt theo trạng thái mới
            if (bgMusic != null) {
                if (soundOn) bgMusic.start();
                else         bgMusic.stop();
            }
            // Đồng bộ với nút sound trong màn hình chơi (nếu đã được tạo)
            if (soundBtn != null) {
                soundBtn.setText(soundOn ? "Bật: âm thanh" : "Tắt: âm thanh");
            }
        });

        // Nút Thoát game - đặt bên phải
        JButton exitBtn = createTopBarButton("Thoát", exitColor);
        exitBtn.addActionListener(e -> {
            // Bước: xác nhận trước khi thoát để tránh thoát nhầm
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn thoát game không?",
                    "Xác nhận thoát",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                if (bgMusic != null) bgMusic.stop();
                System.exit(0);
            }
        });

        topBar.add(soundToggleBtn, BorderLayout.WEST);
        topBar.add(exitBtn,        BorderLayout.EAST);

        // ── Bước 2: Nội dung chính của menu (Title + chọn chế độ chơi) ──────────
        JPanel center = new JPanel(new GridLayout(3, 1, 30, 30));
        center.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));
        center.setBackground(bgDark);

        JLabel title = new JLabel("CỜ CARO", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));
        title.setForeground(neonBlue);

        JButton pvp = createBtn("Chơi 2 Người");
        JButton bot = createBtn("Chơi với Máy");

        pvp.addActionListener(e -> {
            vsAI = false;
            startGame();
        });

        bot.addActionListener(e -> {
            vsAI = true;
            layout.show(mainPanel, "difficulty");
        });

        center.add(title);
        center.add(pvp);
        center.add(bot);

        // ── Bước 3: Gộp top bar (NORTH) + center (CENTER) ───────────────────────
        root.add(topBar, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        return root;
    }

    // ── DIFFICULTY ──────────────────────────────────────────────────────────────

    private JPanel difficultyUI() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bgDark);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(bgDark);

        JButton backBtn = createBackButton();
        backBtn.addActionListener(e -> layout.show(mainPanel, "menu"));
        top.add(backBtn, BorderLayout.WEST);

        JLabel title = new JLabel("CHỌN ĐỘ KHÓ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(neonRed);
        top.add(title, BorderLayout.CENTER);

        JPanel center = new JPanel(new GridLayout(3, 1, 30, 30));
        center.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));
        center.setBackground(bgDark);

        JButton easy   = createBtn("Dễ");
        JButton normal = createBtn("Thường");
        JButton hard   = createBtn("Khó");

        easy  .addActionListener(e -> startAI(1));
        normal.addActionListener(e -> startAI(2));
        hard  .addActionListener(e -> startAI(3));

        center.add(easy);
        center.add(normal);
        center.add(hard);

        p.add(top,    BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // ── START helpers ────────────────────────────────────────────────────────────

    private void startAI(int level) {
        difficulty = level;
        vsAI = true;
        startGame();
    }

    private void startGame() {
        // Reset trạng thái game trước khi build UI mới
        currentPlayer = 'X';
        gameActive    = true;
        aiThinking    = false;  // *** reset flag ***

        mainPanel.removeAll();
        mainPanel.add(menuUI(),       "menu");
        mainPanel.add(difficultyUI(), "difficulty");
        mainPanel.add(gameUI(),       "game");
        layout.show(mainPanel, "game");
        mainPanel.revalidate();
        mainPanel.repaint();
        playBackground("bg.wav");
    }

    // ── GAME UI ──────────────────────────────────────────────────────────────────

    private JPanel gameUI() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bgDark);

        status     = new JLabel("Lượt: X", SwingConstants.CENTER);
        scoreLabel = new JLabel("X : " + xScore + "     O : " + oScore, SwingConstants.CENTER);

        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        status.setForeground(Color.WHITE);
        status.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setBackground(bgDark);
        top.add(status);
        top.add(scoreLabel);
        p.add(top, BorderLayout.NORTH);

        // Board 20x20
        board = new JButton[boardSize][boardSize];
        JPanel grid = new JPanel(new GridLayout(boardSize, boardSize, 2, 2));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        grid.setBackground(bgDark);

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                JButton btn = new JButton("");
                btn.setPreferredSize(new Dimension(42, 42));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
                btn.setBackground(panelDark);
                btn.setOpaque(true);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(true);
                btn.setForeground(Color.WHITE);

                int r = i, c = j;
                btn.addActionListener(e -> handleMove(r, c));

                board[i][j] = btn;
                grid.add(btn);
            }
        }

        JScrollPane scroll = new JScrollPane(grid);
        p.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(bgDark);

        JButton reset = createBtn("Chơi lại");
        JButton back  = createBtn("Quay lại Menu");
        soundBtn = createBtn(soundOn ? "Bật: âm thanh" : "Tắt: âm thanh");

        soundBtn.addActionListener(e -> {
            soundOn = !soundOn;
            soundBtn.setText(soundOn ? "Bật: âm thanh" : "Tắt: âm thanh");
            if (bgMusic != null) {
                if (soundOn) bgMusic.start();
                else         bgMusic.stop();
            }
        });

        reset.addActionListener(e -> reset());

        back.addActionListener(e -> {
            if (bgMusic != null) bgMusic.stop();
            aiThinking = false;  // reset khi thoát
            layout.show(mainPanel, "menu");
        });

        bottom.add(reset);
        bottom.add(soundBtn);
        bottom.add(back);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    // ── MOVE logic ───────────────────────────────────────────────────────────────

    private void handleMove(int r, int c) {
        // *** CHẶN: game chưa active, ô đã có quân, hoặc AI đang tính ***
        if (!gameActive)  return;
        if (aiThinking)   return;
        if (!board[r][c].getText().equals("")) return;

        placeMove(r, c);
    }

    /**
     * Đặt quân vào ô (r,c) - dùng chung cho người và AI.
     * Tách ra khỏi handleMove để AI gọi trực tiếp, tránh bị chặn bởi aiThinking.
     */
    private void placeMove(int r, int c) {
        board[r][c].setText("" + currentPlayer);
        board[r][c].setForeground(currentPlayer == 'X' ? neonRed : neonBlue);

        if (soundOn) SoundManager.play("move.wav");

        if (GameLogic.checkWin(board, winCondition)) {
            highlightWinner();

            if (currentPlayer == 'X') xScore++;
            else                      oScore++;

            scoreLabel.setText("X : " + xScore + "     O : " + oScore);
            status.setText("Thắng: " + currentPlayer + " 🏆");

            if (soundOn) SoundManager.play("win.wav");
            gameActive = false;
            aiThinking = false;
            return;
        }

        if (GameLogic.isFull(board)) {
            status.setText("Hòa!");
            gameActive = false;
            aiThinking = false;
            return;
        }

        // Đổi lượt
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        status.setText("Lượt: " + currentPlayer);

        // *** Nếu đến lượt AI: bật flag, chờ 400ms rồi AI đi ***
        if (vsAI && currentPlayer == 'O') {
            aiThinking = true;
            status.setText("Máy đang suy nghĩ...");

            Timer t = new Timer(400, e -> {
                if (!gameActive) {          // game kết thúc trong lúc chờ
                    aiThinking = false;
                    return;
                }
                int[] move = AI.getMove(board, difficulty);
                aiThinking = false;         // tắt flag TRƯỚC khi đặt quân
                placeMove(move[0], move[1]);
            });
            t.setRepeats(false);
            t.start();
        }
    }

    private void highlightWinner() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                String text = board[i][j].getText();
                if (text.equals("X")) {
                    board[i][j].setForeground(neonRed);
                    if (GameLogic.isWinningCell(board, i, j, 'X', winCondition)) {
                        board[i][j].setBackground(neonYellow);
                        board[i][j].setForeground(Color.BLACK);
                    }
                } else if (text.equals("O")) {
                    board[i][j].setForeground(neonBlue);
                    if (GameLogic.isWinningCell(board, i, j, 'O', winCondition)) {
                        board[i][j].setBackground(neonYellow);
                        board[i][j].setForeground(Color.BLACK);
                    }
                }
            }
        }
    }

    private void reset() {
        currentPlayer = 'X';
        gameActive    = true;
        aiThinking    = false;  // *** reset flag khi chơi lại ***

        if (status != null) status.setText("Lượt: X");
        if (board  == null) return;

        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++) {
                board[i][j].setText("");
                board[i][j].setBackground(panelDark);
                board[i][j].setForeground(Color.WHITE);
            }
    }

    private void playBackground(String file) {
        try {
            if (bgMusic != null && bgMusic.isRunning()) bgMusic.stop();
            AudioInputStream audio = AudioSystem.getAudioInputStream(
                    new File("src/assets/" + file));
            bgMusic = AudioSystem.getClip();
            bgMusic.open(audio);
            if (soundOn) bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) { /* bỏ qua nếu không có file âm thanh */ }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Caro().setVisible(true));
    }
}