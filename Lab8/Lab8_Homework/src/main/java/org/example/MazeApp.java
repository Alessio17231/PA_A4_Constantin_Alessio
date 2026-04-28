package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MazeApp extends JFrame {

    private MazePanel mazePanel;
    private JTextField rowsField, colsField;

    public MazeApp() {
        setTitle("Maze Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        add(buildConfigPanel(), BorderLayout.NORTH);
        add(buildCanvasPanel(), BorderLayout.CENTER);
        add(buildControlPanel(), BorderLayout.SOUTH);

        setMinimumSize(new Dimension(500, 450));
        pack();
        setLocationRelativeTo(null);
    }

    //Compulsory

    private JPanel buildConfigPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Configuration"));

        panel.add(new JLabel("Rows:"));
        rowsField = new JTextField("10", 4);
        panel.add(rowsField);

        panel.add(new JLabel("Cols:"));
        colsField = new JTextField("10", 4);
        panel.add(colsField);

        JButton drawButton = new JButton("Draw Grid");
        drawButton.addActionListener(e -> drawGrid());
        panel.add(drawButton);

        return panel;
    }

    //Compulsory

    private JScrollPane buildCanvasPanel() {
        mazePanel = new MazePanel();
        mazePanel.setPreferredSize(new Dimension(420, 420));
        JScrollPane scroll = new JScrollPane(mazePanel);
        scroll.setBorder(BorderFactory.createTitledBorder(
                "Maze Canvas"));
        return scroll;
    }


    private JPanel buildControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
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


        JButton validateBtn = new JButton("Validate");
        validateBtn.addActionListener(e -> {
            if (!guardInitialized()) return;
            boolean ok = mazePanel.validateMaze();
            if (ok)
                JOptionPane.showMessageDialog(this,
                        "Labirintul este traversabil! Exista drum de la S la E.",
                        "Validate", JOptionPane.INFORMATION_MESSAGE);
            else
                JOptionPane.showMessageDialog(this,
                        "Labirintul NU este traversabil! Nu exista drum de la S la E.",
                        "Validate", JOptionPane.WARNING_MESSAGE);
        });

        JButton exportBtn = new JButton("Export PNG");
        exportBtn.addActionListener(e -> {
            if (!guardInitialized()) return;
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("maze.png"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    mazePanel.exportPNG(fc.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Imagine salvata!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Eroare: " + ex.getMessage());
                }
            }
        });

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            if (!guardInitialized()) return;
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("maze.dat"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    mazePanel.saveMaze(fc.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Labirint salvat!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Eroare: " + ex.getMessage());
                }
            }
        });

        JButton loadBtn = new JButton("Load");
        loadBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    mazePanel.loadMaze(fc.getSelectedFile().getAbsolutePath());
                    pack();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Eroare: " + ex.getMessage());
                }
            }
        });

        panel.add(createBtn);
        panel.add(resetBtn);
        panel.add(validateBtn);
        panel.add(exportBtn);
        panel.add(saveBtn);
        panel.add(loadBtn);
        panel.add(exitBtn);

        return panel;
    }


    private void drawGrid() {
        try {
            int rows = Integer.parseInt(rowsField.getText().trim());
            int cols = Integer.parseInt(colsField.getText().trim());
            if (rows < 2 || cols < 2 || rows > 50 || cols > 50) {
                JOptionPane.showMessageDialog(this,
                        "Rows si cols trebuie sa fie intre 2 si 50.");
                return;
            }
            mazePanel.initMaze(rows, cols);
            pack();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Introduceti numere valide pentru rows si cols.");
        }
    }

    private boolean guardInitialized() {
        if (!mazePanel.mazeInitialized) {
            JOptionPane.showMessageDialog(this,
                    "Apasati 'Draw Grid' mai intai.");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MazeApp().setVisible(true));
    }
}