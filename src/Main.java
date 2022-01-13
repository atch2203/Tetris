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
        init();
        while(true){
            System.out.println(board.getDisplay(0));
            processInput(scanner.nextLine());
            board.updateLines();
        }
    }

    public static void init(){
        for(int i = 0; i < 23; i++){
            board.getBoard()[i] = "*          *";
        }
        board.getBoard()[24] = "************";
        board.nextMino();
    }

    public static void processInput(String input){
        switch(input){
            case "l":
                TetraminoUpdater.moveX(board.getCurrentMino(), board.getBoard(), 1);
                break;
            case "r":
                break;
            case "cw":
                break;
            case "ccw":
                break;
            case "180":
                break;
            case "sd":
                break;
            case "hd":
                break;
            case "lock"://lock
                break;
        }
    }
}
