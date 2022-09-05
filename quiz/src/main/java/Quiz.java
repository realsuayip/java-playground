import java.util.List;

public record Quiz(List<Question> questions) {
    public Quiz {
        for (Question question : questions) {
            question.setQuiz(this);
        }
    }

    public void start() {
        Question firstQuestion = this.questions.get(0);
        firstQuestion.ask();
    }
}
