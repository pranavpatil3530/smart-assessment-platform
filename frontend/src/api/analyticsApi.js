import axiosClient from './axiosClient';

export const analyticsApi = {
  getOverview: () => axiosClient.get('/api/admin/analytics/overview'),
  getExamAttempts: (examId, page = 0, size = 10) =>
    axiosClient.get(`/api/admin/analytics/exams/${examId}/attempts`, {
      params: { page, size },
    }),
};
