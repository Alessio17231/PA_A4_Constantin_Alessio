package org.example;

import java.io.Serializable;

public class Cell implements Serializable {
    private static final long serialVersionUID = 1L;
    public int row, col;
    public boolean top, right, bottom, left;
    public boolean visited;


    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        top = right = bottom = left = true;
    }

    public void reset() {

        top = right = bottom = left = true;
        visited = false;

    }
}