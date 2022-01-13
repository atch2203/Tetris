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

public class Board {
    private String[] board = new String[24];
    private Tetramino currentMino;

    public String getDisplay(int score){
        StringBuilder output = new StringBuilder();
        for(String s : board){
            output.append(s).append('\n');
        }
        output.append('\n').append("score: " + score);
        return output.toString();
    }

    /***
     * clears lines and adds to the score depending on how many were cleared
     */
    public void updateLines(){
        int linesCleared = 0;
        for(int i = 0; i < board.length; i++){//clear lines
            if(!board[i].contains(" ")){
                linesCleared++;
                for(int j = i; j > 0; j--){
                    board[j] = board[j-1];
                }
                board[0] = "*          *";
            }
        }
    }

    public String[] getBoard() {
        return board;
    }

    public Tetramino getCurrentMino() {
        return currentMino;
    }

    public void nextMino(){
        currentMino = Tetramino.MinoGenerator.getNext();
    }
}
