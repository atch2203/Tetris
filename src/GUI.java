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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class GUI {
    Board board;
    BoardGUI boardGUI;
    HoldGUI holdGUI;
    QueueGUI queueGUI;
    JFrame frame;

    public static final int sqaureSize = 40;//size of single pixel
    public static final int colSize = sqaureSize * 5;//width of the queue + hold columns

    private static final int DAS = 150;

    public GUI(Board board) {
        this.board = board;
        this.boardGUI = new BoardGUI(board.getFullBoard());
        this.holdGUI = new HoldGUI();
        this.queueGUI = new QueueGUI(board.getQueue());
        frame = new JFrame("Tetris");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setLayout(new GridBagLayout());

        frame.getContentPane().add(holdGUI);
        frame.getContentPane().add(boardGUI);
        frame.getContentPane().add(queueGUI);

        KeyListener listener = new KeyListener() {//key listener for user input
            final Runnable rightDAS = () -> {
                try {
                    Thread.sleep(DAS);
                    while (board.moveR()) ;
                    updateDisplay();
                } catch (InterruptedException ignored) {
                }
            };

            final Runnable leftDAS = () -> {
                try {
                    Thread.sleep(DAS);
                    while (board.moveL()) ;
                    updateDisplay();
                } catch (InterruptedException ignored) {}
            };
            Thread DASThread;
            String dasSide = "left";

            final Set<Integer> pressed = new HashSet<>();

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public synchronized void keyPressed(KeyEvent e) {//CHeck all key inputs
                if (pressed.contains(e.getKeyCode())) {
                    return;
                }
                switch (Character.toLowerCase(e.getKeyChar())) {//checks all inputs
                    case ' ' -> board.hardDrop();
                    case 'x' -> board.clockwise();
                    case 'z' -> board.counterclockwise();
                    case 'a' -> board.rotate180();
                    case 'r' -> board.reset();
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT -> {
                        board.moveR();
                        dasSide = "right";
                        DASThread = new Thread(rightDAS);
                        DASThread.start();
                    }
                    case KeyEvent.VK_LEFT -> {
                        board.moveL();
                        dasSide = "left";
                        DASThread = new Thread(leftDAS);
                        DASThread.start();
                    }
                    case KeyEvent.VK_DOWN -> board.softDrop();
                    case KeyEvent.VK_SHIFT -> board.hold();
                }
                pressed.add(e.getKeyCode());
                updateDisplay();
            }

            @Override
            public synchronized void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT && dasSide.equals("right")) {
                    DASThread.interrupt();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT && dasSide.equals("left")) {
                    DASThread.interrupt();
                }
                pressed.remove(e.getKeyCode());
            }
        };
        frame.addKeyListener(listener);

        updateDisplay();

        frame.pack();
        frame.setVisible(true);

    }

    private void updateDisplay() {
        System.out.println("update");
        boardGUI.setBoard(board.getFullBoard());
        boardGUI.updateUI();
        holdGUI.setMino(board.getHold());
        holdGUI.updateUI();
        queueGUI.setQueue(board.getQueue());
        queueGUI.updateUI();
    }

    public static class QueueGUI extends JPanel {
        private Queue<Tetramino> queue;

        public QueueGUI(Queue<Tetramino> queue){
            this.queue = queue;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int queueNum = 0;
            for(Tetramino mino : queue){
                g.setColor(mino.getType().color);

                int center = switch (mino.getType()) {
                    case I, O -> colSize / 2 - sqaureSize;
                    default -> colSize / 2 - sqaureSize / 2;
                };

                int[][] coords = mino.getType().upCoords;

                for(int i = 0; i < 4; i++){
                    g.fillRect(coords[i][0] * sqaureSize + center, coords[i][1] * sqaureSize + center + queueNum * colSize, sqaureSize, sqaureSize);
                }
                queueNum++;
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(colSize, colSize *5);
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
            if(mino == null){
                return;
            }
            g.setColor(mino.getType().color);

            center = switch (mino.getType()) {
                case I, O -> colSize / 2 - sqaureSize;
                default -> colSize / 2 - sqaureSize / 2;
            };

            int[][] coords = mino.getType().upCoords;

            for(int i = 0; i < 4; i++){
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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for(int i = 0; i < 24; i++){
                for(int j = 0; j < 12; j++){
                    g.setColor(switch(board[i].charAt(j)){
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
                        default -> throw new IllegalStateException("Unexpected value: " + board[i].charAt(j));
                    });
                    g.fillRect(j*sqaureSize, i*sqaureSize, sqaureSize, sqaureSize);
                    if(board[i].charAt(j) == ' '){
                        g.setColor(Color.BLACK);
                        g.drawRect(j*sqaureSize, i*sqaureSize, sqaureSize, sqaureSize);
                    }
                }
            }

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(sqaureSize * 12, sqaureSize * 24);
        }

        public BoardGUI(String[] board){
            this.board = board;
        }

        public void setBoard(String[] board) {
            this.board = board;
        }
    }
}

