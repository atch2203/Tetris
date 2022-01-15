import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient extends MainGameMultiPlayer {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }catch(IOException e){
            System.out.println("broke");
        }
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        }catch(IOException e){
            System.out.println("broke 3");
        }
    }

    public static void main(String[] args) {
        GameClient client = new GameClient();
        client.startConnection("127.0.0.1", 6666);
        client.setUpGame(true);
        client.runGame();
    }
}
