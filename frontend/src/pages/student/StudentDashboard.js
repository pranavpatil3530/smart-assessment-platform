import React, { useState, useEffect, useCallback } from 'react';
import { examApi } from '../../api/examApi';
import Pagination from '../../components/Pagination';
import ExamInstructionModal from './ExamInstructionModal';

const StudentDashboard = () => {
  const [exams, setExams] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [keyword, setKeyword] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 6;

  // Selected exam for instruction modal
  const [selectedExam, setSelectedExam] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const fetchCategories = async () => {
    try {
      const res = await examApi.getAllCategories();
      if (res.success) setCategories(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchPublishedExams = useCallback(async () => {
    setLoading(true);
    try {
      const res = await examApi.getPublishedExams({
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
      setError(err.message || 'Failed to load examinations catalog.');
    } finally {
      setLoading(false);
    }
  }, [selectedCategory, keyword, page]);

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    fetchPublishedExams();
  }, [fetchPublishedExams]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchPublishedExams();
  };

  const handleStartClick = (exam) => {
    setSelectedExam(exam);
    setIsModalOpen(true);
  };

  return (
    <div className="container py-4">
      {/* Banner */}
      <div className="card border-0 shadow-sm rounded-4 overflow-hidden mb-4 bg-gradient" style={{ background: 'linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%)' }}>
        <div className="card-body p-4 p-md-5 text-white">
          <div className="row align-items-center">
            <div className="col-lg-8">
              <span className="badge bg-white text-primary mb-2 px-3 py-1 fw-bold">ONLINE CERTIFICATIONS</span>
              <h2 className="fw-bold mb-2">Technical Skill Assessments</h2>
              <p className="lead mb-0 text-white-50" style={{ fontSize: '1rem' }}>
                Validate your Java, Spring Boot, and problem solving skills with timed assessments, automated MCQ evaluation and coding workspaces.
              </p>
            </div>
            <div className="col-lg-4 text-lg-end mt-3 mt-lg-0">
              <div className="p-3 bg-white bg-opacity-10 rounded-3 d-inline-block text-start">
                <div className="small text-white-50">Active Assessments</div>
                <div className="fs-3 fw-bold">{totalElements} Available</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Filter Category Pills & Search */}
      <div className="row g-3 mb-4 align-items-center">
        <div className="col-lg-7">
          <div className="d-flex flex-wrap gap-2">
            <button
              className={`btn btn-sm rounded-pill px-3 ${!selectedCategory ? 'btn-primary' : 'btn-light border'}`}
              onClick={() => {
                setSelectedCategory('');
                setPage(0);
              }}
            >
              All Categories
            </button>
            {categories.map((cat) => (
              <button
                key={cat.id}
                className={`btn btn-sm rounded-pill px-3 ${selectedCategory === String(cat.id) ? 'btn-primary' : 'btn-light border'}`}
                onClick={() => {
                  setSelectedCategory(String(cat.id));
                  setPage(0);
                }}
              >
                {cat.name}
              </button>
            ))}
          </div>
        </div>

        <div className="col-lg-5">
          <form onSubmit={handleSearchSubmit} className="input-group">
            <input
              type="text"
              className="form-control"
              placeholder="Search assessment by title..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
            />
            <button className="btn btn-primary" type="submit">
              <i className="bi bi-search me-1"></i> Search
            </button>
          </form>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {/* Exam Grid */}
      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status"></div>
          <p className="text-muted mt-2">Loading assessments...</p>
        </div>
      ) : exams.length > 0 ? (
        <div className="row g-4">
          {exams.map((exam) => (
            <div key={exam.id} className="col-md-6 col-lg-4">
              <div className="card exam-card h-100 shadow-sm border-0 d-flex flex-column">
                <div className="card-body p-4 d-flex flex-column">
                  <div className="d-flex justify-content-between align-items-center mb-2">
                    <span className="badge bg-primary-subtle text-primary border border-primary-subtle">
                      {exam.categoryName}
                    </span>
                    <span className="text-muted small">
                      <i className="bi bi-clock me-1"></i> {exam.durationMinutes} Mins
                    </span>
                  </div>

                  <h5 className="fw-bold text-dark mb-2">{exam.title}</h5>
                  <p className="text-muted small flex-grow-1" style={{ minHeight: '40px' }}>
                    {exam.description || 'Comprehensive evaluation covering key competencies and problem solving.'}
                  </p>

                  <div className="bg-light p-3 rounded-3 mb-3 border">
                    <div className="row g-2 text-center small">
                      <div className="col-4 border-end">
                        <span className="text-muted d-block">Marks</span>
                        <strong className="text-dark">{exam.totalMarks}</strong>
                      </div>
                      <div className="col-4 border-end">
                        <span className="text-muted d-block">Passing</span>
                        <strong className="text-success">{exam.passingMarks}</strong>
                      </div>
                      <div className="col-4">
                        <span className="text-muted d-block">Negative</span>
                        <strong className={exam.negativeMarkingRate > 0 ? 'text-danger' : 'text-muted'}>
                          {exam.negativeMarkingRate > 0 ? `-${exam.negativeMarkingRate}` : 'None'}
                        </strong>
                      </div>
                    </div>
                  </div>

                  <button
                    className="btn btn-primary w-100 fw-semibold d-flex align-items-center justify-content-center gap-2 py-2 mt-auto"
                    onClick={() => handleStartClick(exam)}
                  >
                    <span>Start Assessment</span>
                    <i className="bi bi-arrow-right"></i>
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="card border-0 shadow-sm text-center py-5">
          <div className="card-body">
            <i className="bi bi-journal-x fs-1 text-muted mb-2 d-inline-block"></i>
            <h5>No published assessments available</h5>
            <p className="text-muted">Check back later or adjust your category filter.</p>
          </div>
        </div>
      )}

      {/* Pagination */}
      <Pagination
        currentPage={page}
        totalPages={totalPages}
        totalElements={totalElements}
        pageSize={pageSize}
        onPageChange={(p) => setPage(p)}
      />

      {/* Instruction Modal */}
      <ExamInstructionModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        exam={selectedExam}
      />
    </div>
  );
};

export default StudentDashboard;
