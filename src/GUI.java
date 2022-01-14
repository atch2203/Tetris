/*
Alex Tong
Vincent Xu

Tetris Project
2022-01-13

Class description:
Displays the board and all the graphical info
 */


import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GUI {
    Board board;
    JPanel panel;
    JFrame frame;
    JLabel text;

    private static final int DAS = 150;


    public GUI(Board board) {
        this.board = board;
        panel = new JPanel();
        frame = new JFrame("Tetris");
        text = new JLabel();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);

        panel.setPreferredSize(new Dimension(800, 800));//sets the window size

        text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));//sets the font
        panel.add(text);

        KeyListener listener = new KeyListener() {//key listener for user input
            final Runnable rightDAS = new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(DAS);
                        while(board.moveR());
                        updateDisplay();
                    }catch(InterruptedException ignored){}
                }
            };

            final Runnable leftDAS = new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(DAS);
                        while(board.moveL());
                        updateDisplay();
                    }catch(InterruptedException ignored){}
                }
            };
            Thread DASThread;

            final Set<Integer> pressed = new HashSet<>();

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public synchronized void keyPressed(KeyEvent e) {
                if(pressed.contains(e.getKeyCode())){
                    return;
                }
                switch(Character.toLowerCase(e.getKeyChar())){//checks all inputs
                    case ' ' -> board.hardDrop();
                    case 'x' -> board.clockwise();
                    case 'z' -> board.counterclockwise();
                    case 'a' -> board.rotate180();
                }

                switch(e.getKeyCode()){
                    case KeyEvent.VK_RIGHT -> {
                        board.moveR();
                        DASThread = new Thread(rightDAS);
                        DASThread.start();
                    }
                    case KeyEvent.VK_LEFT -> {
                        board.moveL();
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
                if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT){
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

    private void updateDisplay(){
        text.setText("<html>" + board.getDisplay()
                .replaceAll("\n", "<br />")
                .replaceAll(" ", "&nbsp;") + "</html>");

        panel.updateUI();
    }
}


/*private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add the ubiquitous "Hello World" label.
        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(label);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }*/
