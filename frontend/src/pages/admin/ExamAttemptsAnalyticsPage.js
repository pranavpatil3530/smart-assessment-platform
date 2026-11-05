import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { analyticsApi } from '../../api/analyticsApi';
import { examApi } from '../../api/examApi';
import Pagination from '../../components/Pagination';

const ExamAttemptsAnalyticsPage = () => {
  const { examId } = useParams();
  const [exam, setExam] = useState(null);
  const [attempts, setAttempts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  const fetchExamAndAttempts = useCallback(async () => {
    setLoading(true);
    try {
      const [examRes, attemptsRes] = await Promise.all([
        examApi.getExamDetails(examId),
        analyticsApi.getExamAttempts(examId, page, pageSize),
      ]);

      if (examRes.success) setExam(examRes.data);
      if (attemptsRes.success) {
        setAttempts(attemptsRes.data.content);
        setTotalPages(attemptsRes.data.totalPages);
        setTotalElements(attemptsRes.data.totalElements);
      }
    } catch (err) {
      setError(err.message || 'Failed to fetch candidate assessment data.');
    } finally {
      setLoading(false);
    }
  }, [examId, page]);

  useEffect(() => {
    fetchExamAndAttempts();
  }, [fetchExamAndAttempts]);

  const passedCount = attempts.filter((a) => a.passed).length;
  const passRate = attempts.length > 0 ? Math.round((passedCount / attempts.length) * 100) : 0;

  if (loading) {
    return (
      <div className="container py-5 text-center">
        <div className="spinner-border text-primary" role="status"></div>
      </div>
    );
  }

  return (
    <div className="container py-4">
      {/* Breadcrumb */}
      <nav aria-label="breadcrumb" className="mb-3">
        <ol className="breadcrumb">
          <li className="breadcrumb-item">
            <Link to="/admin/exams">Examinations</Link>
          </li>
          <li className="breadcrumb-item active" aria-current="page">
            Candidate Submissions ({exam?.title})
          </li>
        </ol>
      </nav>

      {/* Header Overview Card */}
      <div className="card shadow-sm border-0 mb-4 p-4 rounded-3 bg-white">
        <div className="row g-3 align-items-center">
          <div className="col-md-7">
            <span className="badge bg-primary-subtle text-primary mb-2">{exam?.categoryName}</span>
            <h3 className="fw-bold mb-1">{exam?.title}</h3>
            <p className="text-muted mb-0 small">
              Total Questions: {exam?.totalQuestions} | Max Marks: {exam?.totalMarks} | Passing Cutoff: {exam?.passingMarks}
            </p>
          </div>
          <div className="col-md-5 d-flex justify-content-md-end gap-3">
            <div className="text-center p-3 bg-light rounded-3">
              <div className="small text-muted text-uppercase fw-semibold">Total Submissions</div>
              <h4 className="fw-bold text-dark mb-0">{totalElements}</h4>
            </div>
            <div className="text-center p-3 bg-light rounded-3">
              <div className="small text-muted text-uppercase fw-semibold">Sample Pass Rate</div>
              <h4 className="fw-bold text-success mb-0">{passRate}%</h4>
            </div>
          </div>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {/* Attempts Table */}
      <div className="card shadow-sm border-0 rounded-3">
        <div className="card-header bg-white py-3 d-flex justify-content-between align-items-center">
          <h5 className="fw-bold mb-0">Candidate Assessment History</h5>
          <span className="badge bg-secondary-subtle text-secondary">{totalElements} Records</span>
        </div>

        <div className="card-body p-0">
          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-light text-muted small text-uppercase">
                <tr>
                  <th className="ps-4">Attempt ID</th>
                  <th>Score Obtained</th>
                  <th>Percentage</th>
                  <th>Status</th>
                  <th>Result</th>
                  <th>Time Taken</th>
                  <th className="pe-4 text-end">Submitted At</th>
                </tr>
              </thead>
              <tbody>
                {attempts.length > 0 ? (
                  attempts.map((att) => {
                    const mins = Math.floor((att.timeTakenSeconds || 0) / 60);
                    const secs = (att.timeTakenSeconds || 0) % 60;

                    return (
                      <tr key={att.attemptId}>
                        <td className="ps-4 fw-semibold text-primary">#{att.attemptId}</td>
                        <td>
                          <span className="fw-bold fs-6">{att.score}</span> / {att.totalMarks}
                        </td>
                        <td>
                          <div className="d-flex align-items-center gap-2">
                            <div className="progress flex-grow-1" style={{ height: '6px', width: '70px' }}>
                              <div
                                className={`progress-bar ${att.passed ? 'bg-success' : 'bg-danger'}`}
                                style={{ width: `${Math.min(100, att.percentage)}%` }}
                              ></div>
                            </div>
                            <span className="small fw-semibold">{att.percentage}%</span>
                          </div>
                        </td>
                        <td>
                          <span className="badge bg-light text-dark border">{att.status}</span>
                        </td>
                        <td>
                          {att.passed ? (
                            <span className="badge bg-success">PASSED</span>
                          ) : (
                            <span className="badge bg-danger">FAILED</span>
                          )}
                        </td>
                        <td className="small text-muted">{mins}m {secs}s</td>
                        <td className="pe-4 text-end text-muted small">
                          {att.submittedTime ? new Date(att.submittedTime).toLocaleString() : 'In Progress'}
                        </td>
                      </tr>
                    );
                  })
                ) : (
                  <tr>
                    <td colSpan="7" className="text-center py-5 text-muted">
                      No candidate attempts recorded for this examination yet.
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

export default ExamAttemptsAnalyticsPage;
