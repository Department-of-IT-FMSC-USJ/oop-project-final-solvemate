import { useState } from "react";
import Sidebar from "../components/Sidebar";
import StatCard from "../components/StatCard";
import ActionCard from "../components/ActionCard";
import TrialItem from "../components/TrialItem";
import PolymersPage from "./PolymersPage";
import SolventsPage from "./SolventsPage";
import CompatibilityPage from "./CompatibilityPage";
import TrialsPage from "./TrialsPage";
import ReportsPage from "./ReportsPage";
import "../styles/layout.css";


export default function UserDashboard() {
    const [activePage, setActivePage] = useState("dashboard");
    const user = JSON.parse(localStorage.getItem("user") || "{}");

    const renderPage = () => {
        switch (activePage) {
            case "polymers":      return <PolymersPage />;
            case "solvents":      return <SolventsPage />;
            case "compatibility": return <CompatibilityPage />;
            case "trials":        return <TrialsPage />;
            case "reports":       return <ReportsPage />;
            default:              return <DashboardHome onNavigate={setActivePage} user={user} />;
        }
    };

    return (
        <div className="dashboard-layout">
            <Sidebar
                fullName={user.fullName || "Lab User"}
                email={user.email || "user@solvemate.lab"}
                role={user.role || "LAB_USER"}
                activePage={activePage}
                onNavigate={setActivePage}
            />
            <main className="dashboard-main">
                {renderPage()}
            </main>
        </div>
    );
}

// Dashboard Home

function DashboardHome({ onNavigate, user }: { onNavigate: (p: string) => void; user: any }) {
    return (
        <>
            <div className="dashboard-header">
                <h1>Laboratory Dashboard</h1>
                <p>Welcome back, {user.fullName || "Lab User"}! Here's your overview.</p>
            </div>

            <div className="stats-grid">
                <StatCard title="Total Polymers" value="10" subtitle="Available for testing"  icon="▣" accentClass="blue" />
                <StatCard title="Total Solvents" value="80" subtitle="In database"             icon="◔" accentClass="teal" />
                <StatCard title="Trial Records"  value="10" subtitle="Sample trials loaded"    icon="⚗" accentClass="purple" />
                <StatCard title="Reports Ready"  value="10" subtitle="Click Reports to view"   icon="☰" accentClass="green" />
            </div>

            <h2 className="section-title">Quick Actions</h2>
            <div className="actions-grid">
                <ActionCard title="Run Analysis"  icon="⌗" onClick={() => onNavigate("compatibility")} />
                <ActionCard title="Record Trial"  icon="⚗" onClick={() => onNavigate("trials")} />
                <ActionCard title="View Reports"  icon="☰" onClick={() => onNavigate("reports")} />
            </div>

            <h2 className="section-title">Recent Trials</h2>
            <div className="trials-panel">
                <TrialItem title="Polystyrene (PS) + Acetone"      date="17/03/2026" status="successful" />
                <TrialItem title="PMMA (Acrylic) + Acetone"        date="12/03/2026" status="successful" />
                <TrialItem title="Polyethylene (PE) + Cyclohexane" date="10/03/2026" status="partial" />
                <TrialItem title="PET + Benzaldehyde"              date="25/02/2026" status="failed" />
            </div>
        </>
    );
}
