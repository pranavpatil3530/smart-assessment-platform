import axiosClient from './axiosClient';

export const authApi = {
  login: (credentials) => axiosClient.post('/api/auth/login', credentials),
  register: (userData) => axiosClient.post('/api/auth/register', userData),
  getCurrentUser: () => axiosClient.get('/api/auth/me'),
};
