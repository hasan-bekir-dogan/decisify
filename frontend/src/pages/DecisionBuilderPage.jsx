import {useParams} from 'react-router-dom'

function DecisionBuilderPage() {
    const {id} = useParams()

    return (
        <section className="page">
            <h1>Decision Builder</h1>
            <p>Decision ID: {id}</p>
        </section>
    )
}

export default DecisionBuilderPage