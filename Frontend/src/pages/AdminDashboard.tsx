import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { logoutUser } from "../services/api";
import AdminPolymersPage from "./AdminPolymersPage";
import AdminSolventsPage from "./AdminSolventsPage";
import AdminUsersPage from "./AdminUsersPage";
import "../styles/admin.css";


export default function AdminDashboard() {
    const [activePage, setActivePage] = useState("dashboard");
    const navigate  = useNavigate();
    const user      = JSON.parse(localStorage.getItem("user") || "{}");

    const handleLogout = async () => {
        try { await logoutUser(); } catch { /* ignore */ }
        finally { localStorage.removeItem("user"); navigate("/"); }
    };

    const menuItems = [
        { key: "dashboard", label: "Dashboard",  icon: "▦" },
        { key: "polymers",  label: "Polymers",   icon: "▣" },
        { key: "solvents",  label: "Solvents",   icon: "◔" },
        { key: "users",     label: "Users",       icon: "👤" },
    ];

    const renderPage = () => {
        switch (activePage) {
            case "polymers": return <AdminPolymersPage />;
            case "solvents": return <AdminSolventsPage />;
            case "users":    return <AdminUsersPage />;
            default:         return <AdminHome user={user} onNavigate={setActivePage} />;
        }
    };

    return (
        <div className="admin-layout">
            {/* Sidebar */}
            <aside className="admin-sidebar">
                <div>
                    <div className="admin-brand">
                        <div className="admin-logo">🔬</div>
                        <div>
                            <h2>SolveMate</h2>
                            <p>Admin Panel</p>
                        </div>
                    </div>
                    <nav className="admin-nav">
                        {menuItems.map(item => (
                            <button
                                key={item.key}
                                className={`admin-nav-item ${activePage === item.key ? "active" : ""}`}
                                onClick={() => setActivePage(item.key)}
                            >
                                <span className="admin-nav-icon">{item.icon}</span>
                                {item.label}
                            </button>
                        ))}
                    </nav>
                </div>
                <div className="admin-sidebar-footer">
                    <div className="admin-user-info">
                        <div className="admin-avatar">A</div>
                        <div>
                            <p className="admin-user-name">{user.fullName || "Admin"}</p>
                            <p className="admin-user-role">Administrator</p>
                        </div>
                    </div>
                    <button className="admin-logout-btn" onClick={handleLogout}>Logout</button>
                </div>
            </aside>

            {/* Main Content */}
            <main className="admin-main">
                {renderPage()}
            </main>
        </div>
    );
}

// ── Admin Home Stats ───────────────────────────────────────────────────────────
function AdminHome({ user, onNavigate }: { user: any; onNavigate: (p: string) => void }) {
    return (
        <div className="admin-home">
            <div className="admin-home-header">
                <h1>Welcome back, {user.fullName || "Admin"} 👋</h1>
                <p>Manage your SolveMate system from here</p>
            </div>

            <div className="admin-stats-grid">
                <div className="admin-stat-card blue">
                    <div className="admin-stat-icon">▣</div>
                    <div>
                        <h3>Polymers</h3>
                        <p>Manage polymer database</p>
                    </div>
                    <button onClick={() => onNavigate("polymers")}>Manage →</button>
                </div>
                <div className="admin-stat-card teal">
                    <div className="admin-stat-icon">◔</div>
                    <div>
                        <h3>Solvents</h3>
                        <p>Manage solvent catalog</p>
                    </div>
                    <button onClick={() => onNavigate("solvents")}>Manage →</button>
                </div>
                <div className="admin-stat-card purple">
                    <div className="admin-stat-icon">👤</div>
                    <div>
                        <h3>Users</h3>
                        <p>View registered users</p>
                    </div>
                    <button onClick={() => onNavigate("users")}>View →</button>
                </div>
            </div>

            <div className="admin-info-box">
                <h3>📌 Quick Guide</h3>
                <ul>
                    <li>Go to <strong>Polymers</strong> to add, edit or delete polymer records</li>
                    <li>Go to <strong>Solvents</strong> to manage the solvent catalog with HSP parameters</li>
                    <li>Go to <strong>Users</strong> to view all registered lab users</li>
                    <li>Lab users can run compatibility analysis from their own dashboard</li>
                </ul>
            </div>
        </div>
    );
}
