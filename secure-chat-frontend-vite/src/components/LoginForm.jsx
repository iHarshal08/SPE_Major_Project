import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginUser } from "../api/chatApi";
import SecureChatPage from "../pages/SecureChatPage";



function LoginForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');

        try {
            console.log(email);
            console.log(password)
            const token = await loginUser(email, password);
            sessionStorage.setItem('jwt_token', token);
            navigate('/chat');
        } catch (err) {
            setError('Invalid credentials. Please try again.');
        }
    };

    return (
        <form className="login-form" onSubmit={handleLogin}>
            <input
                type="email"
                placeholder="Email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <input
                type="password"
                placeholder="Password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <button type="submit">Log In</button>
            {error && <p className="error">{error}</p>}
        </form>
    );
}

export default LoginForm;
