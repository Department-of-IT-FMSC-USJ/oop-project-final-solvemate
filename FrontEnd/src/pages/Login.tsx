import { useState } from "react";
import { Link } from "react-router-dom";
import { loginUser } from "../services/api";
import "../styles/auth.css";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    const result = await loginUser({
      email,
      password,
    });

    console.log(result);
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>SolveMate</h1>
        <p>Polymer-Solvent Compatibility System</p>

        <input
          type="email"
          placeholder="your.email@lab.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <input
          type="password"
          placeholder="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button onClick={handleLogin}>Sign In</button>

        <p style={{ marginTop: "15px" }}>
        Don't have an account? <Link to="/register">Create Account</Link>
        </p>
      </div>
    </div>
  );
}