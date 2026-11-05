import React, { useState, useEffect } from 'react';
import { instituteApi } from '../../api/instituteApi';
import { useAuth } from '../../context/AuthContext';
import Pagination from '../../components/Pagination';

const InstituteCandidatesPage = () => {
  const { user } = useAuth();
  const [candidates, setCandidates] = useState([]);
  const [stats, setStats] = useState(null);
  const [profile, setProfile] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [copied, setCopied] = useState(false);
  const [error, setError] = useState(null);

  const pageSize = 10;

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [candRes, statsRes, profRes] = await Promise.all([
        instituteApi.getCandidates(page, pageSize),
        instituteApi.getStats(),
        instituteApi.getProfile(),
      ]);

      const candPayload = candRes?.data || candRes;
      if (candPayload) {
        setCandidates(candPayload.content || (Array.isArray(candPayload) ? candPayload : []));
        setTotalPages(candPayload.totalPages || 0);
        setTotalElements(candPayload.totalElements || 0);
      }

      const statsPayload = statsRes?.data || statsRes;
      if (statsPayload) {
        setStats(statsPayload);
      }

      const profPayload = profRes?.data || profRes;
      if (profPayload) {
        setProfile(profPayload);
      }
    } catch (err) {
      console.error('Failed to load institute candidates', err);
      setError('Failed to load candidate roster.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [page]);

  const handleCopyCode = () => {
    const code = profile?.code || stats?.instituteCode;
    if (code) {
      navigator.clipboard.writeText(code);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  return (
    <div className="container py-4">
      {/* Header */}
      <div className="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
          <h2 className="fw-bold mb-1 d-flex align-items-center gap-2">
            <i className="bi bi-people-fill text-primary"></i> Enrolled Candidates (Students)
          </h2>
          <p className="text-muted mb-0">
            {profile?.name || stats?.instituteName || user?.instituteName || 'Institute'} &bull; Candidate Enrollment Roster
          </p>
        </div>
        <div>
          <button className="btn btn-outline-secondary btn-sm" onClick={loadData} disabled={loading}>
            <i className={`bi bi-arrow-clockwise me-1 ${loading ? 'spin' : ''}`}></i> Refresh
          </button>
        </div>
      </div>

      {error && (
        <div className="alert alert-danger py-2 small mb-3">
          <i className="bi bi-exclamation-triangle-fill me-2"></i> {error}
        </div>
      )}

      {/* Institute Info & Invitation Card */}
      <div className="card shadow-sm border-0 mb-4 bg-light">
        <div className="card-body p-4">
          <div className="row align-items-center g-3">
            <div className="col-md-8">
              <h5 className="fw-bold mb-1">
                <i className="bi bi-building me-2 text-primary"></i>
                {profile?.name || stats?.instituteName || 'Your Institute'}
              </h5>
              <p className="text-muted small mb-2">
                Share your unique <strong>Institute Code</strong> with students. When students register using this code,
                they will be enrolled in your institute and eligible for your examinations.
              </p>
              <div className="d-flex align-items-center gap-2">
                <span className="badge bg-dark font-monospace fs-6 px-3 py-2">
                  {profile?.code || stats?.instituteCode || 'N/A'}
                </span>
                <button
                  className="btn btn-sm btn-outline-primary"
                  onClick={handleCopyCode}
                  title="Copy code to clipboard"
                >
                  <i className={`bi ${copied ? 'bi-check-lg text-success' : 'bi-clipboard'} me-1`}></i>
                  {copied ? 'Copied!' : 'Copy Institute Code'}
                </button>
              </div>
            </div>
            <div className="col-md-4 text-md-end">
              <div className="d-inline-block text-start p-3 bg-white rounded border">
                <div className="text-muted small text-uppercase fw-semibold">Total Candidates</div>
                <h3 className="fw-bold mb-0 text-primary">{totalElements}</h3>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Candidates Table */}
      <div className="card shadow-sm border-0">
        <div className="card-header bg-white py-3">
          <h5 className="mb-0 fw-bold">Candidate Roster</h5>
        </div>
        <div className="card-body p-0">
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary spinner-border-sm me-2" role="status"></div>
              Loading candidates...
            </div>
          ) : candidates.length === 0 ? (
            <div className="text-center py-5 text-muted">
              <i className="bi bi-people display-4 d-block mb-3"></i>
              <h5>No Candidates Enrolled Yet</h5>
              <p className="small">
                Instruct your students to register on the platform with code{' '}
                <strong className="text-dark">{profile?.code || stats?.instituteCode}</strong>.
              </p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th className="ps-4">Candidate Name</th>
                    <th>Username</th>
                    <th>Email Address</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {candidates.map((cand) => (
                    <tr key={cand.id}>
                      <td className="ps-4">
                        <div className="d-flex align-items-center gap-2">
                          <div
                            className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold"
                            style={{ width: '32px', height: '32px', fontSize: '0.8rem' }}
                          >
                            {cand.fullName ? cand.fullName.charAt(0).toUpperCase() : cand.username.charAt(0).toUpperCase()}
                          </div>
                          <div>
                            <div className="fw-semibold">{cand.fullName || cand.username}</div>
                          </div>
                        </div>
                      </td>
                      <td>
                        <span className="text-muted font-monospace small">@{cand.username}</span>
                      </td>
                      <td>
                        <span className="small">{cand.email}</span>
                      </td>
                      <td>
                        <span className="badge bg-success-subtle text-success border border-success-subtle">
                          Enrolled
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
        {totalPages > 1 && (
          <div className="card-footer bg-white border-0 py-3">
            <Pagination
              currentPage={page}
              totalPages={totalPages}
              onPageChange={(p) => setPage(p)}
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default InstituteCandidatesPage;
