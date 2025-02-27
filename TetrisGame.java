import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class TetrisGame extends JPanel {
    private static final int ROWS = 20, COLS = 10, BLOCK_SIZE = 30;
    private Color[][] gameBoard = new Color[ROWS][COLS]; // Stores colors for locked blocks
    private Block currentBlock;
    private Timer timer;
    private boolean gameOver = false;
    private int score = 0;
    private static final int[][][] SHAPES = {
            { { 1, 1, 1, 1 } }, // I
            { { 1, 1 }, { 1, 1 } }, // O
            { { 0, 1, 0 }, { 1, 1, 1 } }, // T
            { { 1, 1, 0 }, { 0, 1, 1 } }, // S
            { { 0, 1, 1 }, { 1, 1, 0 } }, // Z
            { { 1, 0, 0 }, { 1, 1, 1 } }, // J
            { { 0, 0, 1 }, { 1, 1, 1 } } // L
    };
    private Random random = new Random();

    public TetrisGame() {
        setPreferredSize(new Dimension(COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (currentBlock == null || gameOver)
                    return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> moveLeft();
                    case KeyEvent.VK_RIGHT -> moveRight();
                    case KeyEvent.VK_DOWN -> moveDown();
                    case KeyEvent.VK_UP -> rotateBlock();
                }
                repaint();
            }
        });
        startGame();
    }

    private void startGame() {
        gameOver = false;
        gameBoard = new Color[ROWS][COLS];
        score = 0;
        generateNewBlock();
        timer = new Timer(500, e -> gameLoop());
        timer.start();
    }

    private void gameLoop() {
        if (!moveDown()) {
            lockBlock();
            if (checkGameOver()) {
                gameOver = true;
                timer.stop();
                showGameOverDialog();
            }
        }
        repaint();
    }

    private void generateNewBlock() {
        int[][] shape = SHAPES[random.nextInt(SHAPES.length)];
        Color randomColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        currentBlock = new Block(shape, COLS / 2 - shape[0].length / 2, 0, randomColor);
    }

    private boolean moveDown() {
        if (canMove(currentBlock.x, currentBlock.y + 1, currentBlock.shape)) {
            currentBlock.y++;
            return true;
        }
        return false;
    }

    private void moveLeft() {
        if (canMove(currentBlock.x - 1, currentBlock.y, currentBlock.shape)) {
            currentBlock.x--;
        }
    }

    private void moveRight() {
        if (canMove(currentBlock.x + 1, currentBlock.y, currentBlock.shape)) {
            currentBlock.x++;
        }
    }

    private void rotateBlock() {
        int[][] rotated = rotate(currentBlock.shape);
        if (canMove(currentBlock.x, currentBlock.y, rotated)) {
            currentBlock.shape = rotated;
        }
    }

    private boolean canMove(int newX, int newY, int[][] shape) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int boardX = newX + j;
                    int boardY = newY + i;
                    if (boardX < 0 || boardX >= COLS || boardY >= ROWS ||
                            (boardY >= 0 && gameBoard[boardY][boardX] != null)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void lockBlock() {

        for (int i = 0; i < currentBlock.shape.length; i++) {
            for (int j = 0; j < currentBlock.shape[i].length; j++) {
                if (currentBlock.shape[i][j] == 1) {
                    gameBoard[currentBlock.y + i][currentBlock.x + j] = currentBlock.color;
                }
            }
        }

        Color newLockedColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (gameBoard[row][col] != null) {
                    gameBoard[row][col] = newLockedColor;
                }
            }
        }

        clearRows();
        generateNewBlock();
    }

    private void clearRows() {
        for (int row = ROWS - 1; row >= 0; row--) {
            boolean fullRow = true;
            for (int col = 0; col < COLS; col++) {
                if (gameBoard[row][col] == null) {
                    fullRow = false;
                    break;
                }
            }
            if (fullRow) {
                for (int r = row; r > 0; r--) {
                    System.arraycopy(gameBoard[r - 1], 0, gameBoard[r], 0, COLS);
                }
                gameBoard[0] = new Color[COLS];
                score += 100;
                row++;
            }
        }
    }

    private boolean checkGameOver() {
        for (int col = 0; col < COLS; col++) {
            if (gameBoard[0][col] != null)
                return true;
        }
        return false;
    }

    private int[][] rotate(int[][] shape) {
        int rows = shape.length, cols = shape[0].length;
        int[][] rotated = new int[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = shape[i][j];
            }
        }
        return rotated;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(173, 216, 230),
                0, ROWS * BLOCK_SIZE, Color.WHITE,
                false);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE);

        // Draw locked blocks
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (gameBoard[row][col] != null) {
                    g.setColor(gameBoard[row][col]);
                    g.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
                g.setColor(new Color(100, 100, 100)); // Medium gray for grid lines
                g.drawRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            }
        }

        // Draw falling block
        if (currentBlock != null) {
            g.setColor(currentBlock.color);
            for (int i = 0; i < currentBlock.shape.length; i++) {
                for (int j = 0; j < currentBlock.shape[i].length; j++) {
                    if (currentBlock.shape[i][j] == 1) {
                        g.fillRect((currentBlock.x + j) * BLOCK_SIZE,
                                (currentBlock.y + i) * BLOCK_SIZE,
                                BLOCK_SIZE, BLOCK_SIZE);
                    }
                }
            }
        }
        g.setColor(new Color(0, 0, 0)); // Black for score text
        g.drawString("Score: " + score, 10, 20);
    }

    private void showGameOverDialog() {
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 16));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 14));
        UIManager.put("OptionPane.background", new Color(255, 228, 196));
        UIManager.put("Panel.background", new Color(255, 228, 196));

        int response = JOptionPane.showConfirmDialog(this,
                "Game Over! Score: " + score + "\nRestart?", "Game Over",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            startGame();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        TetrisGame game = new TetrisGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static class Block {
        int[][] shape;
        int x, y;
        Color color;

        Block(int[][] shape, int x, int y, Color color) {
            this.shape = shape;
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }
}
