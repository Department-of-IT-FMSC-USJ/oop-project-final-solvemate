interface Props {
    title:   string;
    icon:    string;
    onClick: () => void;
}

export default function ActionCard({ title, icon, onClick }: Props) {
    return (
        <button className="action-card" onClick={onClick}>
            <span className="action-card-icon">{icon}</span>
            <span className="action-card-label">{title}</span>
        </button>
    );
}
