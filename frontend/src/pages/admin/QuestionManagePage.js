import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { examApi } from '../../api/examApi';
import { questionApi } from '../../api/questionApi';

const QuestionManagePage = () => {
  const { examId } = useParams();
  const [exam, setExam] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Modal State for Adding/Editing Question
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [questionType, setQuestionType] = useState('MCQ');
  const [content, setContent] = useState('');
  const [marks, setMarks] = useState(2.0);
  const [negativeMarks, setNegativeMarks] = useState(0.5);
  const [explanation, setExplanation] = useState('');

  // MCQ Options
  const [options, setOptions] = useState([
    { optionText: '', correct: true },
    { optionText: '', correct: false },
    { optionText: '', correct: false },
    { optionText: '', correct: false },
  ]);

  // Coding Question State
  const [starterCode, setStarterCode] = useState(
    'public class Solution {\n    public static void main(String[] args) {\n        // Write solution\n    }\n}'
  );
  const [language, setLanguage] = useState('Java');
  const [constraints, setConstraints] = useState('1 <= N <= 10^5');
  const [sampleInput, setSampleInput] = useState('5\n1 2 3 4 5');
  const [sampleOutput, setSampleOutput] = useState('15');

  const fetchExamAndQuestions = useCallback(async () => {
    setLoading(true);
    try {
      const [examRes, questionsRes] = await Promise.all([
        examApi.getExamDetails(examId),
        questionApi.getQuestionsByExam(examId),
      ]);
      if (examRes.success) setExam(examRes.data);
      if (questionsRes.success) setQuestions(questionsRes.data);
    } catch (err) {
      setError(err.message || 'Failed to fetch questions.');
    } finally {
      setLoading(false);
    }
  }, [examId]);

  useEffect(() => {
    fetchExamAndQuestions();
  }, [fetchExamAndQuestions]);

  const handleOptionChange = (idx, text) => {
    const updated = [...options];
    updated[idx].optionText = text;
    setOptions(updated);
  };

  const handleCorrectOptionSelect = (idx) => {
    const updated = options.map((opt, i) => ({
      ...opt,
      correct: i === idx,
    }));
    setOptions(updated);
  };

  const handleSaveQuestion = async (e) => {
    e.preventDefault();
    try {
      let payload = {
        questionType,
        content: content.trim(),
        marks: Number(marks),
        negativeMarks: Number(negativeMarks),
        explanation: explanation.trim(),
      };

      if (questionType === 'MCQ') {
        const validOptions = options.map((opt, i) => ({
          optionText: opt.optionText.trim(),
          optionOrder: i + 1,
          correct: opt.correct,
        }));
        payload.options = validOptions;
      } else {
        payload.codingProblem = {
          starterCode,
          language,
          constraints,
          sampleInput,
          sampleOutput,
        };
      }

      await questionApi.addQuestion(examId, payload);
      setIsModalOpen(false);
      resetForm();
      fetchExamAndQuestions();
    } catch (err) {
      alert(err.message || 'Failed to add question');
    }
  };

  const handleDelete = async (questionId) => {
    if (window.confirm('Delete this question?')) {
      try {
        await questionApi.deleteQuestion(questionId);
        fetchExamAndQuestions();
      } catch (err) {
        alert(err.message || 'Failed to delete question');
      }
    }
  };

  const resetForm = () => {
    setContent('');
    setExplanation('');
    setMarks(2.0);
    setNegativeMarks(0.5);
    setQuestionType('MCQ');
    setOptions([
      { optionText: '', correct: true },
      { optionText: '', correct: false },
      { optionText: '', correct: false },
      { optionText: '', correct: false },
    ]);
  };

  if (loading) {
    return (
      <div className="container py-5 text-center">
        <div className="spinner-border text-primary" role="status"></div>
      </div>
    );
  }

  return (
    <div className="container py-4">
      {/* Breadcrumb Navigation */}
      <nav aria-label="breadcrumb" className="mb-3">
        <ol className="breadcrumb">
          <li className="breadcrumb-item">
            <Link to="/admin/exams">Examinations</Link>
          </li>
          <li className="breadcrumb-item active" aria-current="page">
            {exam?.title} (Question Bank)
          </li>
        </ol>
      </nav>

      {/* Header Info */}
      <div className="card shadow-sm border-0 mb-4 bg-primary text-white p-4 rounded-3">
        <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
          <div>
            <span className="badge bg-white text-primary mb-2">{exam?.categoryName}</span>
            <h3 className="fw-bold mb-1">{exam?.title}</h3>
            <p className="mb-0 text-white-50">
              Duration: {exam?.durationMinutes} mins | Total Marks: {exam?.totalMarks} | Pass Mark: {exam?.passingMarks} | Negative Mark: -{exam?.negativeMarkingRate}
            </p>
          </div>
          <button
            className="btn btn-light fw-semibold d-flex align-items-center gap-2 shadow-sm text-primary"
            onClick={() => {
              resetForm();
              setIsModalOpen(true);
            }}
          >
            <i className="bi bi-plus-circle-fill"></i> Add New Question
          </button>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {/* Questions List */}
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h5 className="fw-bold mb-0">Configured Questions ({questions.length})</h5>
      </div>

      {questions.length > 0 ? (
        <div className="d-flex flex-column gap-3">
          {questions.map((q, idx) => (
            <div key={q.id} className="card shadow-sm border-0 rounded-3">
              <div className="card-body p-4">
                <div className="d-flex justify-content-between align-items-start mb-2">
                  <div className="d-flex align-items-center gap-2">
                    <span className="badge bg-secondary">Q{idx + 1}</span>
                    <span className={`badge ${q.questionType === 'MCQ' ? 'bg-info-subtle text-info' : 'bg-warning-subtle text-dark'}`}>
                      {q.questionType}
                    </span>
                    <span className="text-muted small">
                      +{q.marks} Marks {q.negativeMarks > 0 && `(-${q.negativeMarks} Neg)`}
                    </span>
                  </div>
                  <button
                    className="btn btn-outline-danger btn-sm"
                    onClick={() => handleDelete(q.id)}
                    title="Delete Question"
                  >
                    <i className="bi bi-trash"></i>
                  </button>
                </div>

                <div className="fw-semibold text-dark fs-5 mb-3">{q.content}</div>

                {/* MCQ Options Display */}
                {q.questionType === 'MCQ' && (
                  <div className="row g-2 mb-3">
                    {q.options?.map((opt, oIdx) => (
                      <div key={opt.id || oIdx} className="col-md-6">
                        <div
                          className={`p-2 rounded border small d-flex align-items-center justify-content-between ${
                            opt.correct ? 'bg-success-subtle border-success text-success-emphasis fw-bold' : 'bg-light'
                          }`}
                        >
                          <span>
                            <strong>{String.fromCharCode(65 + oIdx)}.</strong> {opt.optionText}
                          </span>
                          {opt.correct && (
                            <span className="badge bg-success">
                              <i className="bi bi-check-lg"></i> Correct
                            </span>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                )}

                {/* Coding Problem Display */}
                {q.questionType === 'CODING' && q.codingProblem && (
                  <div className="bg-light p-3 rounded border mb-3">
                    <div className="d-flex justify-content-between align-items-center mb-2">
                      <span className="fw-bold small text-muted text-uppercase">Starter Template ({q.codingProblem.language})</span>
                    </div>
                    <pre className="p-2 bg-dark text-white rounded font-monospace small mb-2" style={{ maxHeight: '140px' }}>
                      {q.codingProblem.starterCode}
                    </pre>
                  </div>
                )}

                {/* Explanation */}
                {q.explanation && (
                  <div className="bg-light-subtle p-2 px-3 rounded border-start border-3 border-primary small text-muted">
                    <strong>Solution Explanation:</strong> {q.explanation}
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="card shadow-sm border-0 text-center py-5">
          <div className="card-body">
            <i className="bi bi-patch-question fs-1 text-muted mb-3 d-inline-block"></i>
            <h5>No questions created yet</h5>
            <p className="text-muted">Start adding MCQ and coding questions to enable student tests.</p>
            <button className="btn btn-primary btn-sm" onClick={() => setIsModalOpen(true)}>
              <i className="bi bi-plus-lg me-1"></i> Add First Question
            </button>
          </div>
        </div>
      )}

      {/* Add Question Modal */}
      {isModalOpen && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
            <div className="modal-content border-0 shadow-lg">
              <div className="modal-header bg-dark text-white">
                <h5 className="modal-title fw-bold">
                  <i className="bi bi-patch-plus me-2 text-primary"></i> Add Question to Exam
                </h5>
                <button type="button" className="btn-close btn-close-white" onClick={() => setIsModalOpen(false)}></button>
              </div>

              <form onSubmit={handleSaveQuestion}>
                <div className="modal-body p-4">
                  <div className="row g-3 mb-3">
                    <div className="col-md-6">
                      <label className="form-label small fw-semibold">Question Type</label>
                      <select
                        className="form-select"
                        value={questionType}
                        onChange={(e) => setQuestionType(e.target.value)}
                      >
                        <option value="MCQ">Multiple Choice (MCQ)</option>
                        <option value="CODING">Hands-on Coding Assessment</option>
                      </select>
                    </div>

                    <div className="col-md-3">
                      <label className="form-label small fw-semibold">Marks Awarded</label>
                      <input
                        type="number"
                        step="0.5"
                        min="0.5"
                        className="form-control"
                        value={marks}
                        onChange={(e) => setMarks(e.target.value)}
                        required
                      />
                    </div>

                    <div className="col-md-3">
                      <label className="form-label small fw-semibold">Negative Marking</label>
                      <input
                        type="number"
                        step="0.05"
                        min="0"
                        className="form-control"
                        value={negativeMarks}
                        onChange={(e) => setNegativeMarks(e.target.value)}
                      />
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Question Content / Problem Statement *</label>
                    <textarea
                      className="form-control"
                      rows={3}
                      placeholder="Write your question or programming task description..."
                      value={content}
                      onChange={(e) => setContent(e.target.value)}
                      required
                    />
                  </div>

                  {/* MCQ Options Config */}
                  {questionType === 'MCQ' && (
                    <div className="mb-3">
                      <label className="form-label small fw-semibold d-flex justify-content-between">
                        <span>Answer Choices (Select radio button for Correct Answer) *</span>
                      </label>
                      {options.map((opt, i) => (
                        <div key={i} className="input-group mb-2">
                          <div className="input-group-text bg-white">
                            <input
                              className="form-check-input mt-0"
                              type="radio"
                              name="correctOptionRadio"
                              checked={opt.correct}
                              onChange={() => handleCorrectOptionSelect(i)}
                              title="Mark as correct answer"
                            />
                            <span className="ms-2 fw-semibold">{String.fromCharCode(65 + i)}</span>
                          </div>
                          <input
                            type="text"
                            className="form-control"
                            placeholder={`Option ${String.fromCharCode(65 + i)} text`}
                            value={opt.optionText}
                            onChange={(e) => handleOptionChange(i, e.target.value)}
                            required
                          />
                        </div>
                      ))}
                    </div>
                  )}

                  {/* Coding Question Config */}
                  {questionType === 'CODING' && (
                    <div className="mb-3">
                      <div className="row g-2 mb-2">
                        <div className="col-md-6">
                          <label className="form-label small fw-semibold">Programming Language</label>
                          <input
                            type="text"
                            className="form-control form-control-sm"
                            value={language}
                            onChange={(e) => setLanguage(e.target.value)}
                          />
                        </div>
                        <div className="col-md-6">
                          <label className="form-label small fw-semibold">Constraints</label>
                          <input
                            type="text"
                            className="form-control form-control-sm"
                            value={constraints}
                            onChange={(e) => setConstraints(e.target.value)}
                          />
                        </div>
                      </div>

                      <div className="mb-2">
                        <label className="form-label small fw-semibold">Starter Code</label>
                        <textarea
                          className="form-control font-monospace small"
                          rows={4}
                          value={starterCode}
                          onChange={(e) => setStarterCode(e.target.value)}
                        />
                      </div>

                      <div className="row g-2">
                        <div className="col-md-6">
                          <label className="form-label small fw-semibold">Sample Input</label>
                          <textarea
                            className="form-control form-control-sm font-monospace"
                            rows={2}
                            value={sampleInput}
                            onChange={(e) => setSampleInput(e.target.value)}
                          />
                        </div>
                        <div className="col-md-6">
                          <label className="form-label small fw-semibold">Sample Output</label>
                          <textarea
                            className="form-control form-control-sm font-monospace"
                            rows={2}
                            value={sampleOutput}
                            onChange={(e) => setSampleOutput(e.target.value)}
                          />
                        </div>
                      </div>
                    </div>
                  )}

                  <div className="mb-2">
                    <label className="form-label small fw-semibold">Solution Explanation / Rationale</label>
                    <textarea
                      className="form-control"
                      rows={2}
                      placeholder="Explain why the answer is correct for candidate feedback and review..."
                      value={explanation}
                      onChange={(e) => setExplanation(e.target.value)}
                    />
                  </div>
                </div>

                <div className="modal-footer bg-light">
                  <button type="button" className="btn btn-secondary" onClick={() => setIsModalOpen(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary fw-semibold">
                    Save Question
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default QuestionManagePage;
