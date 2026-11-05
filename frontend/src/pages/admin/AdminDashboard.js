import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { analyticsApi } from '../../api/analyticsApi';
import { superAdminApi } from '../../api/superAdminApi';
import { useAuth } from '../../context/AuthContext';

const AdminDashboard = () => {
  const { isSuperAdmin, isInstituteAdmin, user } = useAuth();
  const [analytics, setAnalytics] = useState(null);
  const [pendingInstitutes, setPendingInstitutes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoadingId, setActionLoadingId] = useState(null);
  const [actionMessage, setActionMessage] = useState(null);
  const [error, setError] = useState('');

  // Reject modal state
  const [rejectModalOpen, setRejectModalOpen] = useState(false);
  const [selectedInstitute, setSelectedInstitute] = useState(null);
  const [rejectReason, setRejectReason] = useState('');

  useEffect(() => {
    fetchOverview();
  }, [isSuperAdmin]);

  const fetchOverview = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await analyticsApi.getOverview();
      if (response?.data) {
        setAnalytics(response.data);
      } else if (response?.totalExams !== undefined) {
        setAnalytics(response);
      }

      if (isSuperAdmin) {
        try {
          const pendingRes = await superAdminApi.getPendingInstitutes(0, 50);
          const pendingList =
            pendingRes?.data?.content ||
            pendingRes?.data?.data?.content ||
            pendingRes?.content ||
            (Array.isArray(pendingRes?.data) ? pendingRes.data : Array.isArray(pendingRes) ? pendingRes : []);
          setPendingInstitutes(pendingList || []);
        } catch (err) {
          console.error('Failed to load pending institutes for admin overview', err);
        }
      }
    } catch (err) {
      setError(err.message || 'Failed to load analytics.');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (institute) => {
    setActionLoadingId(institute.id);
    setActionMessage(null);
    try {
      const res = await superAdminApi.approveInstitute(institute.id);
      setActionMessage(res?.message || res?.data?.message || `Approved ${institute.name}! The admin can now log in.`);
      fetchOverview();
    } catch (err) {
      setError(err?.message || 'Failed to approve institute.');
    } finally {
      setActionLoadingId(null);
    }
  };

  const openRejectModal = (institute) => {
    setSelectedInstitute(institute);
    setRejectReason('');
    setRejectModalOpen(true);
  };

  const handleConfirmReject = async () => {
    if (!selectedInstitute) return;
    setActionLoadingId(selectedInstitute.id);
    setActionMessage(null);
    try {
      const res = await superAdminApi.rejectInstitute(selectedInstitute.id, rejectReason);
      setActionMessage(res?.message || res?.data?.message || `Rejected ${selectedInstitute.name}.`);
      setRejectModalOpen(false);
      setSelectedInstitute(null);
      fetchOverview();
    } catch (err) {
      setError(err?.message || 'Failed to reject institute.');
    } finally {
      setActionLoadingId(null);
    }
  };

  if (loading) {
    return (
      <div className="container py-5 text-center">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading analytics...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="container py-4">
      {/* Header */}
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
        <div>
          <h2 className="fw-bold mb-1 d-flex align-items-center gap-2">
            <i className="bi bi-speedometer2 text-primary"></i> Administrative Overview
          </h2>
          <p className="text-muted mb-0">Platform health, assessment stats and candidate performance metrics</p>
        </div>
        <div className="d-flex flex-wrap gap-2">
          {isSuperAdmin && (
            <Link to="/super-admin/dashboard" className="btn btn-danger d-flex align-items-center gap-2 shadow-sm">
              <i className="bi bi-shield-check"></i> Super Admin Center
              {pendingInstitutes.length > 0 && (
                <span className="badge bg-white text-danger fw-bold ms-1">{pendingInstitutes.length}</span>
              )}
            </Link>
          )}
          {isInstituteAdmin && (
            <Link to="/institute/candidates" className="btn btn-outline-primary d-flex align-items-center gap-2">
              <i className="bi bi-people-fill"></i> Candidate Roster
            </Link>
          )}
          <Link to="/admin/exams" className="btn btn-primary d-flex align-items-center gap-2">
            <i className="bi bi-plus-circle"></i> Manage & Create Exams
          </Link>
        </div>
      </div>

      {actionMessage && (
        <div className="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
          <i className="bi bi-check-circle-fill me-2"></i> {actionMessage}
          <button type="button" className="btn-close" onClick={() => setActionMessage(null)}></button>
        </div>
      )}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* Institute Admin College Info Banner */}
      {isInstituteAdmin && user?.instituteName && (
        <div className="alert alert-primary bg-primary-subtle border-primary text-primary d-flex align-items-center justify-content-between p-3 rounded-3 mb-4">
          <div>
            <h6 className="fw-bold mb-1">
              <i className="bi bi-buildings-fill me-2"></i> Institute: {user.instituteName}
            </h6>
            <small className="text-muted">
              Institute Code: <strong className="font-monospace text-dark">{user.instituteCode || 'N/A'}</strong> | Only candidates enrolled with your code can take assigned exams.
            </small>
          </div>
          <Link to="/institute/candidates" className="btn btn-sm btn-primary">
            View Enrolled Candidates
          </Link>
        </div>
      )}

      {/* Super Admin: Pending Institutes Highlight & Direct Review */}
      {isSuperAdmin && (
        <div className="card shadow-sm border-0 mb-4 border-start border-warning border-4">
          <div className="card-header bg-white py-3 d-flex flex-wrap justify-content-between align-items-center gap-2">
            <div>
              <h5 className="mb-0 fw-bold d-flex align-items-center gap-2 text-dark">
                <i className="bi bi-hourglass-split text-warning"></i>
                Pending Institute Registrations Awaiting Approval
                {pendingInstitutes.length > 0 && (
                  <span className="badge bg-danger ms-2">{pendingInstitutes.length} Action Needed</span>
                )}
              </h5>
              <small className="text-muted">
                These institute administrators have registered but cannot sign in until approved by Main Admin.
              </small>
            </div>
            <Link to="/super-admin/dashboard" className="btn btn-sm btn-outline-danger">
              Full Super Admin Center <i className="bi bi-arrow-right ms-1"></i>
            </Link>
          </div>
          <div className="card-body p-0">
            {pendingInstitutes.length === 0 ? (
              <div className="text-center py-4 text-muted">
                <i className="bi bi-check2-circle text-success fs-3 d-block mb-1"></i>
                <span className="small fw-semibold">No pending institute registrations. All institutes are reviewed!</span>
              </div>
            ) : (
              <div className="table-responsive">
                <table className="table table-hover align-middle mb-0">
                  <thead className="table-light text-muted small text-uppercase">
                    <tr>
                      <th className="ps-4">Institute Name</th>
                      <th>Code</th>
                      <th>Admin User</th>
                      <th>Contact</th>
                      <th>Registered On</th>
                      <th className="text-end pe-4">Quick Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pendingInstitutes.map((inst) => (
                      <tr key={inst.id}>
                        <td className="ps-4">
                          <div className="fw-bold">{inst.name}</div>
                          {inst.description && (
                            <small className="text-muted text-truncate d-inline-block" style={{ maxWidth: '240px' }}>
                              {inst.description}
                            </small>
                          )}
                        </td>
                        <td>
                          <span className="badge bg-secondary font-monospace px-2 py-1">{inst.code}</span>
                        </td>
                        <td>
                          <div className="fw-semibold">{inst.adminFullName || inst.adminUsername}</div>
                          <small className="text-muted">@{inst.adminUsername}</small>
                        </td>
                        <td className="small">
                          <div>
                            <i className="bi bi-envelope me-1 text-muted"></i> {inst.contactEmail || 'N/A'}
                          </div>
                          {inst.contactPhone && (
                            <div className="text-muted">
                              <i className="bi bi-telephone me-1 text-muted"></i> {inst.contactPhone}
                            </div>
                          )}
                        </td>
                        <td className="small text-muted">
                          {inst.createdAt ? new Date(inst.createdAt).toLocaleDateString() : 'N/A'}
                        </td>
                        <td className="text-end pe-4">
                          <div className="btn-group">
                            <button
                              className="btn btn-sm btn-success fw-semibold"
                              onClick={() => handleApprove(inst)}
                              disabled={actionLoadingId === inst.id}
                            >
                              {actionLoadingId === inst.id ? (
                                <span className="spinner-border spinner-border-sm me-1" role="status"></span>
                              ) : (
                                <i className="bi bi-check-lg me-1"></i>
                              )}
                              Approve
                            </button>
                            <button
                              className="btn btn-sm btn-outline-danger"
                              onClick={() => openRejectModal(inst)}
                              disabled={actionLoadingId === inst.id}
                            >
                              <i className="bi bi-x-lg me-1"></i> Deny
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* KPI Cards Grid */}
      <div className="row g-3 mb-4">
        <div className="col-sm-6 col-lg-3">
          <div className="kpi-card p-4">
            <div className="d-flex justify-content-between align-items-center mb-2">
              <span className="text-muted small fw-semibold text-uppercase">Total Examinations</span>
              <div className="p-2 bg-primary-subtle text-primary rounded-3">
                <i className="bi bi-journal-text fs-5"></i>
              </div>
            </div>
            <h3 className="fw-bold mb-0">{analytics?.totalExams || 0}</h3>
            <span className="badge bg-success-subtle text-success small mt-2">
              {analytics?.totalPublishedExams || 0} Published & Active
            </span>
          </div>
        </div>

        <div className="col-sm-6 col-lg-3">
          <div className="kpi-card p-4">
            <div className="d-flex justify-content-between align-items-center mb-2">
              <span className="text-muted small fw-semibold text-uppercase">Registered Students</span>
              <div className="p-2 bg-info-subtle text-info rounded-3">
                <i className="bi bi-people fs-5"></i>
              </div>
            </div>
            <h3 className="fw-bold mb-0">{analytics?.totalStudents || 0}</h3>
            <span className="text-muted small mt-2 d-block">Active candidates</span>
          </div>
        </div>

        <div className="col-sm-6 col-lg-3">
          <div className="kpi-card p-4">
            <div className="d-flex justify-content-between align-items-center mb-2">
              <span className="text-muted small fw-semibold text-uppercase">Total Attempts</span>
              <div className="p-2 bg-warning-subtle text-warning rounded-3">
                <i className="bi bi-check2-circle fs-5"></i>
              </div>
            </div>
            <h3 className="fw-bold mb-0">{analytics?.totalAttempts || 0}</h3>
            <span className="text-muted small mt-2 d-block">
              {analytics?.passedAttempts || 0} Successful passes
            </span>
          </div>
        </div>

        <div className="col-sm-6 col-lg-3">
          <div className="kpi-card p-4">
            <div className="d-flex justify-content-between align-items-center mb-2">
              <span className="text-muted small fw-semibold text-uppercase">Overall Pass Rate</span>
              <div className="p-2 bg-success-subtle text-success rounded-3">
                <i className="bi bi-graph-up-arrow fs-5"></i>
              </div>
            </div>
            <h3 className="fw-bold mb-0">{analytics?.overallPassRate || 0}%</h3>
            <span className="text-muted small mt-2 d-block">
              Avg Score: {analytics?.overallAverageScore || 0}%
            </span>
          </div>
        </div>
      </div>

      {/* Recent Attempts Table */}
      <div className="card shadow-sm border-0 rounded-3">
        <div className="card-header bg-white py-3 d-flex justify-content-between align-items-center">
          <h5 className="fw-bold mb-0">Recent Candidate Assessments</h5>
          <button className="btn btn-sm btn-outline-secondary" onClick={fetchOverview}>
            <i className="bi bi-arrow-clockwise me-1"></i> Refresh
          </button>
        </div>

        <div className="card-body p-0">
          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-light text-muted small text-uppercase">
                <tr>
                  <th className="ps-4">Attempt ID</th>
                  <th>Examination Title</th>
                  <th>Category</th>
                  <th>Score / Marks</th>
                  <th>Percentage</th>
                  <th>Status</th>
                  <th>Outcome</th>
                  <th className="pe-4">Date</th>
                </tr>
              </thead>
              <tbody>
                {analytics?.recentAttempts?.length > 0 ? (
                  analytics.recentAttempts.map((att) => (
                    <tr key={att.attemptId}>
                      <td className="ps-4 fw-semibold text-primary">#{att.attemptId}</td>
                      <td className="fw-medium">{att.examTitle}</td>
                      <td>
                        <span className="badge bg-light text-dark border">{att.categoryName}</span>
                      </td>
                      <td>
                        <span className="fw-bold">{att.score}</span> / {att.totalMarks}
                      </td>
                      <td>
                        <div className="d-flex align-items-center gap-2">
                          <div className="progress flex-grow-1" style={{ height: '6px', width: '60px' }}>
                            <div
                              className={`progress-bar ${att.passed ? 'bg-success' : 'bg-danger'}`}
                              role="progressbar"
                              style={{ width: `${Math.min(100, att.percentage)}%` }}
                            ></div>
                          </div>
                          <span className="small fw-semibold">{att.percentage}%</span>
                        </div>
                      </td>
                      <td>
                        <span className="badge bg-secondary-subtle text-secondary small">
                          {att.status}
                        </span>
                      </td>
                      <td>
                        {att.passed ? (
                          <span className="badge bg-success">PASSED</span>
                        ) : (
                          <span className="badge bg-danger">FAILED</span>
                        )}
                      </td>
                      <td className="pe-4 text-muted small">
                        {att.submittedTime ? new Date(att.submittedTime).toLocaleString() : 'In Progress'}
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="8" className="text-center py-4 text-muted">
                      No assessment attempts recorded yet.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
      {/* Reject Modal */}
      {rejectModalOpen && selectedInstitute && (
        <div
          className="modal fade show d-block"
          tabIndex="-1"
          style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}
          role="dialog"
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content border-0 shadow">
              <div className="modal-header bg-danger text-white">
                <h5 className="modal-title fw-bold">
                  <i className="bi bi-x-circle me-2"></i> Deny Institute Registration
                </h5>
                <button
                  type="button"
                  className="btn-close btn-close-white"
                  onClick={() => setRejectModalOpen(false)}
                ></button>
              </div>
              <div className="modal-body p-4">
                <p>
                  Are you sure you want to deny registration for{' '}
                  <strong>{selectedInstitute.name}</strong> (Code: <code>{selectedInstitute.code}</code>)?
                </p>
                <div className="mb-3">
                  <label className="form-label fw-semibold">Reason for Denial (Optional)</label>
                  <textarea
                    className="form-control"
                    rows="3"
                    placeholder="Provide context or feedback (e.g., missing accreditation documents)..."
                    value={rejectReason}
                    onChange={(e) => setRejectReason(e.target.value)}
                  ></textarea>
                </div>
              </div>
              <div className="modal-footer bg-light">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setRejectModalOpen(false)}
                  disabled={actionLoadingId === selectedInstitute.id}
                >
                  Cancel
                </button>
                <button
                  type="button"
                  className="btn btn-danger fw-semibold"
                  onClick={handleConfirmReject}
                  disabled={actionLoadingId === selectedInstitute.id}
                >
                  {actionLoadingId === selectedInstitute.id ? (
                    <span className="spinner-border spinner-border-sm me-1" role="status"></span>
                  ) : (
                    <i className="bi bi-x-lg me-1"></i>
                  )}
                  Confirm Denial
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;
