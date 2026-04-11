import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { logoutUser } from "../services/api";
import AdminPolymersPage from "./AdminPolymersPage";
import AdminSolventsPage from "./AdminSolventsPage";
import AdminUsersPage from "./AdminUsersPage";
import AdminParametersPage from "./AdminParametersPage";
import "../styles/admin.css";

export default function AdminDashboard() {
    const [activePage, setActivePage] = useState("dashboard");
    const navigate = useNavigate();
    const user = JSON.parse(localStorage.getItem("user") || "{}");

    const handleLogout = async () => {
        try { await logoutUser(); } catch { }
        finally { localStorage.removeItem("user"); navigate("/"); }
    };

    const menuItems = [
        { key: "dashboard",   label: "Dashboard",   icon: "⊞" },
        { key: "polymers",    label: "Polymers",     icon: "▣" },
        { key: "solvents",    label: "Solvents",     icon: "◎" },
        { key: "users",       label: "Users",        icon: "👤" },
        { key: "parameters",  label: "Parameters",   icon: "⚙" },
    ];

    const renderPage = () => {
        switch (activePage) {
            case "polymers":   return <AdminPolymersPage />;
            case "solvents":   return <AdminSolventsPage />;
            case "users":      return <AdminUsersPage />;
            case "parameters": return <AdminParametersPage />;
            default:           return <AdminHome onNavigate={setActivePage} />;
        }
    };

    return (
        <div className="admin-layout">
            <aside className="admin-sidebar">
                <div>
                    <div className="admin-brand">
                        <div className="admin-logo">🔬</div>
                        <div>
                            <h2>SolveMate</h2>
                            <p>Lab System</p>
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
                        <p className="admin-user-name">{user.fullName || "Admin User"}</p>
                        <p className="admin-user-email">{user.email || "admin@solvemate.lab"}</p>
                        <span className="admin-user-role">Admin</span>
                    </div>
                    <button className="admin-logout-btn" onClick={handleLogout}>
                        ↪ Logout
                    </button>
                </div>
            </aside>
            <main className="admin-main">
                {renderPage()}
            </main>
        </div>
    );
}

function AdminHome({ onNavigate }: { onNavigate: (p: string) => void }) {
    const activityItems = [
        { text: "Trial by Lab User",       date: "11/04/2026" },
        { text: "Polymer added: ABS",      date: "10/04/2026" },
        { text: "Solvent added: Acetone",  date: "09/04/2026" },
        { text: "User registered",         date: "08/04/2026" },
    ];

    const topSolvents = [
        { name: "Acetone",    pct: "91%" },
        { name: "Xylene",     pct: "87%" },
        { name: "Cyclohexane",pct: "82%" },
        { name: "Ethanol",    pct: "78%" },
    ];

    return (
        <>
            <h1 className="admin-page-title">Administrator Dashboard</h1>
            <p className="admin-page-subtitle">System overview and management tools.</p>

            <div className="admin-stats-grid">
                <div className="admin-stat-card">
                    <div className="admin-stat-header">
                        <p className="admin-stat-label">Total Users</p>
                        <span className="admin-stat-icon blue">👥</span>
                    </div>
                    <p className="admin-stat-value">3</p>
                    <p className="admin-stat-sub">Active accounts</p>
                </div>
                <div className="admin-stat-card">
                    <div className="admin-stat-header">
                        <p className="admin-stat-label">Total Polymers</p>
                        <span className="admin-stat-icon teal">▣</span>
                    </div>
                    <p className="admin-stat-value">10</p>
                    <p className="admin-stat-sub">In database</p>
                </div>
                <div className="admin-stat-card">
                    <div className="admin-stat-header">
                        <p className="admin-stat-label">Total Solvents</p>
                        <span className="admin-stat-icon green">◎</span>
                    </div>
                    <p className="admin-stat-value">80</p>
                    <p className="admin-stat-sub">In database</p>
                </div>
                <div className="admin-stat-card">
                    <div className="admin-stat-header">
                        <p className="admin-stat-label">Regulations</p>
                        <span className="admin-stat-icon purple">⚙</span>
                    </div>
                    <p className="admin-stat-value">2</p>
                    <p className="admin-stat-sub">Recently updated</p>
                </div>
            </div>

            <p className="admin-section-title">Admin Actions</p>
            <div className="admin-actions-grid">
                <button className="admin-action-card" onClick={() => onNavigate("polymers")}>
                    <span className="admin-action-icon">+</span>
                    <span className="admin-action-label">Add Polymer</span>
                </button>
                <button className="admin-action-card" onClick={() => onNavigate("solvents")}>
                    <span className="admin-action-icon">+</span>
                    <span className="admin-action-label">Add Solvent</span>
                </button>
                <button className="admin-action-card" onClick={() => onNavigate("parameters")}>
                    <span className="admin-action-icon">⚙</span>
                    <span className="admin-action-label">Update Parameters</span>
                </button>
                <button className="admin-action-card" onClick={() => onNavigate("users")}>
                    <span className="admin-action-icon">👤</span>
                    <span className="admin-action-label">Manage Users</span>
                </button>
            </div>

            <div className="admin-bottom-grid">
                <div className="admin-card">
                    <p className="admin-card-title">Recent System Activity</p>
                    {activityItems.map((a, i) => (
                        <div key={i} className="admin-activity-item">
                            <span>{a.text}</span>
                            <span>{a.date}</span>
                        </div>
                    ))}
                </div>
                <div className="admin-card">
                    <p className="admin-card-title">Top Performing Solvents</p>
                    {topSolvents.map((s, i) => (
                        <div key={i} className="admin-solvent-item">
                            <span className="admin-solvent-name">{s.name}</span>
                            <span className="admin-solvent-pct">{s.pct}</span>
                        </div>
                    ))}
                </div>
            </div>
        </>
    );
}
