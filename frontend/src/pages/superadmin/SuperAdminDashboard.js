import React, { useState, useEffect } from 'react';
import { superAdminApi } from '../../api/superAdminApi';

const SuperAdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [pendingInstitutes, setPendingInstitutes] = useState([]);
  const [allInstitutes, setAllInstitutes] = useState([]);
  const [activeTab, setActiveTab] = useState('pending'); // 'pending' or 'all'
  const [loading, setLoading] = useState(true);
  const [actionLoadingId, setActionLoadingId] = useState(null);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  // Reject modal state
  const [rejectModalOpen, setRejectModalOpen] = useState(false);
  const [selectedInstitute, setSelectedInstitute] = useState(null);
  const [rejectReason, setRejectReason] = useState('');

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [statsRes, pendingRes, allRes] = await Promise.all([
        superAdminApi.getStats(),
        superAdminApi.getPendingInstitutes(0, 50),
        superAdminApi.getInstitutes(null, 0, 50),
      ]);

      const statsPayload = statsRes?.data?.data || statsRes?.data || statsRes;
      if (statsPayload && typeof statsPayload === 'object') {
        setStats(statsPayload);
      }

      const pendingPayload =
        pendingRes?.data?.content ||
        pendingRes?.data?.data?.content ||
        pendingRes?.content ||
        (Array.isArray(pendingRes?.data) ? pendingRes.data : Array.isArray(pendingRes) ? pendingRes : []);
      setPendingInstitutes(pendingPayload || []);

      const allPayload =
        allRes?.data?.content ||
        allRes?.data?.data?.content ||
        allRes?.content ||
        (Array.isArray(allRes?.data) ? allRes.data : Array.isArray(allRes) ? allRes : []);
      setAllInstitutes(allPayload || []);
    } catch (err) {
      console.error('Failed to load super admin data', err);
      setError('Failed to fetch platform institutes. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleApprove = async (institute) => {
    setActionLoadingId(institute.id);
    setMessage(null);
    setError(null);
    try {
      const res = await superAdminApi.approveInstitute(institute.id);
      setMessage(res?.message || res?.data?.message || `Approved ${institute.name}! The admin can now log in.`);
      loadData();
    } catch (err) {
      console.error('Approval failed', err);
      setError(err?.message || err.response?.data?.message || 'Failed to approve institute.');
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
    setMessage(null);
    setError(null);
    try {
      const res = await superAdminApi.rejectInstitute(selectedInstitute.id, rejectReason);
      setMessage(res?.message || res?.data?.message || `Rejected ${selectedInstitute.name}.`);
      setRejectModalOpen(false);
      setSelectedInstitute(null);
      loadData();
    } catch (err) {
      console.error('Rejection failed', err);
      setError(err.response?.data?.message || 'Failed to reject institute.');
    } finally {
      setActionLoadingId(null);
    }
  };

  return (
    <div className="container py-4">
      {/* Header */}
      <div className="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
          <h2 className="fw-bold mb-1 d-flex align-items-center gap-2">
            <i className="bi bi-shield-lock-fill text-danger"></i> Main Super Admin Control Center
          </h2>
          <p className="text-muted mb-0">
            Sole authority to approve or deny Institute Admin registrations and supervise multi-institute access.
          </p>
        </div>
        <div>
          <button className="btn btn-outline-secondary btn-sm" onClick={loadData} disabled={loading}>
            <i className={`bi bi-arrow-clockwise me-1 ${loading ? 'spin' : ''}`}></i> Refresh
          </button>
        </div>
      </div>

      {/* Notifications */}
      {message && (
        <div className="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
          <i className="bi bi-check-circle-fill me-2"></i> {message}
          <button type="button" className="btn-close" onClick={() => setMessage(null)}></button>
        </div>
      )}

      {error && (
        <div className="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
          <i className="bi bi-exclamation-triangle-fill me-2"></i> {error}
          <button type="button" className="btn-close" onClick={() => setError(null)}></button>
        </div>
      )}

      {/* Stats Cards */}
      <div className="row g-3 mb-4">
        <div className="col-md-3">
          <div className="card shadow-sm border-0 border-start border-warning border-4 h-100">
            <div className="card-body">
              <span className="text-muted small text-uppercase fw-semibold">Pending Approvals</span>
              <h3 className="fw-bold mt-2 text-warning mb-0">
                {stats?.pendingInstitutes ?? 0}
              </h3>
              <small className="text-muted">Requires Main Admin action</small>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="card shadow-sm border-0 border-start border-success border-4 h-100">
            <div className="card-body">
              <span className="text-muted small text-uppercase fw-semibold">Approved Institutes</span>
              <h3 className="fw-bold mt-2 text-success mb-0">
                {stats?.approvedInstitutes ?? 0}
              </h3>
              <small className="text-muted">Active & running exams</small>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="card shadow-sm border-0 border-start border-primary border-4 h-100">
            <div className="card-body">
              <span className="text-muted small text-uppercase fw-semibold">Total Candidates</span>
              <h3 className="fw-bold mt-2 text-primary mb-0">
                {stats?.totalStudents ?? 0}
              </h3>
              <small className="text-muted">Students across institutes</small>
            </div>
          </div>
        </div>

        <div className="col-md-3">
          <div className="card shadow-sm border-0 border-start border-info border-4 h-100">
            <div className="card-body">
              <span className="text-muted small text-uppercase fw-semibold">Total Examinations</span>
              <h3 className="fw-bold mt-2 text-info mb-0">
                {stats?.totalExams ?? 0}
              </h3>
              <small className="text-muted">Created platform-wide</small>
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <ul className="nav nav-tabs mb-4">
        <li className="nav-item">
          <button
            className={`nav-link fw-semibold position-relative ${activeTab === 'pending' ? 'active text-primary' : 'text-muted'}`}
            onClick={() => setActiveTab('pending')}
          >
            <i className="bi bi-clock-history me-1"></i> Pending Registrations
            {pendingInstitutes.length > 0 && (
              <span className="badge bg-danger ms-2">{pendingInstitutes.length}</span>
            )}
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link fw-semibold ${activeTab === 'all' ? 'active text-primary' : 'text-muted'}`}
            onClick={() => setActiveTab('all')}
          >
            <i className="bi bi-buildings me-1"></i> All Institutes ({allInstitutes.length})
          </button>
        </li>
      </ul>

      {/* Pending Institutes Tab */}
      {activeTab === 'pending' && (
        <div className="card shadow-sm border-0">
          <div className="card-header bg-white py-3">
            <h5 className="mb-0 fw-bold d-flex align-items-center gap-2">
              <i className="bi bi-hourglass-split text-warning"></i>
              Institutes Awaiting Your Approval
            </h5>
            <small className="text-muted">
              These institute administrators have registered but cannot sign in until you grant them access.
            </small>
          </div>
          <div className="card-body p-0">
            {loading ? (
              <div className="text-center py-5">
                <div className="spinner-border text-primary" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              </div>
            ) : pendingInstitutes.length === 0 ? (
              <div className="text-center py-5 text-muted">
                <i className="bi bi-check2-circle display-4 text-success d-block mb-3"></i>
                <h5>No Pending Institute Requests</h5>
                <p className="small">All registered institutes have been reviewed.</p>
              </div>
            ) : (
              <div className="table-responsive">
                <table className="table table-hover align-middle mb-0">
                  <thead className="table-light">
                    <tr>
                      <th>Institute</th>
                      <th>Institute Code</th>
                      <th>Admin User</th>
                      <th>Contact Details</th>
                      <th>Registered On</th>
                      <th className="text-end">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pendingInstitutes.map((inst) => (
                      <tr key={inst.id}>
                        <td>
                          <div className="fw-bold">{inst.name}</div>
                          {inst.description && (
                            <small className="text-muted text-truncate d-inline-block" style={{ maxWidth: '280px' }}>
                              {inst.description}
                            </small>
                          )}
                        </td>
                        <td>
                          <span className="badge bg-secondary font-monospace px-2 py-1">
                            {inst.code}
                          </span>
                        </td>
                        <td>
                          <div className="fw-semibold">{inst.adminFullName || inst.adminUsername}</div>
                          <small className="text-muted">@{inst.adminUsername}</small>
                        </td>
                        <td>
                          <div className="small">
                            <i className="bi bi-envelope me-1 text-muted"></i> {inst.contactEmail || 'N/A'}
                          </div>
                          {inst.contactPhone && (
                            <div className="small text-muted">
                              <i className="bi bi-telephone me-1 text-muted"></i> {inst.contactPhone}
                            </div>
                          )}
                        </td>
                        <td className="small text-muted">
                          {inst.createdAt ? new Date(inst.createdAt).toLocaleDateString() : 'N/A'}
                        </td>
                        <td className="text-end">
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

      {/* All Institutes Tab */}
      {activeTab === 'all' && (
        <div className="card shadow-sm border-0">
          <div className="card-header bg-white py-3">
            <h5 className="mb-0 fw-bold">All Registered Institutes</h5>
            <small className="text-muted">Master list of institutes across the platform.</small>
          </div>
          <div className="card-body p-0">
            {allInstitutes.length === 0 ? (
              <div className="text-center py-5 text-muted">
                <i className="bi bi-buildings display-4 d-block mb-3"></i>
                <h5>No Institutes Found</h5>
              </div>
            ) : (
              <div className="table-responsive">
                <table className="table table-hover align-middle mb-0">
                  <thead className="table-light">
                    <tr>
                      <th>Institute</th>
                      <th>Code</th>
                      <th>Admin</th>
                      <th>Status</th>
                      <th>Candidates</th>
                      <th>Exams</th>
                      <th className="text-end">Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {allInstitutes.map((inst) => {
                      let statusBadge = <span className="badge bg-warning text-dark">PENDING</span>;
                      if (inst.status === 'APPROVED') {
                        statusBadge = <span className="badge bg-success">APPROVED</span>;
                      } else if (inst.status === 'REJECTED') {
                        statusBadge = <span className="badge bg-danger">REJECTED</span>;
                      }

                      return (
                        <tr key={inst.id}>
                          <td>
                            <div className="fw-bold">{inst.name}</div>
                            <small className="text-muted">{inst.contactEmail}</small>
                          </td>
                          <td>
                            <span className="badge bg-secondary font-monospace">{inst.code}</span>
                          </td>
                          <td>
                            <div className="fw-semibold">{inst.adminFullName || inst.adminUsername || 'N/A'}</div>
                            <small className="text-muted">@{inst.adminUsername}</small>
                          </td>
                          <td>
                            {statusBadge}
                            {inst.status === 'REJECTED' && inst.rejectionReason && (
                              <div className="small text-danger mt-1" style={{ maxWidth: '200px' }}>
                                Reason: {inst.rejectionReason}
                              </div>
                            )}
                          </td>
                          <td>
                            <span className="badge bg-light text-dark border">
                              <i className="bi bi-people me-1"></i> {inst.studentCount || 0}
                            </span>
                          </td>
                          <td>
                            <span className="badge bg-light text-dark border">
                              <i className="bi bi-journal-text me-1"></i> {inst.examCount || 0}
                            </span>
                          </td>
                          <td className="text-end">
                            {inst.status === 'PENDING' && (
                              <button
                                className="btn btn-sm btn-success"
                                onClick={() => handleApprove(inst)}
                                disabled={actionLoadingId === inst.id}
                              >
                                Approve
                              </button>
                            )}
                            {inst.status === 'APPROVED' && (
                              <button
                                className="btn btn-sm btn-outline-danger"
                                onClick={() => openRejectModal(inst)}
                                disabled={actionLoadingId === inst.id}
                              >
                                Revoke Access
                              </button>
                            )}
                            {inst.status === 'REJECTED' && (
                              <button
                                className="btn btn-sm btn-outline-success"
                                onClick={() => handleApprove(inst)}
                                disabled={actionLoadingId === inst.id}
                              >
                                Re-Approve
                              </button>
                            )}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Reject Reason Modal */}
      {rejectModalOpen && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content shadow">
              <div className="modal-header">
                <h5 className="modal-title fw-bold text-danger">
                  <i className="bi bi-x-octagon-fill me-2"></i> Reject Institute Registration
                </h5>
                <button type="button" className="btn-close" onClick={() => setRejectModalOpen(false)}></button>
              </div>
              <div className="modal-body">
                <p>
                  Are you sure you want to deny registration for{' '}
                  <strong>{selectedInstitute?.name}</strong>?
                </p>
                <div className="mb-3">
                  <label className="form-label fw-semibold">Reason for Denial (Optional)</label>
                  <textarea
                    className="form-control"
                    rows="3"
                    placeholder="e.g. Incomplete credentials, unauthorized organization domain..."
                    value={rejectReason}
                    onChange={(e) => setRejectReason(e.target.value)}
                  ></textarea>
                  <small className="text-muted">
                    This reason will be displayed to the institute admin if they attempt to log in.
                  </small>
                </div>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setRejectModalOpen(false)}
                >
                  Cancel
                </button>
                <button
                  type="button"
                  className="btn btn-danger fw-semibold"
                  onClick={handleConfirmReject}
                  disabled={actionLoadingId === selectedInstitute?.id}
                >
                  {actionLoadingId === selectedInstitute?.id ? 'Rejecting...' : 'Confirm Rejection'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SuperAdminDashboard;
