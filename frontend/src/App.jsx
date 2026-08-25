import {
  BrowserRouter,
  Navigate,
  Route,
  Routes
} from 'react-router-dom'

import AppLayout from './layouts/AppLayout'
import DashboardPage from './pages/DashboardPage'
import DecisionBuilderPage from './pages/DecisionBuilderPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ResultsPage from './pages/ResultsPage'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="login" element={<LoginPage />}/>
          <Route path="register" element={<RegisterPage />} />
          <Route path="dashboard" element={<DashboardPage />} />
          <Route path="decisions/:id" element={<DecisionBuilderPage />} />
          <Route path="decisions/:id/results" element={<ResultsPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
