package org.cis120.othello;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class RunOthello implements Runnable {

    public void run() {


        // Top-level frame in which game components live.
        final JFrame frame = new JFrame("Othello");
        frame.setLocation(800, 800);
        
        // Instructions 
        final JFrame instructionFrame = new JFrame("Othello Instructions");
        instructionFrame.setLocation(320, 220);
        JOptionPane.showMessageDialog(instructionFrame, 
                "Othello is a 2 person game played on an 8 by 8 board. \n" 
                + "Players take turns clicking on empty squares to add discs "
                + "of their respective color to the board, with black "
                + "for player 1 and white for player 2. \n"
                + "Player 1 always goes first. \n"
                + "A disc can only be added to a spot that surrounds "
                + "an opponent’s disc either in a row horizontally, "
                + "vertically, or diagonally. \n"
                + "This flips all of an opponents "
                + "existing pieces within the completed row to your "
                + "color. \n"
                + "Whichever player fills the board up with the most pieces "
                + "of their respective color wins! \n"
                , "Instructions", JOptionPane.INFORMATION_MESSAGE);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Running...");
        status_panel.add(status);

        // Game board
        final OthelloBoard board = new OthelloBoard(status);
        frame.add(board, BorderLayout.CENTER);

        /**
         * Create a panel using FlowLayout layout manager.
         */
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        // Add an action listener to the reset button.
        final JButton reset = new JButton("Reset");
        // when button is pressed, call actionPerformed()
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.reset();
            }
        });
        control_panel.add(reset);

        // resume button
        final JButton resume = new JButton("Resume Game");
        resume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.resume();
            }
        });
        control_panel.add(resume);

        // save button
        final JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.save();
            }
        });
        control_panel.add(save);

        // undo button
        final JButton undo = new JButton("Undo");
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.undo();
            }
        });
        control_panel.add(undo);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

}
