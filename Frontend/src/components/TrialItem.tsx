interface TrialItemProps {
    title: string;
    date: string;
    status: "successful" | "partial" | "failed";
}

export default function TrialItem({ title, date, status }: TrialItemProps) {
    return (
        <div className="trial-item">
            <div className="trial-left">
                <div className="trial-icon">⚗</div>
                <div>
                    <h4>{title}</h4>
                    <p>{date}</p>
                </div>
            </div>

            <span className={`trial-status ${status}`}>{status}</span>
        </div>
    );
}