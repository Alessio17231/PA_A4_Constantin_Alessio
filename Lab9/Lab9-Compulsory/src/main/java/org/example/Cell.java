package org.example;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Cell implements Serializable {
    private static final long serialVersionUID = 1L;

    private int row, col;
    private boolean top, right, bottom, left;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        top = right = bottom = left = true;
    }

    public void reset() {
        top = right = bottom = left = true;
    }
}