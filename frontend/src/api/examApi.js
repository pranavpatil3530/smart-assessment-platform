import axiosClient from './axiosClient';

export const examApi = {
  // Public/Student APIs
  getPublishedExams: (params = {}) => {
    const { categoryId, keyword, page = 0, size = 9, sortBy = 'createdAt', sortDir = 'desc' } = params;
    return axiosClient.get('/api/exams/published', {
      params: { categoryId, keyword, page, size, sortBy, sortDir },
    });
  },

  getExamDetails: (id) => axiosClient.get(`/api/exams/${id}`),

  // Categories
  getAllCategories: () => axiosClient.get('/api/categories'),
  createCategory: (data) => axiosClient.post('/api/categories', data),

  // Admin APIs
  getAllExamsForAdmin: (params = {}) => {
    const { categoryId, keyword, page = 0, size = 10, sortBy = 'createdAt', sortDir = 'desc' } = params;
    return axiosClient.get('/api/admin/exams', {
      params: { categoryId, keyword, page, size, sortBy, sortDir },
    });
  },

  createExam: (data) => axiosClient.post('/api/admin/exams', data),
  updateExam: (id, data) => axiosClient.put(`/api/admin/exams/${id}`, data),
  deleteExam: (id) => axiosClient.delete(`/api/admin/exams/${id}`),
  togglePublish: (id, published) =>
    axiosClient.patch(`/api/admin/exams/${id}/publish`, null, {
      params: { published },
    }),
};
