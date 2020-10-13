package multiThreadChatRoom;

//import assignment4.ReadJSON;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KnockKnockProtocolTest {

  public KnockKnockProtocol test;

  private String CONNECT_RESPONSE = "20 true";
  private String DISCONNECT_MESSAGE = "4 jack logoff";
  private String QUERY_CONNECTED_USERS = "4 jack who";
  private String QUERY_USER_RESPONSE = "23 msg";
  private String BROADCAST_MESSAGE = "4 jack @all message_to_all";
  private String DIRECT_MESSAGE = "4 jack @rose message_to_rose";
  private String FAILED_MESSAGE = "26 msg";
  private String SEND_INSULT = "4 jack !!rose";
  private String DISCONNECT_RESPONSE = "28 true";
  private String BROADCAST_MESSAGE_RESPONSE = "29 4 jack msg";
  private String DIRECT_MESSAGE_RESPONSE = "30 4 jack msg";

  private String BROADCAST_NULL = "4 jack @all";
  private String DIRECT_NULL = "4 jack @rose";
  private String reminder = "the user does not send any message to you.";

  @Before
  public void setUp() throws Exception {
    test = new KnockKnockProtocol();
  }

  @Test
  public void processInput() {
    String output20 = test.processInput(CONNECT_RESPONSE);
    Assert.assertEquals(output20,
        "You are connected to the system successfully, please start to chat with others and have fun :)");

    String output21 = test.processInput(DISCONNECT_MESSAGE);
    Assert.assertEquals(output21, "21 4 jack");

    String output28 = test.processInput(DISCONNECT_RESPONSE);
    Assert.assertEquals(output28,
        "You are disconnected to the system successfully, see you next time :)");

    String output22 = test.processInput(QUERY_CONNECTED_USERS);
    Assert.assertEquals(output22, "22 4 jack");

    String output23 = test.processInput(QUERY_USER_RESPONSE);
    Assert.assertEquals(output23, "msg");

    String output24 = test.processInput(BROADCAST_MESSAGE);
    Assert.assertEquals(output24, "24 4 jack 14 message_to_all");

    String output29 = test.processInput(BROADCAST_MESSAGE_RESPONSE);
    Assert.assertEquals(output29, "jack to all: msg");

    String output25 = test.processInput(DIRECT_MESSAGE);
    Assert.assertEquals(output25, "25 4 jack 4 rose 15 message_to_rose");

    String output30 = test.processInput(DIRECT_MESSAGE_RESPONSE);
    Assert.assertEquals(output30, "jack to you in private: msg");

    String output26 = test.processInput(FAILED_MESSAGE);
    Assert.assertEquals(output26, "msg");

    String output27 = test.processInput(SEND_INSULT);
    Assert.assertEquals(output27, "27 4 jack 4 rose");

  }

  @Test
  public void processInputException() {
    String outputNull1 = test.processInput(BROADCAST_NULL);
    Assert.assertEquals(outputNull1,
        "24 4 jack " + reminder.length() + " the user does not send any message to you.");

    String outputNull2 = test.processInput(DIRECT_NULL);
    Assert.assertEquals(outputNull2,
        "25 4 jack 4 rose " + reminder.length() + " the user does not send any message to you.");
  }


  @Test
  public void matcher() {
    String input = "4 jack @rose message_to_rose";
    Pattern pattern = Pattern.compile("@[A-Za-z]*");
    Matcher matcher = pattern.matcher(input);
    String secondName = "";
    if (matcher.find()) {
      secondName = matcher.group(0).substring(1);
    }
    Assert.assertEquals(secondName, "rose");
  }

  @Test
  public void insultGenerator(){
    String insult = GenerateInsult.readGrammar("insult_grammar.json").get(0);
    System.out.println(insult);
  }

//  @Test
//  public void insultJar(){
//    String insult = ReadJSON.readGrammar("insult_grammar.json").get(0);
//    System.out.println(insult);
//  }
}