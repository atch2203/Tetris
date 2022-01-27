/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
THe user will run the main method in this, which will call all the other classes.
 */


import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainGameSinglePlayer {
    private static final int DAS = 150;
    static GUI game;
    static JFrame window;

    static KeyListener controls = new KeyListener() {//key listener for user input
        private final Runnable rightDAS = () -> {
            try {
                Thread.sleep(DAS);
                dasing = true;
                while (game.board.moveR());
                game.updateDisplay();
                Thread.sleep(1000000);
            } catch (InterruptedException ignored) {
                dasing = false;
            }
        };
        private final Runnable leftDAS = () -> {
            try {
                Thread.sleep(DAS);
                dasing = true;
                while (game.board.moveL());
                game.updateDisplay();
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
            switch (e.getKeyCode()) {
                case Config.hardDrop -> game.board.hardDrop();
                case Config.cw -> game.board.clockwise();
                case Config.ccw -> game.board.counterclockwise();
                case Config.flip -> game.board.rotate180();
                case Config.reset -> game.board.reset(new Random().nextInt());
                case Config.right -> {
                    DASThread.interrupt();
                    game.board.moveR();
                    dasSide = "right";
                    DASThread = new Thread(rightDAS);
                    DASThread.start();
                }
                case Config.left -> {
                    DASThread.interrupt();
                    game.board.moveL();
                    dasSide = "left";
                    DASThread = new Thread(leftDAS);
                    DASThread.start();
                }
                case Config.softDrop -> game.board.softDrop();
                case Config.hold -> game.board.hold();
            }
            if(pressed.contains(Config.softDrop)){
                game.board.softDrop();
            }
            moveRotate();


            game.keyPressed();
            game.update();
            game.updateDisplay();
        }

        @Override
        public synchronized void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == Config.right && dasSide.equals("right")) {
                dasing = false;
                DASThread.interrupt();
            } else if (e.getKeyCode() == Config.left && dasSide.equals("left")) {
                dasing = false;
                DASThread.interrupt();
            }
            pressed.remove(e.getKeyCode());
        }

        private void moveRotate(){//checks whether to move after a rotate (if still dasing)
            if(dasing){
                if(dasSide.equals("right")){
                    while(game.board.moveR());
                }else{
                    while(game.board.moveL());
                }
            }
        }
    };

    public static void main(String[] args) {
        window = new JFrame();
        game = new GUI(new Board(new Random().nextInt(), true, true));
        game.board.reset(new Random().nextInt());
        game.update();
        window.add(game);
        window.setSize(game.getPreferredSize());

        window.addKeyListener(controls);
        window.pack();
        window.setVisible(true);
    }


}
