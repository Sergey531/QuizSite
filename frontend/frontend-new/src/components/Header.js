import React from 'react';
import { Link } from 'react-router-dom';
import './Header.css';

function Header({ title }) {
    return (
        <header>
            <nav>
                <ul>
                    <li><Link to="/">Home</Link></li>
                    <li><Link to="/quizzes">Quizzes</Link></li>
                    <li><Link to="/create">Create</Link></li>
                </ul>
            </nav>
            <h1>{title}</h1>
        </header>
    );
}

export default Header;
