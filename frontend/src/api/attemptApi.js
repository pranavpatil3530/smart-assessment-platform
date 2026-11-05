import axiosClient from './axiosClient';

export const attemptApi = {
  startExam: (examId) => axiosClient.post(`/api/student/exams/${examId}/start`),
  submitExam: (attemptId, data, autoSubmit = false) =>
    axiosClient.post(`/api/student/attempts/${attemptId}/submit`, data, {
      params: { autoSubmit },
    }),
  getAttemptResult: (attemptId) => axiosClient.get(`/api/student/attempts/${attemptId}/result`),
  getMyHistory: (page = 0, size = 10) =>
    axiosClient.get('/api/student/attempts/my-history', {
      params: { page, size },
    }),
};
