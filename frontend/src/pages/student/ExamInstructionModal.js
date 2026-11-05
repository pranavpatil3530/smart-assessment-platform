import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const ExamInstructionModal = ({ isOpen, onClose, exam }) => {
  const [agreed, setAgreed] = useState(false);
  const navigate = useNavigate();

  if (!isOpen || !exam) return null;

  const handleStartExam = () => {
    onClose();
    navigate(`/exam/take/${exam.id}`);
  };

  return (
    <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.65)' }}>
      <div className="modal-dialog modal-lg modal-dialog-centered">
        <div className="modal-content border-0 shadow-lg rounded-4 overflow-hidden">
          <div className="modal-header bg-primary text-white py-3">
            <h5 className="modal-title fw-bold">
              <i className="bi bi-shield-check me-2"></i> Assessment Instructions & Guidelines
            </h5>
            <button type="button" className="btn-close btn-close-white" onClick={onClose}></button>
          </div>

          <div className="modal-body p-4">
            <div className="mb-4">
              <span className="badge bg-primary-subtle text-primary mb-1">{exam.categoryName}</span>
              <h4 className="fw-bold text-dark mb-1">{exam.title}</h4>
              <p className="text-muted small">{exam.description || 'Standard technical competency evaluation'}</p>
            </div>

            {/* Quick Metrics Grid */}
            <div className="row g-2 mb-4 text-center">
              <div className="col-4">
                <div className="p-3 bg-light rounded-3 border">
                  <div className="text-muted small">Duration</div>
                  <div className="fw-bold fs-5 text-primary">{exam.durationMinutes} Mins</div>
                </div>
              </div>
              <div className="col-4">
                <div className="p-3 bg-light rounded-3 border">
                  <div className="text-muted small">Total Marks</div>
                  <div className="fw-bold fs-5 text-dark">{exam.totalMarks}</div>
                </div>
              </div>
              <div className="col-4">
                <div className="p-3 bg-light rounded-3 border">
                  <div className="text-muted small">Passing Cutoff</div>
                  <div className="fw-bold fs-5 text-success">{exam.passingMarks}</div>
                </div>
              </div>
            </div>

            {/* Key Assessment Rules */}
            <div className="alert alert-info py-3 mb-3">
              <h6 className="fw-bold mb-2">Important Test Rules:</h6>
              <ul className="mb-0 small ps-3">
                <li>
                  <strong>Configured Countdown Timer:</strong> The countdown runs in real-time. If the time expires, your exam will be <strong>automatically submitted</strong>.
                </li>
                <li>
                  <strong>Negative Marking Penalty:</strong> Each incorrect answer incurs a penalty of{' '}
                  <span className="text-danger fw-bold">-{exam.negativeMarkingRate || 0} marks</span>. Unanswered questions receive 0 marks.
                </li>
                <li>
                  <strong>Question Randomization:</strong> Questions and choices are randomized for every attempt.
                </li>
                <li>
                  <strong>Session Persistence:</strong> If your browser accidentally reloads, your active attempt will automatically resume without losing remaining time.
                </li>
              </ul>
            </div>

            <div className="form-check p-3 bg-light rounded border">
              <input
                className="form-check-input ms-0 me-2"
                type="checkbox"
                id="agreeCheckbox"
                checked={agreed}
                onChange={(e) => setAgreed(e.target.checked)}
              />
              <label className="form-check-label small fw-semibold" htmlFor="agreeCheckbox">
                I have carefully read and understood all instructions. I am ready to begin my assessment.
              </label>
            </div>
          </div>

          <div className="modal-footer bg-light py-3">
            <button type="button" className="btn btn-secondary px-4" onClick={onClose}>
              Cancel
            </button>
            <button
              type="button"
              className="btn btn-primary px-4 fw-bold"
              disabled={!agreed}
              onClick={handleStartExam}
            >
              Start Examination Now <i className="bi bi-arrow-right ms-1"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ExamInstructionModal;
