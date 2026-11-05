import axiosClient from './axiosClient';

export const superAdminApi = {
  getInstitutes: (status, page = 0, size = 10) =>
    axiosClient.get('/api/super-admin/institutes', { params: { status: status || undefined, page, size } }),
  getPendingInstitutes: (page = 0, size = 10) =>
    axiosClient.get('/api/super-admin/institutes/pending', { params: { page, size } }),
  getInstituteById: (id) =>
    axiosClient.get(`/api/super-admin/institutes/${id}`),
  approveInstitute: (id) =>
    axiosClient.put(`/api/super-admin/institutes/${id}/approve`),
  rejectInstitute: (id, reason) =>
    axiosClient.put(`/api/super-admin/institutes/${id}/reject`, { reason }),
  getStats: () =>
    axiosClient.get('/api/super-admin/stats'),
};
