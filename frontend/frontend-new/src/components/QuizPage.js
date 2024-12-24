import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './QuizPage.css';

const QuizPage = ({ setPageTitle }) => {
    const { quizId } = useParams();
    const navigate = useNavigate();
    const [quizData, setQuizData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [selectedAnswers, setSelectedAnswers] = useState({});
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [showThanks, setShowThanks] = useState(false);
    const [correctAnswers, setCorrectAnswers] = useState(0);
    const [totalQuestions, setTotalQuestions] = useState(0);

    useEffect(() => {
        const fetchQuizData = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/api/quizzes/${quizId}`);
                const updatedQuestions = response.data.questions.map((q) => {
                    const correctAnswer = q.answers.find((a) => a.isCorrect);
                    return { ...q, correctAnswerId: correctAnswer ? correctAnswer.id : null };
                });
                setQuizData({ ...response.data, questions: updatedQuestions });
                setPageTitle(response.data.title || 'Quiz Page');
                setTotalQuestions(response.data.questions.length);
            } catch (error) {
                console.error('Failed to fetch quiz data:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchQuizData();
    }, [quizId, setPageTitle]);

    const handleAnswerSelect = (questionId, answerId) => {
        setSelectedAnswers(prev => ({
            ...prev,
            [questionId]: answerId
        }));

        const question = quizData.questions.find(q => q.id === questionId);
        const correctAnswerId = question.correctAnswerId;

        if (answerId === correctAnswerId) {
            setCorrectAnswers(prevCorrect => prevCorrect + 1);
        }
    };

    const handleFinishQuiz = () => {
        setShowThanks(true);
    };

    if (loading) return <div>Loading...</div>;

    if (!quizData || !quizData.questions || quizData.questions.length === 0) {
        return <div>No questions available.</div>;
    }

    if (showThanks) {
        return (
            <div className="thanks-container">
                <div className="thanks-message">
                    Thank you for completing the quiz! Your result: {correctAnswers}/{totalQuestions}
                </div>
                <div className="action-buttons">
                    <button onClick={() => setShowThanks(false)}>
                        Show answers
                    </button>
                    <button onClick={() => navigate('/quizzes')}>
                        Back to quiz list
                    </button>
                    <button onClick={() => window.location.reload()}>
                        Take the quiz again
                    </button>
                </div>
                <div className="answers-review">
                    {quizData.questions.map((question) => {
                        const userAnswer = selectedAnswers[question.id];
                        const isCorrect = userAnswer === question.correctAnswerId;

                        return (
                            <div key={question.id} className="review-item">
                                <p className="question-text">{question.questionText}</p>
                                <ul className="answer-list">
                                    {question.answers.map((answer) => (
                                        <li
                                            key={answer.id}
                                            className={`answer-item ${
                                                answer.id === question.correctAnswerId
                                                    ? 'correct-answer'
                                                    : 'wrong-answer'
                                            }`}
                                        >
                                            {answer.answerText}
                                            {answer.id === userAnswer && (
                                                <span className={`user-choice ${isCorrect ? 'correct-choice' : 'wrong-choice'}`}>

                                                    {isCorrect ? (
                                                        <span className="icon correct-icon">✔(Your answer)</span>
                                                    ) : (
                                                        <span className="icon wrong-icon">✘(Your answer)</span>
                                                    )}
                                                </span>
                                            )}
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        );
                    })}
                </div>
            </div>
        );
    }

    const question = quizData.questions[currentQuestionIndex];

    return (
        <div className="quiz-page-container">
            <div className="questions-container">
                <button
                    className="back-to-menu-button"
                    onClick={() => navigate('/quizzes')}
                >
                    Back to quiz list
                </button>
                <p>Questions:</p>
                <div key={question.id} className="question-item">
                    <p className="question-text">{question.questionText}</p>
                    <ul className="answer-list">
                        {question.answers.map((answer) => (
                            <li key={answer.id} className="answer-item">
                                <label>
                                    <input
                                        type="radio"
                                        name={`question-${question.id}`}
                                        value={answer.id}
                                        checked={selectedAnswers[question.id] === answer.id}
                                        onChange={() => handleAnswerSelect(question.id, answer.id)}
                                    />
                                    {answer.answerText}
                                </label>
                            </li>
                        ))}
                    </ul>
                </div>
                <div className="navigation-buttons">
                    {currentQuestionIndex > 0 && (
                        <button className="nav-button" onClick={() => setCurrentQuestionIndex(prev => prev - 1)}>
                            Previous
                        </button>
                    )}
                    {currentQuestionIndex < quizData.questions.length - 1 ? (
                        <button className="nav-button next-button" onClick={() => setCurrentQuestionIndex(prev => prev + 1)}>
                            Next
                        </button>
                    ) : (
                        <button className="finish-button next-button" onClick={handleFinishQuiz}>
                            Finish Quiz
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default QuizPage;
