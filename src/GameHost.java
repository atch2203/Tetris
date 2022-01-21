import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class GameHost extends MainGameMultiPlayer{
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getLocalHost());
            System.out.println(InetAddress.getLocalHost());
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }catch(IOException ignored){
            System.out.println("broke");
        }
    }

    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        }catch(IOException ignored){
            System.out.println("broke 2");
        }
    }

    public static void main(String[] args) {
        GameHost server = new GameHost();
        System.out.print("Port: ");
        Scanner s = new Scanner(System.in);
        int port = s.nextInt();
        server.start(port);
        server.setUpGame(false);
        server.processInput();
    }


}
