import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class Sudoku extends JFrame {
    private JTextField[][] cells;

    public Sudoku() {
        Font font = new Font("Cascadia Code", Font.PLAIN, 15);
        setTitle("Sudoku Solver");
        setSize(500, 500);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(1, 1));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel sudokuPanel = new JPanel();
        sudokuPanel.setLayout(new GridLayout(9, 9));
        cells = new JTextField[9][9];
        initializeCells(sudokuPanel);
        mainPanel.add(sudokuPanel);
        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(252, 3, 36));
        resetButton.setFocusable(false);
        resetButton.setFont(font);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSudoku();
            }
        });

        JButton verifyButton = new JButton("Solve");
        verifyButton.setBackground(new Color(148, 252, 3));
        verifyButton.setFocusable(false);
        verifyButton.setFont(font);
        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifySudoku();
            }
        });

        buttonPanel.add(resetButton);
        buttonPanel.add(verifyButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeCells(JPanel sudokuPanel) {
        Font font = new Font("Cascadia Code", Font.PLAIN, 40);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j] = new JTextField(1);
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                cells[i][j].setFont(font);

                if ((i / 3 + j / 3) % 2 == 0) {
                    cells[i][j].setBackground(new Color(173, 216, 230));
                }

                Border border = new LineBorder(Color.BLACK, 1);
                cells[i][j].setBorder(border);

                sudokuPanel.add(cells[i][j]);
            }
        }
    }

    private void resetSudoku() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j].setText("");
            }
        }
    }

    private void verifySudoku() {
        int[][] sudokuMatrix = new int[9][9];
        int[][] givenNums = new int[9][9];
        HashSet<Integer>[] rows = (HashSet<Integer>[]) new HashSet[9];
        HashSet<Integer>[] cols = (HashSet<Integer>[]) new HashSet[9];
        HashSet<Integer>[] grids = (HashSet<Integer>[]) new HashSet[9];
        for (int i = 0; i < 9; i++) {
            rows[i] = new HashSet<>();
            cols[i] = new HashSet<>();
            grids[i] = new HashSet<>();
        }
        try {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    String cellText = cells[i][j].getText().trim();
                    sudokuMatrix[i][j] = cellText.isEmpty() ? 0 : Integer.parseInt(cellText);
                    if (!cellText.isEmpty()) {
                        rows[i].add(Integer.parseInt(cellText));
                        cols[j].add(Integer.parseInt(cellText));
                        grids[(i / 3) * 3 + j / 3].add(Integer.parseInt(cellText));
                        givenNums[i][j] = 1;
                    }
                    if (Integer.parseInt(cellText) > 9 || Integer.parseInt(cellText) < 1) {
                        resetSudoku();
                        JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers between 1 and 9.");
                        return;
                    }
                }
            }
            int rowindex = 0;
            int colindex = 0;
            solveSudoku(sudokuMatrix, rows, cols, grids, rowindex, colindex);
            printSolvedSudoku(sudokuMatrix, givenNums);
        } catch (NumberFormatException ex) {
            resetSudoku();
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter only numbers.");
        }
    }

    public static int[][] solveSudoku(int[][] sudokuMatrix, HashSet<Integer>[] rows, HashSet<Integer>[] cols,
            HashSet<Integer>[] grids, int rowindex, int colindex) {
        if (rowindex == 9) {
            return sudokuMatrix;
        }
        if (colindex == 9) {
            return solveSudoku(sudokuMatrix, rows, cols, grids, rowindex + 1, 0);
        }
        if (sudokuMatrix[rowindex][colindex] != 0) {
            return solveSudoku(sudokuMatrix, rows, cols, grids, rowindex, colindex + 1);
        }
        for (int i = 1; i <= 9; i++) {
            if (!rows[rowindex].contains(i) && !cols[colindex].contains(i)
                    && !grids[(rowindex / 3) * 3 + colindex / 3].contains(i)) {
                sudokuMatrix[rowindex][colindex] = i;
                rows[rowindex].add(i);
                cols[colindex].add(i);
                grids[(rowindex / 3) * 3 + colindex / 3].add(i);
                int[][] solvedMatrix = solveSudoku(sudokuMatrix, rows, cols, grids, rowindex, colindex + 1);
                if (solvedMatrix != null) {
                    return solvedMatrix;
                }
                sudokuMatrix[rowindex][colindex] = 0;
                rows[rowindex].remove(i);
                cols[colindex].remove(i);
                grids[(rowindex / 3) * 3 + colindex / 3].remove(i);
            }
        }
        return null;
    }

    private void printSolvedSudoku(int[][] solvedMatrix, int[][] givenNums) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String num = Integer.toString(solvedMatrix[i][j]);
                cells[i][j].setText(num);
                if (givenNums[i][j] != 1) {
                    cells[i][j].setForeground(Color.RED);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Sudoku();
            }
        });
    }
}
