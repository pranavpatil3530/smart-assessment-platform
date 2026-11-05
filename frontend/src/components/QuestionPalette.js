import React from 'react';

const QuestionPalette = ({
  questions,
  currentIndex,
  answers,
  reviewFlags,
  onSelectQuestion,
  onSubmitClick,
}) => {
  const answeredCount = questions.filter(
    (q) => answers[q.id]?.selectedOptionId != null || (answers[q.id]?.submittedCode && answers[q.id].submittedCode.trim().length > 0)
  ).length;

  const reviewCount = questions.filter((q) => reviewFlags[q.id]).length;
  const unansweredCount = questions.length - answeredCount;

  return (
    <div className="card shadow-sm border-0 sticky-top" style={{ top: '80px' }}>
      <div className="card-header bg-white fw-bold py-3 d-flex justify-content-between align-items-center">
        <span><i className="bi bi-grid-3x3-gap-fill me-2 text-primary"></i> Question Palette</span>
        <span className="badge bg-light text-dark border">{questions.length} Total</span>
      </div>

      <div className="card-body">
        {/* Status Legend */}
        <div className="row g-2 mb-3 text-muted" style={{ fontSize: '0.75rem' }}>
          <div className="col-6 d-flex align-items-center gap-1">
            <span className="palette-btn answered" style={{ width: '18px', height: '18px' }}></span>
            <span>Answered ({answeredCount})</span>
          </div>
          <div className="col-6 d-flex align-items-center gap-1">
            <span className="palette-btn reviewed" style={{ width: '18px', height: '18px' }}></span>
            <span>Review ({reviewCount})</span>
          </div>
          <div className="col-6 d-flex align-items-center gap-1">
            <span className="palette-btn unanswered" style={{ width: '18px', height: '18px' }}></span>
            <span>Unanswered ({unansweredCount})</span>
          </div>
          <div className="col-6 d-flex align-items-center gap-1">
            <span className="palette-btn active" style={{ width: '18px', height: '18px' }}></span>
            <span>Current</span>
          </div>
        </div>

        <hr className="my-2" />

        {/* Buttons Grid */}
        <div className="d-flex flex-wrap gap-2 py-2" style={{ maxHeight: '280px', overflowY: 'auto' }}>
          {questions.map((q, idx) => {
            const isAnswered =
              answers[q.id]?.selectedOptionId != null ||
              (answers[q.id]?.submittedCode && answers[q.id].submittedCode.trim().length > 0);
            const isReviewed = reviewFlags[q.id];
            const isCurrent = currentIndex === idx;

            let btnClass = 'palette-btn';
            if (isReviewed) {
              btnClass += ' reviewed';
            } else if (isAnswered) {
              btnClass += ' answered';
            } else {
              btnClass += ' unanswered';
            }

            if (isCurrent) {
              btnClass += ' active border-primary border-2';
            }

            return (
              <button
                key={q.id}
                className={btnClass}
                onClick={() => onSelectQuestion(idx)}
                title={`Question ${idx + 1}`}
              >
                {idx + 1}
              </button>
            );
          })}
        </div>

        <div className="mt-4 pt-2 border-top">
          <button
            className="btn btn-success w-100 fw-bold py-2 shadow-sm d-flex align-items-center justify-content-center gap-2"
            onClick={onSubmitClick}
          >
            <i className="bi bi-send-check-fill"></i> Submit Examination
          </button>
        </div>
      </div>
    </div>
  );
};

export default QuestionPalette;
