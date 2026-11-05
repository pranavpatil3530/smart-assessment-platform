import React from 'react';

const Pagination = ({ currentPage, totalPages, totalElements, pageSize, onPageChange }) => {
  if (totalPages <= 1) return null;

  const pages = [];
  const startPage = Math.max(0, currentPage - 2);
  const endPage = Math.min(totalPages - 1, currentPage + 2);

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }

  const startRecord = currentPage * pageSize + 1;
  const endRecord = Math.min((currentPage + 1) * pageSize, totalElements);

  return (
    <div className="d-flex flex-column flex-md-row justify-content-between align-items-center mt-4 gap-2">
      <div className="text-muted small">
        Showing <strong>{startRecord}</strong> to <strong>{endRecord}</strong> of{' '}
        <strong>{totalElements}</strong> entries
      </div>

      <nav aria-label="Page navigation">
        <ul className="pagination pagination-sm mb-0">
          <li className={`page-item ${currentPage === 0 ? 'disabled' : ''}`}>
            <button
              className="page-link"
              onClick={() => onPageChange(currentPage - 1)}
              disabled={currentPage === 0}
            >
              <i className="bi bi-chevron-left"></i>
            </button>
          </li>

          {startPage > 0 && (
            <>
              <li className="page-item">
                <button className="page-link" onClick={() => onPageChange(0)}>
                  1
                </button>
              </li>
              {startPage > 1 && <li className="page-item disabled"><span className="page-link">...</span></li>}
            </>
          )}

          {pages.map((p) => (
            <li key={p} className={`page-item ${p === currentPage ? 'active' : ''}`}>
              <button className="page-link" onClick={() => onPageChange(p)}>
                {p + 1}
              </button>
            </li>
          ))}

          {endPage < totalPages - 1 && (
            <>
              {endPage < totalPages - 2 && <li className="page-item disabled"><span className="page-link">...</span></li>}
              <li className="page-item">
                <button className="page-link" onClick={() => onPageChange(totalPages - 1)}>
                  {totalPages}
                </button>
              </li>
            </>
          )}

          <li className={`page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`}>
            <button
              className="page-link"
              onClick={() => onPageChange(currentPage + 1)}
              disabled={currentPage === totalPages - 1}
            >
              <i className="bi bi-chevron-right"></i>
            </button>
          </li>
        </ul>
      </nav>
    </div>
  );
};

export default Pagination;
