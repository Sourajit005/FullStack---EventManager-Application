import React, { createContext, useState, useContext, useEffect } from 'react';
import authService from '../services/authService';
import { jwtDecode } from 'jwt-decode'; // We need a library to decode the token

// Run: npm install jwt-decode

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null); // Holds user info { sub, role }
  const [token, setToken] = useState(localStorage.getItem('token')); // Get token from storage

  useEffect(() => {
    // When the app loads, check if a token exists in localStorage
    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        
        // Check if token is expired
        const isExpired = decodedToken.exp * 1000 < Date.now();
        
        if (isExpired) {
          logout(); // If expired, log out
        } else {
          // If valid, set the user state
          setUser({
            username: decodedToken.sub, // 'sub' is typically the username
            role: decodedToken.authorities[0].authority, // Get role from token
          });
        }
      } catch (error) {
        console.error('Invalid token:', error);
        logout(); // If token is invalid, log out
      }
    }
  }, [token]);

  const login = async (username, password) => {
    try {
      const response = await authService.login({ username, password });
      const { token } = response.data;
      
      localStorage.setItem('token', token); // Save token
      setToken(token); // Update state
      
      const decodedToken = jwtDecode(token);
      setUser({
        username: decodedToken.sub,
        role: decodedToken.authorities[0].authority,
      });
      return true; // Indicate success
    } catch (error) {
      console.error('Login failed:', error);
      return false; // Indicate failure
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
    setToken(null);
  };
  
  // We pass down the user, login, and logout functions
  const value = {
    user,
    token,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// This is a helper hook to easily access the context
export const useAuth = () => {
  return useContext(AuthContext);
};