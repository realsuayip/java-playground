import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) throws IOException {
        String content = Files.readString(Paths.get("src/main/resources/beck.json"));
        JsonArray questionJsonArray = JsonParser.parseString(content).getAsJsonArray();
        List<Question> questions = new ArrayList<>();

        questionJsonArray.forEach(item -> {
            JsonArray values = item.getAsJsonArray();
            List<Option> options = new ArrayList<>();
            int sequence = 0;
            for (JsonElement value : values) {
                options.add(new Option(String.valueOf(sequence), value.getAsString()));
                sequence++;
            }
            questions.add(new Question("Choose one", options));
        });

        Quiz quiz = new Quiz(questions);
        quiz.start();

        int score = quiz.questions().stream().mapToInt(question -> {
            String answer = question.getAnswer().orElseThrow().name;
            return Integer.parseInt(answer);
        }).sum();

        LinkedHashMap<Integer, String> severityMap = new LinkedHashMap<>();
        severityMap.put(10, "These ups and downs are considered normal");
        severityMap.put(16, "Mild mood disturbance");
        severityMap.put(20, "Borderline clinical depression");
        severityMap.put(30, "Moderate depression");
        severityMap.put(40, "Severe depression");
        severityMap.put(63, "Extreme depression");

        for (Map.Entry<Integer, String> entry : severityMap.entrySet()) {
            if (score <= entry.getKey()) {
                System.out.printf("%s - %s%n", score, entry.getValue());
                System.exit(0);
            }
        }
    }
}
