/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
Parses all the data(board state, score, next pieces, etc) and turns it into a string

 */

public class BoardDisplay {
    public String displayBoard(String[] boardState, int score){
        StringBuilder output = new StringBuilder();
        for(String s : boardState){
            output.append(s).append('\n');
        }
        output.append('\n').append("score: " + score);
        return output.toString();
    }
}
