import { useNavigate } from "react-router-dom";
import { logoutUser } from "../services/api";

interface Props {
    fullName:   string;
    email:      string;
    role:       string;
    activePage: string;
    onNavigate: (page: string) => void;
}

export default function Sidebar({ fullName, email, role, activePage, onNavigate }: Props) {
    const navigate = useNavigate();

    const handleLogout = async () => {
        try { await logoutUser(); } catch { }
        finally { localStorage.removeItem("user"); navigate("/"); }
    };

    const menuItems = [
        { key: "dashboard",     label: "Dashboard",     icon: "⊞" },
        { key: "polymers",      label: "Polymers",       icon: "▣" },
        { key: "solvents",      label: "Solvents",       icon: "◎" },
        { key: "compatibility", label: "Compatibility",  icon: "⌗" },
        { key: "trials",        label: "Trials",         icon: "⚗" },
        { key: "reports",       label: "Reports",        icon: "☰" },
    ];

    return (
        <aside className="sidebar">
            <div>
                <div className="sidebar-brand">
                    <div className="sidebar-logo">🔬</div>
                    <div>
                        <h2>SolveMate</h2>
                        <p>Lab System</p>
                    </div>
                </div>
                <nav className="sidebar-nav">
                    {menuItems.map(item => (
                        <button
                            key={item.key}
                            className={`sidebar-nav-item ${activePage === item.key ? "active" : ""}`}
                            onClick={() => onNavigate(item.key)}
                        >
                            <span className="sidebar-nav-icon">{item.icon}</span>
                            {item.label}
                        </button>
                    ))}
                </nav>
            </div>
            <div className="sidebar-footer">
                <div className="sidebar-user">
                    <p className="sidebar-user-name">{fullName}</p>
                    <p className="sidebar-user-email">{email}</p>
                    <span className="sidebar-user-role">{role === "ADMIN" ? "Admin" : "Lab User"}</span>
                </div>
                <button className="sidebar-logout" onClick={handleLogout}>
                    ↪ Logout
                </button>
            </div>
        </aside>
    );
}
