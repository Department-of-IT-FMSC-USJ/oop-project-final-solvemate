import { useState, useEffect } from "react";
import type { PolymerResponse, CompatibilityResult } from "../services/api";
import { getAllPolymers, recommendTop5 } from "../services/api";
import "../styles/compatibility.css";

export default function CompatibilityPage() {
    const [polymers, setPolymers]             = useState<PolymerResponse[]>([]);
    const [selectedId, setSelectedId]         = useState<number | null>(null);
    const [results, setResults]               = useState<CompatibilityResult[]>([]);
    const [loading, setLoading]               = useState(false);
    const [polymerLoading, setPolymerLoading] = useState(true);
    const [error, setError]                   = useState("");
    const [ran, setRan]                       = useState(false);

    useEffect(() => {
        getAllPolymers()
            .then(setPolymers)
            .catch(() => setError("Failed to load polymers"))
            .finally(() => setPolymerLoading(false));
    }, []);

    const handleRun = async () => {
        if (!selectedId) { setError("Please select a polymer first"); return; }
        setError(""); setLoading(true); setRan(false);
        try {
            const data = await recommendTop5(selectedId);
            setResults(data); setRan(true);
        } catch (err: unknown) {
            setError(err instanceof Error ? err.message : "Analysis failed");
        } finally { setLoading(false); }
    };

    const selectedPolymer = polymers.find(p => p.polymerId === selectedId);
    const resultColor = (r: string) => r === "COMPATIBLE" ? "compat-compatible" : r === "BORDERLINE" ? "compat-borderline" : "compat-incompatible";
    const rankIcon = (rank: number) => ["🥇","🥈","🥉","4️⃣","5️⃣"][rank - 1] ?? rank;

    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1 className="page-title">Compatibility Analysis</h1>
                    <p className="page-subtitle">Hybrid Hansen Solubility Parameter + ML model recommendation</p>
                </div>
            </div>
            {error && <div className="flash-error">{error} <button onClick={() => setError("")}>✕</button></div>}
            <div className="compat-selector-card">
                <h2>Select Polymer</h2>
                <p>Choose a polymer to find the top 5 most compatible solvents</p>
                {polymerLoading ? <div className="loading-state">Loading polymers...</div> : (
                    <div className="polymer-grid">
                        {polymers.map(p => (
                            <button key={p.polymerId}
                                    className={`polymer-chip ${selectedId === p.polymerId ? "selected" : ""}`}
                                    onClick={() => { setSelectedId(p.polymerId); setRan(false); setResults([]); }}>
                                <span className="chip-name">{p.polymerName}</span>
                                <span className="chip-cat">{p.polymerCategory}</span>
                            </button>
                        ))}
                    </div>
                )}
                {selectedPolymer && (
                    <div className="polymer-params">
                        <span>δD: <strong>{selectedPolymer.deltaD}</strong></span>
                        <span>δP: <strong>{selectedPolymer.deltaP}</strong></span>
                        <span>δH: <strong>{selectedPolymer.deltaH}</strong></span>
                        <span>R₀: <strong>{selectedPolymer.r0}</strong></span>
                        <span>δT: <strong>{selectedPolymer.deltaT.toFixed(2)}</strong></span>
                    </div>
                )}
                <button className="btn-run" onClick={handleRun} disabled={!selectedId || loading}>
                    {loading ? "⏳ Running analysis..." : "⌗ Run Compatibility Analysis"}
                </button>
            </div>
            <div className="algo-info">
                <div className="algo-step"><div className="algo-icon">📐</div><div><h4>Hansen Distance (Ra)</h4><p>Ra = √(4·ΔδD² + ΔδP² + ΔδH²)</p></div></div>
                <div className="algo-arrow">→</div>
                <div className="algo-step"><div className="algo-icon">📊</div><div><h4>RED Filter</h4><p>RED = Ra / R₀ &lt; 1.2</p></div></div>
                <div className="algo-arrow">→</div>
                <div className="algo-step"><div className="algo-icon">🤖</div><div><h4>ML Ranking</h4><p>Logistic Regression P(compatible)</p></div></div>
                <div className="algo-arrow">→</div>
                <div className="algo-step"><div className="algo-icon">🏆</div><div><h4>Top 5</h4><p>Hybrid score = 60% ML + 40% Physics</p></div></div>
            </div>
            {ran && results.length > 0 && (
                <div className="results-section">
                    <h2 className="results-title">Top {results.length} Recommended Solvents<span className="results-polymer"> for {selectedPolymer?.polymerName}</span></h2>
                    <div className="results-list">
                        {results.map(r => (
                            <div key={r.resultId} className={`result-card ${r.rankPosition === 1 ? "rank-1" : ""}`}>
                                <div className="result-rank">{rankIcon(r.rankPosition)}</div>
                                <div className="result-main">
                                    <div className="result-name-row">
                                        <h3>{r.solventName}</h3>
                                        <span className={`compat-badge ${resultColor(r.result)}`}>{r.result}</span>
                                    </div>
                                    <div className="result-scores">
                                        <div className="score-bar-wrap"><span>Hybrid Score</span><div className="score-bar"><div className="score-fill" style={{ width: `${r.compatibilityScore}%` }} /></div><span className="score-val">{r.compatibilityScore.toFixed(1)}%</span></div>
                                        <div className="score-bar-wrap"><span>ML Probability</span><div className="score-bar"><div className="score-fill ml-fill" style={{ width: `${r.mlProbability * 100}%` }} /></div><span className="score-val">{(r.mlProbability * 100).toFixed(1)}%</span></div>
                                    </div>
                                    <div className="result-details">
                                        <div className="detail-chip"><span>Ra</span><strong>{r.raValue.toFixed(3)}</strong></div>
                                        <div className="detail-chip"><span>RED</span><strong className={r.redValue < 1.0 ? "text-green" : r.redValue < 1.2 ? "text-yellow" : "text-red"}>{r.redValue.toFixed(3)}</strong></div>
                                        <div className="detail-chip"><span>ΔδD</span><strong>{r.deltaDDifference.toFixed(3)}</strong></div>
                                        <div className="detail-chip"><span>ΔδP</span><strong>{r.deltaPDifference.toFixed(3)}</strong></div>
                                        <div className="detail-chip"><span>ΔδH</span><strong>{r.deltaHDifference.toFixed(3)}</strong></div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
            {ran && results.length === 0 && (<div className="empty-state"><p>⌗</p><h3>No results found</h3><p>No solvents available to analyse.</p></div>)}
        </div>
    );
}
