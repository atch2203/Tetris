/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
fdsa
fdsa
fds
af
dsa
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
    Queue<Tetramino> queue = new LinkedList<>();


    public Board() {//initializes the board
        for (int i = 0; i < 23; i++) {
            board[i] = "|          |";
        }
        board[23] = "------------";
        for (int i = 0; i < 6; i++) {//fills the queue
            queue.add(Tetramino.MinoGenerator.getNext());
        }
        currentMino = queue.poll();
    }

    /***
     * @return A very long string containing the entire board and the score
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
                if (hold != null) {
                    output.append("hold ").append(hold.getType());
                }
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
            flavorText = switch (linesCleared) {
                case 1 -> "Single";
                case 2 -> "Double";
                case 3 -> "Triple";
                case 4 -> "Quad";
                default -> throw new IllegalStateException("Unexpected value: " + linesCleared);
            };
        }
        if (board[22].equals("|          |")) {
            flavorText = "All Clear";
            score += 10;
        }
    }

    /***
     * Generates the next mino
     */
    public void nextMino() {
        queue.add(Tetramino.MinoGenerator.getNext());
        currentMino = queue.poll();
    }

    /***
     * Holds current piece (replace current if exists, store current if not)
     */
    public void hold() {
        if (hold == null) {
            hold = currentMino;
            nextMino();
        } else {
            Tetramino.Type type = hold.getType();
            hold = currentMino;
            currentMino = new Tetramino(type);
        }
    }

    public String[] getBoard() {
        return board;
    }

    public Tetramino getCurrentMino() {
        return currentMino;
    }


}
