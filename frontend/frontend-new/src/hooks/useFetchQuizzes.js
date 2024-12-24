import { useState, useEffect } from 'react';

/**
 * Пользовательский хук для получения списка квизов с сервера.
 * @returns {{ quizzes: Array, error: string | null }} - Возвращает массив квизов и строку ошибки (если есть).
 */
export default function useFetchQuizzes() {
    const [quizzes, setQuizzes] = useState([]); // Массив квизов
    const [error, setError] = useState(null);  // Ошибка (строка или null)

    useEffect(() => {
        fetch('http://localhost:8080/api/quizzes')
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then((data) => {
                console.log("Received data from server:", data); // Лог для проверки данных
                if (!Array.isArray(data)) {
                    throw new Error("Invalid JSON response");
                }
                setQuizzes(data);
            })
            .catch((error) => {
                console.error('Error fetching quizzes:', error);
                setError(error.message);
            });
    }, []);



    return { quizzes, error }; // Возвращаем объект с массивом квизов и ошибкой
}
