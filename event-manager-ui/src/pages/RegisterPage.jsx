import React, { useState } from 'react';
import authService from '../services/authService';
import { useNavigate } from 'react-router-dom';

const RegisterPage = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('ROLE_USER');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  /**
   * This is the updated handleSubmit function
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      await authService.register({ username, email, password, role });
      // On success, go to login page
      navigate('/login');
      
    } catch (err) {
      // --- THIS IS THE UPDATED CATCH BLOCK ---
      if (err.response && err.response.data && err.response.data.message) {
        // 1. Check if the backend sent a specific error message
        // 2. Display that specific message (e.g., "Error: Username is already taken!")
        setError(err.response.data.message);
      } else {
        // 3. If no specific message (e.g., network error), show a generic one
        setError('Registration failed. Please try again.');
      }
      console.error(err);
      // --- END OF UPDATE ---
    }
  };

  return (
    <div className="form-container">
      <h2>Register</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Username:</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label>Email:</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label>Password:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label>Register as:</label>
          <select value={role} onChange={(e) => setRole(e.target.value)}>
            <option value="ROLE_USER">Attendee</option>
            <option value="ROLE_ORGANIZER">Event Organizer</option>
          </select>
        </div>
        {/* This <p> will now show the better error message */}
        {error && <p className="error-message">{error}</p>} 
        <button type="submit" className="btn-submit">Register</button>
      </form>
    </div>
  );
};

export default RegisterPage;