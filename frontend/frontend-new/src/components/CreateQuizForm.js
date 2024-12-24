import React, { useState, useEffect } from 'react';
import './createQuiz.css';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function CreateQuizForm({ onQuizCreated, setPageTitle }) {
    const [quizTitle, setQuizTitle] = useState('');
    const [quizType, setQuizType] = useState('Personality Quiz');
    const [quizDescription, setQuizDescription] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        setPageTitle('Create a New Quiz');
    }, [setPageTitle]);

    const handleSubmit = async (event) => {
        event.preventDefault();

        const newQuiz = {
            title: quizTitle,
            quizType,
            description: quizDescription,
        };

        try {
            await axios.post('http://localhost:8080/api/quizzes', newQuiz);
            onQuizCreated();
            navigate('/quizzes');
        } catch (error) {
            console.error('There was an error creating the quiz:', error);
            alert('Error creating quiz. Please try again.');
        }
    };

    return (
        <div className="create-quiz-form-container">
            <form className="create-quiz-form" onSubmit={handleSubmit}>
                <input
                    type="text"
                    value={quizTitle}
                    onChange={(e) => setQuizTitle(e.target.value)}
                    placeholder="Quiz Title"
                    required
                />
                <select value={quizType} onChange={(e) => setQuizType(e.target.value)}>
                    <option value="Personality Quiz">Personality Quiz</option>
                    <option value="Trivia Quiz">Trivia Quiz</option>
                    <option value="Knowledge Quiz">Knowledge Quiz</option>
                </select>
                <textarea
                    value={quizDescription}
                    onChange={(e) => setQuizDescription(e.target.value)}
                    placeholder="Quiz Description"
                    required
                />
                <button type="submit">Create Quiz</button>
            </form>
        </div>
    );
}

export default CreateQuizForm;
