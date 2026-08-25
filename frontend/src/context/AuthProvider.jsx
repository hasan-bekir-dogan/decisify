import {
  useEffect,
  useState,
} from 'react'

import { tokenStorage } from '../api/tokenStorage'
import { AuthContext } from './authContext'
import { authApi } from '../api/authApi'

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [isLoading, setIsLoading] = useState(true)

  async function loadCurrentUser() {
    const currentUser = await authApi.getCurrentUser()
    setUser(currentUser)

    return currentUser
  }

  async function login(credentials) {
    const response = await authApi.login(credentials)

    tokenStorage.setAccessToken(response.accessToken)
    tokenStorage.setRefreshToken(response.refreshToken)

    return loadCurrentUser()
  }

  async function register(registration) {
    return authApi.register(registration)
  }

  function logout() {
    tokenStorage.clear()
    setUser(null)
  }

  useEffect(() => {
    async function restoreSession() {
      const refreshToken = tokenStorage.getRefreshToken()

      if (!refreshToken) {
        setIsLoading(false)
        return
      }

      try {
        const response = await authApi.refresh(refreshToken)

        tokenStorage.setAccessToken(response.accessToken)

        await loadCurrentUser()
      } catch {
        tokenStorage.clear()
        setUser(null)
      } finally {
        setIsLoading(false)
      }
    }

    restoreSession()
  }, [])

  const value = {
    user,
    isAuthenticated: user !== null,
    isLoading,
    login,
    register,
    logout,
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}