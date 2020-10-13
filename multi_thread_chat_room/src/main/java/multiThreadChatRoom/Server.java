package multiThreadChatRoom;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * The type Server.
 */
public class Server {

  /**
   * The Port.
   */
  static int port = 2222;
  /**
   * The Client list.
   */
  static ConcurrentHashMap<String, ClientThread> clientList = new ConcurrentHashMap<String, ClientThread>(
      10);

  /**
   * Process input array list.
   *
   * @param received the received
   * @return the array list
   */
  public static ArrayList<String> processInput(String received) {
    // break the string into parts according to the blank
    StringTokenizer in = new StringTokenizer(received, " ");
    ArrayList<String> fromClient = new ArrayList<String>();
    int t = in.countTokens();
    for (int i = 0; i < t; i++) {
      fromClient.add(in.nextToken());
    }
    return fromClient;
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws IOException the io exception
   */
  public static void main(String[] args) throws IOException {
    // server is listening on port 2222
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("Chat Server is listening on port " + port);
    Socket clientSocket = null;
    String received;
    String clientName;
    String msg;
    int chatRmNum;

    // running infinite loop for getting client request
    while (true) {
      try {
        // Accept the incoming request, create input and output streams
        clientSocket = serverSocket.accept();
        System.out.println("New client request received: " + clientSocket);
        DataInputStream serverIn = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream serverOut = new DataOutputStream(clientSocket.getOutputStream());

        // Waiting for client input, if no input, then waiting for 1 sec
        while ((received = serverIn.readUTF()) == null) {
          TimeUnit.SECONDS.sleep(1);
        }

        // Check: if ChatRoom is not full, create thread for the client
        // Otherwise, no thread created, close input/output streams, close socket
        ArrayList<String> fromClient = processInput(received);
        clientName = fromClient.get(2);
        chatRmNum = clientList.size();

        if (fromClient.get(0).equals("19")) {
          if (chatRmNum < 10 && !(clientList.containsKey(clientName))) {
            msg = "There are " + chatRmNum + " other connected clients.";
            serverOut.writeUTF("20 true " + msg);
            //serverOut.writeUTF("20 true");
            //ChatRoom isn't full, create a new ClientThread object for handling this request
            ClientThread clientThread = new ClientThread(clientSocket, clientName, serverIn,
                serverOut);
            Thread t = new Thread(clientThread);
            clientList.put(clientName, clientThread);
            t.start();
            System.out.println("New client logged in ChatRoom: " + clientName);


          } else if (chatRmNum >= 10 && !(clientList.containsKey(clientName))) {
            msg = "CharRoom is full. Please try again later.";
            serverOut.writeUTF("20 false " + msg);
            serverIn.close();
            serverOut.close();
            clientSocket.close();
            System.out.println("ChatRoom is full. No connection build with " + clientName);
          } else if (clientList.containsKey(clientName)) {
            msg = "ERROR: " + clientName + " already connected to server.";
            serverOut.writeUTF("20 true " + msg);
          }
        } else {
          msg = "26 ERROR: Wrong connection code. Please try again.";
          System.out.println("line 75");
          serverOut.writeUTF("26 " + msg);
        }
      } catch (IOException | InterruptedException ex) {
        System.out.println("Error in the server: " + ex.getMessage());
        ex.printStackTrace();
        clientSocket.close();
      }
    }
  }
}



