package org.cis120.othello;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * This class constructs an Othello object, which consists of a 2D array
 * representing the game board, the number of turns played, the current
 * player, a map of moves made (which maps added pieces to the pieces
 * that they overtake), an ordered list of added pieces, and whether or
 * not the game is over.
 * 
 * When playing the game, the playTurn function is called to carry
 * out the logic needed that checks if a colored othello piece can be added
 * to that particular position. The class also keeps track of the most recent
 * game history (i.e. the current player, number of moves, board setup)
 * using File I/O through the save function. The class can revert back
 * to a previously saved state using the resume function. The class uses
 * a list and map in order to keep track of the history of moves in a game,
 * allowing for a user to undo their moves up until they reach the starting
 * state of an othello game. The class can also check for a winning condition
 * through the checkWinner function.
 */

public class Othello {

    // 8 x 8 2D arrau to replicate the board
    private GamePiece[][] board;

    // number of turns played
    private int numTurns;

    // first player (black pieces)
    private boolean player1;

    // boolean for whether or not the game is over
    private boolean gameOver;

    /*
     * moves made in the game storing the added game piece as keys
     * and the pieces that were overtaken by them as values
     */
    private TreeMap<GamePiece, LinkedList<GamePiece>> movesMade;

    // List of pieces added to the game in order of addition
    private LinkedList<GamePiece> addedPieces;

    /**
     * Constructor sets up game state.
     * 
     * @param String name of the save file to clear
     */
    public Othello(String fileName) {
        reset(fileName);
    }

    /**
     * playTurn allows players to play a turn. Returns true if the move is
     * successful and false if a player tries to play in a location that is
     * taken or after the game has ended. If the turn is successful
     * and the game has not ended, the player is changed
     * and the number of turns increases.
     * If the turn is unsuccessful or the game has ended,
     * the player is not changed.
     *
     * @param r row to play in
     * @param c column to play in
     * @return whether the turn was successful
     */
    public boolean playTurn(int r, int c) {
        // piecesOvertaken by the added piece
        LinkedList<GamePiece> piecesOvertaken = new LinkedList<GamePiece>();

        // check if spot is empty
        if (board[r][c].getColor() != 0) {
            System.out.println("not empty spot");
            return false;
        }

        // check if game is over
        if (this.gameOver) {
            System.out.println("game is over");
            return false;
        }

        if (player1) {
            LinkedList<GamePiece> piecesToOvertake = canOvertake(r, c, 1);
            if (piecesToOvertake.isEmpty()) {
                System.out.println("No pieces to overtake");
                return false;
            } else {
                piecesOvertaken = overtake(piecesToOvertake, 1);
                // add colored piece to board
                board[r][c].setColor(1);
            }

        } else {
            LinkedList<GamePiece> piecesToOvertake = canOvertake(r, c, 2);
            if (piecesToOvertake.isEmpty()) {
                System.out.println("No pieces to overtake");
                return false;
            } else {
                piecesOvertaken = overtake(piecesToOvertake, 2);
                // add colored piece to board
                board[r][c].setColor(2);
            }
        }

        // increase number of turns
        this.numTurns++;
        // add game piece to list of added pieces
        this.addedPieces.add(this.board[r][c]);
        // add game piece and overtaken pieces to map
        this.movesMade.put(this.board[r][c], piecesOvertaken);
        // no winner yet
        if (checkWinner() == 0) {
            // switch players
            player1 = !player1;
        } else {
            this.gameOver = true;
        }

        return true;
    }

    /**
     * canOvertake returns a list of game pieces that can be overtaken
     * by the addition of a new game piece specified by its
     * row, column, and color.
     * If there are no pieces on the board that can be overtaken,
     * return an empty list.
     *
     * @param r     row of new piece
     * @param c     column of new piece
     * @param color of new piece
     * @return list of pieces that will be overtaken by the addition of
     *         the new piece.
     */
    public LinkedList<GamePiece> canOvertake(int r, int c, int color) {

        // List of pieces to overtake that may contain duplicates
        LinkedList<GamePiece> piecesToOvertake = new LinkedList<GamePiece>();

        // List of pieces to return with no duplicates
        LinkedList<GamePiece> noDuplicates = new LinkedList<GamePiece>();

        // same column
        LinkedList<GamePiece> topCol = canOvertakeTopCol(r, c, color);
        LinkedList<GamePiece> botCol = canOvertakeBotCol(r, c, color);

        // same row
        LinkedList<GamePiece> leftRow = canOvertakeLeftRow(r, c, color);
        LinkedList<GamePiece> rightRow = canOvertakeRightRow(r, c, color);

        // diagonals
        LinkedList<GamePiece> topLeftDiag = canOvertakeTopLeft(r, c, color);
        LinkedList<GamePiece> topRightDiag = canOvertakeTopRight(r, c, color);
        LinkedList<GamePiece> botRightDiag = canOvertakeBotRight(r, c, color);
        LinkedList<GamePiece> botLeftDiag = canOvertakeBotLeft(r, c, color);

        // add to list
        piecesToOvertake.addAll(topCol);
        piecesToOvertake.addAll(botCol);
        piecesToOvertake.addAll(leftRow);
        piecesToOvertake.addAll(rightRow);
        piecesToOvertake.addAll(topLeftDiag);
        piecesToOvertake.addAll(topRightDiag);
        piecesToOvertake.addAll(botRightDiag);
        piecesToOvertake.addAll(botLeftDiag);

        // check for duplicates
        Iterator<GamePiece> iter = piecesToOvertake.iterator();
        while (iter.hasNext()) {
            GamePiece g = iter.next();
            if (!noDuplicates.contains(g)) {
                noDuplicates.add(g);
            }
        }

        // return list
        return noDuplicates;

    }

