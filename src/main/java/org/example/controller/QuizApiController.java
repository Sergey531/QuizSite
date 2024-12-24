package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.ForQuiz.Answer;
import org.example.ForQuiz.Question;
import org.example.ForQuiz.Quiz;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Разрешаем запросы с фронтенда
public class QuizApiController {

    @PersistenceContext
    private EntityManager entityManager;

    // Получаем все квизы
    @GetMapping("/api/quizzes")
    @Transactional
    public ResponseEntity<?> getQuizzes() {
        try {
            // Используем JPQL запрос для получения квизов с вопросами и ответами
            List<Quiz> quizzes = entityManager.createQuery(
                            "SELECT DISTINCT q FROM Quiz q " +
                                    "LEFT JOIN FETCH q.questions questions " +
                                    "LEFT JOIN FETCH questions.answers " +
                                    "ORDER BY q.id ASC", Quiz.class)
                    .getResultList();

            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred while fetching quizzes");
        }
    }

    // Получаем квиз по ID
    @GetMapping("/api/quizzes/{quizId}")
    @Transactional
    public ResponseEntity<?> getQuizById(@PathVariable Long quizId) {
        try {
            Quiz quiz = entityManager.createQuery(
                            "SELECT q FROM Quiz q " +
                                    "LEFT JOIN FETCH q.questions questions " +
                                    "LEFT JOIN FETCH questions.answers " +
                                    "WHERE q.id = :quizId", Quiz.class)
                    .setParameter("quizId", quizId)
                    .getSingleResult();

            for (Question question : quiz.getQuestions()) {
                question.setCorrectAnswer(
                        question.getAnswers().stream()
                                .filter(Answer::isCorrect)
                                .findFirst()
                                .map(Answer::getId)
                                .orElse(null)
                );
            }

            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error loading quiz");
        }
    }

    // Метод для добавления нового квиза
    @PostMapping("/api/quizzes")
    @Transactional
    public ResponseEntity<?> addQuiz(@RequestBody Quiz quiz) {
        try {
            entityManager.persist(quiz);
            return ResponseEntity.status(201).body("Quiz created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating quiz");
        }
    }

    // Метод для удаления квиза
    @DeleteMapping("/api/quizzes/{quizId}")
    @Transactional
    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId) {
        try {
            Quiz quiz = entityManager.find(Quiz.class, quizId);
            if (quiz != null) {
                entityManager.remove(quiz);
                return ResponseEntity.ok("Quiz deleted successfully");
            } else {
                return ResponseEntity.status(404).body("Quiz not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error deleting quiz");
        }
    }

    @PostMapping("/api/quizzes/{quizId}/questions")
    @Transactional
    public ResponseEntity<?> addQuestionToQuiz(@PathVariable Long quizId, @RequestBody Question question) {
        try {
            // Логирование сырых данных
            System.out.println("Полученный JSON: " + new ObjectMapper().writeValueAsString(question));

            // Находим квиз по quizId
            Quiz quiz = entityManager.find(Quiz.class, quizId);
            if (quiz == null) {
                return ResponseEntity.status(404).body("Quiz not found");
            }

            // Устанавливаем связь между вопросом и квизом
            question.setQuiz(quiz);

            // Проверка данных ответов
            for (Answer ans : question.getAnswers()) {
                System.out.println("Ответ: текст = " + ans.getAnswerText() + ", isCorrect = " + ans.isCorrect());
                ans.setQuestion(question); // Привязка ответа к вопросу
            }

            // Логируем обработанный вопрос перед сохранением
            System.out.println("Обработанный вопрос: " + question);

            // Сохраняем вопрос (ответы сохраняются каскадно)
            entityManager.persist(question);
            return ResponseEntity.status(201).body("Question added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error adding question");
        }
    }








    @DeleteMapping("/api/quizzes/{quizId}/questions/{questionId}")
    @Transactional
    public ResponseEntity<?> deleteQuestionFromQuiz(@PathVariable Long quizId, @PathVariable Long questionId) {
        try {
            Quiz quiz = entityManager.find(Quiz.class, quizId);
            if (quiz == null) {
                return ResponseEntity.status(404).body("Quiz not found");
            }

            Question question = entityManager.find(Question.class, questionId);
            if (question == null || !question.getQuiz().getId().equals(quizId)) {
                // Либо вопрос не найден, либо он не принадлежит этому квизу
                return ResponseEntity.status(404).body("Question not found in this quiz");
            }

            // Удаляем вопрос из квиза
            quiz.getQuestions().remove(question);
            entityManager.remove(question);

            return ResponseEntity.ok("Question deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error deleting question");
        }
    }

    // Добавление метода для проверки правильных ответов
    @PostMapping("/api/quizzes/{quizId}/submit")
    @Transactional
    public ResponseEntity<?> submitQuiz(@PathVariable Long quizId, @RequestBody List<AnswerSubmission> submissions) {
        try {
            // Получаем квиз
            Quiz quiz = entityManager.find(Quiz.class, quizId);
            if (quiz == null) {
                return ResponseEntity.status(404).body("Quiz not found");
            }

            int correctCount = 0;


            for (AnswerSubmission submission : submissions) {
                System.out.println("Проверяем вопрос ID: " + submission.getQuestionId());
                for (Long answerId : submission.getSelectedAnswerIds()) {
                    Answer answer = entityManager.find(Answer.class, answerId);
                    System.out.println("Ответ ID: " + answerId + ", isCorrect: " + (answer != null ? answer.isCorrect() : "не найден"));
                }
            }

            // Перебираем все вопросы и их ответы
            for (AnswerSubmission submission : submissions) {
                Question question = entityManager.find(Question.class, submission.getQuestionId());
                if (question == null || !question.getQuiz().getId().equals(quizId)) {
                    // Вопрос не найден или не принадлежит этому квизу
                    return ResponseEntity.status(404).body("Question not found in this quiz");
                }

                // Проверяем выбранные ответы
                for (Long answerId : submission.getSelectedAnswerIds()) {
                    Answer answer = entityManager.find(Answer.class, answerId);
                    if (answer != null && answer.isCorrect()) {
                        correctCount++;  // Увеличиваем количество правильных ответов
                    }
                }
            }

            // Возвращаем результат
            return ResponseEntity.ok("You scored " + correctCount + "/" + quiz.getQuestions().size() + " correct answers.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error submitting quiz");
        }
    }

    // DTO для отправки ответов
    public static class AnswerSubmission {
        private Long questionId;
        private List<Long> selectedAnswerIds;

        // Геттеры и сеттеры
        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public List<Long> getSelectedAnswerIds() {
            return selectedAnswerIds;
        }

        public void setSelectedAnswerIds(List<Long> selectedAnswerIds) {
            this.selectedAnswerIds = selectedAnswerIds;
        }
    }


}
