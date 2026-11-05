import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { examApi } from '../../api/examApi';
import Pagination from '../../components/Pagination';
import ExamFormModal from './ExamFormModal';

const ExamListPage = () => {
  const [exams, setExams] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Search & Filter State
  const [keyword, setKeyword] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 8;

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [examToEdit, setExamToEdit] = useState(null);

  const fetchCategories = async () => {
    try {
      const res = await examApi.getAllCategories();
      if (res.success) setCategories(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchExams = useCallback(async () => {
    setLoading(true);
    try {
      const res = await examApi.getAllExamsForAdmin({
        categoryId: selectedCategory || null,
        keyword: keyword || null,
        page,
        size: pageSize,
      });
      if (res.success) {
        setExams(res.data.content);
        setTotalPages(res.data.totalPages);
        setTotalElements(res.data.totalElements);
      }
    } catch (err) {
      setError(err.message || 'Failed to retrieve examinations.');
    } finally {
      setLoading(false);
    }
  }, [selectedCategory, keyword, page]);

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    fetchExams();
  }, [fetchExams]);

  const handleTogglePublish = async (examId, currentStatus) => {
    try {
      await examApi.togglePublish(examId, !currentStatus);
      fetchExams();
    } catch (err) {
      alert(err.message || 'Cannot toggle publish status');
    }
  };

  const handleDeleteExam = async (examId, title) => {
    if (window.confirm(`Are you sure you want to delete '${title}'? This will remove all questions and student attempts permanently.`)) {
      try {
        await examApi.deleteExam(examId);
        fetchExams();
      } catch (err) {
        alert(err.message || 'Failed to delete exam');
      }
    }
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchExams();
  };

  return (
    <div className="container py-4">
      {/* Header */}
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
        <div>
          <h2 className="fw-bold mb-1">Examination Management</h2>
          <p className="text-muted mb-0">Create, configure, publish, and monitor question banks</p>
        </div>
        <button
          className="btn btn-primary d-flex align-items-center gap-2 shadow-sm"
          onClick={() => {
            setExamToEdit(null);
            setIsModalOpen(true);
          }}
        >
          <i className="bi bi-plus-lg"></i> Create New Exam
        </button>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {/* Filter & Search Bar */}
      <div className="card shadow-sm border-0 mb-4">
        <div className="card-body p-3">
          <form onSubmit={handleSearchSubmit} className="row g-2 align-items-center">
            <div className="col-md-5">
              <div className="input-group">
                <span className="input-group-text bg-white border-end-0">
                  <i className="bi bi-search text-muted"></i>
                </span>
                <input
                  type="text"
                  className="form-control border-start-0"
                  placeholder="Search exams by title or keyword..."
                  value={keyword}
                  onChange={(e) => setKeyword(e.target.value)}
                />
              </div>
            </div>

            <div className="col-md-4">
              <select
                className="form-select"
                value={selectedCategory}
                onChange={(e) => {
                  setSelectedCategory(e.target.value);
                  setPage(0);
                }}
              >
                <option value="">All Subject Categories</option>
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.id}>
                    {cat.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="col-md-3 d-flex gap-2">
              <button type="submit" className="btn btn-outline-primary flex-grow-1">
                Filter
              </button>
              {(keyword || selectedCategory) && (
                <button
                  type="button"
                  className="btn btn-light"
                  onClick={() => {
                    setKeyword('');
                    setSelectedCategory('');
                    setPage(0);
                  }}
                >
                  Reset
                </button>
              )}
            </div>
          </form>
        </div>
      </div>

      {/* Table */}
      <div className="card shadow-sm border-0 rounded-3">
        <div className="card-body p-0">
          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-light text-muted small text-uppercase">
                <tr>
                  <th className="ps-4">Examination</th>
                  <th>Category</th>
                  <th>Duration</th>
                  <th>Marks</th>
                  <th>Questions</th>
                  <th>Status</th>
                  <th className="text-end pe-4">Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan="7" className="text-center py-5">
                      <div className="spinner-border text-primary spinner-border-sm me-2" role="status"></div>
                      Loading examinations...
                    </td>
                  </tr>
                ) : exams.length > 0 ? (
                  exams.map((exam) => (
                    <tr key={exam.id}>
                      <td className="ps-4">
                        <div className="fw-bold text-dark">{exam.title}</div>
                        <div className="text-muted small text-truncate" style={{ maxWidth: '300px' }}>
                          {exam.description || 'No description provided'}
                        </div>
                      </td>
                      <td>
                        <span className="badge bg-light text-primary border">{exam.categoryName}</span>
                      </td>
                      <td>
                        <i className="bi bi-clock me-1 text-muted"></i>
                        {exam.durationMinutes} mins
                      </td>
                      <td>
                        <span className="fw-semibold">{exam.totalMarks}</span> (Pass: {exam.passingMarks})
                        {exam.negativeMarkingRate > 0 && (
                          <div className="text-danger small" style={{ fontSize: '0.75rem' }}>
                            -{exam.negativeMarkingRate} neg
                          </div>
                        )}
                      </td>
                      <td>
                        <span className="badge bg-secondary-subtle text-dark">
                          {exam.totalQuestions} Questions
                        </span>
                      </td>
                      <td>
                        <div className="form-check form-switch">
                          <input
                            className="form-check-input"
                            type="checkbox"
                            role="switch"
                            checked={exam.published}
                            onChange={() => handleTogglePublish(exam.id, exam.published)}
                            title={exam.published ? 'Published to students' : 'Draft mode'}
                          />
                          <label className="form-check-label small ms-1">
                            {exam.published ? (
                              <span className="badge bg-success">Live</span>
                            ) : (
                              <span className="badge bg-secondary">Draft</span>
                            )}
                          </label>
                        </div>
                      </td>
                      <td className="text-end pe-4">
                        <div className="btn-group btn-group-sm">
                          <Link
                            to={`/admin/exams/${exam.id}/questions`}
                            className="btn btn-outline-primary"
                            title="Manage Questions"
                          >
                            <i className="bi bi-list-task me-1"></i> Questions
                          </Link>
                          <Link
                            to={`/admin/exams/${exam.id}/attempts`}
                            className="btn btn-outline-info"
                            title="Candidate Attempts"
                          >
                            <i className="bi bi-bar-chart"></i>
                          </Link>
                          <button
                            className="btn btn-outline-secondary"
                            onClick={() => {
                              setExamToEdit(exam);
                              setIsModalOpen(true);
                            }}
                            title="Edit Exam"
                          >
                            <i className="bi bi-pencil"></i>
                          </button>
                          <button
                            className="btn btn-outline-danger"
                            onClick={() => handleDeleteExam(exam.id, exam.title)}
                            title="Delete Exam"
                          >
                            <i className="bi bi-trash"></i>
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="7" className="text-center py-5 text-muted">
                      No examinations found matching your criteria.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Pagination */}
      <Pagination
        currentPage={page}
        totalPages={totalPages}
        totalElements={totalElements}
        pageSize={pageSize}
        onPageChange={(newPage) => setPage(newPage)}
      />

      {/* Create / Edit Modal */}
      <ExamFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={fetchExams}
        examToEdit={examToEdit}
        categories={categories}
      />
    </div>
  );
};

export default ExamListPage;
