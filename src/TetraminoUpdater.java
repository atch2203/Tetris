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
    // column 0 is rotations, 1 - 5 are kick positions
    // in the format of "String,String" where each String is a number
    // needs to be parsed into an int in order to use
    // rotations: start,end where start is the starting orientation of the mino and end is the final rotation
    // positions: x,y where x is the amount moved horizontally and y is the amount moved vertically
    // does not account for 180 spins
    // TODO account for 180 kicks
    static final String[][] otherTable = new String[8][6];
    static final String[][] iTable = new String[8][6];

    /*
     * reading in the kick tables and intialiing otherTable and iTable
     */
    static {
        try {
            Scanner input;
            File file = new File("src/kickTables.txt");
            input = new Scanner(file);

            input.skip("\\s*A\\s*");
            for (int i = 0; i < 8; i++) {
                String line = input.nextLine(); // tetrio 180 kicks are weird https://tetris.wiki/images/5/52/TETR.IO_180kicks.png
                line = line.replace("(", "");
                line = line.replace(")", "");
                String[] data = line.split("\\t");
                System.arraycopy(data, 0, otherTable[i], 0, 6);
            }

//            System.out.println("nextline " + input.nextLine() + "end");
            input.skip("\\s*B\\s*");
            for (int i = 0; i < 8; i++) {
                String line = input.nextLine(); // tetrio 180 kicks are weird https://tetris.wiki/images/5/52/TETR.IO_180kicks.png
                line = line.replace("(", "");
                line = line.replace(")", "");
                String[] data = line.split("\\t");
                System.arraycopy(data, 0, iTable[i], 0, 6);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Kick tables not found");
        }
    }

    //Combo table
    //https://tetris.wiki/images/9/96/TETR.IO_Combo-Table.png
    //Multiplier is rounded up (ie b2b#2 tspin triple 5 combo is (13 / 6) * (6+2) rounded up)

    /***
     * rotates the piece and calls the kick function if the piece collides with the board
     * @param tetramino tetramino to modify
     * @param boardState modifiable boardState
     * @param n is the number of rotations right (negative number means rotate left)
     */
    public static void rotate(Tetramino tetramino, String[] boardState, int n) {
        int orientation = tetramino.getOrientation();
        int newOrientation = orientation;
        newOrientation += n;
        newOrientation = (newOrientation + 3) % 4 + 1;
        /*
         * SRS kick info
         *  The game checks a piece's different possible positions in order depending on the kick table
         *  If there's no collision, the piece is rotated, if there is, move on to the next
         */
        // checks for collisions using a dummy tetramino and then kicks it if it does collide
        Tetramino dummy = new Tetramino(tetramino);
        dummy.updateMino(newOrientation);
        if (checkCollision(dummy, boardState)) {
            kick(dummy, tetramino, boardState, orientation + "," + newOrientation);
        } else {
            tetramino.updateMino(tetramino.getX(), tetramino.getY(), newOrientation);
        }
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
     * moves the dummy tetramino into its position based on the kick table
     * @param dummy dummy tetramino
     * @param tetramino tetramino that is being kicked
     * @param boardState board state
     * @param rotation the rotation string that matches that of the kick table
     */
    public static void kick(Tetramino dummy, Tetramino tetramino, String[] boardState, String rotation) {
        int row = 0;
        int x, y;
        int originalX = dummy.getX();
        int originalY = dummy.getY();
        if (dummy.getType() == Tetramino.Type.O) { }
        else if (dummy.getType() == Tetramino.Type.I) {
            for (int i = 0; i < 8; i++) {
                if (iTable[i][0].equals(rotation)) {
                    row = i;
                    break;
                } else {
//                    System.out.println("Error: rotation not found: " + rotation + " " + iTable[i][0]);
                }
            }
            for (int i = 1; i < 6; i++) {
                x = Integer.parseInt(iTable[row][i].split(",")[0]);
                y = Integer.parseInt(iTable[row][i].split(",")[1]);
//                System.out.println("x: " + x + ", y: " + y);
                dummy.updateMino(originalX + x, originalY - y, dummy.getOrientation());
                if (!checkCollision(dummy, boardState)) {
                    tetramino.updateMino(dummy.getX(), dummy.getY(), dummy.getOrientation());
                    break;
                }
                dummy.updateMino(originalX, originalY, dummy.getOrientation());
            }
        } else {
            for (int i = 0; i < 8; i++) {
                if (otherTable[i][0].equals(rotation)) {
                    row = i;
//                    System.out.println("row" + row);
                    break;
                } else {
//                    System.out.println("Error: rotation not found: " + rotation + " " + otherTable[i][0]);
                }
            }
            for (int i = 1; i < 6; i++) {
                x = Integer.parseInt(otherTable[row][i].split(",")[0]);
                y = Integer.parseInt(otherTable[row][i].split(",")[1]);
//                System.out.println("x: " + x + ", y: " + y);
                dummy.updateMino(originalX + x, originalY - y, dummy.getOrientation());
                if (!checkCollision(dummy, boardState)) {
                    tetramino.updateMino(dummy.getX(), dummy.getY(), dummy.getOrientation());
//                    System.out.println("originalX: " + originalX);
//                    System.out.println("originalY: " + originalY);
//                    System.out.println("new coords: " + dummy.getX() + "," + dummy.getY());
                    break;
                }
                dummy.updateMino(originalX, originalY, dummy.getOrientation());

            }
        }
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
            String line = boardState[Math.max(tetramino.getCoords()[i][1], 0)];
            line = line.substring(0, tetramino.getCoords()[i][0]) + tetramino.getType().getMino() + line.substring(tetramino.getCoords()[i][0] + 1);
            boardState[Math.max(tetramino.getCoords()[i][1], 0)] = line;
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
    public static boolean isFilled(int x, int y, String[] boardState) {
        return boardState[Math.max(y, 0)].charAt(Math.max(Math.min(x, 11), 0)) != ' ';
    }
}