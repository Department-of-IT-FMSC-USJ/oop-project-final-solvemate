import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { loginUser } from "../services/api";
import "../styles/auth.css";

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail]       = useState("");
  const [password, setPassword] = useState("");
  const [error, setError]       = useState("");
  const [loading, setLoading]   = useState(false);

  const handleLogin = async () => {
    setError("");
    if (!email || !password) { setError("Please enter email and password"); return; }
    try {
      setLoading(true);
      const result = await loginUser({ email, password }) as { role: string; fullName: string };
      localStorage.setItem("user", JSON.stringify(result));
      if (result.role === "ADMIN") navigate("/admin");
      else navigate("/user");
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Login failed");
    } finally { setLoading(false); }
  };

  return (
      <div className="auth-container">
        <div className="auth-card">
          <h1>Login</h1>
          <p>Welcome back to SolveMate</p>
          {error && <p className="error-text">{error}</p>}
          <input type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} />
          <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
          <button onClick={handleLogin} disabled={loading}>{loading ? "Logging in..." : "Login"}</button>
          <p>Don&apos;t have an account? <Link to="/register">Register</Link></p>
        </div>
      </div>
  );
}
