import React from 'react';

const CodeEditor = ({ codingProblem, code, onChange, onReset }) => {
  const handleKeyDown = (e) => {
    // Enable tab indentation inside textarea
    if (e.key === 'Tab') {
      e.preventDefault();
      const start = e.target.selectionStart;
      const end = e.target.selectionEnd;
      const newCode = code.substring(0, start) + '    ' + code.substring(end);
      onChange(newCode);
      setTimeout(() => {
        e.target.selectionStart = e.target.selectionEnd = start + 4;
      }, 0);
    }
  };

  return (
    <div className="mt-3">
      {/* Problem Specifications */}
      {(codingProblem?.constraints || codingProblem?.sampleInput) && (
        <div className="card bg-light border mb-3">
          <div className="card-body py-2 px-3 small">
            {codingProblem.constraints && (
              <div className="mb-1">
                <strong>Constraints:</strong> <code>{codingProblem.constraints}</code>
              </div>
            )}
            <div className="row g-2">
              {codingProblem.sampleInput && (
                <div className="col-md-6">
                  <strong>Sample Input:</strong>
                  <pre className="bg-white p-2 rounded border mb-0 mt-1 font-monospace">{codingProblem.sampleInput}</pre>
                </div>
              )}
              {codingProblem.sampleOutput && (
                <div className="col-md-6">
                  <strong>Sample Output:</strong>
                  <pre className="bg-white p-2 rounded border mb-0 mt-1 font-monospace">{codingProblem.sampleOutput}</pre>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Editor Header */}
      <div className="d-flex justify-content-between align-items-center bg-dark text-white px-3 py-2 rounded-top border-bottom border-secondary">
        <div className="d-flex align-items-center gap-2">
          <i className="bi bi-code-slash text-warning"></i>
          <span className="fw-semibold small">Code Solution ({codingProblem?.language || 'Java'})</span>
        </div>
        <button
          className="btn btn-outline-light btn-sm py-0 px-2"
          onClick={onReset}
          title="Reset to starter template"
          type="button"
        >
          <i className="bi bi-arrow-counterclockwise me-1"></i> Reset Code
        </button>
      </div>

      {/* Code Textarea */}
      <textarea
        className="code-editor-box rounded-bottom rounded-0"
        value={code}
        onChange={(e) => onChange(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder="// Write your solution here..."
        rows={12}
        spellCheck="false"
      />
      <div className="text-muted small mt-1 d-flex justify-content-between">
        <span><i className="bi bi-info-circle me-1"></i> Press [Tab] to indent 4 spaces</span>
        <span>Characters: {code.length}</span>
      </div>
    </div>
  );
};

export default CodeEditor;
