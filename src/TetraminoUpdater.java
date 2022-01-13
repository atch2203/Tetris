/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
When rotating a piece, read kicks from a text file and return updated position
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TetraminoUpdater {
    private Scanner s = new Scanner(new File("tetraminoPositions.txt"));

    public TetraminoUpdater() throws FileNotFoundException {
    }

    /***
     *
     * @param tetramino tetramino to modify
     * @param boardState modifiable boardState
     * @param n is the number of rotations right (negative number means rotate left)
     */
    public void rotate(Tetramino tetramino, String[] boardState, int n) {
        int orientation = tetramino.getOrientation();

        /*
         * SRS kick info
         *  The game checks a piece's different possible positions in order depending on the kick table
         *  If there's no collision, the piece is rotated, if there is, move on to the next
         */

//        n = Math.abs(n) == 2 ? 0 : n;
//        orientation += n;
//        orientation = (orientation + 3) % 4 + 1;
        tetramino.updateMino(tetramino.getX(), tetramino.getY(), orientation);
    }

    /***
     *
     * @param tetramino tetramino to check
     * @param surroundings 6x6?(maybe 4x4) area around tetramino
     * @return
     */
    public boolean checkCollision(Tetramino tetramino, String[] surroundings){
        return false;
    }

    /***
     * moves tetramino to the left or right
     * @param tetramino tetramino to modify
     * @param boardState current boardstate
     * @param n direction, 1 is right, -1 is left
     */
    public void move(Tetramino tetramino, String[] boardState, int n){

    }

    /***
     * drops the tetramino all the way to the bottom
     * @param tetramino tetramino to modify
     * @param boardState modifiable boardstate
     */
    public void softDrop(Tetramino tetramino, String[] boardState){

    }

    /***
     * integrates the tetramino into the board
     * @param tetramino tetramino to modify
     * @param boardState modifiable boardstate
     */
    public void lock(Tetramino tetramino, String[] boardState){

    }

    /***
     * drops the tetramino to the bottom and locks it instantly
     * @param tetramino tetramino to modify
     * @param boardState modifiable boardstate
     */
    public void hardDrop(Tetramino tetramino, String[] boardState){
        softDrop(tetramino, boardState);
        lock(tetramino, boardState);
    }

}