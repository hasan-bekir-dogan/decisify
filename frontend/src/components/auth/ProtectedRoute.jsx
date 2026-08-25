import { Navigate } from 'react-router-dom'

import { useAuth } from '../../context/useAuth'

function ProtectedRoute({ children }) {
  const { isAuthenticated, isLoading } = useAuth()

  if (isLoading) {
    return <p>Loading...</p>
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return children
}

export default ProtectedRoute