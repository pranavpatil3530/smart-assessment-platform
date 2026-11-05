import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../../api/authApi';

const RegisterPage = () => {
  const [roleType, setRoleType] = useState('STUDENT'); // 'STUDENT' or 'INSTITUTE_ADMIN'
  const [formData, setFormData] = useState({
    fullName: '',
    username: '',
    email: '',
    password: '',
    instituteName: '',
    instituteCode: '',
    contactPhone: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isPendingNotice, setIsPendingNotice] = useState(false);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setIsPendingNotice(false);
    setLoading(true);

    try {
      const payload = {
        fullName: formData.fullName,
        username: formData.username,
        email: formData.email,
        password: formData.password,
        role: roleType === 'INSTITUTE_ADMIN' ? 'ROLE_INSTITUTE_ADMIN' : 'ROLE_STUDENT',
        instituteName: roleType === 'INSTITUTE_ADMIN' ? formData.instituteName : undefined,
        instituteCode: formData.instituteCode ? formData.instituteCode.trim().toUpperCase() : undefined,
        contactPhone: formData.contactPhone ? formData.contactPhone.trim() : undefined,
      };

      const response = await authApi.register(payload);
      if (response.success) {
        if (roleType === 'INSTITUTE_ADMIN') {
          setIsPendingNotice(true);
          setSuccess(
            'Your Institute Admin registration has been submitted successfully! It is now pending approval by the Main Admin. You will be able to sign in once the Main Admin approves your request.'
          );
        } else {
          setSuccess('Registration successful! Redirecting to sign in...');
          setTimeout(() => {
            navigate('/login');
          }, 1500);
        }
      }
    } catch (err) {
      setError(err.message || 'Registration failed. Please check your information.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-md-8 col-lg-6">
          <div className="card shadow-lg border-0 rounded-4 overflow-hidden">
            <div className="card-header bg-dark text-white text-center py-4">
              <i className="bi bi-person-plus-fill fs-1 mb-2 d-inline-block text-primary"></i>
              <h4 className="fw-bold mb-0">Create Platform Account</h4>
              <p className="small text-white-50 mb-0">Join as a Candidate Student or Register your Institute</p>
            </div>

            <div className="card-body p-4 p-md-5">
              {/* Role Selection Tabs */}
              <div className="btn-group w-100 mb-4" role="group">
                <button
                  type="button"
                  className={`btn py-2 fw-semibold ${roleType === 'STUDENT' ? 'btn-primary' : 'btn-outline-secondary'}`}
                  onClick={() => {
                    setRoleType('STUDENT');
                    setError('');
                    setSuccess('');
                  }}
                >
                  <i className="bi bi-mortarboard-fill me-2"></i> Student Candidate
                </button>
                <button
                  type="button"
                  className={`btn py-2 fw-semibold ${roleType === 'INSTITUTE_ADMIN' ? 'btn-warning text-dark' : 'btn-outline-secondary'}`}
                  onClick={() => {
                    setRoleType('INSTITUTE_ADMIN');
                    setError('');
                    setSuccess('');
                  }}
                >
                  <i className="bi bi-building me-2"></i> Institute Admin
                </button>
              </div>

              {/* Information Banner for Institute Admin */}
              {roleType === 'INSTITUTE_ADMIN' && !isPendingNotice && (
                <div className="alert alert-warning small d-flex align-items-start gap-2 mb-4" role="alert">
                  <i className="bi bi-shield-exclamation fs-5 flex-shrink-0 text-warning"></i>
                  <div>
                    <strong>Main Admin Approval Required:</strong>
                    <div className="mt-1">
                      Institute registrations are submitted for authorization. Once approved by the Main Super Admin,
                      you will be able to sign in, create exams, and manage candidates.
                    </div>
                  </div>
                </div>
              )}

              {error && (
                <div className="alert alert-danger small d-flex align-items-center gap-2 mb-4" role="alert">
                  <i className="bi bi-x-circle-fill"></i>
                  <span>{error}</span>
                </div>
              )}

              {success && (
                <div className={`alert ${isPendingNotice ? 'alert-info' : 'alert-success'} small mb-4`} role="alert">
                  <div className="d-flex align-items-start gap-2">
                    <i className={`bi ${isPendingNotice ? 'bi-hourglass-top fs-4 text-primary' : 'bi-check-circle-fill fs-5'}`}></i>
                    <div>
                      {isPendingNotice && <h6 className="fw-bold mb-1">Registration Under Review</h6>}
                      <p className="mb-2">{success}</p>
                      {isPendingNotice && (
                        <Link to="/login" className="btn btn-sm btn-primary mt-1">
                          Go to Sign In
                        </Link>
                      )}
                    </div>
                  </div>
                </div>
              )}

              {!isPendingNotice && (
                <form onSubmit={handleRegister}>
                  <div className="mb-3">
                    <label className="form-label small fw-semibold">
                      {roleType === 'INSTITUTE_ADMIN' ? 'Administrator Full Name' : 'Candidate Full Name'}
                    </label>
                    <input
                      type="text"
                      name="fullName"
                      className="form-control"
                      placeholder="e.g. Dr. Arthur Miller"
                      value={formData.fullName}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  <div className="row g-3 mb-3">
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Username</label>
                      <input
                        type="text"
                        name="username"
                        className="form-control"
                        placeholder="e.g. amiller"
                        value={formData.username}
                        onChange={handleChange}
                        required
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Email Address</label>
                      <input
                        type="email"
                        name="email"
                        className="form-control"
                        placeholder="admin@college.edu"
                        value={formData.email}
                        onChange={handleChange}
                        required
                      />
                    </div>
                  </div>

                  {/* Institute Admin Specific Fields */}
                  {roleType === 'INSTITUTE_ADMIN' && (
                    <>
                      <div className="mb-3">
                        <label className="form-label small fw-semibold">Institute / College Name</label>
                        <input
                          type="text"
                          name="instituteName"
                          className="form-control"
                          placeholder="e.g. Boston Institute of Technology"
                          value={formData.instituteName}
                          onChange={handleChange}
                          required
                        />
                      </div>

                      <div className="row g-3 mb-3">
                        <div className="col-md-6">
                          <label className="form-label small fw-semibold">
                            Desired Institute Code <span className="text-muted">(Optional)</span>
                          </label>
                          <input
                            type="text"
                            name="instituteCode"
                            className="form-control font-monospace"
                            placeholder="e.g. BIT-TECH"
                            value={formData.instituteCode}
                            onChange={handleChange}
                          />
                          <small className="text-muted" style={{ fontSize: '0.75rem' }}>
                            Leave blank to auto-generate
                          </small>
                        </div>
                        <div className="col-md-6">
                          <label className="form-label small fw-semibold">Contact Phone</label>
                          <input
                            type="tel"
                            name="contactPhone"
                            className="form-control"
                            placeholder="e.g. +1 555-0199"
                            value={formData.contactPhone}
                            onChange={handleChange}
                          />
                        </div>
                      </div>
                    </>
                  )}

                  {/* Student Candidate Specific Fields */}
                  {roleType === 'STUDENT' && (
                    <div className="mb-3">
                      <label className="form-label small fw-semibold">
                        Institute Code <span className="text-muted">(Optional - e.g. MIT-TECH)</span>
                      </label>
                      <input
                        type="text"
                        name="instituteCode"
                        className="form-control font-monospace"
                        placeholder="e.g. MIT-TECH"
                        value={formData.instituteCode}
                        onChange={handleChange}
                      />
                      <small className="text-muted" style={{ fontSize: '0.75rem' }}>
                        Enter the code provided by your institute to access college exams.
                      </small>
                    </div>
                  )}

                  <div className="mb-4">
                    <label className="form-label small fw-semibold">Password</label>
                    <input
                      type="password"
                      name="password"
                      className="form-control"
                      placeholder="At least 6 characters"
                      value={formData.password}
                      onChange={handleChange}
                      minLength={6}
                      required
                    />
                  </div>

                  <button
                    type="submit"
                    className={`btn w-100 py-2 fw-semibold shadow-sm mb-3 ${
                      roleType === 'INSTITUTE_ADMIN' ? 'btn-warning text-dark' : 'btn-primary'
                    }`}
                    disabled={loading}
                  >
                    {loading ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                        Submitting...
                      </>
                    ) : roleType === 'INSTITUTE_ADMIN' ? (
                      'Submit Institute Registration for Approval'
                    ) : (
                      'Register Student Account'
                    )}
                  </button>
                </form>
              )}

              <div className="text-center small text-muted">
                Already registered?{' '}
                <Link to="/login" className="text-primary fw-semibold text-decoration-none">
                  Sign In
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
