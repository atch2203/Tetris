/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
This class generates pieces from a 7 bag
 */



import java.util.Random;

public class MinoGenerator {
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
