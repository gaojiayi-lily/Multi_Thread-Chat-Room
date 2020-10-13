package multiThreadChatRoom;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * The type Client. The Client that can be run as a console
 */
public class Client {

  private static InetAddress ip;
  private String userName;
  private int portNumber;
  private static final int CONNECT_MESSAGE = 19;

  /**
   * Instantiates a new Client.
   *
   * @param ip         the ip
   * @param portNumber the port number
   * @param userName   the user name
   * @throws IOException the io exception
   */
  Client(InetAddress ip, int portNumber, String userName) throws IOException {
    this.ip = ip;
    this.portNumber = portNumber;
    this.userName = userName;
  }

  /**
   * Gets user name.
   *
   * @return the user name
   */
  public String getUserName() {
    return userName;
  }

  /**
   * The entry point of application. To start the Client in console mode use one of the following
   * command.
   * <p>
   * Java Client username; java Client username portNumber; java Client username portNumber
   * serverAddress at the console prompt.
   * <p>
   * If the portNumber is not specified 2222 is used, if the serverAddress is not specified
   * "localHost" is used, if the username is not specified "Anonymous" is used.
   *
   * @param args the input arguments
   * @throws UnknownHostException the unknown host exception
   * @throws IOException          the io exception
   * @throws InterruptedException the interrupted exception
   */
  public static void main(String[] args)
      throws UnknownHostException, IOException, InterruptedException {
    // default values if not entered
    int portNumber = 2222;
    String serverAddress = "localhost";
    String userName = "Anonymous";
    Scanner scan = new Scanner(System.in);
    String fromServer;

    InetAddress ip = InetAddress.getByName(serverAddress);
    Socket clientSocket = new Socket(ip, portNumber);
    DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
    DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

    System.out.println("Enter the username && optional [portNumber] [serverAddress]: ");
    userName = scan.nextLine();

    // different case according to the length of the arguments.
    switch (args.length) {
      case 3:
        // for > javac Client username portNumber serverAddr
        serverAddress = args[2];
      case 2:
        // for > javac Client username portNumber
        try {
          portNumber = Integer.parseInt(args[1]);
        } catch (Exception e) {
          System.out.println("Invalid port number.");
          System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
          return;
        }
      case 1:
        // for > javac Client username
        userName = args[0];
      case 0:
        // for > java Client
        break;
      // if number of arguments are invalid
      default:
        System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
        return;
    }

    // create the Client object
    Client client = new Client(ip, portNumber, userName);
    outputStream
        .writeUTF(
            Integer.toString(CONNECT_MESSAGE) + " " + Integer.toString(userName.length()) + " "
                + userName);

    while ((fromServer = inputStream.readUTF()) == null) {
      TimeUnit.SECONDS.sleep(1);
    }

    if (fromServer.contains("20 true")) {
      ArrayList<String> fromServerMsg = Server.processInput(fromServer);
      String msg1 = "";
      for (int i = 2; i < fromServerMsg.size(); i++) {
        msg1 = msg1 + fromServerMsg.get(i) + " ";
      }
      System.out.println("\nHello.! Welcome to the Multi-Thread Chat Room.");
      System.out.println(msg1);
      System.out.println("\nHere are the instructions:");
      System.out.println("Type 'logoff' if you want to disconnect from the server.");
      System.out.println("Type 'who' to see the list of active clients.");
      System.out.println("Type '@user_specified' to send message to a specified user.");
      System.out.println("Type '@all' to broadcast to all users connected.");
      System.out.println(
          "Type '!!user_specified' to send auto-generated insult message to a specified user.");

      // a thread that send messages to the server
      Thread sendMessage = new Thread(() -> {
        while (true) {
          String inputLine = client.getUserName().length() + " " + client.getUserName() +
              " " + scan.nextLine();
          KnockKnockProtocol kkp = new KnockKnockProtocol();
          String outputLine = kkp.processInput(inputLine);
          try {
            outputStream.writeUTF(outputLine);
            if (inputLine.equalsIgnoreCase("logoff")) {
              return;
            }
          } catch (IOException e) {
            System.out.println("Server has closed the connection: " + e);
          }
        }
      });

      // a thread that waits for the message from the server
      Thread readMessage = new Thread(() -> {
        while (true) {
          try {
            // read the message form the input data stream
            String msg = inputStream.readUTF();
            KnockKnockProtocol kkp = new KnockKnockProtocol();
            String msgOutput = kkp.processInput(msg);
            System.out.println(msgOutput);
            if (msg.contains("28 true")) {
              break;
            }
          } catch (IOException e) {
            System.out.println("Server has closed the connection: " + e);
          }
        }
      });

      // creates the Thread to read && write from the server
      readMessage.start();
      sendMessage.start();

    } else {
      System.out.println("The connection is not successful, please try again later :)");
    }
  }


}