    /**
     * piecesInBetween is a helper function that returns the list of
     * pieces that can be overtaken in between a given GamePiece and
     * a target location in the grid.
     * Returns an empty list if there are no such pieces.
     *
     * @param r         row of the target location
     * @param c         column of the target location
     * @param gp        given GamePiece with the same color as the target
     * @param direction of iteration between the two pieces
     * @param color     to switch to
     * @return linkedList of game pieces that can be overtaken
     * 
     */

    public LinkedList<GamePiece> piecesInBetween(
            int r, int c, int color,
            GamePiece gp, String direction
    ) {

        // List of pieces to return
        LinkedList<GamePiece> piecesToOvertake = new LinkedList<GamePiece>();

        // top Column
        if (direction.equals("TopCol")) {
            // get row of the same colored piece
            int row = gp.getX();
            // iterate through the pieces in between
            for (int i = row + 1; i < r; i++) {
                // if there empty spaces in between return empty
                GamePiece b = board[i][c];
                if (b.getColor() == 0) {
                    return new LinkedList<GamePiece>();
                } else if (b.getColor() != color) {
                    piecesToOvertake.add(b);
                }
            }
        }

        // bottom Column
        if (direction.equals("BotCol")) {
            // get row of the same colored piece
            int row = gp.getX();
            // iterate through the pieces in between
            for (int i = r + 1; i < row; i++) {
                // if there empty spaces in between return empty
                GamePiece b = board[i][c];
                if (b.getColor() == 0) {
                    return new LinkedList<GamePiece>();
                } else if (b.getColor() != color) {
                    piecesToOvertake.add(b);
                }
            }

        }

        // left Row
        if (direction.equals("LeftRow")) {
            // get column of the same colored piece
            int column = gp.getY();
            for (int i = column + 1; i < c; i++) {
                // if there empty spaces in between return empty
                GamePiece b = board[r][i];
                if (b.getColor() == 0) {
                    return new LinkedList<GamePiece>();
                } else if (b.getColor() != color) {
                    piecesToOvertake.add(b);
                }
            }
        }

        // right Row
        if (direction.equals("RightRow")) {
            // get column of the same colored piece
            int column = gp.getY();

            for (int i = c + 1; i < column; i++) {
                // if there empty spaces in between return empty
                GamePiece b = board[r][i];

                if (b.getColor() == 0) {
                    return new LinkedList<GamePiece>();
                } else if (b.getColor() != color) {
                    piecesToOvertake.add(b);
                }
            }
        }

        // top Left
        if (direction.equals("TopLeft")) {
            // get row and column of the same colored piece
            int row = gp.getX();
            int column = gp.getY();

            // iterating
            int i = row + 1;
            int j = column + 1;

            // check if there is a piece with the same color
            while ((i < r) && (j < c)) {
                GamePiece b = board[i][j];
                if (b.getColor() == 0) {
                    return new LinkedList<GamePiece>();
                } else {
                    if (b.getColor() != color) {
                        piecesToOvertake.add(b);
                    }
                    // move column and row forward by one
                    i++;
                    j++;
                }
            }
        }

        // bottom right
        if (direction.equals("BotRight")) {
            // get row and column of the same colored piece
            int row = gp.getX();
            int column = gp.getY();

            // iterating
            int i = r + 1;
            int j = c + 1;

            // check if there is a piece with the same color
            while ((i < row) && (j < column)) {
                GamePiece b = board[i][j];
                if (b.getColor() == 0) {
                    return new LinkedList<GamePiece>();
                } else {
                    if (b.getColor() != color) {
                        piecesToOvertake.add(b);
                    }
                    // move column and row forward by one
                    i++;
                    j++;
                }
            }
        }

        // top right
        if (direction.equals("TopRight")) {
            // get row and column of the same colored piece
            int row = gp.getX();
            int column = gp.getY();

            // iterating
            int i = row + 1;
            int j = column - 1;

            // check if there is a piece with the same color
            while ((i < r) && (j > c)) {
                GamePiece b = board[i][j];
                if (b.getColor() == 0) {
                    return new LinkedList<GamePiece>();
                } else {
                    if (b.getColor() != color) {
                        piecesToOvertake.add(b);
                    }
                    // move row down and column back by one
                    i++;
                    j--;
                }
            }
        }

        // bottom left
        if (direction.equals("BotLeft")) {
            // get row and column of the same colored piece
            int row = gp.getX();
            int column = gp.getY();

            // iterating
            int i = r + 1;
            int j = c - 1;

            // check if there is a piece with the same color
            while ((i < row) && (j > column)) {
                GamePiece b = board[i][j];
                if (b.getColor() == 0) {
                    return new LinkedList<GamePiece>();
                } else {
                    if (b.getColor() != color) {
                        piecesToOvertake.add(b);
                    }
                    // move row down and column back by one
                    i++;
                    j--;
                }
            }
        }

        // return list
        return piecesToOvertake;
    }

