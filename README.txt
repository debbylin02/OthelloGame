=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: debbylin
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Arrays:
  I used a 8 x 8 2D array of GamePiece objects to replicate the othello board.
  "Empty" squares stored GamePieces with a color of 0,
  black squares stored GamePieces with a color of 1,
  and white squares stored GamePieces with a color of 2.
  This was appropriate as it allowed for me to keep track of where certain
  pieces were placed on the grid and replicated the grid format of an othello
  board. It also allowed for me to find and switch empty squares to colored
  squares much more easily when given an x,y position from a mouse click
  since x and y could be represented as rows and columns respectively. 

  2. JUnit Testable Component: 
  I checked the internal state of the game by checking to see if was properly 
  updating after my function calls in a testing file for the game. 
  For example, calling the playTurn function 
  (which was called whenever a colored piece was added to the board) 
  should update the state of the game by changing the colors of pieces
  on the board, updating the number of turns in the game, and changing 
  the current player. It also allowed for me to check if values were being
  properly added to my list of addedPieces and my map of movesMade, ensuring
  that the history of the game was being properly recorded in my collections. 
  It also allowed for me to check for edge cases, such as when a user tries
  to resume a game from an empty save file.  

  3. File I/O 
   I used File I/O to store the most recent othello game board
   and the history of all moves made. This used a text file format storing 
   the number of turn made, the current player, whether or not the game 
   was over, the entire 2D array board (treating 0s as empty spaces,
   1s as black pieces, and 2s as white pieces), and the collections keeping 
   track of the moves made and pieces added. 
   The game can read a saved file in order to set up the board 
   and rewrite the file in order to store the game state. 
   Every time a game was reset, this file was cleared. 
   This way, users are able to quit the game and resume playing 
   a previously saved game without it being reset. 

  4. Collections 
   I used a LinkedList to store all the added pieces to the Othello game 
   called "addedPieces." This allowed me to keep track of the most recent
   piece added to the board, as LinkedLists maintain the insertion order 
   of the elements, which was essential for implementing my undo method.
   I also used a TreeMap to store a history of the moves made in the game
   by mapping added pieces to a list of pieces that they overtake in the 
   board. I chose a TreeMap since it contains no duplicate keys, which
   matches the condition that a user cannot add a colored piece to an 
   already filled square, therefore no two added pieces are the same. Since 
   I could easily find the last added piece using my LinkedList, I could 
   then use the map to easily find the list of pieces that needed to be 
   reverted back to their previous state since the last added piece was kept 
   a key in the map. Additionally, there were no null keys as it was 
   impossible to add a null piece to the board (since the only way to
   "add" a colored piece was for a user to change the color in the square).  
   

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
  
  GamePiece.java constructs a GamePiece object, 
  which consists of a color (either none , black, or white), 
  and coordinates for its position on the 2D board used in Othello.java, 
  with x representing rows and y representing columns. Since the color of 
  a GamePiece object can change throughout the course of a game,
  GamePieces are identified based on their immutable x and y coordinates,
  as no two GamePieces can fill the same spot on the game board. GamePiece
  objects were used to fill the 2D array representing the game board, and 
  their color could be changed in order to represent either being "added"
  to the board (i.e. an "empty" square containing a GamePiece with a 
  color of 0 being changed to a color of 1 or 2). The comparesTo, equals, and 
  hashCode function were overridden in order to better suit the 
  needs of a GamePiece (since again, the color function could be constantly
  changing but the GamePiece itself is still the same). 
  
  Othello.java constructs an Othello object, which consists of a 2D array
  of GamePiece objects representing the game board, 
  the number of turns played, the current player, 
  a map of moves made (which maps added pieces to the pieces 
  that they overtake), an ordered list of added pieces, and whether or
  not the game is over. When playing the game, the playTurn function 
  is called to carry out the logic needed that checks 
  if a colored Othello piece can be added to that particular position 
  (such as whether or not it can overtake other pieces or if that particular
  position is empty). The class also keeps track of the most recent
  game history (i.e. the current player, number of moves, board setup)
  using File I/O through the save function. The class can revert back
  to a previously saved state using the resume function. The class uses
  a list and map in order to keep track of the history of moves in a game,
  allowing for a user to undo their moves up until they reach the starting
  state of an Othello game. The class can also check for a winning condition
  through the checkWinner function. This class carries out the "game logic"
  needed for Othello's game functionality. 
  
  OthelloBoard.java uses a Model-View-Controller framework to store an 
  instantiated Othello object (which is the model for the game) as a field,
  act as a controller by using a MouseListener, and to repaint and update
  the view. As a user clicks the game board, the model is updated. 
  Whenever the model is updated, the game board repaints itself
  and updates its status JLabel to reflect the current state of the model.
  It also implements a save, resume, undo, and reset function that 
  call upon the functions in Othello.java as well as update the view of the
  model. 
  
  RunOthello.java is the Game Main class that specifies 
  the frame and widgets of the GUI. It creates the top-level frame as 
  well as a status panel and buttons that call upon functions specified 
  in OthelloBoard.java when clicked on. 
  

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
  There was a stumbling block when trying to figure out how to implement the
  undo function, since a LinkedList would allow me to find the last piece 
  added but I also needed to keep track of the pieces that were overturned. 
  A TreeMap would allow me to keep track of which pieces were overtaken
  by an added piece, but would not allow me to maintain insertion order.
  Therefore I decided to use both.  
    

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you re-factor, if given the chance?
  I think I maintain separation fairly well. Game pieces are able to keep
  track of their own characteristic fields (i.e. colors and exact locations),
  an Othello object implements all of the game functions and logic needed 
  to make changes to the internal state of the game, an Othello Board maintains
  the controller and view, and the RunOthello file handles the frame 
  and widgets of the game. 
  All fields are kept private, and fields that can only be changed and 
  retrieved using setter and getter methods. This way fields like the color
  of a GamePiece or the 2D array board cannot be changed outside of those 
  specified methods. 
  Something that I re-factored was my overtake function in my Othello class.
  In order to shorten some code, I created a helper function piecesInBetween
  so that I could check for all directions around a target GamePiece. 
  I also used a canOvertake function to take combine all the lists of 
  possible pieces that could be overtaken in all directions to then
  pass to an overtake function, which carries out the actual "overtaking" 
  (i.e. switching the colors). These three functions were then used 
  together in order to carry out a playTurn function. 
  



========================
=: External Resources :=
========================

- Cite any external resources (libraries, images, tutorials, etc.) that you may
  have used while implementing your game.
  
  
  
