/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
Displays the board and all the graphical info
Also contains controls
 */


import javax.swing.*;
import java.awt.*;
import java.util.Queue;

public class GUI extends JPanel {
    public final Board board;
    public final BoardGUI boardGUI;
    public final HoldGUI holdGUI;
    public final QueueGUI queueGUI;
    public final JLabel text;

    private long placedTime = 0;
    private long gravityTime = 1000;
    private boolean gravityDone = true;
    private Thread autoPlace, gravity;
    private Thread sendThread;
    private boolean isDone = false;

    private static final Font font = new Font(Font.MONOSPACED, Font.PLAIN, 30);
    public static final int sqaureSize = 20;//size of single pixel
    public static final int colSize = sqaureSize * 5;//width of the queue + hold columns

    public GUI(Board board, Thread sendThread){
        this(board);
        this.sendThread = sendThread;
        sendThread.start();
    }

    public GUI(Board board) {
        this.board = board;
        this.boardGUI = new BoardGUI(board.getFullBoard(), board.getGarbage());
        this.holdGUI = new HoldGUI();
        this.queueGUI = new QueueGUI(board.getQueue());

        this.text = new JLabel("");
        text.setHorizontalAlignment(SwingConstants.CENTER);
        text.setVerticalAlignment(SwingConstants.BOTTOM);
        text.setPreferredSize(new Dimension(colSize, sqaureSize * 16));
        text.setFont(font);

        JLabel holdHeader = new JLabel("Hold");
        holdHeader.setHorizontalAlignment(SwingConstants.CENTER);
        holdHeader.setVerticalAlignment(SwingConstants.TOP);
        holdHeader.setPreferredSize(new Dimension(colSize, sqaureSize));
        holdHeader.setFont(font);

        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.PAGE_AXIS));
        leftColumn.add(holdHeader);
        leftColumn.add(holdGUI);
        leftColumn.add(text);

        JLabel queueHeader = new JLabel("Queue");
        queueHeader.setHorizontalAlignment(SwingConstants.CENTER);
        queueHeader.setVerticalAlignment(SwingConstants.TOP);
        queueHeader.setPreferredSize(new Dimension(colSize, sqaureSize));
        queueHeader.setFont(font);

        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.PAGE_AXIS));
        rightColumn.add(queueHeader);
        rightColumn.add(queueGUI);

        this.setLayout(new GridBagLayout());
        this.add(leftColumn);
        this.add(boardGUI);
        this.add(rightColumn);

        this.setPreferredSize(new Dimension(sqaureSize * 30, sqaureSize * 30));

        updateDisplay();
    }

    public void setFlavorText(String s){
        text.setText("<html>" + s.replaceAll("\n", "<br />") + "</html>");
    }

    public void updateDisplayMultiPlayer(){
        boardGUI.updateUI();
        holdGUI.updateUI();
        queueGUI.updateUI();
    }

    public void updateDisplay() {
        boardGUI.setBoard(board.getFullBoard());
        boardGUI.setGarbageQueue(board.getGarbage());
        boardGUI.updateUI();
        holdGUI.setMino(board.getHold());
        holdGUI.updateUI();
        queueGUI.setQueue(board.getQueue());
        queueGUI.updateUI();
        text.setText("<html>" + board.getSideText().replaceAll("\n", "<br />") + "</html>");
    }

    public void keyPressed(){
        if(autoPlace != null) {
            autoPlace.interrupt();
        }
    }

    public synchronized void update(){
        updateDisplay();
        if(isDone){
            return;
        }
        if(sendThread != null) {
            sendThread.interrupt();
        }
        if(board.touchingGround()){
            if(autoPlace == null || !autoPlace.isAlive()) {
                autoPlace = new Thread(() -> {
                    long start = System.currentTimeMillis();
                    try {
                        Thread.sleep(1000);
                        board.hardDrop();
                        placedTime = 0;
                        update();
                    } catch (InterruptedException e) {
                        placedTime += System.currentTimeMillis() - start;
                        if (placedTime > 10000) {
                            board.hardDrop();
                            placedTime = 0;
                        }
                        update();
                    }
                });
                autoPlace.start();
            }
        }else{
            if(gravityDone) {
                gravityDone = false;
                gravity = new Thread(() -> {
                    try {
                        Thread.sleep(gravityTime);
                        TetraminoUpdater.moveY(board.getCurrentMino(), board.getBoard(), 1);
                        gravityDone = true;
                        update();
                    } catch (InterruptedException e) {
                        gravityDone = true;
                        update();
                    }
                });
                gravity.start();
            }
        }
    }

    public void setDone(boolean done){
        this.isDone = done;
        if(gravity != null) {
            gravity.interrupt();
        }
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(sqaureSize * 12 + colSize * 2, sqaureSize * 30);
    }

    public static class QueueGUI extends JPanel {
        private Queue<Tetramino> queue;

        public QueueGUI(Queue<Tetramino> queue) {
            this.queue = queue;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int queueNum = 0;
            for (Tetramino mino : queue) {
                g.setColor(mino.getType().color);

                int center = switch (mino.getType()) {
                    case I, O -> colSize / 2 - sqaureSize;
                    default -> colSize / 2 - sqaureSize / 2;
                };

                int[][] coords = mino.getType().upCoords;

                for (int i = 0; i < 4; i++) {
                    g.fillRect(coords[i][0] * sqaureSize + center, coords[i][1] * sqaureSize + center + queueNum * colSize, sqaureSize, sqaureSize);
                }
                queueNum++;
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(colSize, colSize * 5);
        }

        public void setQueue(Queue<Tetramino> queue) {
            this.queue = queue;
        }
    }

    public static class HoldGUI extends JPanel {

        private Tetramino mino = null;
        private int center = colSize / 2 - sqaureSize / 2;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (mino == null) {
                return;
            }
            g.setColor(mino.getType().color);

            center = switch (mino.getType()) {
                case I, O -> colSize / 2 - sqaureSize;
                default -> colSize / 2 - sqaureSize / 2;
            };

            int[][] coords = mino.getType().upCoords;

            for (int i = 0; i < 4; i++) {
                g.fillRect(coords[i][0] * sqaureSize + center, coords[i][1] * sqaureSize + center, sqaureSize, sqaureSize);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(colSize, colSize);
        }

        public void setMino(Tetramino mino) {
            this.mino = mino;
        }
    }

    public static class BoardGUI extends JPanel {

        private String[] board;
        private Queue<Integer> garbageQueue;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 12; j++) {
                    g.setColor(switch (board[i].charAt(j)) {
                        case '|' -> Color.BLACK;
                        case '-' -> Color.BLACK;
                        case 'I' -> Color.CYAN;
                        case 'O' -> Color.YELLOW;
                        case 'T' -> Color.MAGENTA;
                        case 'L' -> Color.ORANGE;
                        case 'J' -> Color.BLUE;
                        case 'S' -> Color.GREEN;
                        case 'Z' -> Color.RED;
                        case ' ' -> Color.WHITE;
                        case 'P' -> Color.GRAY;
                        case 'N' -> Color.LIGHT_GRAY;
                        case 'W' -> Color.RED;
                        case 'G' -> Color.DARK_GRAY;
                        default -> throw new IllegalStateException("Unexpected value: " + board[i]);
                    });
                    g.fillRect(j * sqaureSize, i * sqaureSize, sqaureSize, sqaureSize);
                    if (board[i].charAt(j) == ' ') {
                        g.setColor(Color.BLACK);
                        g.drawRect(j * sqaureSize, i * sqaureSize, sqaureSize, sqaureSize);
                    }
                    if(g.getColor() == Color.LIGHT_GRAY){
                        g.setColor(Color.RED);
                        g.drawLine(j*sqaureSize, i*sqaureSize, (j+1) * sqaureSize, (i+1) * sqaureSize);
                        g.drawLine((j+1)*sqaureSize, i*sqaureSize, j * sqaureSize, (i+1) * sqaureSize);
                    }
                }
            }
            int garbageBottom = sqaureSize * 23;
            for(int garbage : garbageQueue){
                if(garbageBottom - garbage * sqaureSize < 0){
                    break;
                }
                g.setColor(Color.BLACK);
                g.drawRect(0, garbageBottom - garbage * sqaureSize, sqaureSize, garbage * sqaureSize);
                garbageBottom -= garbage * sqaureSize;
            }

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(sqaureSize * 12, sqaureSize * 24);
        }

        public BoardGUI(String[] board, Queue<Integer> garbageQueue) {
            this.board = board;
            this.garbageQueue = garbageQueue;
        }

        public void setGarbageQueue(Queue<Integer> garbageQueue) {
            this.garbageQueue = garbageQueue;
        }

        public void setBoard(String[] board) {
            this.board = board;
        }
    }
}

