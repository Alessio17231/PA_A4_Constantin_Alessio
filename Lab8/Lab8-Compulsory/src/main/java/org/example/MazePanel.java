package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MazePanel extends JPanel {

    static final int CELL_SIZE = 40;
    static final int MARGIN = 20;

    Cell[][] cells;
    int rows, cols;
    boolean mazeInitialized = false;

    static final Color COLOR_CELL  = new Color(230, 242, 255);
    static final Color COLOR_WALL  = Color.BLACK;
    static final Color COLOR_BG    = Color.WHITE;
    static final Color COLOR_START = new Color(100, 200, 100);
    static final Color COLOR_END   = new Color(200, 100, 100);

    public MazePanel() {
        setBackground(COLOR_BG);
    }


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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
                if (cell.top)    g2.drawLine(x,y,x + CELL_SIZE, y);
                if (cell.right)  g2.drawLine(x + CELL_SIZE, y,x + CELL_SIZE, y + CELL_SIZE);
                if (cell.bottom) g2.drawLine(x,y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE);
                if (cell.left)   g2.drawLine(x,y, x,y + CELL_SIZE);
            }
        }

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString("S", MARGIN + 3, MARGIN + 13);
        g2.drawString("E", MARGIN + (cols - 1) * CELL_SIZE + 3, MARGIN + (rows - 1) * CELL_SIZE + 13);
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
}