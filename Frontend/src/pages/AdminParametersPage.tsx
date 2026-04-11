import { useState } from "react";



export default function AdminParametersPage() {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    const today = new Date().toLocaleDateString("en-GB");

    const [deltaD, setDeltaD]           = useState(4);
    const [deltaP, setDeltaP]           = useState(1);
    const [deltaH, setDeltaH]           = useState(1);
    const [mlWeight, setMlWeight]       = useState(30);
    const [threshold, setThreshold]     = useState(1.2);
    const [saved, setSaved]             = useState(false);
    const [lastModified, setLastModified] = useState(today);

    const handleSave = () => {
        localStorage.setItem("solvemate_params", JSON.stringify({
            deltaD, deltaP, deltaH, mlWeight, threshold,
            modifiedBy: user.fullName || "Admin",
            modifiedAt: today
        }));
        setLastModified(today);
        setSaved(true);
        setTimeout(() => setSaved(false), 3000);
    };

    return (
        <>
            <h1 className="admin-page-title">Compatibility Parameters</h1>
            <p className="admin-page-subtitle">Configure Hansen Solubility calculation weights and thresholds</p>

            {saved && <div className="flash-success">Parameters saved successfully ✓</div>}

            <div className="params-layout">

                {/* ── Left: Main settings ─────────────────────────────── */}
                <div className="params-card">
                    <p className="params-section-title">⚙ System Parameters</p>

                    {/* ── Hansen Parameter Weights ──────────────────── */}
                    <p className="params-group-title">Hansen Parameter Weights</p>
                    <p className="params-group-desc">
                        Adjust the relative importance of each Hansen parameter in the Ra calculation
                    </p>

                    <div className="param-row">
                        <div className="param-label-row">
                            <span className="param-label">δD (Dispersion) Weight</span>
                            <span className="param-value">{deltaD}</span>
                        </div>
                        <input
                            type="range" min={1} max={8} value={deltaD}
                            className="param-slider"
                            onChange={e => setDeltaD(Number(e.target.value))}
                        />
                        <p className="param-hint">Current: {deltaD}x (Standard: 4x) — used as {deltaD}·(ΔδD)² in Ra formula</p>
                    </div>

                    <div className="param-row">
                        <div className="param-label-row">
                            <span className="param-label">δP (Polar) Weight</span>
                            <span className="param-value">{deltaP}</span>
                        </div>
                        <input
                            type="range" min={1} max={4} value={deltaP}
                            className="param-slider"
                            onChange={e => setDeltaP(Number(e.target.value))}
                        />
                        <p className="param-hint">Current: {deltaP}x (Standard: 1x)</p>
                    </div>

                    <div className="param-row">
                        <div className="param-label-row">
                            <span className="param-label">δH (Hydrogen Bonding) Weight</span>
                            <span className="param-value">{deltaH}</span>
                        </div>
                        <input
                            type="range" min={1} max={4} value={deltaH}
                            className="param-slider"
                            onChange={e => setDeltaH(Number(e.target.value))}
                        />
                        <p className="param-hint">Current: {deltaH}x (Standard: 1x)</p>
                    </div>

                    <hr className="param-divider" />

                    {/* ── ML Model Weight ───────────────────────────── */}
                    <p className="params-group-title">ML Model Weight</p>
                    <p className="params-group-desc">
                        Percentage of weight given to the ML model's probability in the final compatibility score.
                        The remaining percentage uses the physics-based Hansen calculation.
                    </p>

                    <div className="param-row">
                        <div className="param-label-row">
                            <span className="param-label">ML Model Weight</span>
                            <span className="param-value">{mlWeight}%</span>
                        </div>
                        <input
                            type="range" min={0} max={100} value={mlWeight}
                            className="param-slider"
                            onChange={e => setMlWeight(Number(e.target.value))}
                        />
                        <p className="param-hint">
                            {mlWeight}% ML (Logistic Regression), {100 - mlWeight}% Hansen physics calculation
                        </p>
                    </div>

                    <hr className="param-divider" />

                    {/* Compatibility Threshold  */}
                    <p className="params-group-title">Compatibility Threshold</p>
                    <p className="params-group-desc">
                        Ra/R₀ ratio threshold for pre-filtering solvents before ML ranking.
                        Solvents with RED above this value are excluded from recommendations.
                    </p>

                    <div className="param-row">
                        <div className="param-label-row">
                            <span className="param-label">Ra/R₀ Threshold</span>
                            <span className="param-value">{threshold}</span>
                        </div>
                        <input
                            type="number"
                            className="param-number-input"
                            value={threshold}
                            step={0.1}
                            min={0.5}
                            max={3.0}
                            onChange={e => setThreshold(parseFloat(e.target.value) || 1.2)}
                        />
                        <p className="param-hint">
                            Solvents with Ra/R₀ &lt; {threshold} are considered compatible and passed to ML ranking
                        </p>
                    </div>

                    <button className="param-save-btn" onClick={handleSave}>
                        💾 Save Changes
                    </button>
                </div>


                <div className="params-right-panel">

                    {/* About Hansen Parameters */}
                    <div className="params-info-card">
                        <p className="params-info-title">About Hansen Parameters</p>
                        <div className="params-info-item">
                            <h4>δD — Dispersion</h4>
                            <p>Accounts for London dispersion forces between molecules</p>
                        </div>
                        <div className="params-info-item">
                            <h4>δP — Polar</h4>
                            <p>Represents dipole-dipole interactions between molecules</p>
                        </div>
                        <div className="params-info-item">
                            <h4>δH — Hydrogen Bonding</h4>
                            <p>Describes hydrogen bonding capacity of the substance</p>
                        </div>
                    </div>

                    {/* Change History */}
                    <div className="params-info-card">
                        <p className="params-info-title">🕐 Change History</p>
                        <div className="change-history-item">
                            <strong>Last Modified</strong><br />
                            {lastModified}<br />
                            By: {user.fullName || "Admin User"}<br /><br />
                            <span style={{ color: "#9ca3af" }}>No previous changes recorded</span>
                        </div>
                    </div>

                    {/* Formula */}
                    <div className="params-info-card">
                        <p className="params-info-title">Formula</p>
                        <div className="formula-box">
                            <p className="formula-text">Ra = √({deltaD}·(ΔδD)² + {deltaP}·(ΔδP)² + {deltaH}·(ΔδH)²)</p>
                            <p className="formula-text">RED = Ra / R₀</p>
                            <p className="formula-text">Compatible if RED &lt; {threshold}</p>
                            <hr style={{ border: "none", borderTop: "1px solid #e5e7eb", margin: "10px 0" }} />
                            <p className="formula-text">
                                Final Score = {mlWeight}% × ML_Probability + {100 - mlWeight}% × Physics_Score
                            </p>
                            <p className="formula-text" style={{ fontSize: "11px", color: "#9ca3af" }}>
                                ML: Logistic Regression model (sklearn)<br />
                                Trained on 800 polymer-solvent pairs
                            </p>
                        </div>
                    </div>

                </div>
            </div>
        </>
    );
}
