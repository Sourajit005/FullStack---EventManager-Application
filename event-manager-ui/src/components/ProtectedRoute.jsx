import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Navigate, useLocation } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
  const { user } = useAuth();
  const location = useLocation();

  // 1. Check if user is logged in and is an ORGANIZER
  if (user && user.role === 'ROLE_ORGANIZER') {
    // 2. If yes, show the page content
    return children;
  }

  // 3. If no, redirect them to the login page
  // We also pass the page they tried to visit, so we can redirect them back after login
  return <Navigate to="/login" state={{ from: location }} replace />;
};

export default ProtectedRoute;