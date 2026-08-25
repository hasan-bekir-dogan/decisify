import { useParams } from 'react-router-dom'

function ResultsPage() {
    const { id } = useParams()

    return (
        <section className="page">
            <h1>Decision Results</h1>
            <p>Decision ID: {id}</p>
        </section>
    )
}

export default ResultsPage