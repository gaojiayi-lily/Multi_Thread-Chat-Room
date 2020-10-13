package multiThreadChatRoom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Knock knock protocol.
 */
public class KnockKnockProtocol {

  private static final int CONNECT_RESPONSE = 20;
  private static final int DISCONNECT_MESSAGE = 21;
  private static final int QUERY_CONNECTED_USERS = 22;
  private static final int QUERY_USER_RESPONSE = 23;
  private static final int BROADCAST_MESSAGE = 24;
  private static final int DIRECT_MESSAGE = 25;
  private static final int FAILED_MESSAGE = 26;
  private static final int SEND_INSULT = 27;
  private static final int DISCONNECT_RESPONSE = 28;
  private static final int BROADCAST_MESSAGE_RESPONSE = 29;
  private static final int DIRECT_MESSAGE_RESPONSE = 30;

  /**
   * Process input string.
   *
   * @param input the input
   * @return the string
   */
  public String processInput(final String input) {
    String output = "";

    //connect response (sample input: 20 true)
    if (input.substring(0, 2).equals(Integer.toString(CONNECT_RESPONSE))) {
      if (input.contains("true")) {
        output = "You are connected to the system successfully, please start to chat with others and have fun :)";
      } else if (input.contains("false")) {
        output = "Unfortunately, the connection fails, please retry later. ";
      }
    }

    //disconnected message (sample input: 4 jack logoff, output: 21 4 jack)
    if (input.toLowerCase().contains("logoff")) {
      int endInt = Integer.parseInt(input.substring(0, 1));
      output = Integer.toString(DISCONNECT_MESSAGE) + " " + input.substring(0, endInt + 2);
    }

    //disconnected response (sample input: 28 true)
    if (input.substring(0, 2).equals(Integer.toString(DISCONNECT_RESPONSE))) {
      if (input.contains("true")) {
        output = "You are disconnected to the system successfully, see you next time :)";
      } else if (input.contains("false")) {
        output = "Unfortunately, the disconnection fails, please retry later. ";
      }
    }

    //query connected users (sample input: 4 jack who, output: 22 4 jack)
    if (input.toLowerCase().contains("who")) {
      int endInt = Integer.parseInt(input.substring(0, 1));
      output = Integer.toString(QUERY_CONNECTED_USERS) + " " + input.substring(0, endInt + 2);
    }

    //query user response (sample input: 23 msg)
    if (input.substring(0, 2).equals(Integer.toString(QUERY_USER_RESPONSE))) {
      output = input.substring(3);
    }

    //broadcast message (sample input: 4 jack @all message_to_all)
    //(sample output 24 4 jack 14 message_to_all)
    if (input.toLowerCase().contains("@all")) {
      int nameSize = Integer.parseInt(input.substring(0, 1));
      if (input.length() > 7 + nameSize) {
        String msgBro = input.substring(8 + nameSize);
        int msgSize = msgBro.length();
        output = Integer.toString(BROADCAST_MESSAGE) + " " + input.substring(0, nameSize + 2) + " "
            + Integer.toString(msgSize) + " " + msgBro;
      } else {
        String reminder = "the user does not send any message to you.";
        output = Integer.toString(BROADCAST_MESSAGE) + " " + input.substring(0, nameSize + 2)
            + " " + Integer.toString(reminder.length()) + " " + reminder;
      }
    }

    //broadcast message response (sample input: 29 4 jack msg)
    //(sample output: jack to all: msg)
    if (input.substring(0, 2).equals(Integer.toString(BROADCAST_MESSAGE_RESPONSE))) {
      int firstNameSize = Integer.parseInt(input.substring(3, 4));
      output =
          input.substring(5, firstNameSize + 5) + " to all: " + input.substring(firstNameSize + 6);
    }

    //direct message (sample input: 4 jack @rose message_to_rose)
    //(sample output = 25 4 jack 4 rose 15 message_to_rose)
    if (!input.toLowerCase().contains("@all") && input.contains("@")) {
      int firstNameSize = Integer.parseInt(input.substring(0, 1));
      Pattern pattern = Pattern.compile("@[A-Za-z]*");
      Matcher matcher = pattern.matcher(input);
      String secondName = "";
      if (matcher.find()) {
        secondName = matcher.group(0).substring(1);
      }
      if (input.length() > 4 + firstNameSize + secondName.length()) {
        String msgDir = input.substring(5 + firstNameSize + secondName.length());
        output =
            Integer.toString(DIRECT_MESSAGE) + " " + input.substring(0, firstNameSize + 2) + " "
                + Integer.toString(secondName.length()) + " " + secondName + " " + msgDir.length()
                + " "
                + msgDir;
      } else {
        String reminderP = "the user does not send any message to you.";
        output =
            Integer.toString(DIRECT_MESSAGE) + " " + input.substring(0, firstNameSize + 2) + " "
                + Integer.toString(secondName.length()) + " " + secondName + " " + Integer
                .toString(reminderP.length()) + " " + reminderP;
      }
    }

    //direct message response (sample input: 30 4 jack msg)
    //(sample output: jack to you in private: msg)
    if (input.substring(0, 2).equals(Integer.toString(DIRECT_MESSAGE_RESPONSE))) {
      int firstNameSize = Integer.parseInt(input.substring(3, 4));
      output = input.substring(5, firstNameSize + 5) + " to you in private: " + input
          .substring(firstNameSize + 6);
    }

    //failed message (sample input: 26 msg)
    if (input.substring(0, 2).equals(Integer.toString(FAILED_MESSAGE))) {
      output = input.substring(3);
    }

    //send insult (sample input: 4 jack !!rose, output: 27 4 jack 4 rose)
    if (input.contains("!!")) {
      Pattern pattern = Pattern.compile("!![A-Za-z]*");
      Matcher matcher = pattern.matcher(input);
      String secondName = "";
      if (matcher.find()) {
        secondName = matcher.group(0).substring(2);
      }
      int firstNameSize = Integer.parseInt(input.substring(0, 1));

      output = Integer.toString(SEND_INSULT) + " " + input.substring(0, firstNameSize + 2) + " "
          + Integer.toString(secondName.length()) + " " + secondName;
    }

    return output;
  }
}