    /**
     * canOvertakeTopCol returns the list of pieces that can be overtaken
     * by a newly added piece in the same column above the added piece.
     * Returns an empty list if there are no such pieces.
     *
     * @param r     row to play in
     * @param c     column to play in
     * @param color to switch to
     * @return linkedList of game pieces that can be overtaken
     * 
     */
    public LinkedList<GamePiece> canOvertakeTopCol(int r, int c, int color) {

        // List of pieces to return
        LinkedList<GamePiece> piecesToOvertake = new LinkedList<GamePiece>();
        // List of pieces with the same color
        LinkedList<GamePiece> sameColorPieces = new LinkedList<GamePiece>();

        // check if there is a piece with the same color
        for (int i = 0; i < r; i++) {
            if (board[i][c].getColor() == color) {
                sameColorPieces.add(board[i][c]);
            }
        }

        // no pieces of same color in the column, cannot overtake
        if (sameColorPieces.isEmpty()) {
            return new LinkedList<GamePiece>();
        } else {
            // iterate over the list of same color pieces
            Iterator<GamePiece> iter = sameColorPieces.iterator();
            while (iter.hasNext()) {
                GamePiece gp = iter.next();
                // get pieces in between
                LinkedList<GamePiece> piecesInBetween = piecesInBetween(r, c, color, gp, "TopCol");
                piecesToOvertake.addAll(piecesInBetween);

            }

        }
        return piecesToOvertake;
    }

    /**
     * canOvertakeBotCol returns the list of pieces that can be overtaken
     * by a newly added piece in the same column below the added piece.
     * Returns an empty list if there are no such pieces.
     *
     * @param r     row to play in
     * @param c     column to play in
     * @param color to switch to
     * @return linkedList of game pieces that can be overtaken
     */

    public LinkedList<GamePiece> canOvertakeBotCol(int r, int c, int color) {

        // List of pieces to return
        LinkedList<GamePiece> piecesToOvertake = new LinkedList<GamePiece>();
        // List of pieces with the same color
        LinkedList<GamePiece> sameColorPieces = new LinkedList<GamePiece>();

        // check if there is a piece with the same color
        for (int i = r + 1; i < board.length; i++) {
            if (board[i][c].getColor() == color) {
                sameColorPieces.add(board[i][c]);
            }
        }

        // no pieces of same color in the column, cannot overtake
        if (sameColorPieces.isEmpty()) {
            return new LinkedList<GamePiece>();
        } else {
            // iterate over the list of same color pieces
            Iterator<GamePiece> iter = sameColorPieces.iterator();
            while (iter.hasNext()) {
                GamePiece gp = iter.next();
                // get pieces in between
                LinkedList<GamePiece> piecesInBetween = piecesInBetween(r, c, color, gp, "BotCol");
                piecesToOvertake.addAll(piecesInBetween);

            }

        }
        return piecesToOvertake;

    }

    /**
     * canOvertakeLeftRow returns the list of pieces that can be overtaken
     * by a newly added piece in the same row to the left of the added piece.
     * Returns an empty list if there are no such pieces.
     *
     * @param r     row to play in
     * @param c     column to play in
     * @param color to switch to
     * @return linkedList of game pieces that can be overtaken
     */

    public LinkedList<GamePiece> canOvertakeLeftRow(int r, int c, int color) {

        // List of pieces to return
        LinkedList<GamePiece> piecesToOvertake = new LinkedList<GamePiece>();
        // List of pieces with the same color
        LinkedList<GamePiece> sameColorPieces = new LinkedList<GamePiece>();

        // check if there is a piece with the same color
        for (int i = 0; i < c; i++) {
            if (board[r][i].getColor() == color) {
                sameColorPieces.add(board[r][i]);
            }
        }

        // no pieces of same color in the row, cannot overtake
        if (sameColorPieces.isEmpty()) {
            return new LinkedList<GamePiece>();
        } else {
            // iterate over the list of same color pieces
            Iterator<GamePiece> iter = sameColorPieces.iterator();
            while (iter.hasNext()) {
                GamePiece gp = iter.next();
                // get pieces in between
                LinkedList<GamePiece> piecesInBetween = piecesInBetween(r, c, color, gp, "LeftRow");
                piecesToOvertake.addAll(piecesInBetween);
            }

        }

        return piecesToOvertake;
    }

