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
    private boolean lost = false;
    private boolean hasJustLost = false;
    private final boolean isSinglePlayer, isUser;

    private int isTSpin = 0;
    private int moveY = 0;
    private int b2b = -1;
    private int combo = -1;
    volatile private Queue<Integer> garbage = new LinkedList<>();
    private int attack = 0;
    private volatile int totalAttack = 0;
    private volatile int totalLines = 0;
    private volatile int piecesPlaced = 0;
    private volatile long startTime = 0;
    private volatile long endTime = 0;
    private String sideText = "";
    private volatile String flavorText = "";
    private String multiplayerText = "";
    Thread timeUpdater = new Thread();

    private static String importantCorner = "1+---2+-++3++-+4-+--";

    public Board(int seed, boolean isSinglePlayer, boolean isUser) {//initializes the board
        reset(seed);
        this.isSinglePlayer = isSinglePlayer;
        this.isUser = isUser;
    }

    public void start() {

    }

    public void reset(int seed) {
        garbageGenerator = new Random(seed + 1);
        minoGenerator = new Tetramino.MinoGenerator(seed);
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
        endTime = 0;
        garbage = new LinkedList<>();
        lost = false;
        isTSpin = 0;
        moveY = 0;
        b2b = -1;
        combo = -1;
        attack = 0;
        totalAttack = 0;
        totalLines = 0;
        piecesPlaced = 0;
        sideText = "";
        if(isUser) {
            timeUpdater = new Thread(() -> {
                while (true) {
                    flavorText = "pps: " + String.format("%.2f", piecesPlaced * 1000.0 / ((endTime == 0 ? System.currentTimeMillis() : endTime) - startTime)) + "\n" +
                            "apm: " + String.format("%.2f", totalAttack * 60000.0 / ((endTime == 0 ? System.currentTimeMillis() : endTime) - startTime));
                    if (isSinglePlayer) {
                        flavorText += "\nlines: " + totalLines + "/40\n" +
                                "time: " + (((endTime == 0 ? System.currentTimeMillis() : endTime) - startTime) / 1000.0);
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            timeUpdater.start();
        }
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
        output.append(sideText);
        sideText = "";
        return output.toString();
    }

    /***
     * gets board with current mino and preview drawn
     * @return board with mino drawn
     */
    public String[] getFullBoard() {
        if (lost) {
            return board;
        }
        String[] boardCopy = board.clone();

        if (getHeight() < 8) {//displays the next tetramino if the board is getting high
            preview = new Tetramino(queue.peek());
            insertMino(preview, boardCopy, "N");
        }

        preview = new Tetramino(currentMino);
        TetraminoUpdater.softDrop(preview, board);

        insertMino(preview, boardCopy, "P");

        insertMino(currentMino, boardCopy, currentMino.getType().toString());

        int garbageBottom = 22;
        int garbageOverlap = 0;
        for (int g : garbage) {

            for (int i = garbageBottom; i > Math.max(garbageBottom - g, 0); i--) {
                boardCopy[i] = "W" + boardCopy[i].substring(1);
            }
            garbageBottom -= g;
            if (garbageBottom < 0) {
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
    private void insertMino(Tetramino tetramino, String[] board, String character) {
        for (int i = 0; i < 4; i++) {//inserts the mino into the display board
            String line = board[Math.max(0, tetramino.getCoords()[i][1])];
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
            sideText = "";
            totalLines += linesCleared;
            if (endTime == 0 && totalLines >= 40 && isSinglePlayer) {
                endTime = System.currentTimeMillis();
                timeUpdater.interrupt();
            }

            if (isTSpin == 2) {
                sideText += "T spin ";
            } else if (isTSpin == 1) {
                sideText += "T spin mini ";
            }
            sideText += switch (linesCleared) {
                case 1 -> "Single";
                case 2 -> "Double";
                case 3 -> "Triple";
                case 4 -> "Quad";
                default -> throw new IllegalStateException("Unexpected value: " + linesCleared);
            } + "\n";
            if (board[22].equals("|          |")) {
                sideText += "\nAll Clear";
                attack += 10;
            }
            if (linesCleared == 4 || isTSpin != 0) {
                b2b++;
            } else {
                b2b = -1;
            }
            combo++;
            if (combo > 0) {
                sideText += combo + " combo\n";
            }
            if (b2b > 0) {
                sideText += b2b + "x b2b\n";
            }
            attack = isTSpin == 2 ? linesCleared * 2 - 1 : linesCleared - 1;
            attack += (int) (2 * Math.log(Math.max(1, combo))) + Math.min(4, (int) (Math.sqrt(Math.max(0, b2b))));
            totalAttack += attack;
            while (!garbage.isEmpty() && attack > 0) {
                attack -= garbage.poll();
                if (attack < 0) {
                    Queue<Integer> temp = new LinkedList<>();
                    temp.add(-attack);
                    temp.addAll(garbage);
                    garbage = temp;
                    attack = 0;
                }
            }
        } else {
            if (isTSpin == 2) {
                sideText = "";
                sideText += "T spin";
            } else if (isTSpin == 1) {
                sideText = "";
                sideText += "T spin mini";
            }
            processGarbage();
            combo = -1;
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
    private void processGarbage() {
        int garbageHeight;
        try {
            garbageHeight = garbage.poll();
        } catch (NullPointerException e) {
            return;
        }

        //move board up
        for (int i = garbageHeight; i < 23; i++) {
            board[i - garbageHeight] = board[i];
        }

        //move tetramino up
        int oldMinoSpot = currentMino.getY();
        currentMino.updateMino(currentMino.getX(), currentMino.getY() - garbageHeight, currentMino.getOrientation());

        //Add garbage
        int garbageColumn = garbageGenerator.nextInt(10) + 1;
        String garbageLine = "|GGGGGGGGGG|".substring(0, garbageColumn) + " " + "|GGGGGGGGGG|".substring(garbageColumn + 1);
        for (int i = 22; i > 22 - garbageHeight; i--) {
            board[i] = garbageLine;
        }

        //move tetramino back down
        while (currentMino.getY() <= oldMinoSpot && TetraminoUpdater.moveY(currentMino, board, 1)) ;
    }

    /***
     * checks if the board + tetramino is in a t spin state
     * @param tetramino current tetramino, can be non-t
     * @param board current board state
     * @return whether the tetramino is considered a t spin
     */
    private int checkTSpin(Tetramino tetramino, String[] board) {
        if (tetramino.getType() != Tetramino.Type.T) {
            return 0;
        }

        int cornerCounter = 0;
        if (TetraminoUpdater.isFilled(tetramino.getX() + 1, tetramino.getY() + 1, board)) {
            cornerCounter++;
        }
        if (TetraminoUpdater.isFilled(tetramino.getX() + 1, tetramino.getY() - 1, board)) {
            cornerCounter++;
        }
        if (TetraminoUpdater.isFilled(tetramino.getX() - 1, tetramino.getY() + 1, board)) {
            cornerCounter++;
        }
        if (TetraminoUpdater.isFilled(tetramino.getX() - 1, tetramino.getY() - 1, board)) {
            cornerCounter++;
        }
        if (cornerCounter < 3) {
            return 0;
        }
        int startIndex = importantCorner.indexOf(Integer.toString(tetramino.getOrientation()));
        int importantCounter = 0;
        String facingCorners = importantCorner.substring(startIndex + 1, startIndex + 5);//++-+
        for (int i = 0; i <= 1; i++) {
            if (TetraminoUpdater.isFilled(tetramino.getX() + (facingCorners.charAt(i * 2) == '+' ? 1 : -1), tetramino.getY() + (facingCorners.charAt(i * 2 + 1) == '+' ? 1 : -1), board)) {
                importantCounter++;
            }
        }
        if (importantCounter >= 2 || moveY >= 2) {
            return 2;
        } else {
            return 1;
        }
    }

    /***
     * Generates the next mino and places it in the queue, and then polls the next mino to use
     */
    public void nextMino() {
        if (TetraminoUpdater.checkCollision(queue.peek(), board)) {
            timeUpdater.interrupt();
            lost = true;
            hasJustLost = true;
            for (int i = 0; i < 23; i++) {
                board[i] = board[i].replaceAll("[^\\s|\\-G]", "G");
            }
            return;
        }
        queue.add(minoGenerator.getNext());
        currentMino = queue.poll();
        hasHold = false;
    }


    /***
     * gets the height of the maximum piece on the board
     * @return the height, with 0 being highest and 24 being lowest
     */
    public int getHeight() {
        for (int i = 0; i < 24; i++) {
            if (!board[i].equals("|          |")) {
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
        isTSpin = 0;
        return TetraminoUpdater.moveX(currentMino, board, -1);
    }

    //moves tetramino 1 right
    public boolean moveR() {
        isTSpin = 0;
        return TetraminoUpdater.moveX(currentMino, board, 1);
    }

    //rotates tetramino clockwise
    public void clockwise() {
        moveY = currentMino.getY();
        TetraminoUpdater.rotate(currentMino, board, 1);
        moveY = currentMino.getY() - moveY;
        isTSpin = checkTSpin(currentMino, board);
    }

    //rotates tetramino counterclockwise
    public void counterclockwise() {
        moveY = currentMino.getY();
        TetraminoUpdater.rotate(currentMino, board, -1);
        moveY = currentMino.getY() - moveY;
        isTSpin = checkTSpin(currentMino, board);
    }

    //rotates mino 180 degrees
    public void rotate180() {
        moveY = currentMino.getY();
        TetraminoUpdater.rotate(currentMino, board, 2);
        moveY = currentMino.getY() - moveY;
        isTSpin = checkTSpin(currentMino, board);
    }

    public void softDrop() {
        TetraminoUpdater.softDrop(currentMino, board);
    }

    public void hardDrop() {
        piecesPlaced++;
        TetraminoUpdater.hardDrop(currentMino, board);
        updateLines();
        nextMino();
    }

    public boolean touchingGround() {
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

    public String getSideText() {
        return multiplayerText + "\n\n" + flavorText + "\n\n" + sideText;
    }

    public Queue<Integer> getGarbage() {
        return garbage;//new LinkedList<>(garbage);
    }

    public boolean isLost() {
        return lost;
    }

    public boolean hasJustLost() {
        if (hasJustLost) {
            hasJustLost = false;
            return true;
        }
        return false;
    }

    public void setMultiplayerText(String multiplayerText) {
        this.multiplayerText = multiplayerText;
    }

    public void setSideText(String sideText) {
        this.sideText = sideText;
    }

    public boolean isUser(){
        return isUser;
    }

    public void stopTimer(){
        timeUpdater.interrupt();
    }
}
