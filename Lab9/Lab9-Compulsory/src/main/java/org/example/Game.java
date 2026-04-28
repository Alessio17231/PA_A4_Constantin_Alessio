package org.example;

import java.io.*;
import java.util.*;

public class Game {

    private final Cell[][] cells;
    private final int rows, cols;

    private final Bunny bunny;
    private final List<Robot> robots;

    private final Actor[][] occupied;

    public Game(Cell[][] cells, int rows, int cols, int numRobots) {
        this.cells = cells;
        this.rows = rows;
        this.cols = cols;
        this.occupied = new Actor[rows][cols];

        Random rand = new Random();

        int br = rand.nextInt(rows);
        int bc = rand.nextInt(cols);
        bunny = new Bunny(br, bc, this);
        occupied[br][bc] = bunny;

        robots = new ArrayList<>();
        for (int i = 0; i < numRobots; i++) {
            int rr, rc;
            do {
                rr = rand.nextInt(rows);
                rc = rand.nextInt(cols);
            } while (occupied[rr][rc] != null);

            Robot robot = new Robot("Robot" + (i + 1), rr, rc, this);
            robots.add(robot);
            occupied[rr][rc] = robot;
        }
    }


    public List<Cell> getNeighbors(int r, int c) {
        List<Cell> result = new ArrayList<>();
        Cell cell = cells[r][c];
        if (!cell.isTop() && r > 0) result.add(cells[r - 1][c]);
        if (!cell.isRight() && c < cols - 1) result.add(cells[r][c + 1]);
        if (!cell.isBottom() && r < rows - 1) result.add(cells[r + 1][c]);
        if (!cell.isLeft() && c > 0) result.add(cells[r][c - 1]);
        return result;
    }


    public synchronized void moveActor(Actor actor, int newRow, int newCol) {
        if (occupied[newRow][newCol] != null) return;

        occupied[actor.getRow()][actor.getCol()] = null;
        actor.setRow(newRow);
        actor.setCol(newCol);
        occupied[newRow][newCol] = actor;

        printState();
    }


    public synchronized void printState() {
        System.out.println();

        for (int r = 0; r < rows; r++) {

            StringBuilder topLine = new StringBuilder();
            for (int c = 0; c < cols; c++) {
                topLine.append("+");
                topLine.append(cells[r][c].isTop() ? "--" : "  ");
            }
            topLine.append("+");
            System.out.println(topLine);

            StringBuilder midLine = new StringBuilder();
            for (int c = 0; c < cols; c++) {
                midLine.append(cells[r][c].isLeft() ? "|" : " ");
                Actor a = occupied[r][c];
                if (a instanceof Bunny) midLine.append("B ");
                else if (a instanceof Robot) midLine.append("R ");
                else midLine.append("  ");
            }
            midLine.append(cells[r][cols - 1].isRight() ? "|" : " ");
            System.out.println(midLine);
        }

        StringBuilder bottomLine = new StringBuilder();
        for (int c = 0; c < cols; c++) {
            bottomLine.append("+");
            bottomLine.append(cells[rows - 1][c].isBottom() ? "--" : "  ");
        }
        bottomLine.append("+");
        System.out.println(bottomLine);
    }


    public static Cell[][] loadMaze(String filePath)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            int rows = ois.readInt();
            int cols = ois.readInt();
            return (Cell[][]) ois.readObject();
        }
    }

    public boolean isOver() {
        return false;
    }


    public static void main(String[] args) throws Exception {
        Cell[][] cells = loadMaze("maze6x6.dat");
        int rows = cells.length;
        int cols = cells[0].length;

        System.out.println("Labirint incarcat: " + rows + "x" + cols);

        Game game = new Game(cells, rows, cols, 3);

        System.out.println("Jocul incepe! (Ctrl+C pentru a opri)");
        game.printState();

        Thread bunnyThread = new Thread(game.bunny);
        List<Thread> robotThreads = new ArrayList<>();
        for (Robot robot : game.robots)
            robotThreads.add(new Thread(robot));

        bunnyThread.start();
        for (Thread t : robotThreads) t.start();

        bunnyThread.join();
        for (Thread t : robotThreads) t.join();
    }
}