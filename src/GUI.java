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
                } catch (InterruptedException ignored) {
                    System.out.println("Interrupted");
                }
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
        text.setText("<html>" + board.getDisplay()
                .replaceAll("\n", "<br />")
                .replaceAll(" ", "&nbsp;") + "</html>");

        panel.updateUI();
    }
}


/*
try {
			// sign into the peer-to-peer network,
			// using the username "serverpeer", the password "serverpeerpassword",
			// and create/find a scoped peer-to-peer network named "TestNetwork"
			//System.out.println("Signing into the P2P network...");
			//P2PNetwork.signin("serverpeer", "serverpeerpassword", "TestNetwork");

			// start a server socket for the domain
			// "www.nike.laborpolicy" on port 100
			System.out.println("Creating server socket for " + "www.nike.laborpolicy:100...");
			ServerSocket server = new ServerSocket(100);

			// wait for a client
			System.out.println("Waiting for client...");
			Socket client = server.accept();
			System.out.println("Client Accepted.");

			// now communicate with this client
			DataInputStream in = new DataInputStream(client.getInputStream());
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			out.writeUTF("Hello client world!");
			String results = in.readUTF();
			System.out.println("Message from client: " + results);
			System.out.println(client.toString());

			// shut everything down!
			client.close();
			server.close();
		  }

		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
 */