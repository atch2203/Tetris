/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
THe user will run the main method in this, which will call all the other classes.
 */


import java.util.Scanner;

public class Main {

    static Board board;

    static Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        board = new Board();
        while (true) {
            System.out.println(board.getDisplay());
            processInput(scanner.nextLine());
        }
    }


    public static void processInput(String input) {
        switch (input) {//handles all the console inputs
            case "l":
                TetraminoUpdater.moveX(board.getCurrentMino(), board.getBoard(), -1);
                break;
            case "r":
                TetraminoUpdater.moveX(board.getCurrentMino(), board.getBoard(), 1);
                break;
            case "cw":
                TetraminoUpdater.rotate(board.getCurrentMino(), board.getBoard(), 1);
                break;
            case "ccw":
                TetraminoUpdater.rotate(board.getCurrentMino(), board.getBoard(), -1);
                break;
            case "180":
                TetraminoUpdater.rotate(board.getCurrentMino(), board.getBoard(), 2);
                break;
            case "sd":
                TetraminoUpdater.softDrop(board.getCurrentMino(), board.getBoard());
                break;
            case "hd":
                TetraminoUpdater.hardDrop(board.getCurrentMino(), board.getBoard());
                board.updateLines();
                board.nextMino();
                break;
            case "lock"://lock
                TetraminoUpdater.lock(board.getCurrentMino(), board.getBoard());
                board.updateLines();
                board.nextMino();
                break;
            case "hold":
                board.hold();
                break;
        }
    }
}
