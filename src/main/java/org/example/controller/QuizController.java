package org.example.controller;

import org.example.ForQuiz.HibernateUtil;
import org.example.ForQuiz.Question;
import org.example.ForQuiz.Quiz;
import org.example.ForQuiz.UserAnswer;
import org.example.repository.UserAnswerRepository;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class QuizController {

    private final UserAnswerRepository userAnswerRepository;

    @Autowired
    public QuizController(UserAnswerRepository userAnswerRepository) {
        this.userAnswerRepository = userAnswerRepository;
    }

    @GetMapping("/quizzes")
    public String getQuizzes(Model model) {
        List<Quiz> quizzes = getAllQuizzes();
        model.addAttribute("quizzes", quizzes);
        return "QuizLibrary"; // Название HTML-шаблона для отображения
    }

    @GetMapping("/create")
    public String showCreateQuizPage() {
        return "createQuiz"; // Возвращаем страницу создания квиза
    }

    @PostMapping("/addQuiz")
    public String addQuiz(@RequestParam String title,
                          @RequestParam String quizType,
                          @RequestParam String description) {
        addQuizToDatabase(title, quizType, description); // Передаем параметры в метод
        return "redirect:/quizzes"; // Перенаправление на страницу викторин
    }

    public List<Quiz> getAllQuizzes() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Quiz> quizzes = null;
        try {
            quizzes = session.createQuery("FROM Quiz", Quiz.class).list();
        } finally {
            session.close();
        }
        return quizzes;
    }

    public void addQuizToDatabase(String title, String quizType, String description) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Quiz quiz = new Quiz(title, quizType, description, new HashSet<>()); // Создаем новый объект Quiz с параметрами
            session.save(quiz);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @GetMapping("/quiz/{quizId}")
    public String viewQuiz(@PathVariable Long quizId, Model model) {
        Quiz quiz = getQuizById(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questionIndex", 0); // Начинаем с первого вопроса
        return "quiz"; // Укажите имя вашего HTML-шаблона
    }

    @GetMapping("/quiz/{quizId}/question/{index}")
    public String viewQuizQuestion(@PathVariable Long quizId, @PathVariable int index, Model model) {
        Quiz quiz = getQuizById(quizId);
        if (quiz != null) {
            List<Question> questions = new ArrayList<>(quiz.getQuestions()); // Преобразуем Set в List
            if (index >= 0 && index < questions.size()) {
                Question question = questions.get(index); // Получаем вопрос по индексу
                model.addAttribute("quiz", quiz);
                model.addAttribute("question", question);
                model.addAttribute("answers", question.getAnswers()); // добавляем варианты ответов
                model.addAttribute("questionIndex", index); // добавляем индекс вопроса
                return "quiz"; // имя вашего шаблона для отображения
            }
        }
        return "redirect:/quizzes"; // если квиз или вопрос не найдены, перенаправляем на список квизов
    }


    public Quiz getQuizById(Long quizId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Quiz quiz = null;
        try {
            quiz = session.createQuery("FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :quizId", Quiz.class)
                    .setParameter("quizId", quizId)
                    .uniqueResult();

            // Загрузите вопросы отдельно, чтобы избежать MultipleBagFetchException
            for (Question question : quiz.getQuestions()) {
                Hibernate.initialize(question.getAnswers());
            }
        } finally {
            session.close();
        }
        return quiz;
    }



    @PostMapping("/deleteQuiz/{quizId}")
    public String deleteQuiz(@PathVariable int quizId) {
        deleteQuizById(quizId); // Метод для удаления квиза по ID
        return "redirect:/quizzes"; // Перенаправление на страницу квизов
    }

    public void deleteQuizById(int quizId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Quiz quiz = session.get(Quiz.class, quizId);
            if (quiz != null) {
                session.delete(quiz);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @PostMapping("/submitAnswer")
    public String submitAnswer(@RequestParam Long quizId,
                               @RequestParam Long questionIndex,
                               @RequestParam Long selectedAnswer) {
        // Сохранение ответа в базе данных
        UserAnswer userAnswer = new UserAnswer(quizId, questionIndex, selectedAnswer);
        userAnswerRepository.save(userAnswer); // Не забудьте внедрить репозиторий

        // Получаем количество вопросов в квизе
        Quiz quiz = getQuizById(quizId);
        if (quiz != null && questionIndex < quiz.getQuestions().size() - 1) {
            // Если это не последний вопрос, перенаправляем на следующий вопрос
            return "redirect:/quiz/" + quizId + "/question/" + (questionIndex + 1);
        } else {
            // Если это последний вопрос, перенаправляем на страницу результатов
            return "redirect:/quiz/results?quizId=" + quizId;
        }
    }

    @GetMapping("/quiz/results")
    public String viewResults(@RequestParam Long quizId, Model model) {
        Quiz quiz = getQuizById(quizId);
        if (quiz == null) {
            return "redirect:/quizzes"; // Если квиз не найден, перенаправляем на список квизов
        }

        // Преобразуем Set<Question> в List<Question> для дальнейшего использования
        List<Question> questions = new ArrayList<>(quiz.getQuestions());

        // Получаем ответы пользователя из базы данных
        List<UserAnswer> userAnswers = userAnswerRepository.findByQuizId(quizId);

        // Устанавливаем выбранные ответы для каждого вопроса
        for (Question question : questions) {
            for (UserAnswer userAnswer : userAnswers) {
                if (userAnswer.getQuestionId().equals(question.getId())) {
                    question.setSelectedAnswer(userAnswer.getSelectedAnswerId()); // Устанавливаем выбранный ответ
                    break;
                }
            }
        }

        model.addAttribute("quiz", quiz); // Добавляем сам квиз
        model.addAttribute("questions", questions); // Добавляем вопросы
        return "quizResults"; // Возвращаем имя шаблона для отображения результатов
    }




    // Метод для сохранения ответа пользователя (можно модифицировать по вашему усмотрению)
    private void saveUserAnswer(int quizId, int questionIndex, int selectedAnswerId) {
        // Здесь вы можете сохранить ответ в базе данных или в сессии
        // Например, создание объекта UserAnswer и его сохранение
        // UserAnswer answer = new UserAnswer(quizId, questionIndex, selectedAnswerId);
        // userAnswerRepository.save(answer);

        // Или сохранение в сессии
        // HttpSession session = request.getSession();
        // List<Integer> userAnswers = (List<Integer>) session.getAttribute("userAnswers");
        // if (userAnswers == null) {
        //     userAnswers = new ArrayList<>();
        // }
        // userAnswers.add(selectedAnswerId);
        // session.setAttribute("userAnswers", userAnswers);
    }


}
