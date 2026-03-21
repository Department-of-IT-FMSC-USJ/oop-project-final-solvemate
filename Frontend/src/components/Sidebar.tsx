import { useNavigate } from "react-router-dom";
import { logoutUser } from "../services/api";

interface SidebarProps {
    fullName: string;
    email: string;
    role: string;
}

export default function Sidebar({ fullName, email, role }: SidebarProps) {
    const navigate = useNavigate();

    const handleLogout = async () => {
        try {
            await logoutUser();
        } catch (error) {
            console.error("Logout failed", error);
        } finally {
            localStorage.removeItem("user");
            navigate("/");
        }
    };

    const menuItems = [
        "Dashboard",
        "Polymers",
        "Solvents",
        "Compatibility",
        "Trials",
        "Reports",
    ];

    return (
        <aside className="sidebar">
            <div>
                <div className="sidebar-brand">
                    <div className="sidebar-logo">🧪</div>
                    <div>
                        <h2>SolveMate</h2>
                        <p>Lab System</p>
                    </div>
                </div>

                <nav className="sidebar-nav">
                    {menuItems.map((item, index) => (
                        <button
                            key={item}
                            className={`sidebar-item ${index === 0 ? "active" : ""}`}
                            type="button"
                        >
              <span className="sidebar-icon">
                {item === "Dashboard" && "▦"}
                  {item === "Polymers" && "▣"}
                  {item === "Solvents" && "◔"}
                  {item === "Compatibility" && "⌗"}
                  {item === "Trials" && "⚗"}
                  {item === "Reports" && "☰"}
              </span>
                            {item}
                        </button>
                    ))}
                </nav>
            </div>

            <div className="sidebar-footer">
                <div className="user-info">
                    <h4>{fullName}</h4>
                    <p>{email}</p>
                    <span>{role === "LAB_USER" ? "Lab User" : "Admin"}</span>
                </div>

                <button className="logout-btn" onClick={handleLogout}>
                    Logout
                </button>
            </div>
        </aside>
    );
}