import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.*;
import java.io.*;

public class Parser {

    private File file;
    private BufferedReader reader;
    private JSONObject mainObject;

    Parser(File file) {
        this.file = file;

        try (FileReader fileReader = new FileReader(file)) {

            reader = new BufferedReader(fileReader);
            JSONParser parser = new JSONParser();
            mainObject = (JSONObject) parser.parse(reader);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    File extractBackground() {
        return new File(mainObject.get("background").toString());
    }

    List<Dialog> extractDialog() {
        List<Dialog> dialogs = new ArrayList<>();
        JSONObject dialogObject = (JSONObject) mainObject.get("dialog");
        JSONArray text = (JSONArray) dialogObject.get("text");
        JSONArray choices = (JSONArray) dialogObject.get("choice");

        // using integers instead of constants or enum because these
        // seem to add more complexity and verbosity
        // 0 = CHARACTER | 1 = SPRITE | 2 = CHOICE | 3 = TEXT

        JSONArray innerArray;

        for (int i = 0; i < text.toArray().length - 1; i++) {
            innerArray = (JSONArray) text.get(i);

            dialogs.add(new Dialog((String) innerArray.get(0),
                    (String) innerArray.get(1),
                    (String) innerArray.get(3)
                    ));
        }

        innerArray = (JSONArray) text.get(text.toArray().length - 1);

        try {
            dialogs.add(new ChoiceDialog((String) innerArray.get(0),
                    (String) innerArray.get(1),
                    (String) innerArray.get(3),
                    new Choice((HashMap) choices.get(0)),
                    new Choice((HashMap) choices.get(1)
                    )));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialogs;
    }
}
