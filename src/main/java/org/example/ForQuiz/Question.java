package org.example.ForQuiz;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", nullable = false)
    @JsonProperty("questionText") // Указывает, что JSON-ключ "text" маппируется на это поле
    private String questionText;

    @ManyToOne(fetch = FetchType.EAGER) // Заменил LAZY на EAGER, чтобы сразу загружать квиз
    @JoinColumn(name = "quiz_id", nullable = false) // Указывает внешний ключ для связи с квизом
    @JsonBackReference // Избегает рекурсии при сериализации в JSON
    private Quiz quiz;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @JsonManagedReference // Позволяет сериализовать связанные ответы в JSON
    private Set<Answer> answers = new HashSet<>();

    @Transient
    private Long selectedAnswer; // Временное поле для хранения выбранного ответа (не сохраняется в БД)

    @Transient
    private Long correctAnswer; // Временное поле для хранения правильного ответа (не сохраняется в БД)

    // Конструкторы
    public Question() {}

    public Question(String questionText, Quiz quiz, Set<Answer> answers) {
        this.questionText = questionText;
        this.quiz = quiz;
        this.answers = (answers == null) ? new HashSet<>() : answers;
    }

    // Utility метод для добавления ответа
    public void addAnswer(Answer answer) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        this.answers.add(answer);
        answer.setQuestion(this); // Устанавливаем связь с текущим вопросом
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public Long getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(Long selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public Long getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Long correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    // Переопределение toString для удобной отладки
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", quizId=" + (quiz != null ? quiz.getId() : "null") +
                ", answers=" + answers.stream().map(Answer::getId).collect(Collectors.toList()) + // Выводим только ID ответов
                '}';
    }
}
