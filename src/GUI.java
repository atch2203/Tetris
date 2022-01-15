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

    public static final int sqaureSize = 40;//size of single pixel
    public static final int colSize = sqaureSize * 5;//width of the queue + hold columns

    public GUI(Board board) {
        this.board = board;
        this.boardGUI = new BoardGUI(board.getFullBoard());
        this.holdGUI = new HoldGUI();
        this.queueGUI = new QueueGUI(board.getQueue());

        this.setLayout(new GridBagLayout());

        this.add(holdGUI);
        this.add(boardGUI);
        this.add(queueGUI);
        updateDisplay();
    }

    public void updateDisplay() {
        boardGUI.setBoard(board.getFullBoard());
        boardGUI.updateUI();
        holdGUI.setMino(board.getHold());
        holdGUI.updateUI();
        queueGUI.setQueue(board.getQueue());
        queueGUI.updateUI();
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
                        default -> throw new IllegalStateException("Unexpected value: " + board[i].charAt(j));
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

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(sqaureSize * 12, sqaureSize * 24);
        }

        public BoardGUI(String[] board) {
            this.board = board;
        }

        public void setBoard(String[] board) {
            this.board = board;
        }
    }
}

