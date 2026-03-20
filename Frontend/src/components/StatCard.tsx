interface StatCardProps {
    title: string;
    value: string;
    subtitle: string;
    icon: string;
    accentClass?: string;
    compactValue?: boolean;
}

export default function StatCard({
                                     title,
                                     value,
                                     subtitle,
                                     icon,
                                     accentClass = "",
                                     compactValue = false,
                                 }: StatCardProps) {
    return (
        <div className="stat-card">
            <div className="stat-card-top">
                <h3>{title}</h3>
                <span className={`stat-icon ${accentClass}`}>{icon}</span>
            </div>

            <div className={`stat-value ${compactValue ? "compact" : ""}`}>{value}</div>
            <p className="stat-subtitle">{subtitle}</p>
        </div>
    );
}