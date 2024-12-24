import React from 'react';
import useManageAnswers from '../hooks/useManageAnswers'; // Подключаем хук

function AddQuestion() {
    const { answers, addAnswer, handleAnswerChange } = useManageAnswers(); // Используем хук

    const handleSubmit = (event) => {
        event.preventDefault();
        console.log("Question Text:", event.target.questionText.value);
        console.log("Answers:", answers);
        // Здесь можно добавить отправку данных на сервер
    };

    return (
        <div>
            <header>
                <h1>Adding Questions to Quiz</h1>
                <nav>
                    <ul>
                        <li><a href="/">Home</a></li>
                        <li><a href="/quizzes">Quizzes</a></li>
                        <li><a href="/about">About Us</a></li>
                        <li><a href="/create">Create</a></li>
                    </ul>
                </nav>
            </header>

            <main>
                <section>
                    <form onSubmit={handleSubmit}>
                        <label htmlFor="questionText">Enter Question Text:</label>
                        <input type="text" id="questionText" name="questionText" required />

                        <h3>Answer Options:</h3>
                        <div id="answerFields">
                            {answers.map((answer, index) => (
                                <div key={index}>
                                    <input
                                        type="text"
                                        name="answerTexts"
                                        placeholder={`Answer ${index + 1}`}
                                        required
                                        value={answer.text}
                                        onChange={(e) => handleAnswerChange(index, e)}
                                    />
                                    <input
                                        type="checkbox"
                                        name="isCorrect"
                                        checked={answer.isCorrect}
                                        onChange={(e) => handleAnswerChange(index, e)}
                                    /> Correct
                                </div>
                            ))}
                        </div>

                        <button type="button" onClick={addAnswer}>Add Answer Option</button>
                        <button type="submit">Add Question</button>
                    </form>
                </section>
            </main>

            <footer>
                <p>&copy; 2024 Your Quiz Site</p>
            </footer>
        </div>
    );
}

export default AddQuestion;
