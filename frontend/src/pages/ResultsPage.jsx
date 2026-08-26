import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { decisionApi } from '../api/decisionApi'

function ResultsPage() {
  const { id } = useParams()

  const [result, setResult] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    async function calculateDecision() {
      try {
        setIsLoading(true)
        setError(null)

        const calculationResult =
          await decisionApi.calculateDecision(id)

        setResult(calculationResult)
      } catch (err) {
        setError(
          err.message || 'Failed to calculate decision'
        )
      } finally {
        setIsLoading(false)
      }
    }

    calculateDecision()
  }, [id])

  if (isLoading) {
    return (
      <section className="page">
        <p>Calculating decision...</p>
      </section>
    )
  }

  if (error) {
    return (
      <section className="page">
        <h1>Results</h1>
        <p>{error}</p>
      </section>
    )
  }

  const alternatives = result?.alternatives ?? []

  return (
    <section className="page">
      <h1>Results</h1>

      {alternatives.length === 0 ? (
        <p>No results available.</p>
      ) : (
        <>
          <h2>Ranking</h2>

          <ol>
            {alternatives.map((alternative) => (
              <li key={alternative.alternativeId}>
                <strong>
                  #{alternative.rank}{' '}
                  {alternative.alternativeName}
                </strong>

                {' — '}

                Score: {alternative.score}

                <h3>Criterion contributions</h3>

                {alternative.contributions?.length ? (
                  <table>
                    <thead>
                      <tr>
                        <th>Criterion</th>
                        <th>Normalized value</th>
                        <th>Weight</th>
                        <th>Contribution</th>
                      </tr>
                    </thead>

                    <tbody>
                      {alternative.contributions.map(
                        (contribution) => (
                          <tr
                            key={contribution.criterionId}
                          >
                            <td>
                              {contribution.criterionName}
                            </td>

                            <td>
                              {
                                contribution.normalizedValue
                              }
                            </td>

                            <td>
                              {contribution.weight}%
                            </td>

                            <td>
                              {contribution.contribution}
                            </td>
                          </tr>
                        )
                      )}
                    </tbody>
                  </table>
                ) : (
                  <p>
                    No contribution breakdown available.
                  </p>
                )}
              </li>
            ))}
          </ol>
        </>
      )}

      <h2>AI Explanation</h2>

      <p>
        AI-generated explanation will be available in
        the GenAI phase.
      </p>
    </section>
  )
}

export default ResultsPage