package org.cis120.othello;

/**
 * This class constructs a GamePiece, which consists of a color (either none,
 * black, or white), and coordinates for its position on the 2D board used
 * in Othello.java, with x representing rows and y representing columns.
 */
public class GamePiece implements Comparable<GamePiece> {

    // color for a game piece, which is represented as an int 0, 1, or 2
    private int color;

    // x and y positions of the game piece in the board
    private int x;
    private int y;

    // constructor for game pieces
    public GamePiece(int c, int x, int y) {

        // check for valid colors
        if ((c == 0) || (c == 1) || (c == 2)) {
            this.color = c;
        } else {
            System.out.println("invalid color");
            throw new IllegalArgumentException();
        }

        // check for bounds of the grid
        if ((x < 0) || (x > 7) || (y < 0) || (y > 7)) {
            System.out.println("invalid positions");
            throw new IllegalArgumentException();
        } else {
            this.x = x;
            this.y = y;
        }
    }

    // getter method for color
    public int getColor() {
        return this.color;
    }

    // getter method for x
    public int getX() {
        return this.x;
    }

    // getter method for y
    public int getY() {
        return this.y;
    }

    // getter method for color
    public boolean isEmpty() {
        return (this.color == 0);
    }

    // setter method for color
    public void setColor(int c) {
        // check if it is a valid color (none, black, or white)
        if ((c == 0) || (c == 1) || (c == 2)) {
            this.color = c;
        } else {
            System.out.println("invalid color");
            throw new IllegalArgumentException();
        }
    }

    @Override
    // equals method for two game pieces
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(getClass() == o.getClass())) {
            return false;
        }
        GamePiece that = (GamePiece) o;
        // equal if they have the same hash code
        return (this.hashCode() == that.hashCode());

    }

    /**
     * Overriding the compareTo function
     *
     * @return Returns an int < 0 if the GamePiece is less than the input
     *         GamePiece, > 0 if it is greater than the input,
     *         and 0 if they are equal.
     */
    @Override
    public int compareTo(GamePiece g) {

        // check hash codes
        if (this.hashCode() == g.hashCode()) {
            return 0;
        } else if (this.hashCode() < g.hashCode()) {
            return -1;
        } else {
            return 1;
        }

    }

    /**
     * Overriding the compareTo function
     *
     * @return Returns an int representing the hashcode of a Game Piece object
     */
    @Override
    public int hashCode() {
        int tmp = y + ((x + 1) / 2);
        return x + (tmp * tmp);
    }

}
