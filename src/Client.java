import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket; //connection
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // init obj
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));// la server si scrierea la servver
            this.username = username;
        } catch (IOException e) {
            System.out.println("Connection error: " + e);
            closeConnection();
        }
    }

    public void sendMessage() {  //1st
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);  //new obj for reading from input
            while (socket.isConnected()) {
                String msgToSend = scanner.nextLine();
                bufferedWriter.write("<" + username + "> " + msgToSend);  //formatted msg
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            System.out.println("Error sending message: " + e);
            closeConnection();
        }
    }

    public void listenForMsg() { //2nd
        new Thread(() -> {
            String msgFromGroupChat;
            while (socket.isConnected()) {
                try {
                    msgFromGroupChat = bufferedReader.readLine();  //read what got from de server
                    System.out.println(msgFromGroupChat);
                } catch (IOException e) {
                    System.out.println("Server disconnected " + e);
                    closeConnection();
                    break;
                }
            }
        }).start();
    }

    public void closeConnection() {
        try {
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Closing connection error: " + e);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in); //reads from input
        System.out.println("Please enter your username: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 12345); //obj
        Client client = new Client(socket, username);  //new client
        client.listenForMsg();  //execute in parallel
        client.sendMessage();
    }
}