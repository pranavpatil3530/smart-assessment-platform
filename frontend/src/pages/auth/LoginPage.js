import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { authApi } from '../../api/authApi';
import { useAuth } from '../../context/AuthContext';

const LoginPage = () => {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const queryParams = new URLSearchParams(location.search);
  const isExpired = queryParams.get('expired') === 'true';

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authApi.login({ usernameOrEmail, password });
      if (response.success && response.data) {
        const { accessToken, userId, username, email, fullName, role, instituteId, instituteName, approvalStatus } = response.data;
        login(accessToken, { id: userId, username, email, fullName, role, instituteId, instituteName, approvalStatus });

        if (role === 'ROLE_SUPER_ADMIN') {
          navigate('/super-admin/dashboard');
        } else if (role === 'ROLE_INSTITUTE_ADMIN') {
          navigate('/admin/exams');
        } else if (role === 'ROLE_ADMIN') {
          navigate('/admin/dashboard');
        } else {
          navigate('/dashboard');
        }
      }
    } catch (err) {
      setError(err.message || 'Authentication failed. Please verify your credentials.');
    } finally {
      setLoading(false);
    }
  };

  const fillCredentials = (user, pass) => {
    setUsernameOrEmail(user);
    setPassword(pass);
    setError('');
  };

  const isPendingApprovalError = error && (error.toLowerCase().includes('pending') || error.toLowerCase().includes('approval'));

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-md-7 col-lg-5">
          <div className="card shadow-lg border-0 rounded-4 overflow-hidden">
            <div className="card-header bg-dark text-white text-center py-4">
              <i className="bi bi-shield-lock-fill fs-1 mb-2 d-inline-block text-primary"></i>
              <h4 className="fw-bold mb-0">Platform Authentication</h4>
              <p className="small text-white-50 mb-0">Sign in to your assessment account</p>
            </div>

            <div className="card-body p-4 p-md-5">
              {isExpired && (
                <div className="alert alert-warning small d-flex align-items-center gap-2" role="alert">
                  <i className="bi bi-exclamation-triangle-fill"></i>
                  <span>Your session has expired. Please sign in again.</span>
                </div>
              )}

              {error && (
                <div
                  className={`alert ${isPendingApprovalError ? 'alert-warning' : 'alert-danger'} small d-flex align-items-start gap-2 shadow-sm`}
                  role="alert"
                >
                  <i className={`bi ${isPendingApprovalError ? 'bi-hourglass-split fs-5 text-warning' : 'bi-x-circle-fill text-danger'}`}></i>
                  <div>
                    {isPendingApprovalError && <strong className="d-block mb-1">Authorization Notice:</strong>}
                    <span>{error}</span>
                  </div>
                </div>
              )}

              <form onSubmit={handleLogin}>
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Username or Email</label>
                  <div className="input-group">
                    <span className="input-group-text bg-light"><i className="bi bi-person"></i></span>
                    <input
                      type="text"
                      className="form-control"
                      value={usernameOrEmail}
                      onChange={(e) => setUsernameOrEmail(e.target.value)}
                      placeholder="e.g. superadmin, mit_admin, student1"
                      required
                    />
                  </div>
                </div>

                <div className="mb-4">
                  <label className="form-label small fw-semibold">Password</label>
                  <div className="input-group">
                    <span className="input-group-text bg-light"><i className="bi bi-key"></i></span>
                    <input
                      type="password"
                      className="form-control"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      placeholder="••••••••"
                      required
                    />
                  </div>
                </div>

                <button
                  type="submit"
                  className="btn btn-primary w-100 py-2 fw-semibold shadow-sm mb-3"
                  disabled={loading}
                >
                  {loading ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                      Authenticating...
                    </>
                  ) : (
                    'Sign In'
                  )}
                </button>
              </form>

              {/* Quick Login Badges for Instant Demo */}
              <div className="bg-light p-3 rounded-3 mb-3 border">
                <div className="small fw-semibold text-muted mb-2 text-center">
                  Quick Demo Accounts:
                </div>
                <div className="row g-2">
                  <div className="col-6">
                    <button
                      type="button"
                      className="btn btn-outline-danger btn-sm w-100 text-truncate"
                      onClick={() => fillCredentials('superadmin', 'Admin@123')}
                      title="Main Admin (Super Admin)"
                    >
                      <i className="bi bi-shield-fill me-1"></i> Main Admin
                    </button>
                  </div>
                  <div className="col-6">
                    <button
                      type="button"
                      className="btn btn-outline-warning text-dark btn-sm w-100 text-truncate"
                      onClick={() => fillCredentials('mit_admin', 'Admin@123')}
                      title="Approved Institute Admin (MIT-TECH)"
                    >
                      <i className="bi bi-building me-1"></i> Inst. Admin
                    </button>
                  </div>
                  <div className="col-6">
                    <button
                      type="button"
                      className="btn btn-outline-secondary btn-sm w-100 text-truncate"
                      onClick={() => fillCredentials('stanford_admin', 'Admin@123')}
                      title="Pending Institute Admin (Demonstrates pending approval block)"
                    >
                      <i className="bi bi-hourglass me-1"></i> Pending Admin
                    </button>
                  </div>
                  <div className="col-6">
                    <button
                      type="button"
                      className="btn btn-outline-primary btn-sm w-100 text-truncate"
                      onClick={() => fillCredentials('student1', 'Student@123')}
                      title="Candidate Student"
                    >
                      <i className="bi bi-person-fill me-1"></i> Student
                    </button>
                  </div>
                </div>
              </div>

              <div className="text-center small text-muted">
                Don't have an account yet?{' '}
                <Link to="/register" className="text-primary fw-semibold text-decoration-none">
                  Create Account / Register Institute
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
