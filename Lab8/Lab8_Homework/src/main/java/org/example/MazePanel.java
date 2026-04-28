package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class MazePanel extends JPanel {

    static final int CELL_SIZE = 40;
    static final int MARGIN = 20;
    static final int WALL_THRESHOLD = 8;

    Cell[][] cells;
    int rows, cols;
    boolean mazeInitialized = false;

    static final Color COLOR_CELL = new Color(230, 242, 255);
    static final Color COLOR_WALL = Color.BLACK;
    static final Color COLOR_BG = Color.WHITE;
    static final Color COLOR_START = new Color(100, 200, 100);
    static final Color COLOR_END = new Color(200, 100, 100);

    public MazePanel() {
        setBackground(COLOR_BG);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mazeInitialized)
                    handleClick(e.getX(), e.getY());
            }
        });
    }

    //Compulsory
    public void initMaze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                cells[r][c] = new Cell(r, c);
        mazeInitialized = true;
        setPreferredSize(new Dimension(
                cols * CELL_SIZE + 2 * MARGIN,
                rows * CELL_SIZE + 2 * MARGIN));
        revalidate();
        repaint();
    }


    public void resetMaze() {
        if (!mazeInitialized) return;
        for (Cell[] row : cells)
            for (Cell cell : row)
                cell.reset();
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!mazeInitialized) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = MARGIN + c * CELL_SIZE;
                int y = MARGIN + r * CELL_SIZE;
                Cell cell = cells[r][c];

                if (r == 0 && c == 0)
                    g2.setColor(COLOR_START);
                else if (r == rows - 1 && c == cols - 1)
                    g2.setColor(COLOR_END);
                else
                    g2.setColor(COLOR_CELL);

                g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                g2.setColor(COLOR_WALL);
                if (cell.top) g2.drawLine(x, y, x + CELL_SIZE, y);
                if (cell.right) g2.drawLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE);
                if (cell.bottom) g2.drawLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE);
                if (cell.left) g2.drawLine(x, y, x, y + CELL_SIZE);
            }
        }

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString("S", MARGIN + 3, MARGIN + 13);
        g2.drawString("E", MARGIN + (cols - 1) * CELL_SIZE + 3,
                MARGIN + (rows - 1) * CELL_SIZE + 13);
    }


    public void randomlyRemoveWalls() {
        if (!mazeInitialized) return;
        Random rand = new Random();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (c < cols - 1 && rand.nextBoolean()) {
                    cells[r][c].right = false;
                    cells[r][c + 1].left = false;
                }
                if (r < rows - 1 && rand.nextBoolean()) {
                    cells[r][c].bottom = false;
                    cells[r + 1][c].top = false;
                }
            }
        }
        repaint();
    }

    //Homework
    void handleClick(int mx, int my) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = MARGIN + c * CELL_SIZE;
                int y = MARGIN + r * CELL_SIZE;
                Cell cell = cells[r][c];

                if (nearSegment(mx, my, x, y, x + CELL_SIZE, y)) {
                    cell.top = !cell.top;
                    if (r > 0) cells[r - 1][c].bottom = cell.top;
                    repaint();
                    return;
                }
                if (nearSegment(mx, my, x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE)) {
                    cell.bottom = !cell.bottom;
                    if (r < rows - 1) cells[r + 1][c].top = cell.bottom;
                    repaint();
                    return;
                }
                if (nearSegment(mx, my, x, y, x, y + CELL_SIZE)) {
                    cell.left = !cell.left;
                    if (c > 0) cells[r][c - 1].right = cell.left;
                    repaint();
                    return;
                }
                if (nearSegment(mx, my, x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE)) {
                    cell.right = !cell.right;
                    if (c < cols - 1) cells[r][c + 1].left = cell.right;
                    repaint();
                    return;
                }
            }
        }
    }


    boolean nearSegment(int mx, int my, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1, dy = y2 - y1;
        double len2 = dx * dx + dy * dy;
        if (len2 == 0) return false;
        double t = Math.max(0, Math.min(1, ((mx - x1) * dx + (my - y1) * dy) / len2));
        double px = x1 + t * dx, py = y1 + t * dy;
        return Math.hypot(mx - px, my - py) < WALL_THRESHOLD;
    }


    public boolean validateMaze() {
        if (!mazeInitialized) return false;

        boolean[][] seen = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{0, 0});
        seen[0][0] = true;

        int[] dr = {-1, 0, 1, 0};
        int[] dc = {0, 1, 0, -1};

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1];

            if (r == rows - 1 && c == cols - 1) return true;

            for (int d = 0; d < 4; d++) {
                int nr = r + dr[d];
                int nc = c + dc[d];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (seen[nr][nc]) continue;

                Cell cell = cells[r][c];
                boolean wall = (d == 0) ? cell.top
                        : (d == 1) ? cell.right
                        : (d == 2) ? cell.bottom
                        : cell.left;

                if (!wall) {
                    seen[nr][nc] = true;
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        return false;
    }

    public void exportPNG(String filePath) throws IOException {
        int w = cols * CELL_SIZE + 2 * MARGIN;
        int h = rows * CELL_SIZE + 2 * MARGIN;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(COLOR_BG);
        g2.fillRect(0, 0, w, h);
        paintComponent(g2);
        g2.dispose();
        ImageIO.write(img, "PNG", new File(filePath));
    }

    public void saveMaze(String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeInt(rows);
            oos.writeInt(cols);
            oos.writeObject(cells);
        }
    }

    public void loadMaze(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            rows = ois.readInt();
            cols = ois.readInt();
            cells = (Cell[][]) ois.readObject();
            mazeInitialized = true;
            setPreferredSize(new Dimension(
                    cols * CELL_SIZE + 2 * MARGIN,
                    rows * CELL_SIZE + 2 * MARGIN));
            revalidate();
            repaint();
        }
    }
}