import React from 'react';

const TimerBanner = ({ formattedTime, isWarning, isCritical, examTitle }) => {
  let timerClass = 'bg-primary text-white';
  let badgeIcon = 'bi-stopwatch';

  if (isCritical) {
    timerClass = 'bg-danger text-white timer-warning';
    badgeIcon = 'bi-exclamation-triangle-fill';
  } else if (isWarning) {
    timerClass = 'bg-warning text-dark border-warning';
    badgeIcon = 'bi-hourglass-split';
  }

  return (
    <div className="bg-white border-bottom shadow-sm sticky-top py-2 px-3 mb-3">
      <div className="container-fluid d-flex justify-content-between align-items-center">
        <div>
          <span className="badge bg-secondary me-2">EXAM IN PROGRESS</span>
          <span className="fw-bold text-dark d-none d-md-inline">{examTitle}</span>
        </div>

        <div className="d-flex align-items-center gap-3">
          <span className="text-muted small d-none d-sm-inline">
            <i className="bi bi-info-circle me-1"></i> Exam will automatically submit at 00:00
          </span>

          <div className={`timer-container px-3 py-1 d-flex align-items-center gap-2 ${timerClass}`}>
            <i className={`bi ${badgeIcon} fs-5`}></i>
            <div>
              <div style={{ fontSize: '0.65rem', textTransform: 'uppercase', lineHeight: 1 }}>Time Remaining</div>
              <div className="fs-5 font-monospace fw-bold">{formattedTime}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TimerBanner;
