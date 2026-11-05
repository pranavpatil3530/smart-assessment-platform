import axiosClient from './axiosClient';

export const questionApi = {
  getQuestionsByExam: (examId) => axiosClient.get(`/api/admin/exams/${examId}/questions`),
  addQuestion: (examId, data) => axiosClient.post(`/api/admin/exams/${examId}/questions`, data),
  updateQuestion: (questionId, data) => axiosClient.put(`/api/admin/questions/${questionId}`, data),
  deleteQuestion: (questionId) => axiosClient.delete(`/api/admin/questions/${questionId}`),
};
