import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, isAuthenticated, isAdmin, isStudent, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  // Don't render standard navbar during the active exam taking session for distraction-free exam mode
  if (location.pathname.startsWith('/exam/take/')) {
    return null;
  }

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm py-2">
      <div className="container">
        <Link className="navbar-brand d-flex align-items-center gap-2" to={isAdmin ? '/admin/dashboard' : '/dashboard'}>
          <span className="navbar-brand-badge">
            <i className="bi bi-mortarboard-fill me-1"></i> SmartAssessment
          </span>
        </Link>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarContent"
          aria-controls="navbarContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navbarContent">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            {isAuthenticated && isStudent && (
              <>
                <li className="nav-item">
                  <Link className={`nav-link ${isActive('/dashboard') ? 'active fw-semibold' : ''}`} to="/dashboard">
                    <i className="bi bi-grid me-1"></i> Available Exams
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className={`nav-link ${isActive('/student/history') ? 'active fw-semibold' : ''}`} to="/student/history">
                    <i className="bi bi-clock-history me-1"></i> Exam History
                  </Link>
                </li>
              </>
            )}

            {isAuthenticated && isAdmin && (
              <>
                <li className="nav-item">
                  <Link className={`nav-link ${isActive('/admin/dashboard') ? 'active fw-semibold' : ''}`} to="/admin/dashboard">
                    <i className="bi bi-speedometer2 me-1"></i> Analytics Dashboard
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className={`nav-link ${isActive('/admin/exams') ? 'active fw-semibold' : ''}`} to="/admin/exams">
                    <i className="bi bi-journal-check me-1"></i> Manage Examinations
                  </Link>
                </li>
              </>
            )}
          </ul>

          <ul className="navbar-nav align-items-center gap-2">
            {isAuthenticated ? (
              <li className="nav-item dropdown">
                <a
                  className="nav-link dropdown-toggle d-flex align-items-center gap-2 text-white"
                  href="#profile"
                  role="button"
                  data-bs-toggle="dropdown"
                  aria-expanded="false"
                >
                  <div
                    className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold"
                    style={{ width: '32px', height: '32px', fontSize: '0.85rem' }}
                  >
                    {user?.fullName?.charAt(0).toUpperCase() || 'U'}
                  </div>
                  <span>{user?.fullName}</span>
                  <span className={`badge ${isAdmin ? 'bg-danger' : 'bg-primary'} ms-1`} style={{ fontSize: '0.7rem' }}>
                    {isAdmin ? 'ADMIN' : 'STUDENT'}
                  </span>
                </a>
                <ul className="dropdown-menu dropdown-menu-end shadow-sm">
                  <li>
                    <div className="dropdown-item-text text-muted small">
                      Signed in as <strong>{user?.username}</strong>
                    </div>
                  </li>
                  <li><hr className="dropdown-divider" /></li>
                  <li>
                    <button className="dropdown-item text-danger" onClick={handleLogout}>
                      <i className="bi bi-box-arrow-right me-2"></i> Logout
                    </button>
                  </li>
                </ul>
              </li>
            ) : (
              <>
                <li className="nav-item">
                  <Link className="btn btn-outline-light btn-sm px-3" to="/login">
                    Sign In
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="btn btn-primary btn-sm px-3" to="/register">
                    Register
                  </Link>
                </li>
              </>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
