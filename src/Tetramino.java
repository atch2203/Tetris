/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:

4x4 grid contains each tetramino
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class Tetramino {
    private int x, y; // bottom left corner of the 4x4 grid  Should be SRS center?
    //SRS center for s,z,l,j,t, and top left(-,-) for i and o
    private int orientation; // 1 - 4, 1 being up and 2 being right
    private Type type;//Type of piece


    enum Type{
        I,
        T,
        O,
        L,
        J,
        S,
        Z;
        int[][] upCoords = new int[4][2]
                , rightCoords = new int[4][2], downCoords = new int[4][2], leftCoords = new int[4][2];

        Type() {
            readCoords(this);
        }
        //Sets the piece coordinates for each piece
        private void readCoords(Type t) {
            Scanner scanner;
            try {
                scanner = new Scanner(new File("tetraminoPositions.txt"));//reads from position file
            }catch (FileNotFoundException e){
                return;
            }
            scanner.skip(".*?" + t.toString());//skips to piece index
            for(int i = 1; i <= 4; i++){
                scanner.skip(".*?" + i);//skips to orientation number
                int[][] coords = switch(i) {//sets the corresponding string array depending on i
                    case 1 -> upCoords;
                    case 2 -> rightCoords;
                    case 3 -> downCoords;
                    case 4 -> leftCoords;
                    default -> throw new IllegalStateException("Unexpected value: " + i);
                };
                int cur = 0;
                for (int j = 0; j < 4; j++) {//reads through the grid and add coordinates that have a star in it to position array
                    String line = scanner.nextLine();
                    for(int k = 0; k < 4; k++){
                        if(line.charAt(k) == '*'){
                            coords[cur++] = new int[]{(k - 1), (j - 1)};//subtraction to offset from center
                        }
                    }
                }
            }
        }
    }



    char c = '■';

    public Tetramino(Type type) {
        x = 5;
        y = 0;
        orientation = 1;
        this.type = type;
    }

    public void updateMino(int x, int y, int o){
        this.x = x;
        this.y = y;
        this.orientation = o;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Type getType() {
        return type;
    }

    public int getOrientation() {
        return orientation;
    }

    /***
     * returns coordinate of each part of the tetramino
     * @return a string array in the format {"1,2","3,3","3,2","2,2"}
     */
    public int[][] getCoords(){
        return applyTransformation(switch(orientation){
            case 1 -> type.upCoords;
            case 2 -> type.rightCoords;
            case 3 -> type.downCoords;
            case 4 -> type.leftCoords;
            default -> throw new IllegalStateException("Unexpected value: " + orientation);
        });
    }

    public int[][] applyTransformation(int[][] input){
        for(int i = 0; i < input.length; i++){
            input[i] = new int[]{input[i][0] + x, input[i][1] + y};
        }
        return input;
    }

    public static class MinoGenerator {
        private static String used = "0000000";
        private static int left = 7;
        private static Random random = new Random();

        public static Tetramino getNext(){
            if(used.equals("1111111")){
                used = "0000000";
                left = 7;
            }
            int next = random.nextInt(left--);
            int index = 0;
            while(next >= 0){
                if(used.charAt(index) == 0){
                    next--;
                }
                index++;
            }
            index--;
            used = used.substring(0, index) + "1" + used.substring(index + 1);
            return switch (index) {
                case 0 -> new Tetramino(Tetramino.Type.I);
                case 1 -> new Tetramino(Tetramino.Type.O);
                case 2 -> new Tetramino(Tetramino.Type.T);
                case 3 -> new Tetramino(Tetramino.Type.S);
                case 4 -> new Tetramino(Tetramino.Type.Z);
                case 5 -> new Tetramino(Tetramino.Type.J);
                case 6 -> new Tetramino(Tetramino.Type.L);
                default -> throw new IllegalStateException("Unexpected value: " + index);
            };
        }
    }

}