    /**
     * canOvertakeRightRow returns the list of pieces that can be overtaken
     * by a newly added piece in the same row to the right of the added piece.
     * Returns an empty list if there are no such pieces.
     *
     * @param r     row to play in
     * @param c     column to play in
     * @param color to switch to
     * @return linkedList of game pieces that can be overtaken
     */

    public LinkedList<GamePiece> canOvertakeRightRow(int r, int c, int color) {

        // List of pieces to return
        LinkedList<GamePiece> piecesToOvertake = new LinkedList<GamePiece>();
        // List of pieces with the same color
        LinkedList<GamePiece> sameColorPieces = new LinkedList<GamePiece>();

        // check if there is a piece with the same color
        for (int i = c + 1; i < board[0].length; i++) {
            if (board[r][i].getColor() == color) {
                sameColorPieces.add(board[r][i]);
            }
        }

        // no pieces of same color in the row, cannot overtake
        if (sameColorPieces.isEmpty()) {
            return new LinkedList<GamePiece>();
        } else {
            // iterate over the list of same color pieces
            Iterator<GamePiece> iter = sameColorPieces.iterator();
            while (iter.hasNext()) {
                GamePiece gp = iter.next();
                // get pieces in between
                LinkedList<GamePiece> piecesInBetween = piecesInBetween(
                        r, c, color, gp, "RightRow"
                );
                piecesToOvertake.addAll(piecesInBetween);
            }

        }

        return piecesToOvertake;
    }

    /**
     * canOvertakeTopLeft returns the list of pieces that can be overtaken
     * by a newly added piece in the same diagonal to the top left
     * of the added piece.
     * Returns an empty list if there are no such pieces.
     *
     * @param r     row to play in
     * @param c     column to play in
     * @param color to switch to
     * @return linkedList of game pieces that can be overtaken
     */

    public LinkedList<GamePiece> canOvertakeTopLeft(int r, int c, int color) {

        // List of pieces to return
        LinkedList<GamePiece> piecesToOvertake = new LinkedList<GamePiece>();
        // List of pieces with the same color
        LinkedList<GamePiece> sameColorPieces = new LinkedList<GamePiece>();

        // starting column and row
        int i = r - 1;
        int j = c - 1;

        // check if there is a piece with the same color
        while ((i > -1) && (j > -1)) {
            if (board[i][j].getColor() == color) {
                sameColorPieces.add(board[i][j]);
            }
            // move row and column by 1
            i--;
            j--;

        }

        // no pieces of same color in the diagonal, cannot overtake
        if (sameColorPieces.isEmpty()) {
            return new LinkedList<GamePiece>();
        } else {
            // iterate over the list of same color pieces
            Iterator<GamePiece> iter = sameColorPieces.iterator();
            while (iter.hasNext()) {
                GamePiece gp = iter.next();
                // get pieces in between
                LinkedList<GamePiece> piecesInBetween = piecesInBetween(r, c, color, gp, "TopLeft");
                piecesToOvertake.addAll(piecesInBetween);
            }

        }

        return piecesToOvertake;
    }

    /**
     * canOvertakeBotRight returns the list of pieces that can be overtaken
     * by a newly added piece in the same diagonal to the bottom right
     * of the added piece.
     * Returns an empty list if there are no such pieces.
     *
     * @param r     row to play in
     * @param c     column to play in
     * @param color to switch to
     * @return linkedList of game pieces that can be overtaken
     */

    public LinkedList<GamePiece> canOvertakeBotRight(int r, int c, int color) {

        // List of pieces to return
        LinkedList<GamePiece> piecesToOvertake = new LinkedList<GamePiece>();
        // List of pieces with the same color
        LinkedList<GamePiece> sameColorPieces = new LinkedList<GamePiece>();

        // starting column and row
        int i = r + 1;
        int j = c + 1;

        // check if there is a piece with the same color
        while ((i < 8) && (j < 8)) {
            if (board[i][j].getColor() == color) {
                sameColorPieces.add(board[i][j]);
            }
            // move row and column by 1
            i++;
            j++;

        }

        // no pieces of same color in the diagonal, cannot overtake
        if (sameColorPieces.isEmpty()) {
            return new LinkedList<GamePiece>();
        } else {
            // iterate over the list of same color pieces
            Iterator<GamePiece> iter = sameColorPieces.iterator();
            while (iter.hasNext()) {
                GamePiece gp = iter.next();
                // get pieces in between
                LinkedList<GamePiece> piecesInBetween = piecesInBetween(
                        r, c, color, gp, "BotRight"
                );
                piecesToOvertake.addAll(piecesInBetween);
            }

        }

        return piecesToOvertake;
    }

    /**
     * canOvertakeTopRight returns the list of pieces that can be overtaken
     * by a newly added piece in the same diagonal to the top right
     * of the added piece.
     * Returns an empty list if there are no such pieces.
     *
     * @param r     row to play in
     * @param c     column to play in
     * @param color to switch to
     * @return linkedList of game pieces that can be overtaken
     */

