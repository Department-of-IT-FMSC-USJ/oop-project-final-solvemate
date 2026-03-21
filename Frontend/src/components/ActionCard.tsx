interface ActionCardProps {
    title: string;
    icon: string;
}

export default function ActionCard({ title, icon }: ActionCardProps) {
    return (
        <button className="action-card" type="button">
            <div className="action-icon">{icon}</div>
            <div className="action-title">{title}</div>
        </button>
    );
}