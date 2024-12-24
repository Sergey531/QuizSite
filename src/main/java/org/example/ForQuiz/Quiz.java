package org.example.ForQuiz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "quiz")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Игнорируем Hibernate прокси
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "quiz_type")
    private String quizType;

    @Column(name = "description")
    private String description;

    @OneToMany(
            mappedBy = "quiz",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<Question> questions = new HashSet<>();

    // Default constructor
    public Quiz() {}

    // Constructor with parameters
    public Quiz(String title, String quizType, String description, Set<Question> questions) {
        this.title = title;
        this.quizType = quizType;
        this.description = description;
        if (questions != null) {
            questions.forEach(this::addQuestion); // Используем addQuestion для установки связи
        }
    }

    // Utility method to add a question
    public void addQuestion(Question question) {
        if (question != null && !this.questions.contains(question)) {
            this.questions.add(question);
            question.setQuiz(this); // Устанавливаем связь с текущим квизом
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuizType() {
        return quizType;
    }

    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }
}
