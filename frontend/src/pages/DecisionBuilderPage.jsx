import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { decisionApi } from '../api/decisionApi'

function DecisionBuilderPage() {
  const { id } = useParams()

  const [decision, setDecision] = useState(null)
  const [alternatives, setAlternatives] = useState([])
  const [criteria, setCriteria] = useState([])
  const [criterionValues, setCriterionValues] = useState([])

  const [alternativeName, setAlternativeName] = useState('')
  const [alternativeDescription, setAlternativeDescription] = useState('')
  const [alternativeError, setAlternativeError] = useState(null)

  const [criterionName, setCriterionName] = useState('')
  const [criterionWeight, setCriterionWeight] = useState('')
  const [criterionUnit, setCriterionUnit] = useState('')
  const [criterionHigherIsBetter, setCriterionHigherIsBetter] = useState(true)
  const [criterionError, setCriterionError] = useState(null)

  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState(null)

  const totalWeight = criteria.reduce(
    (sum, criterion) => sum + Number(criterion.weight),
    0
  )

  const hasMinimumAlternatives = alternatives.length >= 2
  const hasValidWeightTotal = Math.abs(totalWeight - 100) < 0.001

  function getCriterionValue(alternativeId, criterionId) {
    return criterionValues.find(
      (value) =>
        value.alternativeId === alternativeId &&
        value.criterionId === criterionId
    )
  }

  useEffect(() => {
    async function loadDecisionBuilder() {
      try {
        setIsLoading(true)
        setError(null)

        const [
          decisionData,
          alternativesData,
          criteriaData,
          criterionValuesData,
        ] = await Promise.all([
          decisionApi.getDecision(id),
          decisionApi.getAlternatives(id),
          decisionApi.getCriteria(id),
          decisionApi.getCriterionValues(id),
        ])

        setDecision(decisionData)
        setAlternatives(alternativesData)
        setCriteria(criteriaData)
        setCriterionValues(criterionValuesData)
      } catch (err) {
        setError(err.message || 'Failed to load decision')
      } finally {
        setIsLoading(false)
      }
    }

    loadDecisionBuilder()
  }, [id])

  async function handleAddAlternative(event) {
    event.preventDefault()

    if (!alternativeName.trim()) {
      setAlternativeError('Alternative name is required.')
      return
    }

    try {
      setAlternativeError(null)

      const created = await decisionApi.createAlternative(id, {
        name: alternativeName.trim(),
        description: alternativeDescription.trim(),
      })

      setAlternatives((current) => [...current, created])

      setAlternativeName('')
      setAlternativeDescription('')
    } catch (err) {
      setAlternativeError(
        err.message || 'Failed to create alternative'
      )
    }
  }

  async function handleDeleteAlternative(alternativeId) {
    try {
      setAlternativeError(null)

      await decisionApi.deleteAlternative(id, alternativeId)

      setAlternatives((current) =>
        current.filter(
          (alternative) => alternative.id !== alternativeId
        )
      )

      setCriterionValues((current) =>
        current.filter(
          (value) => value.alternativeId !== alternativeId
        )
      )
    } catch (err) {
      setAlternativeError(
        err.message || 'Failed to delete alternative'
      )
    }
  }

  async function handleAddCriterion(event) {
    event.preventDefault()

    if (!criterionName.trim()) {
      setCriterionError('Criterion name is required.')
      return
    }

    const weight = Number(criterionWeight)

    if (!Number.isFinite(weight) || weight < 0 || weight > 100) {
      setCriterionError('Weight must be between 0 and 100.')
      return
    }

    if (!criterionUnit.trim()) {
      setCriterionError('Unit is required.')
      return
    }

    try {
      setCriterionError(null)

      const created = await decisionApi.createCriterion(id, {
        name: criterionName.trim(),
        weight,
        unit: criterionUnit.trim(),
        higherIsBetter: criterionHigherIsBetter,
      })

      setCriteria((current) => [...current, created])

      setCriterionName('')
      setCriterionWeight('')
      setCriterionUnit('')
      setCriterionHigherIsBetter(true)
    } catch (err) {
      setCriterionError(
        err.message || 'Failed to create criterion'
      )
    }
  }

  async function handleDeleteCriterion(criterionId) {
    try {
      setCriterionError(null)

      await decisionApi.deleteCriterion(id, criterionId)

      setCriteria((current) =>
        current.filter(
          (criterion) => criterion.id !== criterionId
        )
      )

      setCriterionValues((current) =>
        current.filter(
          (value) => value.criterionId !== criterionId
        )
      )
    } catch (err) {
      setCriterionError(
        err.message || 'Failed to delete criterion'
      )
    }
  }

  async function handleValueChange(
    alternativeId,
    criterionId,
    rawValue,
  ) {
    if (rawValue === '') {
      return
    }

    const numericValue = Number(rawValue)

    if (!Number.isFinite(numericValue)) {
      return
    }

    try {
      const savedValue = await decisionApi.createCriterionValue(
        id,
        {
          alternativeId,
          criterionId,
          rawValue: numericValue,
        }
      )

      setCriterionValues((current) => {
        const existingIndex = current.findIndex(
          (value) =>
            value.alternativeId === alternativeId &&
            value.criterionId === criterionId
        )

        if (existingIndex === -1) {
          return [...current, savedValue]
        }

        return current.map((value, index) =>
          index === existingIndex ? savedValue : value
        )
      })
    } catch (err) {
      setError(
        err.message || 'Failed to save criterion value'
      )
    }
  }

  if (isLoading) {
    return (
      <section className="page">
        <p>Loading decision...</p>
      </section>
    )
  }

  if (error) {
    return (
      <section className="page">
        <h1>Decision Builder</h1>
        <p>{error}</p>
      </section>
    )
  }

  return (
    <section className="page">
      <h1>{decision?.title || 'Decision Builder'}</h1>

      {decision?.description && (
        <p>{decision.description}</p>
      )}

      <h2>Alternatives</h2>

      <form onSubmit={handleAddAlternative}>
        <div>
          <label>
            Name
            <input
              type="text"
              value={alternativeName}
              onChange={(event) =>
                setAlternativeName(event.target.value)
              }
              placeholder="e.g. MacBook Pro"
            />
          </label>
        </div>

        <div>
          <label>
            Description
            <input
              type="text"
              value={alternativeDescription}
              onChange={(event) =>
                setAlternativeDescription(event.target.value)
              }
              placeholder="Optional description"
            />
          </label>
        </div>

        <button type="submit">
          Add alternative
        </button>
      </form>

      {alternativeError && (
        <p>{alternativeError}</p>
      )}

      {alternatives.length === 0 ? (
        <p>No alternatives yet.</p>
      ) : (
        <ul>
          {alternatives.map((alternative) => (
            <li key={alternative.id}>
              <strong>{alternative.name}</strong>

              {alternative.description && (
                <> — {alternative.description}</>
              )}

              {' '}

              <button
                type="button"
                onClick={() =>
                  handleDeleteAlternative(alternative.id)
                }
              >
                Remove
              </button>
            </li>
          ))}
        </ul>
      )}

      <h2>Criteria</h2>

      <form onSubmit={handleAddCriterion}>
        <div>
          <label>
            Name
            <input
              type="text"
              value={criterionName}
              onChange={(event) =>
                setCriterionName(event.target.value)
              }
              placeholder="e.g. Price"
            />
          </label>
        </div>

        <div>
          <label>
            Weight (%)
            <input
              type="number"
              min="0"
              max="100"
              value={criterionWeight}
              onChange={(event) =>
                setCriterionWeight(event.target.value)
              }
              placeholder="e.g. 40"
            />
          </label>
        </div>

        <div>
          <label>
            Unit
            <input
              type="text"
              value={criterionUnit}
              onChange={(event) =>
                setCriterionUnit(event.target.value)
              }
              placeholder="e.g. EUR"
            />
          </label>
        </div>

        <div>
          <label>
            <input
              type="checkbox"
              checked={criterionHigherIsBetter}
              onChange={(event) =>
                setCriterionHigherIsBetter(
                  event.target.checked
                )
              }
            />
            Higher is better
          </label>
        </div>

        <button type="submit">
          Add criterion
        </button>
      </form>

      {criterionError && (
        <p>{criterionError}</p>
      )}

      {criteria.length === 0 ? (
        <p>No criteria yet.</p>
      ) : (
        <ul>
          {criteria.map((criterion) => (
            <li key={criterion.id}>
              <strong>{criterion.name}</strong>
              {' — '}
              {criterion.weight}% — {criterion.unit}
              {' — '}
              {criterion.higherIsBetter
                ? 'Higher is better'
                : 'Lower is better'}
              {' '}

              <button
                type="button"
                onClick={() =>
                  handleDeleteCriterion(criterion.id)
                }
              >
                Remove
              </button>
            </li>
          ))}
        </ul>
      )}

      <p>
        Total weight: {totalWeight}%
      </p>

      {criteria.length > 0 && !hasValidWeightTotal && (
        <p>
          Criterion weights must add up to 100%.
        </p>
      )}

      <h2>Values</h2>

      {alternatives.length < 2 ? (
        <p>
          Add at least two alternatives to enter values.
        </p>
      ) : criteria.length === 0 ? (
        <p>
          Add at least one criterion to enter values.
        </p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Alternative</th>

              {criteria.map((criterion) => (
                <th key={criterion.id}>
                  {criterion.name}
                  {criterion.unit &&
                    ` (${criterion.unit})`}
                </th>
              ))}
            </tr>
          </thead>

          <tbody>
            {alternatives.map((alternative) => (
              <tr key={alternative.id}>
                <td>{alternative.name}</td>

                {criteria.map((criterion) => {
                  const existingValue =
                    getCriterionValue(
                      alternative.id,
                      criterion.id
                    )

                  return (
                    <td key={criterion.id}>
                      <input
                        type="number"
                        step="any"
                        defaultValue={
                          existingValue?.rawValue ?? ''
                        }
                        onBlur={(event) =>
                          handleValueChange(
                            alternative.id,
                            criterion.id,
                            event.target.value,
                          )
                        }
                      />
                    </td>
                  )
                })}
              </tr>
            ))}

            {!hasMinimumAlternatives && (
              <p>
                At least two alternatives are required.
              </p>
            )}
          </tbody>
        </table>
      )}

      <h2>Validation</h2>

      <ul>
        <li>
          Alternatives: {hasMinimumAlternatives ? 'Valid' : 'At least 2 required'}
        </li>

        <li>
          Criteria weights:{' '}
          {hasValidWeightTotal
            ? 'Valid (100%)'
            : `Invalid (${totalWeight}%)`}
        </li>
      </ul>
    </section>
  )
}

export default DecisionBuilderPage