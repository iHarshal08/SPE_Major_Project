import React from 'react';
import LoginForm from '../components/LoginForm';
import '../styles/LoginPage.css'

function LoginPage() {
    return (
        <div className="login-page">
            <div className="login-box">
                <h2>Welcome to Chatter</h2>
                <p>Please log in to continue</p>
                <LoginForm />
            </div>
        </div>
    );
}

export default LoginPage;
