import { useNavigate } from "react-router-dom";
import { logoutUser } from "../services/api";
import "../styles/dashboard.css";

export default function AdminDashboard() {
    const navigate = useNavigate();
    const user = JSON.parse(localStorage.getItem("user") || "{}");

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

    return (
        <div className="dashboard-container">
            <div className="dashboard-card">
                <h1>Admin Dashboard</h1>
                <p>Welcome, {user.fullName || "Admin"}</p>
                <p>Role: {user.role || "ADMIN"}</p>

                <div className="dashboard-actions">
                    <button onClick={handleLogout}>Logout</button>
                </div>
            </div>
        </div>
    );
}