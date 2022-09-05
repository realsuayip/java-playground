import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Question extends Scheme {
    final Consumer<String> out = System.out::print;
    final Supplier<String> in = Question::scanNextLine;

    private String answer;
    private Quiz quiz;

    Question(String prompt, List<Option> options) {
        this.prompt = prompt;
        this.options = options;
    }

    private static String scanNextLine() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private String getPrompt() {
        return String.format("%s: \n", this.prompt);
    }

    private Optional<Option> findOptionByName(String name) {
        return this.options.stream().filter(option -> option.name.equals(name)).findFirst();
    }

    public Optional<Option> getAnswer() {
        return this.findOptionByName(this.answer);
    }

    public Optional<Quiz> getQuiz() {
        if (this.quiz == null) {
            return Optional.empty();
        }
        return Optional.of(this.quiz);
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    private Optional<Question> getNextQuestion() {
        Optional<?> quiz = this.getQuiz();
        if (quiz.isPresent()) {
            Quiz realQuiz = (Quiz) quiz.get();
            int currentIndex = realQuiz.questions().indexOf(this);
            try {
                return Optional.of(realQuiz.questions().get(currentIndex + 1));
            } catch (IndexOutOfBoundsException exception) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public void ask() {
        this.out.accept(this.getPrompt());
        for (Option option : this.options) {
            String formattedOption = String.format("%s) %s\n", option.name, option.value);
            this.out.accept(formattedOption);
        }
        this.out.accept("Your choice: ");
        this.answer = this.in.get();
        Optional<Option> answer = this.getAnswer();

        if (answer.isEmpty()) {
            this.out.accept("You did not select a valid option, try again.\n");
            this.ask();
        } else {
            this.getNextQuestion().ifPresent(Question::ask);
        }
    }
}
