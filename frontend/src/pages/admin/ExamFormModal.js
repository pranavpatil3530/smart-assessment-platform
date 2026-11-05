import React, { useState, useEffect } from 'react';
import { examApi } from '../../api/examApi';

const ExamFormModal = ({ isOpen, onClose, onSave, onSaved, examToEdit, categories }) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    categoryId: '',
    durationMinutes: 30,
    totalMarks: 20,
    passingMarks: 12,
    negativeMarkingRate: 0.25,
    maxAttempts: 3,
    randomizeQuestions: true,
    randomizeOptions: true,
    published: false,
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (examToEdit) {
      setFormData({
        title: examToEdit.title || '',
        description: examToEdit.description || '',
        categoryId: examToEdit.categoryId || (categories.length > 0 ? categories[0].id : ''),
        durationMinutes: examToEdit.durationMinutes || 30,
        totalMarks: examToEdit.totalMarks || 20,
        passingMarks: examToEdit.passingMarks || 12,
        negativeMarkingRate: examToEdit.negativeMarkingRate != null ? examToEdit.negativeMarkingRate : 0.25,
        maxAttempts: examToEdit.maxAttempts || 3,
        randomizeQuestions: examToEdit.randomizeQuestions ?? true,
        randomizeOptions: examToEdit.randomizeOptions ?? true,
        published: examToEdit.published ?? false,
      });
    } else {
      setFormData({
        title: '',
        description: '',
        categoryId: categories.length > 0 ? categories[0].id : '',
        durationMinutes: 30,
        totalMarks: 20,
        passingMarks: 12,
        negativeMarkingRate: 0.25,
        maxAttempts: 3,
        randomizeQuestions: true,
        randomizeOptions: true,
        published: false,
      });
    }
    setError('');
  }, [examToEdit, categories, isOpen]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (Number(formData.passingMarks) > Number(formData.totalMarks)) {
      setError('Passing marks cannot exceed total marks');
      return;
    }

    setLoading(true);
    try {
      const payload = {
        ...formData,
        categoryId: Number(formData.categoryId),
        durationMinutes: Number(formData.durationMinutes),
        totalMarks: Number(formData.totalMarks),
        passingMarks: Number(formData.passingMarks),
        negativeMarkingRate: Number(formData.negativeMarkingRate),
        maxAttempts: Number(formData.maxAttempts),
      };

      if (examToEdit && examToEdit.id) {
        await examApi.updateExam(examToEdit.id, payload);
      } else {
        await examApi.createExam(payload);
      }
      const notifySaved = onSaved || onSave;
      if (typeof notifySaved === 'function') {
        notifySaved();
      }
      if (typeof onClose === 'function') {
        onClose();
      }
    } catch (err) {
      setError(err.message || 'Failed to save examination.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
      <div className="modal-dialog modal-lg modal-dialog-centered">
        <div className="modal-content border-0 shadow-lg">
          <div className="modal-header bg-primary text-white">
            <h5 className="modal-title fw-bold">
              {examToEdit ? (
                <>
                  <i className="bi bi-pencil-square me-2"></i> Edit Examination
                </>
              ) : (
                <>
                  <i className="bi bi-plus-circle me-2"></i> Create New Examination
                </>
              )}
            </h5>
            <button type="button" className="btn-close btn-close-white" onClick={onClose}></button>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="modal-body p-4">
              {error && <div className="alert alert-danger small">{error}</div>}

              <div className="row g-3">
                <div className="col-md-8">
                  <label className="form-label small fw-semibold">Examination Title *</label>
                  <input
                    type="text"
                    name="title"
                    className="form-control"
                    placeholder="e.g. Java Microservices Assessment"
                    value={formData.title}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="col-md-4">
                  <label className="form-label small fw-semibold">Category *</label>
                  <select
                    name="categoryId"
                    className="form-select"
                    value={formData.categoryId}
                    onChange={handleChange}
                    required
                  >
                    {categories.map((cat) => (
                      <option key={cat.id} value={cat.id}>
                        {cat.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="col-12">
                  <label className="form-label small fw-semibold">Description / Syllabus</label>
                  <textarea
                    name="description"
                    className="form-control"
                    rows={2}
                    placeholder="Describe exam topics, expectations, and instructions..."
                    value={formData.description}
                    onChange={handleChange}
                  />
                </div>

                <div className="col-md-3">
                  <label className="form-label small fw-semibold">Duration (Minutes) *</label>
                  <input
                    type="number"
                    name="durationMinutes"
                    className="form-control"
                    min="1"
                    max="360"
                    value={formData.durationMinutes}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="col-md-3">
                  <label className="form-label small fw-semibold">Total Marks *</label>
                  <input
                    type="number"
                    name="totalMarks"
                    className="form-control"
                    min="1"
                    value={formData.totalMarks}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="col-md-3">
                  <label className="form-label small fw-semibold">Passing Marks *</label>
                  <input
                    type="number"
                    name="passingMarks"
                    className="form-control"
                    min="1"
                    value={formData.passingMarks}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="col-md-3">
                  <label className="form-label small fw-semibold">Negative Marking (Penalty)</label>
                  <input
                    type="number"
                    step="0.05"
                    name="negativeMarkingRate"
                    className="form-control"
                    min="0"
                    placeholder="e.g. 0.25"
                    value={formData.negativeMarkingRate}
                    onChange={handleChange}
                  />
                  <small className="text-muted" style={{ fontSize: '0.7rem' }}>
                    Marks deducted per wrong answer
                  </small>
                </div>

                <div className="col-md-3">
                  <label className="form-label small fw-semibold">Max Attempts per Student</label>
                  <input
                    type="number"
                    name="maxAttempts"
                    className="form-control"
                    min="1"
                    max="10"
                    value={formData.maxAttempts}
                    onChange={handleChange}
                  />
                </div>

                {/* Toggles */}
                <div className="col-md-9 d-flex flex-wrap align-items-center gap-4 pt-4">
                  <div className="form-check form-switch">
                    <input
                      className="form-check-input"
                      type="checkbox"
                      name="randomizeQuestions"
                      id="randomizeQuestions"
                      checked={formData.randomizeQuestions}
                      onChange={handleChange}
                    />
                    <label className="form-check-label small" htmlFor="randomizeQuestions">
                      Randomize Questions
                    </label>
                  </div>

                  <div className="form-check form-switch">
                    <input
                      className="form-check-input"
                      type="checkbox"
                      name="randomizeOptions"
                      id="randomizeOptions"
                      checked={formData.randomizeOptions}
                      onChange={handleChange}
                    />
                    <label className="form-check-label small" htmlFor="randomizeOptions">
                      Randomize Answer Options
                    </label>
                  </div>

                  <div className="form-check form-switch">
                    <input
                      className="form-check-input"
                      type="checkbox"
                      name="published"
                      id="published"
                      checked={formData.published}
                      onChange={handleChange}
                    />
                    <label className="form-check-label small fw-semibold text-primary" htmlFor="published">
                      Publish to Students Immediately
                    </label>
                  </div>
                </div>
              </div>
            </div>

            <div className="modal-footer bg-light">
              <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>
                Cancel
              </button>
              <button type="submit" className="btn btn-primary fw-semibold" disabled={loading}>
                {loading ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                    Saving...
                  </>
                ) : (
                  'Save Examination'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ExamFormModal;
