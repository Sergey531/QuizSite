import React, { useEffect } from 'react';
import './QuizList.css';
import { useNavigate } from 'react-router-dom';

function QuizList({ quizzes, handleDelete, setPageTitle }) {
    const navigate = useNavigate();

    useEffect(() => {
        setPageTitle('Quiz Library');
    }, [setPageTitle]);

    const handleAddQuestion = (quizId) => {
        navigate(`/addQuestions/${quizId}`);
    };

    const handleStartQuiz = (quizId) => {
        navigate(`/quiz/${quizId}`);
    };

    return (
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            {quizzes.map((quiz, index) => (
                <tr key={quiz.id}>
                    <td>{index + 1}</td>
                    <td>{quiz.title}</td>
                    <td>
                        <button className="start" onClick={() => handleStartQuiz(quiz.id)}>
                            Start
                        </button>
                        <button
                            className="add-question"
                            onClick={() => handleAddQuestion(quiz.id)}
                        >
                            Add Question
                        </button>
                        <button
                            className="delete"
                            onClick={() => handleDelete(quiz.id)}
                        >
                            Delete
                        </button>
                    </td>
                </tr>
            ))}
            </tbody>
        </table>
    );
}

export default QuizList;
