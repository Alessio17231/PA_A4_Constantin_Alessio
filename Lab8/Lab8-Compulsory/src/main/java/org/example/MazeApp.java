package org.example;

import javax.swing.*;
import java.awt.*;

public class MazeApp extends JFrame {

    private MazePanel mazePanel;
    private JTextField rowsField, colsField;

    public MazeApp() {
        setTitle("Maze Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        add(buildConfigPanel(),  BorderLayout.NORTH);
        add(buildCanvasPanel(),  BorderLayout.CENTER);
        add(buildControlPanel(), BorderLayout.SOUTH);

        setMinimumSize(new Dimension(500, 450));
        pack();
        setLocationRelativeTo(null);
    }


    private JPanel buildConfigPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Configuration"));

        panel.add(new JLabel("Rows:"));
        rowsField = new JTextField("10", 4);
        panel.add(rowsField);

        panel.add(new JLabel("Columns:"));
        colsField = new JTextField("10", 4);
        panel.add(colsField);

        JButton drawButton = new JButton("Draw");
        drawButton.addActionListener(e -> drawGrid());
        panel.add(drawButton);

        return panel;
    }


    private JScrollPane buildCanvasPanel() {
        mazePanel = new MazePanel();
        mazePanel.setPreferredSize(new Dimension(420, 420));
        JScrollPane scroll = new JScrollPane(mazePanel);
        scroll.setBorder(BorderFactory.createTitledBorder("Canvas"));
        return scroll;
    }



    private JPanel buildControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Controls"));

        JButton createBtn = new JButton("Create");
        createBtn.addActionListener(e -> {
            if (guardInitialized()) mazePanel.randomlyRemoveWalls();
        });

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> {
            if (guardInitialized()) mazePanel.resetMaze();
        });

        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(createBtn);
        panel.add(resetBtn);
        panel.add(exitBtn);

        return panel;
    }


    private void drawGrid() {
        try {
            int rows = Integer.parseInt(rowsField.getText().trim());
            int cols = Integer.parseInt(colsField.getText().trim());
            if (rows < 2 || cols < 2 || rows > 50 || cols > 50) {
                JOptionPane.showMessageDialog(this, "Rows si columns trebuie sa fie intre 2 si 50.");
                return;
            }
            mazePanel.initMaze(rows, cols);
            pack();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Introduceti numere valide pentru rows si columns.");
        }
    }


    private boolean guardInitialized() {
        if (!mazePanel.mazeInitialized) {
            JOptionPane.showMessageDialog(this,
                    "Apasati 'Draw' mai intai.");
            return false;
        }
        return true;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MazeApp().setVisible(true));
    }
}