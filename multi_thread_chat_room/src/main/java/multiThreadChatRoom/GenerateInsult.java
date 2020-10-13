package multiThreadChatRoom;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GenerateInsult {

  public static List<String> readGrammar(String filePath) {
    JSONParser jsonParser = new JSONParser();
    ArrayList output = new ArrayList();

    try {
      FileReader reader = new FileReader(filePath);

      try {
        Object obj = jsonParser.parse(reader);
        JSONObject grammarInput = (JSONObject) obj;
        JSONArray grammarStart = (JSONArray) grammarInput.get("start");
        Iterator var7 = grammarStart.iterator();

        while (var7.hasNext()) {
          Object o = var7.next();
          String line = (String) o;

          while (line.contains("<")) {
            List<String> allMatches = new ArrayList();
            Matcher m = Pattern.compile("(<.+?>)").matcher(line);

            while (m.find()) {
              allMatches.add(m.group());
            }

            for (int j = 0; j < allMatches.size(); ++j) {
              JSONArray replacement = getIgnoreCase(grammarInput, ((String) allMatches.get(j))
                  .substring(1, ((String) allMatches.get(j)).length() - 1));
              if (replacement == null) {
                throw new IllegalArgumentException(
                    "The non-terminal " + (String) allMatches.get(j) + " is not defined.");
              }

              line = line.replace((CharSequence) allMatches.get(j),
                  (String) replacement.get((int) (Math.random() * (double) replacement.size())));
            }
          }

          output.add(line);
        }
      } catch (Throwable var15) {
        try {
          reader.close();
        } catch (Throwable var14) {
          var15.addSuppressed(var14);
        }

        throw var15;
      }

      reader.close();
    } catch (ParseException | IllegalArgumentException | IOException var16) {
      var16.printStackTrace();
    }

    return output;
  }

  public static JSONArray getIgnoreCase(JSONObject grammarInput, String key) {
    Iterator iter = grammarInput.keySet().iterator();

    String keyOutput;
    do {
      if (!iter.hasNext()) {
        return null;
      }

      keyOutput = (String) iter.next();
    } while (!keyOutput.equalsIgnoreCase(key));

    return (JSONArray) grammarInput.get(keyOutput);
  }
}