package org.cis120.othello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Map;

import org.junit.jupiter.api.*;

public class GameTest {

    // testing GamePieces
    @Test
    public void updatingGamePiece() {
        GamePiece g = new GamePiece(0, 2, 3);
        g.setColor(2);
        assertEquals(g.getColor(), 2);
    }

    @Test
    public void invalidColorGamePiece() {
        assertThrows(IllegalArgumentException.class, () -> {
            new GamePiece(4, 1, 2);
        });
    }

    @Test
    public void negativeXCoorGamePiece() {
        assertThrows(IllegalArgumentException.class, () -> {
            new GamePiece(0, -1, 4);
        });
    }

    @Test
    public void negativeYCoorGamePiece() {
        assertThrows(IllegalArgumentException.class, () -> {
            new GamePiece(0, 1, -3);
        });
    }

    @Test
    public void outOfBoundsXCoorGamePiece() {
        assertThrows(IllegalArgumentException.class, () -> {
            new GamePiece(0, 8, 2);
        });
    }

    @Test
    public void outOfBoundsYCoorGamePiece() {
        assertThrows(IllegalArgumentException.class, () -> {
            new GamePiece(0, 0, 8);
        });
    }

    @Test
    public void invalidUpdatedGamePiece() {
        assertThrows(IllegalArgumentException.class, () -> {
            GamePiece g = new GamePiece(0, 2, 3);
            g.setColor(-1);
        });
    }

    @Test
    public void emptyGamePiece() {
        GamePiece g = new GamePiece(0, 0, 0);
        assertTrue(g.isEmpty());
    }

    @Test
    public void nonemptyGamePiece() {
        GamePiece g = new GamePiece(2, 1, 2);
        assertFalse(g.isEmpty());
    }

    @Test
    public void testingEqualGamePieces() {
        GamePiece g = new GamePiece(2, 2, 3);
        GamePiece g2 = new GamePiece(0, 2, 3);
        GamePiece g3 = new GamePiece(1, 2, 3);
        assertTrue(g.equals(g2));
        assertTrue(g2.equals(g3));
        assertTrue(g.equals(g3));
    }

    @Test
    public void testingUnequalGamePieces() {
        GamePiece g = new GamePiece(2, 2, 3);
        GamePiece g2 = new GamePiece(0, 3, 3);
        GamePiece g3 = new GamePiece(1, 3, 2);
        GamePiece g4 = new GamePiece(2, 2, 2);
        assertFalse(g.equals(g2));
        assertFalse(g2.equals(g3));
        assertFalse(g.equals(g3));
        assertFalse(g.equals(g4));
        assertFalse(g2.equals(g4));
        assertFalse(g3.equals(g4));
    }

    // testing Othello game
    
