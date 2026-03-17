import { useState } from "react";
import { Link } from "react-router-dom";
import { registerUser } from "../services/api";
import "../styles/auth.css";

export default function Register() {
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [role, setRole] = useState("LAB_USER");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const handleRegister = async () => {
    if (password !== confirmPassword) {
      alert("Passwords do not match");
      return;
    }

    const result = await registerUser({
      fullName,
      email,
      role,
      password,
    });

    console.log(result);
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>Create Account</h1>
        <p>Register for SolveMate Laboratory System</p>

        <input
          type="text"
          placeholder="Dr. John Doe"
          value={fullName}
          onChange={(e) => setFullName(e.target.value)}
        />

        <input
          type="email"
          placeholder="your.email@lab.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <select value={role} onChange={(e) => setRole(e.target.value)}>
          <option value="LAB_USER">Laboratory User</option>
          <option value="ADMIN">Administrator</option>
        </select>

        <input
          type="password"
          placeholder="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <input
          type="password"
          placeholder="confirm password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />

        <button onClick={handleRegister}>Create Account</button>

        <p style={{ marginTop: "15px" }}>
         Already have an account? <Link to="/login">Sign In</Link>
        </p>
      </div>
    </div>
  );
}