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
      {/* Brand Logo on the left */}
      <div className="nav-links-left">
        <Link to="/" className="navbar-brand">✨ EventManager</Link>
        
        {/* Only show these links if logged in */}
        {user && <Link to="/my-tickets">My Tickets</Link>}
        {user && user.role === 'ROLE_ORGANIZER' && (
          <Link to="/dashboard">Dashboard</Link>
        )}
      </div>

      <div className="nav-links-right">
        {user ? (
          <>
            <span style={{fontWeight: '600'}}>Hi, {user.username}</span>
            <button onClick={handleLogout} className="btn-logout">Logout</button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">
              <button style={{padding: '8px 20px', fontSize: '14px'}}>Get Started</button>
            </Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;