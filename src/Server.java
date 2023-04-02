import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;  //comunicarea client server

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        System.out.println("Server started!\nWaiting for the clients...");
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();   // init endpoint serverSocketul which accepts an new client and assign a socket
                System.out.println("A new client has connected");
                ClientServerProcessor clientServerProcessor = new ClientServerProcessor(socket); //socket is sent to client serverprocess
                Thread thread = new Thread(clientServerProcessor); // create a new thread for which client
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e);
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Closing connection error: " + e);
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);  ///init server socket
        Server server = new Server(serverSocket); //new obj type server
        server.startServer();
    }
}