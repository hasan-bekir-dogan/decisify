import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'

import { decisionApi } from '../api/decisionApi'
import { useAuth } from '../context/useAuth'

function DashboardPage() {
  const navigate = useNavigate()
  const { user } = useAuth()

  const [decisions, setDecisions] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')

  const [isFormOpen, setIsFormOpen] = useState(false)
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [createError, setCreateError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function loadDecisions() {
    setIsLoading(true)
    setError('')

    try {
      const data = await decisionApi.getDecisions()
      setDecisions(data)
    } catch (requestError) {
      setError(requestError.message ?? 'Failed to load decisions.')
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    async function run() {
      await loadDecisions()
    }

    run()
  }, [])

  async function handleCreateSubmit(event) {
    event.preventDefault()

    setCreateError('')
    setIsSubmitting(true)

    try {
      const decision = await decisionApi.createDecision({
        userId: user.id,
        title,
        description: description || undefined,
      })

      navigate(`/decisions/${decision.id}`)
    } catch (requestError) {
      setCreateError(requestError.message ?? 'Failed to create decision.')
      setIsSubmitting(false)
    }
  }

  return (
    <section className="page">
      <div className="dashboard-header">
        <h1>Dashboard</h1>

        <button
          type="button"
          onClick={() => setIsFormOpen((open) => !open)}
        >
          {isFormOpen ? 'Cancel' : 'New decision'}
        </button>
      </div>

      {isFormOpen && (
        <form className="auth-form" onSubmit={handleCreateSubmit}>
          <label>
            Title
            <input
              type="text"
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              required
            />
          </label>

          <label>
            Description
            <textarea
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              rows={3}
            />
          </label>

          {createError && <p className="form-error">{createError}</p>}

          <button type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Creating...' : 'Create decision'}
          </button>
        </form>
      )}

      {isLoading && <p>Loading decisions...</p>}

      {!isLoading && error && (
        <p className="form-error">{error}</p>
      )}

      {!isLoading && !error && decisions.length === 0 && (
        <p className="empty-state">
          No decisions yet. Create your first one above.
        </p>
      )}

      {!isLoading && !error && decisions.length > 0 && (
        <ul className="decision-list">
          {decisions.map((decision) => (
            <li key={decision.id} className="decision-card">
              <Link to={`/decisions/${decision.id}`}>
                <h3>{decision.title}</h3>
                {decision.description && <p>{decision.description}</p>}
                <span className="status-badge">{decision.status}</span>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}

export default DashboardPage