/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
THe user will run the main method in this, which will call all the other classes.
 */


import java.io.IOException;
import java.util.Scanner;

public class Main {

    static Board board;


    public static void main(String[] args) {
        board = new Board();
        new GUI(board);
    }


}
