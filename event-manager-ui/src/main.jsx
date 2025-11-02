import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom'; // <-- Import
import { AuthProvider } from './context/AuthContext';
import App from './App.jsx';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    {/* Wrap your App in the router */}
    <BrowserRouter>
      <AuthProvider> {/* <-- Wrap the App */}
        <App />
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);