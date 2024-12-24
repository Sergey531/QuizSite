package org.example.ForQuiz;

import jakarta.persistence.*;

@Entity
@Table(name = "user_answers")
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quizId;
    private Long questionId;
    private Long selectedAnswerId;

    // Конструкторы, геттеры и сеттеры

    public UserAnswer() {}

    public UserAnswer(Long quizId, Long questionId, Long selectedAnswerId) {
        this.quizId = quizId;
        this.questionId = questionId;
        this.selectedAnswerId = selectedAnswerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getSelectedAnswerId() {
        return selectedAnswerId;
    }

    public void setSelectedAnswerId(Long selectedAnswerId) {
        this.selectedAnswerId = selectedAnswerId;
    }

    // Геттеры и сеттеры...
}
