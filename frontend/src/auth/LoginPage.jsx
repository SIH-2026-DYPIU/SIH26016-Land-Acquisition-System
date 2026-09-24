import { useState } from "react";
import {
  ShieldCheck,
  LockKeyhole,
  Mail,
  ArrowRight,
  CheckCircle2,
  AlertCircle,
  Building2,
} from "lucide-react";
import { signInWithEmailAndPassword } from "firebase/auth";
import { auth } from "../firebase";

export function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");

    if (!email.trim() || !password) {
      setError("Enter your official email address and password.");
      return;
    }

    setLoading(true);

    try {
      await signInWithEmailAndPassword(auth, email.trim(), password);
      // Sign-in successful, auth state will change via onAuthStateChanged
    } catch (err) {
      // Handle Firebase errors
      console.error(err);
      setError(err.message || "Authentication failed.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-government-bar">
        <span>Government of India</span>
        <span>Secure Digital Governance Portal</span>
      </div>

      <main className="auth-main">
        <section className="auth-card" aria-label="Secure portal authentication">
          <div className="auth-brand">
            <div className="auth-emblem">
              <ShieldCheck size={34} strokeWidth={1.8} />
            </div>

            <div>
              <span className="auth-kicker">NATIONAL LAND MANAGEMENT</span>
              <h1>Land Acquisition & Management System</h1>
              <p>Real-Time National Monitoring & Decision Support</p>
            </div>
          </div>

          <div className="auth-divider" />

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="auth-heading">
              <h2>Secure Portal Login</h2>
              <p>
                Authorized users only. Sign in using your official
                organizational credentials.
              </p>
            </div>

            <div className="auth-notice">
              <Building2 size={18} />
              <div>
                <strong>Authorized access</strong>
                <span>
                  Access is determined by identity, organization, role and
                  administrative scope.
                </span>
              </div>
            </div>

            <label htmlFor="official-email">Official email address</label>
            <div className="auth-input">
              <Mail size={18} />
              <input
                id="official-email"
                type="email"
                autoComplete="username"
                placeholder="name@organization.gov.in"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
              />
            </div>

            <label htmlFor="password">Password</label>
            <div className="auth-input">
              <LockKeyhole size={18} />
              <input
                id="password"
                type="password"
                autoComplete="current-password"
                placeholder="Enter your password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
              />
            </div>

            {error && (
              <div className="auth-error" role="alert">
                <AlertCircle size={17} />
                <span>{error}</span>
              </div>
            )}

            <button className="auth-submit" disabled={loading}>
              {loading ? "Signing in..." : "Sign In"}
              {!loading && <ArrowRight size={18} />}
            </button>

            <div className="auth-sso">
              <span>OR</span>
              <button
                type="button"
                onClick={() => {
                  setError(
                    "Government SSO integration is pending backend/identity-provider configuration."
                  );
                }}
              >
                Continue with Government SSO
              </button>
            </div>
          </form>

          <div className="auth-security">
            <LockKeyhole size={14} />
            <span>Protected session · Authorized access only</span>
          </div>
        </section>
      </main>

      <footer className="auth-footer">
        <span>© 2026 National Land Management System</span>
        <span>Government of India · Secure Access</span>
      </footer>
    </div>
  );
}