    @Test
    public void initialOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        o.printGameState();
        assertTrue(o.getCurrentPlayer());
        assertEquals(o.getNumTurns(), 0);
        assertTrue(o.getMoves().isEmpty());
        assertTrue(o.getAddedPieces().isEmpty());
        assertFalse(o.getGameOver());
    }

    @Test
    public void startingUnchangedOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        o.playTurn(3, 3);
        o.printGameState();
        o.playTurn(4, 4);
        o.printGameState();
        o.playTurn(3, 4);
        o.printGameState();
        o.playTurn(4, 3);
        o.printGameState();
        assertTrue(o.getCurrentPlayer());
        assertTrue(o.getMoves().isEmpty());
        assertTrue(o.getAddedPieces().isEmpty());
        assertFalse(o.getGameOver());
        assertEquals(o.getNumTurns(), 0);
    }

    @Test
    public void invalidTurnsInOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // Should all be p1
        o.playTurn(7, 7);
        o.printGameState();

        o.playTurn(2, 0);
        o.printGameState();

        o.playTurn(0, 5);
        o.printGameState();

        o.playTurn(3, 7);
        o.printGameState();

        o.playTurn(4, 1);
        o.printGameState();

        o.playTurn(3, 6);
        o.printGameState();

        o.playTurn(6, 4);
        o.printGameState();

        o.playTurn(3, 5);
        o.printGameState();
        assertTrue(o.getCurrentPlayer());
        assertTrue(o.getMoves().isEmpty());
        assertTrue(o.getAddedPieces().isEmpty());
        assertFalse(o.getGameOver());
        assertEquals(o.getNumTurns(), 0);
    }
    
        

    @Test
    public void overtakeTopColumnOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(2, 3);
        o.printGameState();
        // p2
        o.playTurn(2, 4);
        o.printGameState();

        assertTrue(o.getCurrentPlayer());
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 2, 3)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 2, 4)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 2, 4)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 2, 3)));
        assertEquals(o.getNumTurns(), 2);
    }

    @Test
    public void overtakeBottomColumnOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(5, 4);
        o.printGameState();
        // p2
        o.playTurn(5, 3);
        o.printGameState();

        assertTrue(o.getCurrentPlayer());
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 5, 4)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 5, 3)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 5, 4)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 5, 3)));
        assertEquals(o.getNumTurns(), 2);
    }

    @Test
    public void overtakeRightRowOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(3, 2);
        o.printGameState();
        // p2
        o.playTurn(4, 2);
        o.printGameState();

        assertTrue(o.getCurrentPlayer());
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 3, 2)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 4, 2)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 3, 2)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 4, 2)));
        assertEquals(o.getNumTurns(), 2);
    }

    @Test
    public void overtakeLeftRowOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(5, 4);
        o.printGameState();
        // p2
        o.playTurn(5, 3);
        o.printGameState();

        assertTrue(o.getCurrentPlayer());
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 5, 4)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 5, 3)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 5, 4)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 5, 3)));
        assertEquals(o.getNumTurns(), 2);
    }

    @Test
    public void overtakeRowsAndColumnsOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(5, 4);
        o.printGameState();
        // p2
        o.playTurn(3, 5);
        o.printGameState();
        // p1
        o.playTurn(2, 4);
        o.printGameState();

        assertFalse(o.getCurrentPlayer());
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 5, 4)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 3, 5)));
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 2, 4)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 5, 4)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 3, 5)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 2, 4)));
        assertEquals(o.getNumTurns(), 3);
    }

    @Test
    public void overtakeTopLeftOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(4, 5);
        o.printGameState();
        // p2
        o.playTurn(5, 5);
        o.printGameState();

        assertTrue(o.getCurrentPlayer());
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 4, 5)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 5, 5)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 4, 5)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 5, 5)));
        assertEquals(o.getNumTurns(), 2);
    }

    @Test
    public void overtakeBottomRightOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(3, 2);
        o.printGameState();
        // p2
        o.playTurn(2, 2);
        o.printGameState();

        assertTrue(o.getCurrentPlayer());
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 3, 2)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 2, 2)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 3, 2)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 2, 2)));
        assertEquals(o.getNumTurns(), 2);
    }

    @Test
    public void overtakeTopRightOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(3, 2);
        o.printGameState();
        // p2
        o.playTurn(2, 4);
        o.printGameState();
        // p1
        o.playTurn(4, 5);
        o.printGameState();
        // p2
        o.playTurn(4, 2);
        o.printGameState();

        assertTrue(o.getCurrentPlayer());
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 3, 2)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 2, 4)));
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 4, 5)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 4, 2)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 3, 2)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 2, 4)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 4, 5)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 4, 2)));
        assertEquals(o.getNumTurns(), 4);
    }

    @Test
    public void overtakeBottomLeftOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(4, 5);
        o.printGameState();
        // p2
        o.playTurn(5, 3);
        o.printGameState();
        // p1
        o.playTurn(4, 2);
        o.printGameState();
        // p2
        o.playTurn(3, 5);
        o.printGameState();

        assertTrue(o.getCurrentPlayer());
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 4, 5)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 5, 3)));
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 4, 2)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 3, 5)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 4, 5)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 5, 3)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 4, 2)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 3, 5)));
        assertEquals(o.getNumTurns(), 4);
    }

    @Test
    public void overtakeDiagonalsTestOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(3, 2);
        o.printGameState();
        // p2
        o.playTurn(2, 4);
        o.printGameState();
        // p1
        o.playTurn(3, 5);
        o.printGameState();
        // p2
        o.playTurn(4, 2);
        o.printGameState();
        // p1
        o.playTurn(5, 4);
        o.printGameState();
        // p2
        o.playTurn(5, 5);
        o.printGameState();

        assertTrue(o.getCurrentPlayer());
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 3, 2)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 2, 4)));
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 3, 5)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 4, 2)));
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 5, 4)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 5, 5)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 3, 2)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 2, 4)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 3, 5)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 4, 2)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 5, 4)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 5, 5)));
        assertEquals(o.getNumTurns(), 6);
    }

    @Test
    public void saveFileTestOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(3, 2);
        o.printGameState();
        // p2
        o.playTurn(2, 2);
        o.printGameState();
        // p1
        o.playTurn(5, 4);
        o.printGameState();
        // p2
        o.playTurn(5, 5);
        o.printGameState();

        // check save file for changes
        o.save("files/saveFile.txt");

        assertTrue(o.getCurrentPlayer());
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 3, 2)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 2, 2)));
        assertTrue(o.getMoves().containsKey(new GamePiece(1, 5, 4)));
        assertTrue(o.getMoves().containsKey(new GamePiece(2, 5, 5)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 3, 2)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 2, 2)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(1, 5, 4)));
        assertTrue(o.getAddedPieces().contains(new GamePiece(2, 5, 5)));
        assertEquals(o.getNumTurns(), 4);
    }

    @Test
    public void undoSingleTestOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(3, 2);
        o.printGameState();
        // p2
        o.playTurn(2, 2);
        o.printGameState();
        // p1
        o.playTurn(5, 4);
        o.printGameState();
        // p2
        o.playTurn(5, 5);
        o.printGameState();
        // undo
        o.undo();
        o.printGameState();

        GamePiece[][] board = o.getBoard();
        assertEquals(board[5][5].getColor(), 0);
        assertEquals(board[4][4].getColor(), 1);
        assertEquals(o.getNumTurns(), 3);
        assertFalse(o.getCurrentPlayer());
    }

    @Test
    public void undoAllMovesOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(3, 2);
        // p2
        o.playTurn(2, 2);
        // p1
        o.playTurn(5, 4);
        // p2
        o.playTurn(5, 5);

        o.printGameState();

        o.undo();
        o.undo();
        o.undo();
        o.undo();

        o.printGameState();

        boolean currPlayer = o.getCurrentPlayer();
        boolean gameOver = o.getGameOver();
        int numTurns = o.getNumTurns();
        GamePiece[][] board = o.getBoard();
        TreeMap<GamePiece, LinkedList<GamePiece>> tm = o.getMoves();
        LinkedList<GamePiece> l = o.getAddedPieces();

        // check fields
        assertTrue(tm.isEmpty());
        assertTrue(l.isEmpty());
        assertEquals(currPlayer, true);
        assertEquals(gameOver, false);
        assertEquals(numTurns, 0);

        // check board
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                GamePiece g = board[i][j];
                // same as starting board
                if ((i == 3) && (j == 3)) {
                    assertEquals(g.getColor(), 2);
                } else if ((i == 4) && (j == 4)) {
                    assertEquals(g.getColor(), 2);
                } else if ((i == 3) && (j == 4)) {
                    assertEquals(g.getColor(), 1);
                } else if ((i == 4) && (j == 3)) {
                    assertEquals(g.getColor(), 1);
                } else {
                    assertEquals(g.getColor(), 0);
                }

            }
        }
    }

    @Test
    public void resumeSameAsSavedOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(3, 2);
        // p2
        o.playTurn(2, 2);
        // p1
        o.playTurn(5, 4);
        // p2
        o.playTurn(5, 5);

        o.printGameState();

        // save
        o.save("files/saveFile.txt");

        TreeMap<GamePiece, LinkedList<GamePiece>> savedTM = o.getMoves();
        boolean savedPlayer = o.getCurrentPlayer();
        boolean savedGO = o.getGameOver();
        int savedNumTurns = o.getNumTurns();
        GamePiece[][] savedBoard = o.getBoard();

        // moves after saving should not be present after resume

        // p1
        o.playTurn(4, 5);
        // p2
        o.playTurn(3, 5);

        // resume
        o.resume("files/saveFile.txt");

        o.printGameState();

        boolean resumePlayer = o.getCurrentPlayer();
        boolean resumeGO = o.getGameOver();
        int resumeNumTurns = o.getNumTurns();
        GamePiece[][] resumeBoard = o.getBoard();

        // check fields
        assertEquals(savedPlayer, resumePlayer);
        assertEquals(savedGO, resumeGO);
        assertEquals(savedNumTurns, resumeNumTurns);
        assertEquals(4, resumeNumTurns);

        // check board
        for (int i = 0; i < resumeBoard.length; i++) {
            for (int j = 0; j < resumeBoard[i].length; j++) {
                assertEquals(savedBoard[i][j], resumeBoard[i][j]);
            }
        }

        TreeMap<GamePiece, LinkedList<GamePiece>> resumeTM = o.getMoves();
        LinkedList<GamePiece> resumeAP = o.getAddedPieces();

        Iterator<GamePiece> iter = resumeAP.iterator();
        while (iter.hasNext()) {
            GamePiece g = iter.next();

            assertTrue(savedTM.containsKey(g));
            assertTrue(resumeTM.containsKey(g));

            LinkedList<GamePiece> val1 = savedTM.get(g);
            LinkedList<GamePiece> val2 = resumeTM.get(g);

            // check if same mappings
            assertTrue(val1.equals(val2));
        }
    }

    @Test
    public void undoAfterResumeOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(4, 5);
        // p2
        o.playTurn(5, 3);
        // p1
        o.playTurn(4, 2);
        // p2
        o.playTurn(5, 5);
        // p1
        o.playTurn(5, 4);
        // p2
        o.playTurn(5, 2);
        // p1
        o.playTurn(6, 2);
        // p2
        o.playTurn(5, 1);

        o.printGameState();

        o.save("files/saveFile.txt");
        o.resume("files/saveFile.txt");

        int size = o.getNumTurns();
        TreeMap<GamePiece, LinkedList<GamePiece>> resumeTM = o.getMoves();
        LinkedList<GamePiece> resumeAP = o.getAddedPieces();
        GamePiece last = resumeAP.getLast();

        o.undo();
        TreeMap<GamePiece, LinkedList<GamePiece>> undoTM = o.getMoves();
        LinkedList<GamePiece> undoAP = o.getAddedPieces();
        assertEquals(o.getNumTurns(), (size - 1));
        assertFalse(undoTM.containsKey(last));
        assertFalse(undoAP.contains(last));

        for (Map.Entry<GamePiece, LinkedList<GamePiece>> entry : 
            undoTM.entrySet()) {
            // check same mappings
            GamePiece g = entry.getKey();
            LinkedList<GamePiece> l = entry.getValue();
            assertEquals(l, resumeTM.get(g));

        }

    }

    @Test
    public void blackWinTestOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        GamePiece[][] b = o.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                b[i][j].setColor(1);
            }
        }
        int winner = o.checkWinner();
        assertEquals(winner, 1);
        assertNotEquals(winner, 2);
        assertNotEquals(winner, 3);
    }

    @Test
    public void whiteWinTestOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        GamePiece[][] b = o.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                b[i][j].setColor(2);
            }
        }
        int winner = o.checkWinner();
        assertEquals(winner, 2);
        assertNotEquals(winner, 1);
        assertNotEquals(winner, 3);
    }

    @Test
    public void tieTestOthelloGame() {
        Othello o = new Othello("files/saveFile.txt");
        GamePiece[][] b = o.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (j < 4) {
                    b[i][j].setColor(1);
                } else {
                    b[i][j].setColor(2);
                }

            }
        }
        int winner = o.checkWinner();
        assertEquals(winner, 3);
        assertNotEquals(winner, 1);
        assertNotEquals(winner, 2);
    }

    @Test
    public void fileNotFoundTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Othello("");
        });
    }
    
    @Test
    public void fileNullTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Othello(null);
        });
    }
    
    @Test 
    public void savingToFileNotFoundTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            Othello o = new Othello("files/saveFile.txt");
            o.save("");
        });
    }
    
    @Test 
    public void savingToNullFileTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            Othello o = new Othello("files/saveFile.txt");
            o.save(null);
        });
    }
    
    @Test 
    public void resumeFromFileNotFoundTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            Othello o = new Othello("files/saveFile.txt");
            o.resume("");
        });
    }
    
    @Test 
    public void resumeFromNullFileTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            Othello o = new Othello("files/saveFile.txt");
            o.resume(null);
        });
    }
    
    @Test 
    public void resumeFromEmptyFileTest() {
        Othello o = new Othello("files/saveFile.txt");
        // p1
        o.playTurn(3, 2);
        // p2
        o.playTurn(2, 2);
        // p1
        o.playTurn(5, 4);
        o.printGameState();
        
        TreeMap<GamePiece, LinkedList<GamePiece>> tm = o.getMoves();
        LinkedList<GamePiece> ap = o.getAddedPieces();
        
        // resume has no change 
        o.resume("files/saveFile.txt");
        TreeMap<GamePiece, LinkedList<GamePiece>> resumedTM = o.getMoves();
        LinkedList<GamePiece> resumedAP = o.getAddedPieces();

        assertEquals(tm, resumedTM);
        assertEquals(ap, resumedAP); 
        assertEquals(o.getNumTurns(), 3);
        assertFalse(o.getCurrentPlayer());
        o.printGameState();  
        
    }
}
