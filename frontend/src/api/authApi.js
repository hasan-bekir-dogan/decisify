import { apiClient } from './client'

export const authApi = {
  register(credentials, options = {}) {
    return apiClient.post('/auth/register', credentials, options)
  },

  login(credentials, options = {}) {
    return apiClient.post('/auth/login', credentials, options)
  },

  refresh(refreshToken, options = {}) {
    return apiClient.post(
      '/auth/refresh',
      { refreshToken },
      options,
    )
  },

  getCurrentUser(options = {}) {
    return apiClient.get('/auth/me', options)
  },
}