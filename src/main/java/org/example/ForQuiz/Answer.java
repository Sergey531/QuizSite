package org.example.ForQuiz;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "answers") // Исправлено имя таблицы для соответствия общему стилю (множественное число)
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("answerText")
    @Column(name = "answer_text", nullable = false) // Добавлен контроль nullable
    private String answerText;

    @JsonProperty("isCorrect")
    @Column(name = "is_correct", nullable = false) // Поле не должно быть null
    private boolean isCorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false) // Добавлен nullable = false
    @JsonBackReference // Исключает зацикливание при сериализации
    private Question question;

    // Конструкторы
    public Answer() {}

    public Answer(String answerText, boolean isCorrect, Question question) {
        this.answerText = answerText;
        this.isCorrect = isCorrect;
        this.question = question;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public String toString() {
        // Выводим только ID вопроса, чтобы избежать рекурсии
        return "Answer{" +
                "id=" + id +
                ", answerText='" + answerText + '\'' +
                ", isCorrect=" + isCorrect +
                ", questionId=" + (question != null ? question.getId() : "null") +
                '}';
    }
}