    public LinkedList<GamePiece> canOvertakeTopRight(int r, int c, int color) {

        // List of pieces to return
        LinkedList<GamePiece> piecesToOvertake = new LinkedList<GamePiece>();
        // List of pieces with the same color
        LinkedList<GamePiece> sameColorPieces = new LinkedList<GamePiece>();

        // starting column and row
        int i = r - 1;
        int j = c + 1;

        // check if there is a piece with the same color
        while ((i > -1) && (j < 8)) {
            if (board[i][j].getColor() == color) {
                sameColorPieces.add(board[i][j]);
            }
            // move row up by 1 and column over by 1
            i--;
            j++;

        }

        // no pieces of same color in the diagonal, cannot overtake
        if (sameColorPieces.isEmpty()) {
            return new LinkedList<GamePiece>();
        } else {
            // iterate over the list of same color pieces
            Iterator<GamePiece> iter = sameColorPieces.iterator();
            while (iter.hasNext()) {
                GamePiece gp = iter.next();
                // get pieces in between
                LinkedList<GamePiece> piecesInBetween = piecesInBetween(
                        r, c, color, gp, "TopRight"
                );
                piecesToOvertake.addAll(piecesInBetween);
            }

        }

        return piecesToOvertake;
    }

    /**
     * canOvertakeBotLeft returns the list of pieces that can be overtaken
     * by a newly added piece in the same diagonal to the bottom left
     * of the added piece.
     * Returns an empty list if there are no such pieces.
     *
     * @param r     row to play in
     * @param c     column to play in
     * @param color to switch to
     * @return linkedList of game pieces that can be overtaken
     */

    public LinkedList<GamePiece> canOvertakeBotLeft(int r, int c, int color) {

        // List of pieces to return
        LinkedList<GamePiece> piecesToOvertake = new LinkedList<GamePiece>();
        // List of pieces with the same color
        LinkedList<GamePiece> sameColorPieces = new LinkedList<GamePiece>();

        // starting column and row
        int i = r + 1;
        int j = c - 1;

        // check if there is a piece with the same color
        while ((i < 8) && (j > -1)) {
            if (board[i][j].getColor() == color) {
                sameColorPieces.add(board[i][j]);
            }
            // move row down by 1 and column back by 1
            i++;
            j--;

        }

        // no pieces of same color in the diagonal, cannot overtake
        if (sameColorPieces.isEmpty()) {
            return new LinkedList<GamePiece>();
        } else {
            Iterator<GamePiece> iter = sameColorPieces.iterator();
            while (iter.hasNext()) {
                GamePiece gp = iter.next();
                // get pieces in between
                LinkedList<GamePiece> piecesInBetween = piecesInBetween(r, c, color, gp, "BotLeft");
                piecesToOvertake.addAll(piecesInBetween);
            }

        }

        return piecesToOvertake;
    }

    /**
     * overtake switches the pieces specified in a given list to
     * the specified color (either 1 for black or 2 for white).
     * 
     * @param list  of pieces to overtake
     * @param color to switch to
     * @return LinkedList of game pieces overtaken
     */
    public LinkedList<GamePiece> overtake(
            LinkedList<GamePiece> piecesToOvertake, int color
    ) {
        // iterator for the list of game pieces
        Iterator<GamePiece> iter = piecesToOvertake.iterator();
        // list of piecesOvertaken to return
        LinkedList<GamePiece> piecesOvertaken = new LinkedList<GamePiece>();
        while (iter.hasNext()) {
            GamePiece gp = iter.next();
            // change the color of the piece
            gp.setColor(color);
            // add piece to the list of overtaken pieces
            piecesOvertaken.add(gp);
        }

        return piecesOvertaken;

    }

