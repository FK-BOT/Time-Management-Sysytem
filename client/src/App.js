import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import UserDashboard from './components/Dashboard/UserDashboard';
import ManagerDashboard from './components/Dashboard/ManagerDashboard';
import ProtectedRoute from './components/Layout/ProtectedRoute';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('token'));

  useEffect(() => {
    const handleStorage = () => setIsAuthenticated(!!localStorage.getItem('token'));
    window.addEventListener('storage', handleStorage);
    return () => window.removeEventListener('storage', handleStorage);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('username');
    setIsAuthenticated(false);
    window.location.href = '/login';
  };

  return (
    <Router>
      <Navbar bg="primary" variant="dark" expand="lg">
        <Container>
          <Navbar.Brand href="/">TimeSheet App</Navbar.Brand>
          <Nav className="me-auto">
            {!isAuthenticated && <Nav.Link href="/login">Login</Nav.Link>}
            {!isAuthenticated && <Nav.Link href="/register">Register</Nav.Link>}
            {isAuthenticated && <Nav.Link onClick={handleLogout}>Logout</Nav.Link>}
          </Nav>
        </Container>
      </Navbar>
      <Container className="mt-4">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/user/dashboard" element={<ProtectedRoute role="USER"><UserDashboard /></ProtectedRoute>} />
          <Route path="/manager/dashboard" element={<ProtectedRoute role="MANAGER"><ManagerDashboard /></ProtectedRoute>} />
          <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
      </Container>
    </Router>
  );
}

export default App;
