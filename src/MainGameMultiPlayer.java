import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.*;

public class MainGameMultiPlayer {
    private GUI user; // board for user
    private GUI other; // board for other player
    JFrame window;
    private final int DAS = 150;
    
    protected final int port = 100;
    int seed = 0;
    private boolean isFirst = true;
    private boolean isPlaying = false;

    KeyListener startControls = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyChar() == 'r'){
                reset(true);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };

    KeyListener gameControls = new KeyListener() {//key listener for game controls
        private final Runnable rightDAS = () -> {
            try {
                Thread.sleep(DAS);
                dasing = true;
                while (!isPlaying);
                while (user.board.moveR());
                updateDisplay();
                Thread.sleep(1000000);
            } catch (InterruptedException ignored) {
                dasing = false;
            }
        };
        private final Runnable leftDAS = () -> {
            try {
                Thread.sleep(DAS);
                dasing = true;
                while (!isPlaying);
                while (user.board.moveL());
                updateDisplay();
                Thread.sleep(1000000);
            } catch (InterruptedException ignored) {
                dasing = false;
            }
        };

        private boolean dasing = false;
        private Thread DASThread = new Thread(() -> {});
        private String dasSide = "left";

        public final Set<Integer> pressed = new HashSet<>();

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public synchronized void keyPressed(KeyEvent e) {//CHeck all key inputs
            if (pressed.contains(e.getKeyCode())) {
                return;
            }

            pressed.add(e.getKeyCode());
            switch (e.getKeyCode()){
                case KeyEvent.VK_RIGHT -> {
                    DASThread.interrupt();
                    if(isPlaying) {
                        user.board.moveR();
                    }
                    dasSide = "right";
                    DASThread = new Thread(rightDAS);
                    DASThread.start();
                }
                case KeyEvent.VK_LEFT -> {
                    DASThread.interrupt();
                    if (isPlaying) {
                        user.board.moveL();
                    }
                    dasSide = "left";
                    DASThread = new Thread(leftDAS);
                    DASThread.start();
                }
            }

            if(isPlaying) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> user.board.hardDrop();
                    case KeyEvent.VK_X -> user.board.clockwise();
                    case KeyEvent.VK_Z -> user.board.counterclockwise();
                    case KeyEvent.VK_A -> user.board.rotate180();
                    case KeyEvent.VK_D -> user.board.displayGame();
                    case KeyEvent.VK_DOWN -> user.board.softDrop();
                    case KeyEvent.VK_SHIFT -> user.board.hold();
                }
                if(pressed.contains(KeyEvent.VK_DOWN)){
                    user.board.softDrop();
                }
                moveRotate();
                user.keyPressed();
                user.update();
                updateDisplay();
            }
        }

        @Override
        public synchronized void keyReleased(KeyEvent e) {
            if(isPlaying) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT && dasSide.equals("right")) {
                    dasing = false;
                    DASThread.interrupt();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT && dasSide.equals("left")) {
                    dasing = false;
                    DASThread.interrupt();
                }
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

    protected void reset(boolean initiator){
        window.removeKeyListener(startControls);
        if(isFirst) {
            window.addKeyListener(gameControls);
        }
        if(initiator){
            out.println("reset");
            if(!isFirst) {
                seed = new Random().nextInt();
                out.println("seed");
                out.println(seed);
            }
        }else{
            if(!isFirst) {
                if (!readLine().equals("seed")) {
                    throw new IllegalStateException("Game setup failed");
                }
                seed = Integer.parseInt(readLine());
            }
        }

        isFirst = false;
        user.board.reset(seed);
        user.setDone(false);
        new Thread(() -> {
            try{
                for(int i = 3; i > 0; i--){
                    user.board.setFlavorText(Integer.toString(i));
                    updateDisplay();
                    Thread.sleep(1000);
                }
                user.board.setFlavorText("");
                updateDisplay();
                user.update();
                isPlaying = true;
            }catch(InterruptedException e){

            }
        }).start();

    }
    
    protected void setUpGame(boolean initiator){
        user = new GUI(new Board(seed), new Thread(() ->{
            while(true) {
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException e) {
                    updateDisplay();
                }
            }
        }));
        other = new GUI(new Board(seed));
        window = new JFrame();
        window.setLayout(new GridLayout(1, 2));
        window.add(user);
        window.add(other);
        user.board.setFlavorText("press r to start game");
        updateDisplay();


        window.pack();
        window.setVisible(true);

        window.addKeyListener(startControls);
    }
    
    protected void processInput(){
        while(true) {
            input = readLine();
            switch (input) {
                case "board" -> {
                    String[] board = new String[24];
                    for(int i = 0; i < 24; i++){
                        board[i] = readLine();
//                        System.out.println(board[i]);
                    }
                    other.boardGUI.setBoard(board);
                    if(!readLine().equals("end board")){
                        throw new IllegalStateException("board not ended");
                    }
                }
                case "queue" -> {
                    Queue<Tetramino> queue = new LinkedList<>();
                    for(int i = 0; i < 5; i++){
                        queue.add(new Tetramino(Tetramino.Type.valueOf(readLine())));
                    }
                    other.queueGUI.setQueue(queue);
                    if(!readLine().equals("end queue")){
                        throw new IllegalStateException("queue not ended");
                    }
                }
                case "hold" -> {
                    other.holdGUI.setMino(new Tetramino(Tetramino.Type.valueOf(readLine())));
                }
                case "text" -> {
                    String read = readLine();
                    StringBuilder total = new StringBuilder();
                    while(!read.equals("end text")){
                        total.append(read).append("\n");
                        read = readLine();
                    }
                    other.setFlavorText(total.toString());
                }
                case "garbage" -> {
                    int garbage = Integer.parseInt(readLine());
                    user.board.getGarbage().add(garbage);
                }
                case "my garbage" -> {
                    String read = readLine();
                    Queue<Integer> garbage = new LinkedList<>();
                    while(!read.equals("end garbage")){
                        garbage.add(Integer.parseInt(read));
                        read = readLine();
                    }
                    other.boardGUI.setGarbageQueue(garbage);
                }
                case "lost" -> {
                    user.board.setSideText("");
                    user.board.setFlavorText("1st\n\npress r to replay");
                    updateDisplay();
                    isPlaying = false;
                    window.addKeyListener(startControls);
                    user.setDone(true);
                }
                case "reset" -> {
                    reset(false);
                }
            }
            other.updateDisplayMultiPlayer();
        }
    }

    private synchronized void outputData(){
        out.println("board");
        String[] userBoard = user.board.getFullBoard();
        for(int i = 0; i < 24; i++){
            out.println(userBoard[i]);
        }
        out.println("end board");
        out.println("queue");
        Queue<Tetramino> queue = user.board.getQueue();
        for(Tetramino t : queue){
            out.println(t.getType());
        }
        out.println("end queue");
        if(user.board.getHold() != null) {
            out.println("hold");
            out.println(user.board.getHold().getType());
        }
        out.println("my garbage");
        for(int g : user.board.getGarbage()){
            out.println(g);
        }
        out.println("end garbage");
        out.println("text");
        out.println(user.board.getSideText());
        out.println("end text");
        int attack = user.board.getAttack();
        if(attack != 0){
            out.println("garbage");
            out.println(attack);
        }
        if(user.board.hasJustLost()){
            user.board.setSideText("");
            user.board.setFlavorText("2nd\n\npress r to replay");
            updateDisplay();
            isPlaying = false;
            window.addKeyListener(startControls);
            user.setDone(true);
            out.println("lost");
        }
    }

    private void updateDisplay(){
        user.updateDisplay();
        outputData();
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
