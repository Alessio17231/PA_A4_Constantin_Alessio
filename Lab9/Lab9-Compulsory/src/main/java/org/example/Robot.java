package org.example;

import java.util.List;
import java.util.Random;

public class Robot extends Actor {

    private final Random rand = new Random();

    public Robot(String name, int row, int col, Game game) {
        super(name, row, col, game);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);

                List<Cell> neighbors = game.getNeighbors(row, col);
                if (neighbors.isEmpty()) continue;

                Cell next = neighbors.get(rand.nextInt(neighbors.size()));
                game.moveActor(this, next.getRow(), next.getCol());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}