    /**
     * checkForMoves checks whether the given color has any possible
     * moves left on the board.
     * 
     * @param color of the given player
     * @return true if there are moves left, false otherwise.
     */
    public boolean checkForMoves(int color) {

        // List of pieces to return
        LinkedList<GamePiece> piecesToOvertake = new LinkedList<GamePiece>();
        // List of empty spots left on the board
        LinkedList<GamePiece> emptySpots = new LinkedList<GamePiece>();

        // iterate through the board
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].getColor() == 0) {
                    emptySpots.add(board[i][j]);
                }
            }
        }

        // no empty spots left to fill, return false
        if (emptySpots.isEmpty()) {
            return false;
        } else {
            // iterate through the list of empty spots
            Iterator<GamePiece> iter = emptySpots.iterator();
            while (iter.hasNext()) {
                GamePiece gp = iter.next();
                // get the row and column of the empty spot
                int row = gp.getX();
                int column = gp.getY();
                // add pieces that can be overtaken
                piecesToOvertake.addAll(canOvertake(row, column, color));
            }

        }

        // if there are pieces left to overtake, then there are moves left
        return (!piecesToOvertake.isEmpty());
    }

    /**
     * checkWinner checks whether the game has reached a win condition.
     *
     * @return 0 if nobody has won yet, 1 if player 1 has won, and 2 if player 2
     *         has won, 3 if the game hits stalemate
     */
    public int checkWinner() {
        // counters
        int blackCount = 0;
        int whiteCount = 0;
        int emptyCount = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].getColor() == 1) {
                    blackCount++;
                } else if (board[i][j].getColor() == 2) {
                    whiteCount++;
                } else {
                    emptyCount++;
                }
            }
        }

        // no black pieces, white wins
        if (blackCount == 0) {

            this.gameOver = true;
            return 2;
        }

        // no white pieces, black wins
        if (whiteCount == 0) {
            this.gameOver = true;
            return 1;
        }

        // no more white spaces, game over
        if (emptyCount == 0) {
            this.gameOver = true;

            if (blackCount > whiteCount) {
                // if more black, black wins
                return 1;
            } else if (blackCount < whiteCount) {
                // more white, white wins
                return 2;
            } else {
                // tie
                return 3;
            }

        } else {
            // check for any moves left for the current player
            if (player1) {
                // check for any moves left for player 1
                if (checkForMoves(1)) {
                    // player still has moves left
                    return 0;
                } else {
                    // no moves left for player1
                    if (checkForMoves(2)) {
                        // player2 still has moves left so the game continues
                        return 0;
                    } else {
                        // player2 also has no moves left
                        if (blackCount > whiteCount) {
                            // if more black, black wins
                            return 1;
                        } else if (blackCount < whiteCount) {
                            // more white, white wins
                            return 2;
                        } else {
                            // tie
                            return 3;
                        }
                    }
                }
            } else {
                // check for any moves left for player 2
                if (checkForMoves(2)) {
                    // player still has moves left
                    return 0;
                } else {
                    // no moves left for player1
                    if (checkForMoves(1)) {
                        // player1 still has moves left so the game continues
                        return 0;
                    } else {
                        // player1 also has no moves left
                        if (blackCount > whiteCount) {
                            // if more black, black wins
                            return 1;
                        } else if (blackCount < whiteCount) {
                            // more white, white wins
                            return 2;
                        } else {
                            // tie
                            return 3;
                        }
                    }
                }
            }
        }

    }

    /**
     * printGameState prints the current game state
     * for debugging.
     */
    public void printGameState() {
        System.out.println("\n\nTurn " + numTurns + ":\n");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j].getColor());
                if (j < 8) {
                    System.out.print(" | ");
                }
            }
            if (i < 8) {
                System.out.println("\n---------------------------------");
            }
        }
    }

    /**
     * reset (re-)sets the game state to start a new game.
     * 
     * @param name of save file to clear
     */
    public void reset(String fileName) {
        this.board = new GamePiece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // create new board full of empty spots
                this.board[i][j] = new GamePiece(0, i, j);
            }
        }
        // set the center four squares of the board
        this.board[3][3].setColor(2);
        this.board[4][4].setColor(2);
        this.board[3][4].setColor(1);
        this.board[4][3].setColor(1);

        // reset the fields
        this.numTurns = 0;
        this.player1 = true;
        this.gameOver = false;
        this.movesMade = new TreeMap<GamePiece, LinkedList<GamePiece>>();
        this.addedPieces = new LinkedList<GamePiece>();

        // clear the contents of the save file
        try {
            FileWriter fw = new FileWriter(fileName);
            fw.close();
        } catch (IOException e) {
            // check if file doesn't exist
            if (e.getClass() == FileNotFoundException.class) {
                System.out.println("File Not Found: " + fileName);
                throw new IllegalArgumentException();
            } else {
                System.out.println("I/O exception occured");
                return;
            }
        } finally {
            // check if fileName is null
            if (fileName == null) {
                System.out.println("Null input: " + fileName);
                throw new IllegalArgumentException();
            }
        }

    }

    /**
     * saves the game state to the save file
     * 
     * @param name of save file
     */
    public void save(String fileName) {

        try {
            // create FileWriter that overwrite the file when called
            FileWriter f = new FileWriter(fileName, false);
            BufferedWriter bw = new BufferedWriter(f);

            // write the number of turns
            String nT = Integer.toString(this.numTurns);
            bw.write(nT);
            bw.newLine();

            // write the current player
            if (player1) {
                bw.write("Current player: player 1 (black)");
                bw.newLine();
            } else {
                bw.write("Current player: player 2 (white)");
                bw.newLine();
            }

            // write whether or not the game is over
            if (gameOver) {
                bw.write("Game is over");
                bw.newLine();
            } else {
                bw.write("Game is not over");
                bw.newLine();
            }

            // iterate through the board
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    GamePiece gp = this.board[i][j];
                    // write either 0, 1, or 2 to represent the piece color
                    if (j == 7) {
                        bw.write(gp.getColor() + " ");
                        // next line
                        bw.newLine();
                    } else {
                        bw.write(gp.getColor() + " ");
                    }
                }
            }

            // iterate through the added pieces
            bw.write("added pieces in order");
            bw.newLine();

            Iterator<GamePiece> iter = addedPieces.iterator();
            while (iter.hasNext()) {
                GamePiece g = iter.next();

                // row and column of the game piece (key)
                String r = Integer.toString(g.getX());
                String c = Integer.toString(g.getY());
                String color = Integer.toString(g.getColor());

                bw.write(r + c + color);
                bw.newLine();
            }

            // iterate through the map of moves made
            bw.write("mapping added pieces to overtaken pieces");
            bw.newLine();
            for (Map.Entry<GamePiece, LinkedList<GamePiece>> entry :
                movesMade.entrySet()) {
                // get the key and value
                GamePiece g = entry.getKey();
                LinkedList<GamePiece> l = entry.getValue();

                // row, column, color of the game piece (key)
                String r = Integer.toString(g.getX());
                String c = Integer.toString(g.getY());
                String color = Integer.toString(g.getColor());

                bw.write("added piece");
                bw.newLine();
                bw.write(r + c + color);
                bw.newLine();
                // number of overtaken pieces
                String length = Integer.toString(l.size());
                bw.write("number overtaken pieces: " + length);
                bw.newLine();

                // iterate through the list
                Iterator<GamePiece> valIter = l.iterator();
                while (valIter.hasNext()) {
                    GamePiece p = valIter.next();
                    // row, column, color of the game piece (value)
                    String row = Integer.toString(p.getX());
                    String column = Integer.toString(p.getY());
                    String col = Integer.toString(p.getColor());
                    bw.write(row + column + col);
                    bw.newLine();
                }
                bw.write("end of entry");
                bw.newLine();
            }

            // close file writer
            bw.close();
        } catch (IOException e) {
            // check if file doesn't exist
            if (e.getClass() == FileNotFoundException.class) {
                System.out.println("File Not Found: " + fileName);
                throw new IllegalArgumentException();
            } else {
                System.out.println("I/O exception occured");
                return;
            }
        } finally {
            // check if fileName is null
            if (fileName == null) {
                System.out.println("Null input: " + fileName);
                throw new IllegalArgumentException();
            }
        }

    }

    /**
     * resumes the game state that is saved in the file
     * 
     * @param name of save file to clear
     */
    public void resume(String fileName) {

        try {
            // create FileReader that reads the save file when called
            FileReader r = new FileReader(fileName);
            BufferedReader br = new BufferedReader(r);

            // first line is the number of turns
            String numTurns = br.readLine();

            // check if file is empty
            if ((numTurns == null) || (numTurns == "")) {
                System.out.println("empty save file");
                br.close();
                return; 
            }

            int nT = Integer.parseInt(numTurns);
            this.numTurns = nT;

            // second line is the current player
            String currPlayer = br.readLine();
            if (currPlayer.equals("Current player: player 1 (black)")) {
                this.player1 = true;
            } else {
                this.player1 = false;
            }

            // third line is whether or not the game is over
            String isOver = br.readLine();
            if (isOver.equals("Game is over")) {
                this.gameOver = true;
            } else {
                this.gameOver = false;
            }

            // the next 8 lines represent the board
            int[] colorsInOrder = new int[64];
            int arrCounter = 0;
            for (int i = 0; i < 8; i++) {
                String row = br.readLine();
                // iterate through the row
                for (int j = 0; j < row.length(); j++) {
                    char c = row.charAt(j);
                    // empty space
                    if (c == '0') {
                        colorsInOrder[arrCounter] = 0;
                        arrCounter++;
                    }
                    // color is black
                    if (c == '1') {
                        colorsInOrder[arrCounter] = 1;
                        arrCounter++;
                    }
                    // color is white
                    if (c == '2') {
                        colorsInOrder[arrCounter] = 2;
                        arrCounter++;
                    }

                }
            }

            // construct the othello board
            int colorInBoard = 0;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    // get color from the array of colors
                    this.board[i][j].setColor(colorsInOrder[colorInBoard]);
                    colorInBoard++;
                }
            }

            // find added pieces
            String hasAddedPieces = br.readLine();
            // list of added game pieces in order
            LinkedList<GamePiece> added = new LinkedList<GamePiece>();

            if (hasAddedPieces.equals("added pieces in order")) {
                // numTurns is equal to the number added pieces
                for (int i = 0; i < this.numTurns; i++) {
                    // added piece info
                    String addedPiece = br.readLine();

                    // row at 0, column at 1, color at 2
                    int x = addedPiece.charAt(0) - '0';
                    int y = addedPiece.charAt(1) - '0';
                    int clr = addedPiece.charAt(2) - '0';
                    GamePiece ap = new GamePiece(clr, x, y);
                    // check if there are duplicates
                    if (!added.contains(ap)) {
                        added.add(ap);
                    }

                }

            }

            // set the addedPieces field
            this.addedPieces = added;

            // find map of pieces to overtaken pieces
            String hasMap = br.readLine();
            // map of added pieces and overtaken pieces
            TreeMap<GamePiece, LinkedList<GamePiece>> moves = 
                    new TreeMap<GamePiece, LinkedList<GamePiece>>();

            if (hasMap.equals("mapping added pieces to overtaken pieces")) {
                // numTurns is equal to the number of keys in the map
                for (int i = 0; i < this.numTurns; i++) {

                    // first is "added piece" string
                    br.readLine();

                    // second is the actual added piece information
                    String addedPiece = br.readLine();

                    // row at 0, column at 1, color at 2
                    int x = addedPiece.charAt(0) - '0';
                    int y = addedPiece.charAt(1) - '0';
                    int clr = addedPiece.charAt(2) - '0';

                    GamePiece key = new GamePiece(clr, x, y);

                    // third the number of values mapped
                    String s = br.readLine();
                    // remove non-digits
                    String clean = s.replaceAll("\\D+", "");
                    int numValues = Integer.parseInt(clean);

                    // get the values mapped to the key
                    LinkedList<GamePiece> values = new LinkedList<GamePiece>();
                    for (int j = 0; j < numValues; j++) {
                        // overtaken game piece information
                        String overtaken = br.readLine();

                        // row at 0, column at 1, color at 2
                        int x2 = overtaken.charAt(0) - '0';
                        int y2 = overtaken.charAt(1) - '0';
                        int clr2 = addedPiece.charAt(2) - '0';

                        // add to list of values mapped to key
                        GamePiece value = new GamePiece(clr2, x2, y2);
                        values.add(value);
                    }

                    // next iteration
                    if (br.readLine().equals("end of entry")) {
                        // add entry to the map
                        moves.put(key, values);
                        // next loop iteration
                        continue;
                    }

                }
            }

            this.movesMade = moves;

            // close file reader
            br.close();

        } catch (IOException e) {
            // check if file doesn't exist
            if (e.getClass() == FileNotFoundException.class) {
                System.out.println("File Not Found: " + fileName);
                throw new IllegalArgumentException();
            } else {
                System.out.println("I/O exception occured");
                return;
            }
        } finally {
            // check if fileName is null
            if (fileName == null) {
                System.out.println("Null input: " + fileName);
                throw new IllegalArgumentException();
            }
        }

    }

    /**
     * undoes the most recent move made
     */
    public void undo() {

        // no moves to undo
        if ((this.addedPieces.isEmpty() || (this.addedPieces == null))) {
            System.out.println("No moves to remove");
            return;
        }

        // get the most recently added piece
        GamePiece g = this.addedPieces.getLast();

        // get pieces that were overtaken by g
        LinkedList<GamePiece> overtakenPieces = this.movesMade.get(g);

        // iterate through the overtaken pieces to revert to previous color
        Iterator<GamePiece> iter = overtakenPieces.iterator();
        while (iter.hasNext()) {
            GamePiece overtaken = iter.next();
            int row = overtaken.getX();
            int column = overtaken.getY();

            // if black switch to white, if white switch to black
            if (overtaken.getColor() == 1) {
                board[row][column].setColor(2);
            } else {
                board[row][column].setColor(1);
            }
        }

        // switch the added piece back to empty
        int r = g.getX();
        int c = g.getY();
        board[r][c].setColor(0);

        // remove entry from list and map
        this.movesMade.remove(g);
        this.addedPieces.removeLast();

        // change the fields
        this.player1 = !this.player1;
        this.numTurns--;

    }

    /**
     * getCurrentPlayer is a getter for the player
     * whose turn it is in the game.
     * 
     * @return true if it's Player 1's turn,
     *         false if it's Player 2's turn.
     */
    public boolean getCurrentPlayer() {
        return this.player1;
    }

    /**
     * getGameOver is a getter for the gameOver state
     * 
     * @return true if game is over,
     *         false otherwise.
     */
    public boolean getGameOver() {
        return this.gameOver;
    }

    /**
     * getBoard is a getter for the Othello board
     * 
     * @return 8 x 8 2D array containing game pieces
     */
    public GamePiece[][] getBoard() {
        return this.board;
    }

    /**
     * getMoves is a getter for the map of moves made
     * 
     * @return tree map that maps added pieces to the pieces that
     *         they overtook
     */
    public TreeMap<GamePiece, LinkedList<GamePiece>> getMoves() {
        return this.movesMade;
    }

    /**
     * getMoves is a getter for the list of added pieces in order
     * 
     * @return linked list containing all the pieces added to the board
     *         in order of their addition
     */
    public LinkedList<GamePiece> getAddedPieces() {
        return this.addedPieces;
    }

    /**
     * getNumTurns is a getter for the number of turns in the game
     * 
     * @return int representing the number of turns taken
     */
    public int getNumTurns() {
        return this.numTurns;
    }

    /**
     * getCell is a getter for the contents of the cell specified by the method
     * arguments.
     *
     * @param c column to retrieve
     * @param r row to retrieve
     * @return a game piece in the corresponding cell on the Othello board
     */
    public GamePiece getCell(int c, int r) {
        return this.board[r][c];
    }

}
