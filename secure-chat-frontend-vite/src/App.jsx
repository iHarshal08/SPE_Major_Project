import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import SecureChatPage from './pages/SecureChatPage';

function App() {
  return (
      <Router>
        <Routes>
          <Route path="/" element={<LoginPage />} />
          <Route path="/chat" element={<SecureChatPage />} />
        </Routes>
      </Router>
  );
}

export default App;
