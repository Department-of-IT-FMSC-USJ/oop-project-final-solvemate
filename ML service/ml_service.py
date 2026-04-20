"""
ml_service.py  –  SolveMate ML Microservice
============================================
Wraps the trained solvemate_model.joblib and exposes it as a REST API
so the Java Spring Boot backend can call it via HTTP.

Run:
    pip install flask scikit-learn joblib pandas
    python ml_service.py

Endpoint:
    POST /predict
    Body (JSON):
    {
        "delta_d_solvent":      18.6,
        "delta_p_solvent":      1.0,
        "delta_h_solvent":      4.1,
        "molar_volume_cm3_mol": 89.4,
        "delta_d_polymer":      22.28,
        "delta_p_polymer":      5.75,
        "delta_h_polymer":      4.3
    }

    Response:
    {
        "probability": 0.823,
        "compatible": true
    }
"""

from flask import Flask, request, jsonify
import joblib
import numpy as np
import os

app = Flask(__name__)

# ── Load model once at startup ────────────────────────────────────────────────
MODEL_PATH = os.path.join(os.path.dirname(__file__), "solvemate_ml_model.pkl")

try:
    model = joblib.load(MODEL_PATH)
    print(f"[ML Service] Model loaded from {MODEL_PATH}")
except Exception as e:
    model = None
    print(f"[ML Service] WARNING: Could not load model: {e}")


# ── Health check ──────────────────────────────────────────────────────────────
@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "status": "ok",
        "model_loaded": model is not None
    })


# ── Prediction endpoint ───────────────────────────────────────────────────────
@app.route("/predict", methods=["POST"])
def predict():
    if model is None:
        return jsonify({"error": "Model not loaded"}), 503

    data = request.get_json()
    if not data:
        return jsonify({"error": "No JSON body provided"}), 400

    # Validate required fields
    required = [
        "delta_d_solvent", "delta_p_solvent", "delta_h_solvent",
        "molar_volume_cm3_mol",
        "delta_d_polymer", "delta_p_polymer", "delta_h_polymer"
    ]
    for field in required:
        if field not in data:
            return jsonify({"error": f"Missing field: {field}"}), 400

    # Build feature vector — order must exactly match 01_train_model.py:
    # [delta_d_solvent, delta_p_solvent, delta_h_solvent,
    #  molar_volume_cm3_mol,
    #  delta_d_polymer, delta_p_polymer, delta_h_polymer]
    features = np.array([[
        data["delta_d_solvent"],
        data["delta_p_solvent"],
        data["delta_h_solvent"],
        data["molar_volume_cm3_mol"],
        data["delta_d_polymer"],
        data["delta_p_polymer"],
        data["delta_h_polymer"]
    ]])

    try:
        probability = float(model.predict_proba(features)[0][1])  # P(compatible=1)
        compatible  = probability >= 0.5

        return jsonify({
            "probability": round(probability, 4),
            "compatible":  compatible
        })
    except Exception as e:
        return jsonify({"error": f"Prediction failed: {str(e)}"}), 500


# ── Batch prediction (optional, used for debugging) ──────────────────────────
@app.route("/predict/batch", methods=["POST"])
def predict_batch():
    if model is None:
        return jsonify({"error": "Model not loaded"}), 503

    data = request.get_json()
    if not data or "samples" not in data:
        return jsonify({"error": "Expected {'samples': [...]}"}), 400

    results = []
    for sample in data["samples"]:
        features = np.array([[
            sample["delta_d_solvent"],
            sample["delta_p_solvent"],
            sample["delta_h_solvent"],
            sample["molar_volume_cm3_mol"],
            sample["delta_d_polymer"],
            sample["delta_p_polymer"],
            sample["delta_h_polymer"]
        ]])
        prob = float(model.predict_proba(features)[0][1])
        results.append({
            "solvent_name": sample.get("name", "Unknown"),
            "probability":  round(prob, 4),
            "compatible":   prob >= 0.5
        })

    return jsonify({"results": results})


# ── Run ───────────────────────────────────────────────────────────────────────
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False)
