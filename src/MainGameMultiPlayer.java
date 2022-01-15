import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class MainGameMultiPlayer {
    private GUI user; // board for user
    private GUI other; // board for other player
    JFrame window;
    private final int DAS = 150;
    
    int port = 0;
    int seed;

    KeyListener controls = new KeyListener() {//key listener for user input
        final Runnable rightDAS = () -> {
            try {
                Thread.sleep(DAS);
                dasing = true;
                while (user.board.moveR()) ;
                user.updateDisplay();
            } catch (InterruptedException ignored) {
            }
        };
        final Runnable leftDAS = () -> {
            try {
                Thread.sleep(DAS);
                dasing = true;
                while (user.board.moveL()) ;
                user.updateDisplay();
            } catch (InterruptedException ignored) {
            }
        };
        private boolean dasing = false;
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

            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE -> user.board.hardDrop();
                case KeyEvent.VK_X -> user.board.clockwise();
                case KeyEvent.VK_Z -> user.board.counterclockwise();
                case KeyEvent.VK_A -> user.board.rotate180();
                case KeyEvent.VK_R -> user.board.reset();
                case KeyEvent.VK_RIGHT -> {
                    user.board.moveR();
                    dasSide = "right";
                    DASThread = new Thread(rightDAS);
                    DASThread.start();
                }
                case KeyEvent.VK_LEFT -> {
                    user.board.moveL();
                    dasSide = "left";
                    DASThread = new Thread(leftDAS);
                    DASThread.start();
                }
                case KeyEvent.VK_DOWN -> user.board.softDrop();
                case KeyEvent.VK_SHIFT -> user.board.hold();
            }
            moveRotate();

            pressed.add(e.getKeyCode());
            user.updateDisplay();


        }

        @Override
        public synchronized void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT && dasSide.equals("right")) {
                dasing = false;
                DASThread.interrupt();
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && dasSide.equals("left")) {
                dasing = false;
                DASThread.interrupt();
            }
            pressed.remove(e.getKeyCode());
        }

        private void moveRotate(){//checks whether to move after a rotate (if still dasing)
            if(dasing){
                if(dasSide.equals("right")){
                    while(user.board.moveR());
                }else{
                    while(user.board.moveL());
                }
            }
        }
    };


    protected PrintWriter out;
    protected BufferedReader in;
    String input;
    
    protected void setUpGame(boolean initiator){
        if(initiator){
            seed = new Random().nextInt();
            out.println("seed");
            out.println(seed);
        }else{
            if(!readLine().equals("seed")){
                throw new IllegalStateException("Game setup failed");
            }
            seed = Integer.parseInt(readLine());
        }
        
        user = new GUI(new Board(seed));
        other = new GUI(new Board(seed));
        window = new JFrame();
        window.add(user);
        window.add(other);

        window.addKeyListener(controls);
        window.pack();
        window.setVisible(true);

    }
    
    protected void runGame(){
        while(true) {
            input = readLine();
            switch (input) {
                case "board" -> {
                    String[] board = new String[24];
                    for(int i = 0; i < 24; i++){
                        board[i] = readLine();
                    }
                    other.board.setBoard(board);
                }
                case "queue" -> {
                    Queue<Tetramino> queue = new LinkedList<>();
                    for(int i = 0; i < 5; i++){
                        queue.add(new Tetramino(Tetramino.Type.valueOf(readLine())));
                    }
                    other.board.setQueue(queue);
                }
                case "hold" -> {
                    other.board.setHold(new Tetramino(Tetramino.Type.valueOf(readLine())));
                }
            }
            other.updateDisplay();
        }
    }

    private void outputData(){
        out.println("board");
        String[] userBoard = user.board.getFullBoard();
        for(int i = 0; i < 24; i++){
            out.println(userBoard[i]);
        }
        out.println("queue");
        Queue<Tetramino> queue = user.board.getQueue();
        for(int i = 0; i < 5; i++){
            out.println();
        }
    }
    
    private String readLine(){
        try{
            return in.readLine();
        }catch(IOException e){
            System.out.println("IO exception");
            System.exit(-2);
        }
        return null;
    }
}
