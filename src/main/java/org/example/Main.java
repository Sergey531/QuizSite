package org.example;

public class Main {

    public static void main(String[] args) {
//        // Пример добавления новой викторины
//        addQuiz("moi novii Quiz");
//
//        // Пример получения всех викторин
//        List<Quiz> quizzes = getAllQuizzes();
//        for (Quiz quiz : quizzes) {
//            System.out.println("Quiz ID: " + quiz.getId() + ", Title: " + quiz.getTitle());
//        }
    }
//
//    public static void addQuiz(String title, String quizType, String description) {
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction transaction = null;
//        try {
//            transaction = session.beginTransaction();
//            Quiz quiz = new Quiz(title, quizType, description, new ArrayList<>()); // Создаем новый объект Quiz с параметрами
//            session.save(quiz);
//            transaction.commit();
//            System.out.println("Quiz added successfully: " + title);
//        } catch (Exception e) {
//            if (transaction != null) transaction.rollback();
//            e.printStackTrace();
//        } finally {
//            session.close();
//        }
//    }
//
//    public static List<Quiz> getAllQuizzes() {
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        List<Quiz> quizzes = null;
//        try {
//            quizzes = session.createQuery("FROM Quiz", Quiz.class).list();
//        } finally {
//            session.close();
//        }
//        return quizzes;
//    }
}
