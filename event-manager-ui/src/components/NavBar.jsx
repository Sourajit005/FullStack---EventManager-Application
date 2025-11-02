import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <div className="nav-links-left">
        <Link to="/">Home</Link>
        {user && (
          <Link to="/my-tickets">My Tickets</Link>
        )}
        {user && user.role === 'ROLE_ORGANIZER' && (
          <Link to="/dashboard">My Dashboard</Link>
        )}
      </div>
      <div className="nav-links-right">
        {user ? (
          <>
            <span>
              Hello, {user.username} ({user.role.replace('ROLE_', '')})
            </span>
            <button onClick={handleLogout}>Logout</button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;