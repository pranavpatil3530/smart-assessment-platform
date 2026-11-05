import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { attemptApi } from '../../api/attemptApi';

const ExamResultPage = () => {
  const { attemptId } = useParams();
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchResult = async () => {
      try {
        const res = await attemptApi.getAttemptResult(attemptId);
        if (res.success) {
          setResult(res.data);
        }
      } catch (err) {
        setError(err.message || 'Failed to fetch examination results.');
      } finally {
        setLoading(false);
      }
    };

    fetchResult();
  }, [attemptId]);

  if (loading) {
    return (
      <div className="container py-5 text-center">
        <div className="spinner-border text-primary" role="status"></div>
        <p className="text-muted mt-2">Computing score & compiling performance report...</p>
      </div>
    );
  }

  if (error || !result) {
    return (
      <div className="container py-5 text-center">
        <div className="card shadow-sm border-0 p-5 mx-auto" style={{ maxWidth: '500px' }}>
          <i className="bi bi-x-circle text-danger fs-1 mb-3"></i>
          <h4>Scorecard Unavailable</h4>
          <p className="text-muted">{error || 'Could not locate result details.'}</p>
          <Link to="/dashboard" className="btn btn-primary">
            Back to Dashboard
          </Link>
        </div>
      </div>
    );
  }

  const mins = Math.floor((result.timeTakenSeconds || 0) / 60);
  const secs = (result.timeTakenSeconds || 0) % 60;

  return (
    <div className="container py-5">
      {/* Result Hero Header */}
      <div
        className={`card border-0 shadow-lg rounded-4 overflow-hidden mb-4 text-white ${
          result.passed ? 'bg-gradient' : 'bg-gradient'
        }`}
        style={{
          background: result.passed
            ? 'linear-gradient(135deg, #059669 0%, #10b981 100%)'
            : 'linear-gradient(135deg, #dc2626 0%, #f87171 100%)',
        }}
      >
        <div className="card-body p-4 p-md-5 text-center">
          <div className="mb-3">
            {result.passed ? (
              <i className="bi bi-check-circle-fill display-3"></i>
            ) : (
              <i className="bi bi-x-circle-fill display-3"></i>
            )}
          </div>

          <h2 className="fw-bold mb-1">
            {result.passed ? 'Assessment Passed!' : 'Assessment Not Cleared'}
          </h2>
          <p className="lead mb-4 text-white-50" style={{ fontSize: '1.1rem' }}>
            {result.passed
              ? 'Congratulations! You have satisfied the qualifying criteria for this assessment.'
              : 'Keep practicing! Review your detailed question breakdown below to identify areas of improvement.'}
          </p>

          <div className="d-inline-flex flex-wrap justify-content-center gap-3 bg-black bg-opacity-25 p-3 rounded-4">
            <div className="px-3 border-end border-white-50">
              <div className="small text-white-50 text-uppercase">Final Score</div>
              <div className="fs-3 fw-bold">{result.score} / {result.totalMarks}</div>
            </div>
            <div className="px-3 border-end border-white-50">
              <div className="small text-white-50 text-uppercase">Percentage</div>
              <div className="fs-3 fw-bold">{result.percentage}%</div>
            </div>
            <div className="px-3">
              <div className="small text-white-50 text-uppercase">Cutoff Marks</div>
              <div className="fs-3 fw-bold">{result.passingMarks}</div>
            </div>
          </div>
        </div>
      </div>

      {/* Summary KPI Cards */}
      <div className="row g-3 mb-4">
        <div className="col-6 col-md-3">
          <div className="kpi-card p-3 text-center">
            <div className="small text-muted mb-1">Correct Answers</div>
            <h4 className="fw-bold text-success mb-0">{result.totalCorrect}</h4>
            <span className="small text-muted">+{result.totalCorrect} questions</span>
          </div>
        </div>

        <div className="col-6 col-md-3">
          <div className="kpi-card p-3 text-center">
            <div className="small text-muted mb-1">Incorrect Answers</div>
            <h4 className="fw-bold text-danger mb-0">{result.totalWrong}</h4>
            <span className="small text-danger">Negative penalty deducted</span>
          </div>
        </div>

        <div className="col-6 col-md-3">
          <div className="kpi-card p-3 text-center">
            <div className="small text-muted mb-1">Unattempted</div>
            <h4 className="fw-bold text-warning-emphasis mb-0">{result.totalUnattempted}</h4>
            <span className="small text-muted">0 marks impacted</span>
          </div>
        </div>

        <div className="col-6 col-md-3">
          <div className="kpi-card p-3 text-center">
            <div className="small text-muted mb-1">Time Taken</div>
            <h4 className="fw-bold text-dark mb-0">{mins}m {secs}s</h4>
            <span className="badge bg-secondary-subtle text-secondary small mt-1">{result.status}</span>
          </div>
        </div>
      </div>

      {/* Detailed Question Review Breakdown */}
      <div className="card shadow-sm border-0 rounded-3 mb-4">
        <div className="card-header bg-white py-3 d-flex justify-content-between align-items-center">
          <h5 className="fw-bold mb-0">
            <i className="bi bi-card-checklist me-2 text-primary"></i> Question Breakdown & Solution Rationale
          </h5>
          <span className="badge bg-light text-dark border">
            {result.questionBreakdown?.length || 0} Questions Evaluated
          </span>
        </div>

        <div className="card-body p-4">
          <div className="d-flex flex-column gap-4">
            {result.questionBreakdown?.map((item, idx) => {
              const isMCQ = item.questionType === 'MCQ';
              const isCorrect = item.isCorrect === true;
              const isWrong = item.isCorrect === false;
              const isSkipped = item.isCorrect === null || item.isCorrect === undefined;

              let cardBorder = 'border-secondary-subtle';
              let badgeColor = 'bg-secondary';
              let statusText = 'UNATTEMPTED';

              if (isCorrect) {
                cardBorder = 'border-success';
                badgeColor = 'bg-success';
                statusText = 'CORRECT';
              } else if (isWrong) {
                cardBorder = 'border-danger';
                badgeColor = 'bg-danger';
                statusText = 'INCORRECT';
              }

              return (
                <div key={item.questionId || idx} className={`card border rounded-3 ${cardBorder}`}>
                  <div className="card-header bg-light py-2 px-3 d-flex justify-content-between align-items-center">
                    <div className="d-flex align-items-center gap-2">
                      <span className="badge bg-dark">Q{idx + 1}</span>
                      <span className={`badge ${badgeColor}`}>{statusText}</span>
                      <span className="badge bg-light text-dark border">{item.questionType}</span>
                    </div>

                    <div>
                      <strong className={item.marksAwarded > 0 ? 'text-success' : item.marksAwarded < 0 ? 'text-danger' : 'text-muted'}>
                        {item.marksAwarded > 0 ? `+${item.marksAwarded}` : item.marksAwarded} Marks
                      </strong>
                    </div>
                  </div>

                  <div className="card-body p-3">
                    <h6 className="fw-semibold mb-3 lh-base">{item.content}</h6>

                    {/* MCQ Options Details */}
                    {isMCQ && (
                      <div className="row g-2 mb-3">
                        {item.options?.map((opt, oIdx) => {
                          const isStudentChoice = item.selectedOptionId === opt.id;
                          const isCorrectChoice = opt.correct;

                          let optionClass = 'bg-light border';
                          if (isCorrectChoice) {
                            optionClass = 'bg-success-subtle border-success text-success-emphasis fw-bold';
                          } else if (isStudentChoice && !isCorrectChoice) {
                            optionClass = 'bg-danger-subtle border-danger text-danger-emphasis fw-bold';
                          }

                          return (
                            <div key={opt.id || oIdx} className="col-md-6">
                              <div className={`p-2 rounded small d-flex align-items-center justify-content-between ${optionClass}`}>
                                <span>
                                  <strong>{String.fromCharCode(65 + oIdx)}.</strong> {opt.optionText}
                                </span>
                                <div>
                                  {isStudentChoice && (
                                    <span className="badge bg-primary me-1">Your Pick</span>
                                  )}
                                  {isCorrectChoice && (
                                    <span className="badge bg-success">Correct</span>
                                  )}
                                </div>
                              </div>
                            </div>
                          );
                        })}
                      </div>
                    )}

                    {/* Coding Problem Submitted Code */}
                    {!isMCQ && item.submittedCode && (
                      <div className="mb-3">
                        <span className="text-muted small fw-semibold">Submitted Solution:</span>
                        <pre className="p-3 bg-dark text-white rounded font-monospace small mb-0 mt-1">
                          {item.submittedCode}
                        </pre>
                      </div>
                    )}

                    {/* Explanation */}
                    {item.explanation && (
                      <div className="p-3 bg-light rounded border-start border-3 border-info small text-muted">
                        <strong className="text-dark d-block mb-1">
                          <i className="bi bi-lightbulb-fill text-warning me-1"></i> Architectural Rationale:
                        </strong>
                        {item.explanation}
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Navigation Buttons */}
      <div className="d-flex justify-content-center gap-3">
        <Link to="/dashboard" className="btn btn-outline-primary px-4">
          <i className="bi bi-arrow-left me-1"></i> Back to Catalog
        </Link>
        <Link to="/student/history" className="btn btn-primary px-4 fw-semibold">
          <i className="bi bi-clock-history me-1"></i> View All Past Attempts
        </Link>
      </div>
    </div>
  );
};

export default ExamResultPage;
