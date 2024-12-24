import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import './AddQuestion.css';

const AddQuestion = ({ setPageTitle }) => {
    const { quizId } = useParams();
    const [questionText, setQuestionText] = useState('');
    const [answers, setAnswers] = useState([
        { answerText: '', isCorrect: false },
        { answerText: '', isCorrect: false },
    ]); // Изначально два ответа
    const [error, setError] = useState(null);

    useEffect(() => {
        setPageTitle('Adding Questions');
    }, [setPageTitle]);

    const handleAddAnswer = () => {
        setAnswers([...answers, { answerText: '', isCorrect: false }]);
    };

    const handleRemoveAnswer = (index) => {
        const updatedAnswers = answers.filter((_, i) => i !== index);
        setAnswers(updatedAnswers);
    };

    const handleSubmit = async (event) => {
        event.preventDefault(); // Предотвращаем обновление страницы

        // Формируем данные вопроса
        const questionData = {
            questionText: questionText,
            quizId: quizId, // Добавляем quizId
            answers,
        };

        // Логируем данные, которые отправляются на сервер
        console.log('Отправляем JSON:', JSON.stringify(questionData, null, 2));

        try {
            // Отправляем запрос на сервер
            await axios.post(`http://localhost:8080/api/quizzes/${quizId}/questions`, questionData);
            alert('Question added successfully!');

            // Сбрасываем поля формы после успешного добавления
            setQuestionText('');
            setAnswers([
                { answerText: '', isCorrect: false },
                { answerText: '', isCorrect: false },
            ]);
        } catch (err) {
            // Если запрос завершился с ошибкой
            setError('Failed to add question');
            console.error('Ошибка при добавлении вопроса:', err.response || err.message);
        }
    };

    return (
        <div className="add-question-container">
            <h2 className="add-question-title">Add a New Question</h2>
            {error && <div className="error-message">{error}</div>}
            <form onSubmit={handleSubmit}>
                <label htmlFor="questionText">Enter Question Text:</label>
                <input
                    type="text"
                    id="questionText"
                    value={questionText}
                    onChange={(e) => setQuestionText(e.target.value)}
                    required
                />
                {answers.map((answer, index) => (
                    <div key={index} className="answer-container">
                        <input
                            type="text"
                            placeholder={`Answer ${index + 1}`}
                            value={answer.answerText}
                            onChange={(e) =>
                                setAnswers((prev) =>
                                    prev.map((a, i) =>
                                        i === index ? { ...a, answerText: e.target.value } : a
                                    )
                                )
                            }
                            required
                        />
                        <label>
                            <input
                                type="checkbox"
                                checked={answer.isCorrect}
                                onChange={(e) =>
                                    setAnswers((prev) =>
                                        prev.map((a, i) =>
                                            i === index
                                                ? { ...a, isCorrect: e.target.checked }
                                                : a
                                        )
                                    )
                                }
                            />{' '}
                            Correct
                        </label>
                        {answers.length > 2 && (
                            <button
                                type="button"
                                className="remove-answer-button"
                                onClick={() => handleRemoveAnswer(index)}
                            >
                                Remove
                            </button>
                        )}
                    </div>
                ))}
                <button type="button" onClick={handleAddAnswer} className="add-answer-button">
                    Add Another Answer
                </button>
                <button type="submit">Add Question</button>
            </form>
        </div>
    );
};

export default AddQuestion;
