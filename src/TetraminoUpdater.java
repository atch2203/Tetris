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
     * @param tetramino tetramino to modify
     * @param boardState modifiable boardState
     * @param n is the number of rotations right (negative number means rotate left)
     */
    public static void rotate(Tetramino tetramino, String[] boardState, int n) {
        int orientation = tetramino.getOrientation();
        /*
         * SRS kick info
         *  The game checks a piece's different possible positions in order depending on the kick table
         *  If there's no collision, the piece is rotated, if there is, move on to the next
         */
        orientation += n;
        orientation = (orientation + 3) % 4 + 1;
        tetramino.updateMino(tetramino.getX(), tetramino.getY(), orientation);
    }

    /***
     *
     * @param tetramino tetramino to check
     * @param boardState 6x6?(maybe 4x4) area around tetramino
     * @return whether the tetramino is intersecting the board/surroundings
     */
    public static boolean checkCollision(Tetramino tetramino, String[] boardState) {
        int[][] coords = tetramino.getCoords();
        for (int i = 0; i < 4; i++) {
            if (isFilled(coords[i][0], coords[i][1], boardState)) {
                return true;
            }
        }
        return false;
    }

    /***
     * moves tetramino to the left or right
     * @param tetramino tetramino to modify
     * @param boardState current boardstate
     * @param n direction, 1 is right, -1 is left
     * @return whether the move was successful
     */
    public static boolean moveX(Tetramino tetramino, String[] boardState, int n) {
        int[][] coords = tetramino.getCoords();
        for (int i = 0; i < 4; i++) {
            if (isFilled(coords[i][0] + n, coords[i][1], boardState)) {
                return false;
            }
        }
        tetramino.updateMino(tetramino.getX() + n, tetramino.getY(), tetramino.getOrientation());
        return true;
    }

    /***
     * moves tetramino up or down
     * @param tetramino tetramino to modify
     * @param boardState current boardstate
     * @param n direction, 1 down, -1 up
     * @return whether the move was successful
     */
    public static boolean moveY(Tetramino tetramino, String[] boardState, int n) {
        int[][] coords = tetramino.getCoords();
        for (int i = 0; i < 4; i++) {
            if (isFilled(coords[i][0], coords[i][1] + n, boardState)) {
                return false;
            }
        }
        tetramino.updateMino(tetramino.getX(), tetramino.getY() + n, tetramino.getOrientation());
        return true;
    }

    /***
     * drops the tetramino all the way to the bottom
     * @param tetramino tetramino to modify
     * @param boardState modifiable boardstate
     */
    public static void softDrop(Tetramino tetramino, String[] boardState) {
        while (moveY(tetramino, boardState, 1)) ;
    }

    /***
     * integrates the tetramino into the board
     * @param tetramino tetramino to modify
     * @param boardState modifiable boardstate
     */
    public static void lock(Tetramino tetramino, String[] boardState) {
        for (int i = 0; i < 4; i++) {
            String line = boardState[tetramino.getCoords()[i][1]];
            line = line.substring(0, tetramino.getCoords()[i][0]) + tetramino.getType().getMino() + line.substring(tetramino.getCoords()[i][0] + 1);
            boardState[tetramino.getCoords()[i][1]] = line;
        }
    }

    /***
     * drops the tetramino to the bottom and locks it instantly
     * @param tetramino tetramino to modify
     * @param boardState modifiable boardstate
     */
    public static void hardDrop(Tetramino tetramino, String[] boardState) {
        softDrop(tetramino, boardState);
        lock(tetramino, boardState);
    }

    /***
     * returns whether the coordinate on the board is filled
     * @param x x coord
     * @param y y coord
     * @param boardState current boardstate
     * @return if the coordinate on the board is filled
     */
    private static boolean isFilled(int x, int y, String[] boardState) {
        return boardState[y].charAt(x) != ' ';
    }

}