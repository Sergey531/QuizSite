package org.example.controller;

import org.example.ForQuiz.Answer;
import org.example.ForQuiz.HibernateUtil;
import org.example.ForQuiz.Question;
import org.example.ForQuiz.Quiz;
import org.example.repository.QuestionRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @PostMapping("/addQuestion/{quizId}")
    public String addQuestion(@PathVariable int quizId,
                              @RequestParam String questionText,
                              @RequestParam List<String> answerTexts,
                              @RequestParam List<Boolean> isCorrect) {
        Question question = new Question();
        question.setQuestionText(questionText);

        // Получите квиз и установите его в вопрос
        Quiz quiz = getQuizById(quizId);
        question.setQuiz(quiz); // Теперь это будет работать

        // Создайте список ответов
        Set<Answer> answers = new HashSet<>(); // Используем Set вместо List
        for (int i = 0; i < answerTexts.size(); i++) {
            Answer answer = new Answer();
            answer.setAnswerText(answerTexts.get(i));
            answer.setCorrect(isCorrect.get(i));
            answer.setQuestion(question); // Установите вопрос для ответа
            answers.add(answer);
        }
        question.setAnswers(answers); // Установите ответы в вопрос

        // Сохраните вопрос в базе данных
        questionRepository.save(question);
        return "redirect:/quiz/" + quizId; // Перенаправление на страницу квиза
    }








    // Метод для получения квиза по ID
    public Quiz getQuizById(int quizId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Quiz quiz = null;
        try {
            quiz = session.createQuery("FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :quizId", Quiz.class)
                    .setParameter("quizId", quizId)
                    .uniqueResult();
        } finally {
            session.close();
        }
        return quiz;
    }


    // Метод для сохранения вопроса в базе данных
    public void saveQuestion(Question question) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(question);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @GetMapping("/addQuestions/{quizId}")
    public String showAddQuestionForm(@PathVariable int quizId, Model model) {
        Quiz quiz = getQuizById(quizId); // Получите квиз по ID
        model.addAttribute("quiz", quiz); // Добавьте квиз в модель
        model.addAttribute("question", new Question()); // Создайте новый объект вопроса
        return "addQuestions"; // Возвратите имя шаблона
    }


}
