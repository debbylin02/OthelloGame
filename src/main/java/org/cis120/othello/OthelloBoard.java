package org.cis120.othello;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.awt.Color;

/**
 * This class instantiates an Othello object, which is the model for the game.
 * As the user clicks the game board, the model is updated. Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model.
 * 
 * Using a Model-View-Controller framework,
 * OthelloBoard stores the model as a field
 * and acts as both the controller (with a MouseListener)
 * and the view (with its paintComponent method and the status JLabel).
 */

@SuppressWarnings("serial")
public class OthelloBoard extends JPanel {

    // current status text
    private JLabel status;
    // game model
    private Othello o;

    // Game constants
    private static final int BOARD_WIDTH = 800;
    private static final int BOARD_HEIGHT = 800;

    // color for board
    private static final Color VERY_DARK_GREEN = new Color(0, 102, 0);

    /**
     * Initializes the game board.
     */
    public OthelloBoard(JLabel statusInit) {

        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        // initialize game model
        this.o = new Othello("files/saveFile.txt");
        // initialize JLabel status
        this.status = statusInit;

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                // get mouse click position
                Point p = e.getPoint();

                // get position based on coordinates of the mouseclick
                int x = p.x / 100;
                int y = p.y / 100;

                // play turn using the position
                o.playTurn(x, y);

                // update the status JLabel
                updateStatus();
                // repaint the game board
                repaint();
            }
        });
    }

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {
        o.reset("files/saveFile.txt");
        status.setText("Player 1's Turn");
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    /**
     * Saves the game to its current state.
     */
    public void save() {
        o.save("files/saveFile.txt");
        status.setText("Game saved");
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    /**
     * Saves the game to its current state.
     */
    public void resume() {
        o.resume("files/saveFile.txt");
        status.setText("Game resumed");
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    /**
     * undo the most recent turn
     */
    public void undo() {
        o.undo();
        status.setText("Undo turn");
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        // check current player
        if (o.getCurrentPlayer()) {
            status.setText("Player 1's Turn");
        } else {
            status.setText("Player 2's Turn");
        }
        // check winner of the game
        int winner = o.checkWinner();
        if (winner == 1) {
            status.setText("Player 1 wins!!!");
        } else if (winner == 2) {
            status.setText("Player 2 wins!!!");
        } else if (winner == 3) {
            status.setText("It's a tie.");
        }
    }

    /**
     * Draw the Othello board.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw green background
        g.setColor(VERY_DARK_GREEN);
        g.fillRect(0, 0, 800, 800);

        // Draw grid for the board
        for (int i = 1; i < 8; i++) {
            g.setColor(Color.BLACK);
            // vertical lines
            g.drawLine(100 * i, 0, 100 * i, 800);
            // horizontal lines
            g.drawLine(0, 100 * i, 800, 100 * i);
        }

        // Draw game pieces
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int state = o.getCell(i, j).getColor();
                if (state == 1) {
                    // set the pen color to black
                    g.setColor(Color.BLACK);
                    // draw the piece
                    g.fillOval(30 + 100 * j, 30 + 100 * i, 40, 40);
                } else if (state == 2) {
                    g.setColor(Color.WHITE);
                    // draw the piece
                    g.fillOval(30 + 100 * j, 30 + 100 * i, 40, 40);
                }
            }
        }
    }

    /**
     * Returns the size of the Othello board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }

}
