package org.example.repository;

import org.example.ForQuiz.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Здесь вы можете добавить дополнительные методы, если это необходимо
}
