import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user, isAuthenticated, loading, isSuperAdmin, isInstituteAdmin } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (allowedRoles && allowedRoles.length > 0) {
    const role = user?.role;
    const hasDirectMatch = allowedRoles.includes(role);
    const hasAdminEquiv = allowedRoles.includes('ROLE_ADMIN') && (isSuperAdmin || isInstituteAdmin);

    if (!hasDirectMatch && !hasAdminEquiv) {
      if (isSuperAdmin) {
        return <Navigate to="/super-admin/dashboard" replace />;
      }
      if (isInstituteAdmin) {
        return <Navigate to="/admin/exams" replace />;
      }
      if (role === 'ROLE_STUDENT') {
        return <Navigate to="/dashboard" replace />;
      }
      return <Navigate to="/" replace />;
    }
  }

  return children;
};

export default ProtectedRoute;
