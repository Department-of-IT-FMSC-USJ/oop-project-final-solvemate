
# ===============================
# SOLVEMATE ML COMPATIBILITY MODEL (TRAINING)
# ===============================

import pandas as pd
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report, roc_auc_score
import joblib

# Load dataset
df = pd.read_csv("solvemate_real_ml_dataset_800.csv")

features = [
    "delta_d_solvent",
    "delta_p_solvent",
    "delta_h_solvent",
    "molar_volume_cm3_mol",
    "delta_d_polymer",
    "delta_p_polymer",
    "delta_h_polymer"
]

X = df[features]
y = df["compatible"]

# Train/Test split
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42
)

# Train model
model = RandomForestClassifier(n_estimators=200, random_state=42)
model.fit(X_train, y_train)

# Evaluate
y_pred = model.predict(X_test)
y_prob = model.predict_proba(X_test)[:, 1]

print("Accuracy:", accuracy_score(y_test, y_pred))
print("ROC-AUC:", roc_auc_score(y_test, y_prob))
print("\nClassification Report:\n")
print(classification_report(y_test, y_pred))

# Cross-validation
cv_scores = cross_val_score(model, X, y, cv=5)
print("Cross-validation Accuracy:", cv_scores.mean())

# Save model
joblib.dump(model, "solvemate_ml_model.pkl")
print("Model saved as solvemate_ml_model.pkl")
