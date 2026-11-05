import React, { useState, useEffect, useCallback } from 'react';
import { examApi } from '../../api/examApi';
import { useAuth } from '../../context/AuthContext';
import Pagination from '../../components/Pagination';
import ExamInstructionModal from './ExamInstructionModal';

const StudentDashboard = () => {
  const { user } = useAuth();
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
    setError('');
    try {
      const res = await examApi.getPublishedExams({
        categoryId: selectedCategory || null,
        keyword: keyword || null,
        page,
        size: pageSize,
      });

      if (res.success) {
        setExams(res.data.content || []);
        setTotalPages(res.data.totalPages || 0);
        setTotalElements(res.data.totalElements || 0);
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
      {/* High-Contrast Hero Banner */}
      <div
        className="card border-0 shadow-sm rounded-4 overflow-hidden mb-4"
        style={{
          background: 'linear-gradient(135deg, #0f172a 0%, #1e3a8a 50%, #2563eb 100%)',
          color: '#ffffff',
        }}
      >
        <div className="card-body p-4 p-md-5">
          <div className="row align-items-center">
            <div className="col-lg-8">
              <div className="d-flex align-items-center gap-2 mb-3 flex-wrap">
                <span className="badge bg-white text-primary px-3 py-2 fw-bold text-uppercase" style={{ letterSpacing: '0.5px' }}>
                  <i className="bi bi-patch-check-fill me-1"></i> Online Certifications
                </span>
                {user?.instituteName ? (
                  <span className="badge bg-light text-dark px-3 py-2 fw-semibold">
                    <i className="bi bi-building me-1 text-primary"></i> {user.instituteName}
                  </span>
                ) : (
                  <span className="badge bg-light text-dark px-3 py-2 fw-semibold">
                    <i className="bi bi-globe me-1 text-primary"></i> General Candidate Access
                  </span>
                )}
              </div>
              <h1 className="fw-bold mb-2 text-white" style={{ fontSize: '2rem' }}>
                Technical Skill Assessments
              </h1>
              <p className="lead mb-0 text-white" style={{ fontSize: '1.05rem', opacity: 0.9 }}>
                Assigned examinations curated by your institute. Validate competencies with timed tests and automated evaluations.
              </p>
            </div>
            <div className="col-lg-4 text-lg-end mt-4 mt-lg-0">
              <div className="p-3 bg-white bg-opacity-10 border border-white border-opacity-25 rounded-3 d-inline-block text-start">
                <div className="small text-white" style={{ opacity: 0.85 }}>Available Assessments</div>
                <div className="fs-3 fw-bold text-white">{totalElements} Active</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Filter Category Pills & Search */}
      <div className="card shadow-sm border-0 mb-4 bg-light">
        <div className="card-body p-3">
          <div className="row g-3 align-items-center">
            <div className="col-lg-7">
              <div className="d-flex flex-wrap gap-2">
                <button
                  className={`btn btn-sm rounded-pill px-3 ${selectedCategory === '' ? 'btn-primary shadow-sm' : 'btn-outline-secondary bg-white'}`}
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
                    className={`btn btn-sm rounded-pill px-3 ${selectedCategory === cat.id ? 'btn-primary shadow-sm' : 'btn-outline-secondary bg-white'}`}
                    onClick={() => {
                      setSelectedCategory(cat.id);
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
                  placeholder="Search assessment by title or topic..."
                  value={keyword}
                  onChange={(e) => setKeyword(e.target.value)}
                />
                <button className="btn btn-primary" type="submit">
                  <i className="bi bi-search me-1"></i> Search
                </button>
                {keyword && (
                  <button
                    className="btn btn-outline-secondary"
                    type="button"
                    onClick={() => {
                      setKeyword('');
                      setPage(0);
                      fetchPublishedExams();
                    }}
                  >
                    Clear
                  </button>
                )}
              </form>
            </div>
          </div>
        </div>
      </div>

      {error && (
        <div className="alert alert-danger shadow-sm d-flex align-items-center gap-2 mb-4" role="alert">
          <i className="bi bi-exclamation-triangle-fill"></i>
          <span>{error}</span>
        </div>
      )}

      {/* Exam Grid */}
      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status"></div>
          <p className="text-muted mt-2 fw-semibold">Loading available assessments...</p>
        </div>
      ) : exams.length > 0 ? (
        <div className="row g-4">
          {exams.map((exam) => (
            <div key={exam.id} className="col-md-6 col-lg-4">
              <div className="card exam-card h-100 shadow-sm border d-flex flex-column bg-white">
                <div className="card-body p-4 d-flex flex-column">
                  <div className="d-flex justify-content-between align-items-center mb-2">
                    <span className="badge bg-primary-subtle text-primary border border-primary-subtle px-2 py-1">
                      {exam.categoryName}
                    </span>
                    <span className="text-muted small fw-semibold">
                      <i className="bi bi-clock me-1 text-primary"></i> {exam.durationMinutes} Mins
                    </span>
                  </div>

                  <h5 className="fw-bold text-dark mb-2">{exam.title}</h5>

                  <div className="mb-2">
                    {exam.instituteName ? (
                      <span className="badge bg-light text-secondary border small">
                        <i className="bi bi-building me-1"></i> {exam.instituteName}
                      </span>
                    ) : (
                      <span className="badge bg-light text-secondary border small">
                        <i className="bi bi-globe me-1"></i> Open Assessment
                      </span>
                    )}
                  </div>

                  <p className="text-muted small flex-grow-1 mb-3" style={{ minHeight: '40px', lineHeight: '1.4' }}>
                    {exam.description || 'Comprehensive evaluation covering key competencies and problem solving.'}
                  </p>

                  <div className="bg-light p-3 rounded-3 mb-3 border">
                    <div className="row g-2 text-center small">
                      <div className="col-4 border-end">
                        <span className="text-muted d-block" style={{ fontSize: '0.75rem' }}>TOTAL MARKS</span>
                        <strong className="text-dark fs-6">{exam.totalMarks}</strong>
                      </div>
                      <div className="col-4 border-end">
                        <span className="text-muted d-block" style={{ fontSize: '0.75rem' }}>PASSING</span>
                        <strong className="text-success fs-6">{exam.passingMarks}</strong>
                      </div>
                      <div className="col-4">
                        <span className="text-muted d-block" style={{ fontSize: '0.75rem' }}>NEGATIVE</span>
                        <strong className={exam.negativeMarkingRate > 0 ? 'text-danger fs-6' : 'text-muted fs-6'}>
                          {exam.negativeMarkingRate > 0 ? `-${exam.negativeMarkingRate}` : 'None'}
                        </strong>
                      </div>
                    </div>
                  </div>

                  <button
                    className="btn btn-primary w-100 fw-semibold d-flex align-items-center justify-content-center gap-2 py-2 mt-auto shadow-sm"
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
        <div className="card border-0 shadow-sm text-center py-5 bg-white rounded-4">
          <div className="card-body">
            <i className="bi bi-journal-check display-4 text-primary opacity-50 mb-3 d-inline-block"></i>
            <h4 className="fw-bold mb-2">No Active Assessments Found</h4>
            <p className="text-muted mx-auto mb-4" style={{ maxWidth: '500px' }}>
              {user?.instituteName ? (
                <>
                  You are enrolled under <strong>{user.instituteName}</strong>. Examinations assigned by your institute
                  administrator will appear here. If an exam is scheduled, ask your institute admin to assign it to you.
                </>
              ) : (
                'There are currently no active public assessments matching your filter criteria.'
              )}
            </p>
            <button
              className="btn btn-outline-primary btn-sm px-4"
              onClick={() => {
                setSelectedCategory('');
                setKeyword('');
                fetchPublishedExams();
              }}
            >
              <i className="bi bi-arrow-clockwise me-1"></i> Refresh Assessment List
            </button>
          </div>
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="mt-4">
          <Pagination
            currentPage={page}
            totalPages={totalPages}
            totalElements={totalElements}
            pageSize={pageSize}
            onPageChange={(p) => setPage(p)}
          />
        </div>
      )}

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
