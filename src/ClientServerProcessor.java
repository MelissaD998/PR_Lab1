import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientServerProcessor implements Runnable {

    public static ArrayList<ClientServerProcessor> clientServerProcessors = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientServerProcessor(Socket socket) {
        try {
            this.socket = socket; //get a new instance that read data from clients socket
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientServerProcessors.add(this);
            messageProcessing("SERVER: " + clientUsername + " has entered the chat!");
        } catch (IOException e) {
            closeConnection();
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();  // reads msg from client
                messageProcessing(messageFromClient);  //process msg to send to other clients
            } catch (IOException e) {
                System.out.println("Force closed connection");
                closeConnection();
                break;
            }
        }
    }

    public void messageProcessing(String messageToSend) { //process a msg to send to others
        for (ClientServerProcessor clientServerProcessor : clientServerProcessors) { //Iterates through the list of all connected clients
            try {
                if (!clientServerProcessor.clientUsername.equals(clientUsername)) { //
                    clientServerProcessor.bufferedWriter.write(messageToSend);
                    clientServerProcessor.bufferedWriter.newLine();
                    clientServerProcessor.bufferedWriter.flush();
                }
            } catch (IOException e) {
                System.out.println("Close error: " + e);
                closeConnection();
            }
        }
    }

    public void removeClientHandler() {
        clientServerProcessors.remove(this);
        messageProcessing("SERVER: " + clientUsername + " has left the chat :(");
    }

    public void closeConnection() {
        removeClientHandler();
        try {
            bufferedReader.close();
            bufferedWriter.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Closing connection error: " + e);
        }
    }
}