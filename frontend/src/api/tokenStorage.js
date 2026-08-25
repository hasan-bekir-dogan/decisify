const REFRESH_TOKEN_KEY = 'decisify.refreshToken'

let accessToken = null

export const tokenStorage = {
  getAccessToken() {
    return accessToken
  },

  setAccessToken(token) {
    accessToken = token
  },

  clearAccessToken() {
    accessToken = null
  },

  getRefreshToken() {
    return sessionStorage.getItem(REFRESH_TOKEN_KEY)
  },

  setRefreshToken(token) {
    sessionStorage.setItem(REFRESH_TOKEN_KEY, token)
  },

  clearRefreshToken() {
    sessionStorage.removeItem(REFRESH_TOKEN_KEY)
  },

  clear() {
    accessToken = null
    sessionStorage.removeItem(REFRESH_TOKEN_KEY)
  },
}