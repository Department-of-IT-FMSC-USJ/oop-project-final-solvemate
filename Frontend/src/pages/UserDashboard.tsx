import Sidebar from "../components/Sidebar";
import StatCard from "../components/StatCard";
import ActionCard from "../components/ActionCard";
import TrialItem from "../components/TrialItem";
import "../styles/layout.css";

export default function UserDashboard() {
    const user = JSON.parse(localStorage.getItem("user") || "{}");

    return (
        <div className="dashboard-layout">
            <Sidebar
                fullName={user.fullName || "Lab User"}
                email={user.email || "user@solvemate.lab"}
                role={user.role || "LAB_USER"}
            />

            <main className="dashboard-main">
                <div className="dashboard-header">
                    <h1>Laboratory Dashboard</h1>
                    <p>Welcome back! Here's your laboratory overview.</p>
                </div>

                <div className="stats-grid">
                    <StatCard
                        title="Total Polymers"
                        value="5"
                        subtitle="Available for testing"
                        icon="▣"
                        accentClass="blue"
                    />
                    <StatCard
                        title="Total Solvents"
                        value="7"
                        subtitle="In database"
                        icon="◔"
                        accentClass="teal"
                    />
                    <StatCard
                        title="Recent Trials"
                        value="3"
                        subtitle="Last 30 days"
                        icon="⚗"
                        accentClass="purple"
                    />
                    <StatCard
                        title="Top Solvent"
                        value="Tetrahydrofura..."
                        subtitle="88% success rate"
                        icon="↗"
                        accentClass="green"
                        compactValue={true}
                    />
                </div>

                <h2 className="section-title">Quick Actions</h2>

                <div className="actions-grid">
                    <ActionCard title="Run Analysis" icon="⌗" />
                    <ActionCard title="Record Trial" icon="⚗" />
                    <ActionCard title="View Reports" icon="☰" />
                </div>

                <h2 className="section-title">Recent Trials</h2>

                <div className="trials-panel">
                    <TrialItem
                        title="Polystyrene + Toluene"
                        date="20/02/2026"
                        status="successful"
                    />
                    <TrialItem
                        title="PMMA + Acetone"
                        date="22/02/2026"
                        status="successful"
                    />
                    <TrialItem
                        title="Polyethylene + Hexane"
                        date="23/02/2026"
                        status="partial"
                    />
                </div>
            </main>
        </div>
    );
}