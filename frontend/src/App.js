import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';

// Auth Pages
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';

// Super Admin Pages
import SuperAdminDashboard from './pages/superadmin/SuperAdminDashboard';

// Institute Admin Pages
import InstituteCandidatesPage from './pages/institute/InstituteCandidatesPage';

// Admin Pages
import AdminDashboard from './pages/admin/AdminDashboard';
import ExamListPage from './pages/admin/ExamListPage';
import QuestionManagePage from './pages/admin/QuestionManagePage';
import ExamAttemptsAnalyticsPage from './pages/admin/ExamAttemptsAnalyticsPage';

// Student Pages
import StudentDashboard from './pages/student/StudentDashboard';
import ExamTakingPage from './pages/student/ExamTakingPage';
import ExamResultPage from './pages/student/ExamResultPage';
import StudentHistoryPage from './pages/student/StudentHistoryPage';

const RootRedirect = () => {
  const { isAuthenticated, isSuperAdmin, isInstituteAdmin, isAdmin } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (isSuperAdmin) return <Navigate to="/super-admin/dashboard" replace />;
  if (isInstituteAdmin) return <Navigate to="/admin/exams" replace />;
  if (isAdmin) return <Navigate to="/admin/dashboard" replace />;
  return <Navigate to="/dashboard" replace />;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="d-flex flex-column min-vh-100">
          <Navbar />
          <main className="flex-grow-1">
            <Routes>
              {/* Root */}
              <Route path="/" element={<RootRedirect />} />

              {/* Public Auth Routes */}
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />

              {/* Super Admin Routes */}
              <Route
                path="/super-admin/dashboard"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN']}>
                    <SuperAdminDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/super-admin/institutes"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN']}>
                    <SuperAdminDashboard />
                  </ProtectedRoute>
                }
              />

              {/* Institute Admin Specific Routes */}
              <Route
                path="/institute/candidates"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_INSTITUTE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_ADMIN']}>
                    <InstituteCandidatesPage />
                  </ProtectedRoute>
                }
              />

              {/* Student Protected Routes */}
              <Route
                path="/dashboard"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_STUDENT', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_INSTITUTE_ADMIN']}>
                    <StudentDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/history"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_STUDENT', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_INSTITUTE_ADMIN']}>
                    <StudentHistoryPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/exam/take/:examId"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_STUDENT', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_INSTITUTE_ADMIN']}>
                    <ExamTakingPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/exam/result/:attemptId"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_STUDENT', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_INSTITUTE_ADMIN']}>
                    <ExamResultPage />
                  </ProtectedRoute>
                }
              />

              {/* Admin Protected Routes */}
              <Route
                path="/admin/dashboard"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_INSTITUTE_ADMIN']}>
                    <AdminDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/exams"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_INSTITUTE_ADMIN']}>
                    <ExamListPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/exams/:examId/questions"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_INSTITUTE_ADMIN']}>
                    <QuestionManagePage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/admin/exams/:examId/attempts"
                element={
                  <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_INSTITUTE_ADMIN']}>
                    <ExamAttemptsAnalyticsPage />
                  </ProtectedRoute>
                }
              />

              {/* Fallback */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
