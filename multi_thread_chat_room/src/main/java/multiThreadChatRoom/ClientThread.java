package multiThreadChatRoom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Client thread.
 */
class ClientThread implements Runnable {

  private Socket clientSocket;
  /**
   * The Server in.
   */
  final DataInputStream serverIn;
  /**
   * The Server out.
   */
  final DataOutputStream serverOut;
  private ConcurrentHashMap<String, ClientThread> clientList;
  private int chatRmNum;
  private String clientName;

  /**
   * Instantiates a new Client thread.
   *
   * @param clientSocket the client socket
   * @param clientName   the client name
   * @param serverIn     the server in
   * @param serverOut    the server out
   */
  public ClientThread(Socket clientSocket, String clientName, DataInputStream serverIn, DataOutputStream serverOut) {
    this.clientSocket = clientSocket;
    this.clientName = clientName;
    this.serverIn = serverIn;
    this.serverOut = serverOut;
    this.clientList = Server.clientList;
  }

  @Override
  public void run() {
    String received;
    ArrayList<String> fromClient;
    String clientName;
    String identifier;
    String message = "";
    String systemString = "";

    while (true) {
      try {
        //System.out.println("thread is running");
        while ((received = serverIn.readUTF())!= null) {
          chatRmNum = Server.clientList.size();
          //System.out.println(received);
          fromClient = Server.processInput(received);
          identifier = fromClient.get(0);
          clientName = fromClient.get(2);

          switch (identifier) {
            // Connect request
            case "19":
              if (clientList.containsKey(clientName)) {
                message = clientName + " already connected to server.";
                serverOut.writeUTF("20 true" + message);
              } else {
                message = "User " + clientName + " doesn't connect to server.";
                serverOut.writeUTF("20 false" + message);
              }
              break;

            // Disconnect request
            case "21":
              if (clientList.containsKey(clientName)) {
                message = "You are no longer connected.";
                serverOut.writeUTF("28 true " + message);
                clientList.remove(clientName, clientList.get(clientName));
                systemString = clientName + " logoff";
                System.out.println(systemString);
                serverIn.close();
                serverOut.close();
                clientSocket.close();
                return;
              } else {
                message = "Disconnect can't happen on unconnected user " + clientName;
                serverOut.writeUTF("28 false " + message);
              }
              break;

            // Query user request
            case "22":
              if (clientList.containsKey(clientName)) {
                if (chatRmNum == 1) {
                  message = "No other clients are connected now.";
                  serverOut.writeUTF("23 " + message);
                  systemString = clientName + " ask who";
                  System.out.println(systemString);
                } else if (chatRmNum > 1) {
                  message = "";
                  for (String key : clientList.keySet()) {
                    if (!key.equals(clientName)) {
                      message = message + key + "  " ;
                    }
                  }
                  message = "Current connected users are: " + message;
                  serverOut.writeUTF("23 " + message);
                }
              } else {
                message =
                    "User " + clientName + " doesn't connect to server.";
                serverOut.writeUTF("20 false " + message);
              }
              break;

            // Broadcast msg request. input eg: 24 4 jack 14 message_to_all. output eg: 29 4 jack msg
            case "24":
              if (clientList.containsKey(clientName)) {
                message = "";
                for (int i = 4; i < fromClient.size(); i++) {
                  message = message + fromClient.get(i) + " ";
                }
                for (String key : clientList.keySet()) {
                  clientList.get(key).serverOut
                      .writeUTF("29 " + clientName.length() + " " + clientName + " " + message);
                }
                systemString = "Broadcast from " + clientName;
                System.out.println(systemString);
              } else {
                message = "User " + clientName + " doesn't connect to server.";
                serverOut.writeUTF("26 " + message);
              }
              break;

            // Direct msg request. input eg: 25 4 jack 4 rose 15 message_to_rose. output eg: 30 4 jack msg
            case "25":
              String receiptClient = fromClient.get(4);
              message = "";
              if (clientList.containsKey(receiptClient) && clientList.containsKey(clientName)) {
                for (int i = 6; i < fromClient.size(); i++) {
                  message = message + fromClient.get(i) + " ";
                }
                clientList.get(receiptClient).serverOut
                    .writeUTF("30 " + clientName.length() + " " + clientName + " " + message);
                systemString = clientName + " sending message to " + receiptClient;
                System.out.println(systemString);
              } else if (!clientList.containsKey(receiptClient)) {
                message = "Message can't be sent to unconnected user " + receiptClient;
                serverOut.writeUTF("26 " + message);
              } else if (!clientList.containsKey(clientName)) {
                message = "User " + clientName + " doesn't connect to server.";
                serverOut.writeUTF("26 " + message);
              }
              break;

            // Insult msg request. input eg: 27 4 jack 4 rose. output eg: 29 4 jack msg
            case "27":
              String insultReceiptClient = fromClient.get(4);
              if (clientList.containsKey(clientName)) {

                String insult = GenerateInsult.readGrammar("insult_grammar.json").get(0);

                for (String key : clientList.keySet()) {
                  clientList.get(key).serverOut
                      .writeUTF("29 " + clientName.length() + " " + clientName + " "
                          + "-->" + insultReceiptClient + ": " + insult);
                }
                systemString = clientName + " broadcasting Insult to " + insultReceiptClient;
                System.out.println(systemString);
              } else {
                message = "User " + clientName + " doesn't connect to server.";
                serverOut.writeUTF("26 " + message);
              }
              break;

            // Any other input cases, response fail msg
            default:
              message = "Input can not be correctly read. Please try again.";
              serverOut.writeUTF("26 " + message);
              systemString = "Wrong input from " + clientName;
              System.out.println(systemString);
          }

        }
      } catch (IOException ex) {
        System.out.println("Error in the server: " + ex.getMessage());
        ex.printStackTrace();
      }
    }
  }
}

