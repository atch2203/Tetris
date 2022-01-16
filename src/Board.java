/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
The board class contains
-the board
-the current piece and queue
-score
-control methods

Functionality includes getting a board display, updating cleared lines, controling the mino, and updating the queue/hold
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Board {
    private String[] board = new String[24];
    private Tetramino currentMino;
    private Tetramino preview;
    private Tetramino hold = null;
    private boolean hasHold = false;
    Queue<Tetramino> queue = new LinkedList<>();
    private Tetramino.MinoGenerator minoGenerator;
    private Random garbageGenerator;

    private int score = 0;
    private int b2b = 0;
    private int combo = 0;
    private Queue<Integer> garbage = new LinkedList<>();
    private int attack = 0;
    private int totalAttack = 0;
    private int piecesPlaced = 0;
    private long startTime = 0;
    private String flavorText = "";


    public Board(int seed) {//initializes the board
        garbageGenerator = new Random(seed + 1);
        minoGenerator = new Tetramino.MinoGenerator(seed);
        reset();
    }

    public void reset() {
        for (int i = 0; i < 23; i++) {
            board[i] = "|          |";
        }
        board[23] = "------------";
        queue.clear();
        for (int i = 0; i < 6; i++) {//fills the queue
            queue.add(minoGenerator.getNext());
        }
        currentMino = queue.poll();
        hold = null;
        startTime = System.currentTimeMillis();
        garbage = new LinkedList<>();
    }


    /***
     * prints the contents of getDisplay
     */
    public void displayGame() {
        System.out.println(getDisplay());
    }

    /***
     * @return A very long string containing the entire board, the queue, and the flavor text+score
     */
    public String getDisplay() {
        StringBuilder output = new StringBuilder();
        String[] boardCopy = getFullBoard();
        int queueNum = 0;
        for (String s : boardCopy) {//adds the queue to the right of the board and parses the board into a single string
            output.append(s);
            output.append("  ");
            if (queueNum < 5) {
                output.append(((Tetramino) queue.toArray()[queueNum]).getType());
            } else if (queueNum == 7) {
                output.append("hold ").append(hold == null ? "X" : hold.getType());
            }
            queueNum++;
            output.append('\n');
        }
        output.append(flavorText).append('\n').append("score: ").append(score);
        flavorText = "";
        return output.toString();
    }

    /***
     * gets board with current mino and preview drawn
     * @return board with mino drawn
     */
    public String[] getFullBoard(){
        String[] boardCopy = board.clone();

        if(getHeight() < 8){//displays the next tetramino if the board is getting high
            preview = new Tetramino(queue.peek());
            insertMino(preview, boardCopy, "N");
        }

        preview = new Tetramino(currentMino);
        TetraminoUpdater.softDrop(preview, board);

        insertMino(preview, boardCopy, "P");

        insertMino(currentMino, boardCopy, currentMino.getType().toString());

        int garbageBottom = 22;
        int garbageOverlap = 0;
        for(int g : garbage) {

            for (int i = garbageBottom; i > Math.max(garbageBottom - g, 0); i--) {
                boardCopy[i] = "W" + boardCopy[i].substring(1);
            }
            garbageBottom -= g;
            if(garbageBottom < 0){
                break;
            }
        }

        return boardCopy;
    }

    /***
     * Inserts mino into the board
      * @param tetramino mino to display
     * @param board current board
     * @param character the character to use when inserting mino
     */
    private void insertMino(Tetramino tetramino, String[] board, String character){
        for (int i = 0; i < 4; i++) {//inserts the mino into the display board
            String line = board[tetramino.getCoords()[i][1]];
            line = line.substring(0, tetramino.getCoords()[i][0]) + character + line.substring(tetramino.getCoords()[i][0] + 1);
            board[tetramino.getCoords()[i][1]] = line;
        }
    }

    /***
     * clears lines and adds to the score depending on how many were cleared
     * checks t spin
     * Also adds flavor text to the display depending on the lines cleared
     */
    public void updateLines() {
        int linesCleared = 0;
        for (int i = 0; i < board.length - 1; i++) {//clear lines
            if (!board[i].contains(" ")) {
                linesCleared++;
                for (int j = i; j > 0; j--) {
                    board[j] = board[j - 1];
                }
                board[0] = "|          |";
            }
        }
        if (linesCleared > 0) {
            flavorText = "";
            score += linesCleared;
            if(combo > 0){
                flavorText += combo + " combo\n";
            }

            if(b2b > 0 && (linesCleared == 4 || checkTSpin(currentMino, board))){
                flavorText += b2b + "x b2b\n";
            }
            if(checkTSpin(currentMino, board)){
                flavorText += "T spin ";
            }
            flavorText += switch (linesCleared) {
                case 1 -> "Single";
                case 2 -> "Double";
                case 3 -> "Triple";
                case 4 -> "Quad";
                default -> throw new IllegalStateException("Unexpected value: " + linesCleared);
            };
            if (board[22].equals("|          |")) {
                flavorText += "\nAll Clear";
                score += 10;
            }
            if(linesCleared == 4 || checkTSpin(currentMino, board)){
                b2b++;
            }else{
                b2b = 0;
            }
            combo++;
            attack = linesCleared;
            totalAttack += attack;
        }else{
            processGarbage();
            combo = 0;
        }
    }

    /***
     * lets the gui get the attack to send and then resets attack
     * @return attack sent by previous move
     */
    public int getAttack() {
        int temp = attack;
        attack = 0;
        return temp;
    }

    /***
     * processes the garbage and adds it to the board
     */
    private void processGarbage(){
        int garbageHeight;
        try{
            garbageHeight = garbage.poll();
        }catch(NullPointerException e){
            System.out.println("null");
            return;
        }

        //move board up
        for(int i = garbageHeight; i < 23; i++){
            board[i - garbageHeight] = board[i];
        }

        //move tetramino up
        int oldMinoSpot = currentMino.getY();
        currentMino.updateMino(currentMino.getX(), currentMino.getY() - garbageHeight, currentMino.getOrientation());

        //Add garbage
        int garbageColumn = garbageGenerator.nextInt(10) + 1;
        String garbageLine = "|GGGGGGGGGG|".substring(0, garbageColumn) + " " + "|GGGGGGGGGG|".substring(garbageColumn + 1);
        for(int i = 22; i > 22 - garbageHeight; i--){
            board[i] = garbageLine;
        }

        //move tetramino back down
        while(currentMino.getY() <= oldMinoSpot && TetraminoUpdater.moveY(currentMino, board, 1));
    }

    /***
     * checks if the board + tetramino is in a t spin state
     * @param tetramino current tetramino, can be non-t
     * @param board current board state
     * @return whether the tetramino is considered a t spin
     */
    private boolean checkTSpin(Tetramino tetramino, String[] board){
        if(tetramino.getType() != Tetramino.Type.T){
            return false;
        }
        //stsd and fin special cases
        return true;
    }

    /***
     * Generates the next mino and places it in the queue, and then polls the next mino to use
     */
    public void nextMino() {
        queue.add(minoGenerator.getNext());
        currentMino = queue.poll();
        if (TetraminoUpdater.checkCollision(currentMino, board) || getHeight() < 0) {
            reset();
        }
        hasHold = false;
    }


    /***
     * gets the height of the maximum piece on the board
     * @return the height, with 0 being highest and 24 being lowest
     */
    public int getHeight(){
        for(int i = 0; i < 24; i++){
            if(!board[i].equals("|          |")){
                return i;
            }
        }
        return 24;
    }


    public String[] getBoard() {
        return board;
    }

    public Tetramino getCurrentMino() {
        return currentMino;
    }

    //moves tetramino 1 left
    public boolean moveL() {
        return TetraminoUpdater.moveX(currentMino, board, -1);
    }

    //moves tetramino 1 right
    public boolean moveR() {
        return TetraminoUpdater.moveX(currentMino, board, 1);
    }

    //rotates tetramino clockwise
    public void clockwise() {
        TetraminoUpdater.rotate(currentMino, board, 1);
    }

    //rotates tetramino counterclockwise
    public void counterclockwise() {
        TetraminoUpdater.rotate(currentMino, board, -1);
    }

    //rotates mino 180 degrees
    public void rotate180() {
        TetraminoUpdater.rotate(currentMino, board, 2);
    }

    public void softDrop() {
        TetraminoUpdater.softDrop(currentMino, board);
    }

    public void hardDrop() {
        TetraminoUpdater.hardDrop(currentMino, board);
        updateLines();
        nextMino();
    }

    public boolean touchingGround(){
        Tetramino temp = new Tetramino(currentMino);
        TetraminoUpdater.softDrop(temp, board);
        return temp.getY() == currentMino.getY();
    }

    /***
     * Holds current piece (replace current if exists, store current if not)
     * If the player has held, they cannot hold again until they place the piece and the next mino is generated
     */
    public void hold() {
        if (hasHold) {
            return;
        }
        if (hold == null) {
            hold = currentMino;
            nextMino();
        } else {
            Tetramino.Type type = hold.getType();
            hold = currentMino;
            currentMino = new Tetramino(type);
        }
        hasHold = true;
    }

    public Tetramino getHold() {
        return hold;
    }

    public boolean isHasHold() {
        return hasHold;
    }

    public Queue<Tetramino> getQueue() {
        return queue;
    }

    public void setBoard(String[] board) {
        this.board = board;
    }

    public void setCurrentMino(Tetramino currentMino) {
        this.currentMino = currentMino;
    }

    public void setHold(Tetramino hold) {
        this.hold = hold;
    }

    public void setQueue(Queue<Tetramino> queue) {
        this.queue = queue;
    }

    public String getFlavorText() {
        return flavorText;
    }

    public Queue<Integer> getGarbage() {
        return garbage;
    }
}
