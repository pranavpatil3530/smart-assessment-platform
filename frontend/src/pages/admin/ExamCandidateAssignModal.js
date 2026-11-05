import React, { useState, useEffect } from 'react';
import { instituteApi } from '../../api/instituteApi';

const ExamCandidateAssignModal = ({ exam, isOpen, onClose, onAssigned }) => {
  const [candidates, setCandidates] = useState([]);
  const [assignAll, setAssignAll] = useState(exam?.assignAllStudents ?? true);
  const [selectedIds, setSelectedIds] = useState(new Set());
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!isOpen || !exam) return;

    const fetchAssignments = async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await instituteApi.getExamCandidates(exam.id);
        const list = res?.data || (Array.isArray(res) ? res : res?.data?.data || []);
        setCandidates(list);
        setAssignAll(exam.assignAllStudents);

        const preSelected = new Set();
        list.forEach((c) => {
          if (c.assigned) preSelected.add(c.id);
        });
        setSelectedIds(preSelected);
      } catch (err) {
        console.error('Failed to load candidates for assignment', err);
        setError(err.message || 'Failed to load candidate list.');
      } finally {
        setLoading(false);
      }
    };

    fetchAssignments();
  }, [isOpen, exam]);

  if (!isOpen || !exam) return null;

  const handleToggleStudent = (id) => {
    const next = new Set(selectedIds);
    if (next.has(id)) {
      next.delete(id);
    } else {
      next.add(id);
    }
    setSelectedIds(next);
  };

  const handleSelectAllFiltered = () => {
    const next = new Set(selectedIds);
    filteredCandidates.forEach((c) => next.add(c.id));
    setSelectedIds(next);
  };

  const handleDeselectAllFiltered = () => {
    const next = new Set(selectedIds);
    filteredCandidates.forEach((c) => next.delete(c.id));
    setSelectedIds(next);
  };

  const handleSave = async () => {
    setSaving(true);
    setError(null);
    try {
      await instituteApi.assignCandidatesToExam(exam.id, {
        assignAll,
        candidateIds: assignAll ? [] : Array.from(selectedIds),
      });
      if (onAssigned) {
        onAssigned();
      }
      onClose();
    } catch (err) {
      console.error('Failed to save assignments', err);
      setError(err.message || 'Failed to save assignments.');
    } finally {
      setSaving(false);
    }
  };

  const filteredCandidates = candidates.filter((c) => {
    const term = searchTerm.toLowerCase();
    return (
      (c.fullName && c.fullName.toLowerCase().includes(term)) ||
      (c.username && c.username.toLowerCase().includes(term)) ||
      (c.email && c.email.toLowerCase().includes(term))
    );
  });

  return (
    <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
      <div className="modal-dialog modal-dialog-centered modal-lg">
        <div className="modal-content shadow-lg border-0">
          <div className="modal-header bg-light">
            <div>
              <h5 className="modal-title fw-bold mb-0">
                <i className="bi bi-person-check-fill text-primary me-2"></i>
                Candidate Exam Assignment
              </h5>
              <small className="text-muted">
                Exam: <strong>{exam.title}</strong>
              </small>
            </div>
            <button type="button" className="btn-close" onClick={onClose} disabled={saving}></button>
          </div>

          <div className="modal-body p-4">
            {error && (
              <div className="alert alert-danger py-2 small mb-3">
                <i className="bi bi-exclamation-triangle-fill me-2"></i>
                {error}
              </div>
            )}

            <div className="card border p-3 mb-3 bg-light">
              <label className="form-label fw-bold mb-2">Assignment Mode:</label>
              <div className="form-check mb-2">
                <input
                  className="form-check-input"
                  type="radio"
                  name="assignmentMode"
                  id="modeAll"
                  checked={assignAll}
                  onChange={() => setAssignAll(true)}
                />
                <label className="form-check-label" htmlFor="modeAll">
                  <strong>Assign to ALL candidates</strong> in this institute
                  <div className="small text-muted">
                    Every candidate enrolled in your institute can view and take this exam.
                  </div>
                </label>
              </div>

              <div className="form-check">
                <input
                  className="form-check-input"
                  type="radio"
                  name="assignmentMode"
                  id="modeSelective"
                  checked={!assignAll}
                  onChange={() => setAssignAll(false)}
                />
                <label className="form-check-label" htmlFor="modeSelective">
                  <strong>Selective assignment</strong> (Only selected candidates)
                  <div className="small text-muted">
                    Only students you explicitly check below will have access to this exam.
                  </div>
                </label>
              </div>
            </div>

            {!assignAll && (
              <div>
                <div className="d-flex justify-content-between align-items-center mb-2">
                  <div className="fw-semibold">
                    Select Candidates ({selectedIds.size} selected of {candidates.length}):
                  </div>
                  <div className="btn-group btn-group-sm">
                    <button
                      type="button"
                      className="btn btn-outline-secondary"
                      onClick={handleSelectAllFiltered}
                    >
                      Select All
                    </button>
                    <button
                      type="button"
                      className="btn btn-outline-secondary"
                      onClick={handleDeselectAllFiltered}
                    >
                      Deselect All
                    </button>
                  </div>
                </div>

                <div className="input-group input-group-sm mb-3">
                  <span className="input-group-text bg-white">
                    <i className="bi bi-search text-muted"></i>
                  </span>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Search candidate by name, username, or email..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                  {searchTerm && (
                    <button
                      className="btn btn-outline-secondary"
                      type="button"
                      onClick={() => setSearchTerm('')}
                    >
                      Clear
                    </button>
                  )}
                </div>

                {loading ? (
                  <div className="text-center py-4">
                    <div className="spinner-border spinner-border-sm text-primary" role="status"></div>
                    <span className="ms-2 small text-muted">Loading candidates...</span>
                  </div>
                ) : candidates.length === 0 ? (
                  <div className="alert alert-warning py-3 text-center small">
                    <i className="bi bi-info-circle me-1"></i> No candidates registered in your institute yet.
                    Candidates must register using your Institute Code.
                  </div>
                ) : filteredCandidates.length === 0 ? (
                  <div className="text-center py-3 text-muted small">
                    No candidates match "{searchTerm}"
                  </div>
                ) : (
                  <div className="border rounded p-2" style={{ maxHeight: '260px', overflowY: 'auto' }}>
                    <div className="list-group list-group-flush">
                      {filteredCandidates.map((cand) => {
                        const isChecked = selectedIds.has(cand.id);
                        return (
                          <label
                            key={cand.id}
                            className={`list-group-item list-group-item-action d-flex align-items-center gap-3 py-2 cursor-pointer ${
                              isChecked ? 'bg-light' : ''
                            }`}
                          >
                            <input
                              className="form-check-input flex-shrink-0 mt-0"
                              type="checkbox"
                              checked={isChecked}
                              onChange={() => handleToggleStudent(cand.id)}
                            />
                            <div className="flex-grow-1">
                              <div className="fw-semibold small">{cand.fullName || cand.username}</div>
                              <div className="text-muted" style={{ fontSize: '0.75rem' }}>
                                @{cand.username} &bull; {cand.email}
                              </div>
                            </div>
                            {isChecked && (
                              <span className="badge bg-success-subtle text-success border border-success-subtle" style={{ fontSize: '0.7rem' }}>
                                Assigned
                              </span>
                            )}
                          </label>
                        );
                      })}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>

          <div className="modal-footer bg-light">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
              disabled={saving}
            >
              Cancel
            </button>
            <button
              type="button"
              className="btn btn-primary fw-semibold"
              onClick={handleSave}
              disabled={saving || loading}
            >
              {saving ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                  Saving...
                </>
              ) : (
                'Save Assignment Settings'
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ExamCandidateAssignModal;
