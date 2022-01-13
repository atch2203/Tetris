/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
When rotating a piece, read kicks from a text file and return updated position
 */


public class TetraminoUpdater {
    /***
     *
     * @param n is the number of rotations right (negative number means rotate left)
     */
    public void rotate(Tetramino tetramino, String[] boardState, int n) {
        int orientation = tetramino.getOrientation();
//        n = Math.abs(n) == 2 ? 0 : n;
//        orientation += n;
//        orientation = (orientation + 3) % 4 + 1;
        tetramino.updateMino(tetramino.getX(), tetramino.getY(), orientation);
    }

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