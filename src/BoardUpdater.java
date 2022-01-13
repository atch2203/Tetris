/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
Takes in a board state and updates it by clearing any cleared lines
 */


public class BoardUpdater {
    /***
     * clears lines and adds to the score depending on how many were cleared
     * @param boardState current boardstate, with lines not cleared yet
     */
    public void updateLines(String[] boardState) {
        int linesCleared = 0;
        for (int i = 0; i < boardState.length; i++) {//clear lines
            if (!boardState[i].contains(" ")) {
                linesCleared++;
                for (int j = i; j > 0; j--) {
                    boardState[j] = boardState[j - 1];
                }
                boardState[0] = "          ";
            }
        }

    }
}
