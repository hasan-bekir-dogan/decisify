import { apiClient } from './client'

export const decisionApi = {
  getDecisions(options = {}) {
    return apiClient.get('/decisions', options)
  },

  getDecision(decisionId, options = {}) {
    return apiClient.get(`/decisions/${decisionId}`, options)
  },

  createDecision(decision, options = {}) {
    return apiClient.post('/decisions', decision, options)
  },

  updateDecision(decisionId, decision, options = {}) {
    return apiClient.put(`/decisions/${decisionId}`, decision, options)
  },

  deleteDecision(decisionId, options = {}) {
    return apiClient.delete(`/decisions/${decisionId}`, options)
  },

  calculateDecision(decisionId, options = {}) {
    return apiClient.post(
      `/decisions/${decisionId}/calculate`,
      undefined,
      options,
    )
  },

  getRecommendation(decisionId, options = {}) {
    return apiClient.get(
      `/decisions/${decisionId}/recommendation`,
      options,
    )
  },

  getAlternatives(decisionId, options = {}) {
    return apiClient.get(
      `/decisions/${decisionId}/alternatives`,
      options,
    )
  },

  createAlternative(decisionId, alternative, options = {}) {
    return apiClient.post(
      `/decisions/${decisionId}/alternatives`,
      alternative,
      options,
    )
  },

  deleteAlternative(decisionId, alternativeId, options = {}) {
    return apiClient.delete(
      `/decisions/${decisionId}/alternatives/${alternativeId}`,
      options,
    )
  },

  getCriteria(decisionId, options = {}) {
    return apiClient.get(
      `/decisions/${decisionId}/criteria`,
      options,
    )
  },

  createCriterion(decisionId, criterion, options = {}) {
    return apiClient.post(
      `/decisions/${decisionId}/criteria`,
      criterion,
      options,
    )
  },

  deleteCriterion(decisionId, criterionId, options = {}) {
    return apiClient.delete(
      `/decisions/${decisionId}/criteria/${criterionId}`,
      options,
    )
  },

  getCriterionValues(decisionId, options = {}) {
    return apiClient.get(
      `/decisions/${decisionId}/values`,
      options,
    )
  },

  createCriterionValue(decisionId, value, options = {}) {
    return apiClient.post(
      `/decisions/${decisionId}/values`,
      value,
      options,
    )
  },
}