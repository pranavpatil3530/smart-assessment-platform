import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { analyticsApi } from '../../api/analyticsApi';

const AdminDashboard = () => {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchOverview();
  }, []);

  const fetchOverview = async () => {
    setLoading(true);
    try {
      const response = await analyticsApi.getOverview();
      if (response.success) {
        setAnalytics(response.data);
      }
    } catch (err) {
      setError(err.message || 'Failed to load analytics.');
    } finally {
      setLoading(false);
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
          <h2 className="fw-bold mb-1">Administrative Overview</h2>
          <p className="text-muted mb-0">Platform health, assessment stats and candidate performance metrics</p>
        </div>
        <div className="d-flex gap-2">
          <Link to="/admin/exams" className="btn btn-primary d-flex align-items-center gap-2">
            <i className="bi bi-plus-circle"></i> Manage & Create Exams
          </Link>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

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
    </div>
  );
};

export default AdminDashboard;
