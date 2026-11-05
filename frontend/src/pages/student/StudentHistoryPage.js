import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { attemptApi } from '../../api/attemptApi';
import Pagination from '../../components/Pagination';

const StudentHistoryPage = () => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  const fetchHistory = useCallback(async () => {
    setLoading(true);
    try {
      const res = await attemptApi.getMyHistory(page, pageSize);
      if (res.success) {
        setHistory(res.data.content);
        setTotalPages(res.data.totalPages);
        setTotalElements(res.data.totalElements);
      }
    } catch (err) {
      setError(err.message || 'Failed to fetch examination history.');
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    fetchHistory();
  }, [fetchHistory]);

  if (loading) {
    return (
      <div className="container py-5 text-center">
        <div className="spinner-border text-primary" role="status"></div>
      </div>
    );
  }

  return (
    <div className="container py-4">
      {/* Header */}
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
        <div>
          <h2 className="fw-bold mb-1">My Assessment History</h2>
          <p className="text-muted mb-0">Track your past attempts, test scores, and performance analytics</p>
        </div>
        <Link to="/dashboard" className="btn btn-outline-primary">
          <i className="bi bi-grid me-1"></i> Browse Catalog
        </Link>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {/* Table */}
      <div className="card shadow-sm border-0 rounded-3">
        <div className="card-header bg-white py-3 d-flex justify-content-between align-items-center">
          <h5 className="fw-bold mb-0">Completed & Finalized Attempts</h5>
          <span className="badge bg-secondary-subtle text-secondary">{totalElements} Total</span>
        </div>

        <div className="card-body p-0">
          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-light text-muted small text-uppercase">
                <tr>
                  <th className="ps-4">Attempt #</th>
                  <th>Assessment Title</th>
                  <th>Category</th>
                  <th>Score</th>
                  <th>Percentage</th>
                  <th>Status</th>
                  <th>Outcome</th>
                  <th>Time Taken</th>
                  <th className="pe-4 text-end">Action</th>
                </tr>
              </thead>
              <tbody>
                {history.length > 0 ? (
                  history.map((att) => {
                    const mins = Math.floor((att.timeTakenSeconds || 0) / 60);
                    const secs = (att.timeTakenSeconds || 0) % 60;

                    return (
                      <tr key={att.attemptId}>
                        <td className="ps-4 fw-semibold text-primary">#{att.attemptId}</td>
                        <td className="fw-medium text-dark">{att.examTitle}</td>
                        <td>
                          <span className="badge bg-light text-primary border">{att.categoryName}</span>
                        </td>
                        <td>
                          <span className="fw-bold">{att.score}</span> / {att.totalMarks}
                        </td>
                        <td>
                          <div className="d-flex align-items-center gap-2">
                            <div className="progress flex-grow-1" style={{ height: '6px', width: '60px' }}>
                              <div
                                className={`progress-bar ${att.passed ? 'bg-success' : 'bg-danger'}`}
                                style={{ width: `${Math.min(100, att.percentage)}%` }}
                              ></div>
                            </div>
                            <span className="small fw-semibold">{att.percentage}%</span>
                          </div>
                        </td>
                        <td>
                          <span className="badge bg-light text-dark border small">{att.status}</span>
                        </td>
                        <td>
                          {att.passed ? (
                            <span className="badge bg-success">PASSED</span>
                          ) : (
                            <span className="badge bg-danger">FAILED</span>
                          )}
                        </td>
                        <td className="small text-muted">{mins}m {secs}s</td>
                        <td className="pe-4 text-end">
                          <Link
                            to={`/exam/result/${att.attemptId}`}
                            className="btn btn-outline-primary btn-sm"
                          >
                            <i className="bi bi-file-earmark-text me-1"></i> Review Report
                          </Link>
                        </td>
                      </tr>
                    );
                  })
                ) : (
                  <tr>
                    <td colSpan="9" className="text-center py-5 text-muted">
                      You haven't completed any assessments yet. Visit the catalog to take your first test!
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <Pagination
        currentPage={page}
        totalPages={totalPages}
        totalElements={totalElements}
        pageSize={pageSize}
        onPageChange={(p) => setPage(p)}
      />
    </div>
  );
};

export default StudentHistoryPage;
