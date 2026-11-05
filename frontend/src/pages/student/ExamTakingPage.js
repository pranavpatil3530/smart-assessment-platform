import React, { useState, useEffect, useCallback, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { attemptApi } from '../../api/attemptApi';
import { useCountdownTimer } from '../../hooks/useCountdownTimer';
import TimerBanner from '../../components/TimerBanner';
import QuestionPalette from '../../components/QuestionPalette';
import CodeEditor from '../../components/CodeEditor';

const ExamTakingPage = () => {
  const { examId } = useParams();
  const navigate = useNavigate();

  const [attemptData, setAttemptData] = useState(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState({}); // { [questionId]: { selectedOptionId, submittedCode } }
  const [reviewFlags, setReviewFlags] = useState({}); // { [questionId]: boolean }
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [isSubmitModalOpen, setIsSubmitModalOpen] = useState(false);

  // Auto-submit guard ref to prevent multiple submission calls
  const isSubmittingRef = useRef(false);

  // Initialize Exam Session
  useEffect(() => {
    let isMounted = true;

    const startSession = async () => {
      try {
        const res = await attemptApi.startExam(examId);
        if (res.success && isMounted) {
          setAttemptData(res.data);

          // Initialize starter codes for coding questions
          const initialAnswers = {};
          res.data.questions?.forEach((q) => {
            if (q.questionType === 'CODING' && q.codingProblem) {
              initialAnswers[q.id] = {
                submittedCode: q.codingProblem.starterCode || '',
                selectedOptionId: null,
              };
            }
          });
          setAnswers(initialAnswers);
        }
      } catch (err) {
        if (isMounted) setError(err.message || 'Failed to start examination session.');
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    startSession();

    // Warn student before navigating away or refreshing
    const handleBeforeUnload = (e) => {
      e.preventDefault();
      e.returnValue = '';
    };
    window.addEventListener('beforeunload', handleBeforeUnload);

    return () => {
      isMounted = false;
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, [examId]);

  // Submit Handler (Manual or Auto on Timer Expiry)
  const handleSubmitExam = useCallback(
    async (isAutoSubmit = false) => {
      if (isSubmittingRef.current || !attemptData) return;
      isSubmittingRef.current = true;
      setSubmitting(true);

      try {
        const answerList = Object.entries(answers).map(([qId, val]) => ({
          questionId: Number(qId),
          selectedOptionId: val.selectedOptionId || null,
          submittedCode: val.submittedCode || null,
        }));

        const res = await attemptApi.submitExam(
          attemptData.attemptId,
          { answers: answerList },
          isAutoSubmit
        );

        if (res.success) {
          navigate(`/exam/result/${attemptData.attemptId}`, { replace: true });
        }
      } catch (err) {
        alert(err.message || 'Submission encountered an error. Please try again.');
        isSubmittingRef.current = false;
        setSubmitting(false);
      }
    },
    [attemptData, answers, navigate]
  );

  // Timer Hook
  const { formattedTime, isWarning, isCritical } = useCountdownTimer(
    attemptData?.endTime,
    () => {
      // Automatic submission on timer expiration!
      console.log('Countdown expired. Auto-submitting assessment...');
      handleSubmitExam(true);
    }
  );

  if (loading) {
    return (
      <div className="d-flex flex-column justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
        <div className="spinner-border text-primary mb-3" role="status"></div>
        <h5 className="fw-semibold text-dark">Initializing Secure Assessment Environment...</h5>
        <p className="text-muted small">Randomizing question sequence & setting up countdown timers</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container py-5 text-center">
        <div className="card shadow-sm border-0 p-5 mx-auto" style={{ maxWidth: '600px' }}>
          <i className="bi bi-exclamation-octagon-fill text-danger fs-1 mb-3"></i>
          <h4 className="fw-bold">Unable to Launch Assessment</h4>
          <p className="text-muted">{error}</p>
          <button className="btn btn-primary" onClick={() => navigate('/dashboard')}>
            Back to Catalog
          </button>
        </div>
      </div>
    );
  }

  const questions = attemptData?.questions || [];
  const currentQuestion = questions[currentIndex];
  const totalQuestions = questions.length;

  const currentAnswer = answers[currentQuestion?.id] || {
    selectedOptionId: null,
    submittedCode: currentQuestion?.codingProblem?.starterCode || '',
  };

  // Option selection handler
  const handleSelectOption = (optionId) => {
    setAnswers((prev) => ({
      ...prev,
      [currentQuestion.id]: {
        ...prev[currentQuestion.id],
        selectedOptionId: optionId,
      },
    }));
  };

  // Coding solution change handler
  const handleCodeChange = (code) => {
    setAnswers((prev) => ({
      ...prev,
      [currentQuestion.id]: {
        ...prev[currentQuestion.id],
        submittedCode: code,
      },
    }));
  };

  const handleResetCode = () => {
    if (currentQuestion?.codingProblem) {
      handleCodeChange(currentQuestion.codingProblem.starterCode || '');
    }
  };

  const handleClearResponse = () => {
    setAnswers((prev) => {
      const updated = { ...prev };
      delete updated[currentQuestion.id];
      return updated;
    });
    setReviewFlags((prev) => ({
      ...prev,
      [currentQuestion.id]: false,
    }));
  };

  const handleToggleReview = () => {
    setReviewFlags((prev) => ({
      ...prev,
      [currentQuestion.id]: !prev[currentQuestion.id],
    }));
  };

  const handleNext = () => {
    if (currentIndex < totalQuestions - 1) {
      setCurrentIndex((prev) => prev + 1);
    }
  };

  const handlePrev = () => {
    if (currentIndex > 0) {
      setCurrentIndex((prev) => prev - 1);
    }
  };

  // Metric counts for confirmation dialog
  const answeredCount = questions.filter(
    (q) => answers[q.id]?.selectedOptionId != null || (answers[q.id]?.submittedCode && answers[q.id].submittedCode.trim().length > 0)
  ).length;
  const unattemptedCount = totalQuestions - answeredCount;

  return (
    <div className="bg-light pb-5" style={{ minHeight: '100vh' }}>
      {/* Sticky Countdown Timer Banner */}
      <TimerBanner
        formattedTime={formattedTime}
        isWarning={isWarning}
        isCritical={isCritical}
        examTitle={attemptData?.examTitle}
      />

      <div className="container-fluid px-md-4">
        <div className="row g-4">
          {/* Main Question Display Area */}
          <div className="col-lg-8 col-xl-9">
            <div className="card shadow-sm border-0 rounded-3 mb-3">
              <div className="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                <div className="d-flex align-items-center gap-2">
                  <span className="badge bg-primary fs-6 px-3 py-2">
                    Question {currentIndex + 1} of {totalQuestions}
                  </span>
                  <span className={`badge ${currentQuestion?.questionType === 'MCQ' ? 'bg-info-subtle text-info' : 'bg-warning-subtle text-dark'}`}>
                    {currentQuestion?.questionType}
                  </span>
                </div>

                <div className="d-flex align-items-center gap-2">
                  <span className="badge bg-success-subtle text-success">
                    +{currentQuestion?.marks} Marks
                  </span>
                  {currentQuestion?.negativeMarks > 0 && (
                    <span className="badge bg-danger-subtle text-danger">
                      -{currentQuestion?.negativeMarks} Neg
                    </span>
                  )}
                </div>
              </div>

              <div className="card-body p-4">
                {/* Question Statement */}
                <h5 className="fw-semibold text-dark mb-4 lh-base" style={{ whiteSpace: 'pre-line' }}>
                  {currentQuestion?.content}
                </h5>

                {/* MCQ Mode */}
                {currentQuestion?.questionType === 'MCQ' && (
                  <div className="options-list mt-3">
                    {currentQuestion?.options?.map((opt, i) => {
                      const isSelected = currentAnswer.selectedOptionId === opt.id;
                      return (
                        <label
                          key={opt.id}
                          className={`option-label ${isSelected ? 'selected' : ''}`}
                        >
                          <input
                            type="radio"
                            name={`question-${currentQuestion.id}`}
                            value={opt.id}
                            checked={isSelected}
                            onChange={() => handleSelectOption(opt.id)}
                          />
                          <span className="fw-semibold me-2">{String.fromCharCode(65 + i)}.</span>
                          <span className="text-dark">{opt.optionText}</span>
                        </label>
                      );
                    })}
                  </div>
                )}

                {/* CODING Mode */}
                {currentQuestion?.questionType === 'CODING' && (
                  <CodeEditor
                    codingProblem={currentQuestion.codingProblem}
                    code={currentAnswer.submittedCode || ''}
                    onChange={handleCodeChange}
                    onReset={handleResetCode}
                  />
                )}
              </div>

              {/* Action Toolbar */}
              <div className="card-footer bg-white py-3 d-flex flex-wrap justify-content-between align-items-center gap-2">
                <div className="d-flex gap-2">
                  <button
                    className="btn btn-outline-secondary btn-sm"
                    onClick={handleClearResponse}
                    disabled={!currentAnswer.selectedOptionId && !currentAnswer.submittedCode}
                  >
                    <i className="bi bi-eraser me-1"></i> Clear Response
                  </button>

                  <button
                    className={`btn btn-sm ${reviewFlags[currentQuestion?.id] ? 'btn-purple text-white' : 'btn-outline-primary'}`}
                    style={reviewFlags[currentQuestion?.id] ? { backgroundColor: '#8b5cf6' } : {}}
                    onClick={handleToggleReview}
                  >
                    <i className="bi bi-bookmark-fill me-1"></i>
                    {reviewFlags[currentQuestion?.id] ? 'Marked for Review' : 'Mark for Review'}
                  </button>
                </div>

                <div className="d-flex gap-2">
                  <button
                    className="btn btn-outline-dark px-3"
                    onClick={handlePrev}
                    disabled={currentIndex === 0}
                  >
                    <i className="bi bi-chevron-left me-1"></i> Previous
                  </button>

                  {currentIndex < totalQuestions - 1 ? (
                    <button className="btn btn-primary px-4 fw-semibold" onClick={handleNext}>
                      Next <i className="bi bi-chevron-right ms-1"></i>
                    </button>
                  ) : (
                    <button
                      className="btn btn-success px-4 fw-bold shadow-sm"
                      onClick={() => setIsSubmitModalOpen(true)}
                    >
                      Finish & Submit <i className="bi bi-check2-circle ms-1"></i>
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Right Column: Question Palette */}
          <div className="col-lg-4 col-xl-3">
            <QuestionPalette
              questions={questions}
              currentIndex={currentIndex}
              answers={answers}
              reviewFlags={reviewFlags}
              onSelectQuestion={(idx) => setCurrentIndex(idx)}
              onSubmitClick={() => setIsSubmitModalOpen(true)}
            />
          </div>
        </div>
      </div>

      {/* Confirmation Modal */}
      {isSubmitModalOpen && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.65)' }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content border-0 shadow-lg rounded-4">
              <div className="modal-header bg-dark text-white">
                <h5 className="modal-title fw-bold">
                  <i className="bi bi-send-check me-2 text-success"></i> Confirm Final Submission
                </h5>
                <button
                  type="button"
                  className="btn-close btn-close-white"
                  onClick={() => setIsSubmitModalOpen(false)}
                ></button>
              </div>

              <div className="modal-body p-4">
                <p className="text-muted">
                  Are you ready to submit your assessment? Once submitted, you cannot change your answers.
                </p>

                <div className="row g-2 text-center mb-3">
                  <div className="col-4">
                    <div className="p-3 bg-light rounded-3 border">
                      <div className="text-muted small">Total Questions</div>
                      <h4 className="fw-bold mb-0 text-dark">{totalQuestions}</h4>
                    </div>
                  </div>
                  <div className="col-4">
                    <div className="p-3 bg-success-subtle rounded-3 border border-success-subtle">
                      <div className="text-success small fw-semibold">Attempted</div>
                      <h4 className="fw-bold mb-0 text-success">{answeredCount}</h4>
                    </div>
                  </div>
                  <div className="col-4">
                    <div className="p-3 bg-warning-subtle rounded-3 border border-warning-subtle">
                      <div className="text-warning-emphasis small fw-semibold">Unanswered</div>
                      <h4 className="fw-bold mb-0 text-warning-emphasis">{unattemptedCount}</h4>
                    </div>
                  </div>
                </div>

                {unattemptedCount > 0 && (
                  <div className="alert alert-warning small d-flex align-items-center gap-2 mb-0">
                    <i className="bi bi-exclamation-triangle-fill"></i>
                    <span>You still have {unattemptedCount} unanswered questions remaining.</span>
                  </div>
                )}
              </div>

              <div className="modal-footer bg-light">
                <button
                  type="button"
                  className="btn btn-secondary px-3"
                  onClick={() => setIsSubmitModalOpen(false)}
                  disabled={submitting}
                >
                  Resume Test
                </button>
                <button
                  type="button"
                  className="btn btn-success px-4 fw-bold"
                  onClick={() => handleSubmitExam(false)}
                  disabled={submitting}
                >
                  {submitting ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                      Evaluating Answers...
                    </>
                  ) : (
                    'Confirm & Submit'
                  )}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ExamTakingPage;
