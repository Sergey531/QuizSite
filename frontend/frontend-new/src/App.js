import React, { useState, useEffect } from 'react';
import './App.css';
import Header from './components/Header';
import Footer from './components/Footer';
import QuizList from './components/QuizList';
import CreateQuizForm from './components/CreateQuizForm';
import AddQuestion from './components/AddQuestion';
import QuizPage from './components/QuizPage';
import axios from 'axios';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

function App() {
    const [quizzes, setQuizzes] = useState([]);
    const [error, setError] = useState(null);
    const [pageTitle, setPageTitle] = useState('Quiz Library');

    useEffect(() => {
        fetchQuizzes();
    }, []);

    const fetchQuizzes = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/quizzes');
            setQuizzes(response.data);
        } catch (err) {
            setError('Failed to load quizzes');
            console.error(err);
        }
    };

    const handleDelete = async (quizId) => {
        try {
            await axios.delete(`http://localhost:8080/api/quizzes/${quizId}`);
            fetchQuizzes();
        } catch (error) {
            console.error('Error deleting quiz:', error);
            alert('Error deleting quiz. Please try again.');
        }
    };

    return (
        <Router>
            <div>
                <Header title={pageTitle} />
                <main>
                    {error && <div style={{ color: 'red' }}>{error}</div>}
                    <Routes>
                        <Route
                            path="/"
                            element={<HomePage setPageTitle={setPageTitle} />}
                        />
                        <Route
                            path="/quizzes"
                            element={<QuizList quizzes={quizzes} handleDelete={handleDelete} setPageTitle={setPageTitle} />}
                        />
                        <Route
                            path="/create"
                            element={<CreateQuizForm onQuizCreated={fetchQuizzes} setPageTitle={setPageTitle} />}
                        />
                        <Route
                            path="/addQuestions/:quizId"
                            element={<AddQuestion setPageTitle={setPageTitle} />}
                        />
                        <Route
                            path="/quiz/:quizId"
                            element={<QuizPage setPageTitle={setPageTitle} />}
                        />
                    </Routes>
                </main>
                <Footer />
            </div>
        </Router>
    );
}

function HomePage({ setPageTitle }) {
    useEffect(() => {
        setPageTitle('Home sweet home');
    }, [setPageTitle]);

    return <h2>Welcome to the Quiz Library</h2>;
}

export default App;
