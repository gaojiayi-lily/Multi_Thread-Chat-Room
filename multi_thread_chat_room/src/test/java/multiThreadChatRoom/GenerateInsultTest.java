package multiThreadChatRoom;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenerateInsultTest {

  String filePathInsult = "insult_grammar.json";

  @Test
  public void readGrammar() {
    Assert.assertNotEquals(GenerateInsult.readGrammar(filePathInsult),
        GenerateInsult.readGrammar(filePathInsult));
    Assert.assertTrue(GenerateInsult.readGrammar(filePathInsult).size() == 1);
    Assert.assertFalse(GenerateInsult.readGrammar(filePathInsult).get(0).contains("<"));
  }

}