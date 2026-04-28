package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Actor implements Runnable {

    protected String name;
    protected int row, col;
    protected Game game;
}