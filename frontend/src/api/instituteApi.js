import axiosClient from './axiosClient';

export const instituteApi = {
  getProfile: () =>
    axiosClient.get('/api/institute/profile'),
  getCandidates: (page = 0, size = 10) =>
    axiosClient.get('/api/institute/candidates', { params: { page, size } }),
  getExamCandidates: (examId) =>
    axiosClient.get(`/api/institute/exams/${examId}/candidates`),
  assignCandidatesToExam: (examId, data) =>
    axiosClient.post(`/api/institute/exams/${examId}/assign`, data),
  getStats: () =>
    axiosClient.get('/api/institute/stats'),
};
