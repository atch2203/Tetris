/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
The board class contains
-the board
-the current piece and queue
-score
-control methods

Functionality includes getting a board display, updating cleared lines, controling the mino, and updating the queue/hold
 */

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Board {
    private String[] board = new String[24];
    private Tetramino currentMino;
    private Tetramino hold = null;
    private int score = 0;
    private String flavorText = "";
    private boolean hasHold = false;
    Queue<Tetramino> queue = new LinkedList<>();
    private boolean lost = false;


    public Board() {//initializes the board
        reset();
    }

    public void reset(){
        for (int i = 0; i < 23; i++) {
            board[i] = "|          |";
        }
        board[23] = "------------";
        queue.clear();
        for (int i = 0; i < 6; i++) {//fills the queue
            queue.add(Tetramino.MinoGenerator.getNext());
        }
        currentMino = queue.poll();
        hold = null;
    }


    /***
     * prints the contents of getDisplay
     */
    public void displayGame() {
        System.out.println(getDisplay());
    }

    /***
     * @return A very long string containing the entire board, the queue, and the flavor text+score
     */
    public String getDisplay() {
        StringBuilder output = new StringBuilder();
        String[] boardCopy = board.clone();
        for (int i = 0; i < 4; i++) {//inserts the mino into the display board
            String line = boardCopy[currentMino.getCoords()[i][1]];
            line = line.substring(0, currentMino.getCoords()[i][0]) + "*"/*replace this?*/ + line.substring(currentMino.getCoords()[i][0] + 1);
            boardCopy[currentMino.getCoords()[i][1]] = line;
        }
        int queueNum = 0;
        for (String s : boardCopy) {//adds the queue to the right of the board and parses the board into a single string
            output.append(s);
            output.append("  ");
            if (queueNum < 5) {
                output.append(((Tetramino) queue.toArray()[queueNum]).getType());
            } else if (queueNum == 7) {
                output.append("hold ").append(hold == null ? "X" : hold.getType());
            }
            queueNum++;
            output.append('\n');
        }
        output.append(flavorText).append('\n').append("score: ").append(score);
        flavorText = "";
        return output.toString();
    }

    /***
     * clears lines and adds to the score depending on how many were cleared
     * Also adds flavor text to the display depending on the lines cleared
     */
    public void updateLines() {
        int linesCleared = 0;
        for (int i = 0; i < board.length - 1; i++) {//clear lines
            if (!board[i].contains(" ")) {
                linesCleared++;
                for (int j = i; j > 0; j--) {
                    board[j] = board[j - 1];
                }
                board[0] = "|          |";
            }
        }
        if (linesCleared > 0) {
            score += linesCleared;
            flavorText += switch (linesCleared) {
                case 1 -> "Single";
                case 2 -> "Double";
                case 3 -> "Triple";
                case 4 -> "Quad";
                default -> throw new IllegalStateException("Unexpected value: " + linesCleared);
            };
        }
        if (board[22].equals("|          |")) {
            flavorText += "All Clear";
            score += 10;
        }
    }

    /***
     * Generates the next mino and places it in the queue, and then polls the next mino to use
     */
    public void nextMino() {
        queue.add(Tetramino.MinoGenerator.getNext());
        currentMino = queue.poll();
        if(TetraminoUpdater.checkCollision(currentMino, board)){
            reset();
        }
        hasHold = false;
    }



    public String[] getBoard() {
        return board;
    }

    public Tetramino getCurrentMino() {
        return currentMino;
    }

    //moves tetramino 1 left
    public boolean moveL(){
        return TetraminoUpdater.moveX(currentMino, board, -1);
    }

    //moves tetramino 1 right
    public boolean moveR(){
        return TetraminoUpdater.moveX(currentMino, board, 1);
    }

    //rotates tetramino clockwise
    public void clockwise(){
        TetraminoUpdater.rotate(currentMino, board, 1);
    }

    //rotates tetramino counterclockwise
    public void counterclockwise(){
        TetraminoUpdater.rotate(currentMino, board, -1);
    }

    //rotates mino 180 degrees
    public void rotate180(){
        TetraminoUpdater.rotate(currentMino, board, 2);
    }

    public void softDrop(){
        TetraminoUpdater.softDrop(currentMino, board);
    }

    public void hardDrop(){
        TetraminoUpdater.hardDrop(currentMino, board);
        updateLines();
        nextMino();
    }

    public void lock(){
        TetraminoUpdater.lock(currentMino, board);
        updateLines();
        nextMino();
    }

    /***
     * Holds current piece (replace current if exists, store current if not)
     * If the player has held, they cannot hold again until they place the piece and the next mino is generated
     */
    public void hold() {
        if(hasHold) {
            return;
        }
        if (hold == null) {
            hold = currentMino;
            nextMino();
        } else {
            Tetramino.Type type = hold.getType();
            hold = currentMino;
            currentMino = new Tetramino(type);
        }
        hasHold = true;
    }
}
