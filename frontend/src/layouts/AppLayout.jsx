import {Link, Outlet} from 'react-router-dom'

function AppLayout() {
    return (
        <div className="app-layout">
            <header className="app-header">
                <nav className="app-nav">
                <Link to="/dashboard" className="brand">
                    Decisify
                </Link>

                <div className="nav-links">
                    <Link to="/dashboard">Dashboard</Link>
                    <Link to="/login">Login</Link>
                    <Link to="/register">Register</Link>
                </div>
                </nav>
            </header>

            <main className="app-main">
                <Outlet />
            </main>
        </div>
    )
}

export default AppLayout