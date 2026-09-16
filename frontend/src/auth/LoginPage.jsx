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

const AUTH_MODE = import.meta.env.VITE_AUTH_MODE || "demo";

export function LoginPage({ onAuthenticated }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [otp, setOtp] = useState("");
  const [step, setStep] = useState("credentials");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleCredentials(event) {
    event.preventDefault();
    setError("");

    if (!email.trim() || !password) {
      setError("Enter your official email address and password.");
      return;
    }

    setLoading(true);

    try {
      if (AUTH_MODE === "api") {
        const response = await fetch("/api/auth/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify({
            email: email.trim().toLowerCase(),
            password,
          }),
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
          throw new Error(data.message || "Authentication failed.");
        }

        // The backend decides whether MFA is required.
        if (data.mfaRequired) {
          setStep("otp");
          return;
        }

        if (data.authenticated) {
          onAuthenticated();
          return;
        }

        throw new Error("The authentication server did not confirm the session.");
      }

      // Explicit SIH prototype mode.
      // This is NOT government authentication. It only lets the UI be demonstrated
      // before the real backend/SSO integration is available.
      setStep("otp");
    } catch (err) {
      setError(err.message || "Unable to authenticate.");
    } finally {
      setLoading(false);
    }
  }

  async function handleOtp(event) {
    event.preventDefault();
    setError("");

    if (!/^\d{6}$/.test(otp)) {
      setError("Enter the 6-digit verification code.");
      return;
    }

    setLoading(true);

    try {
      if (AUTH_MODE === "api") {
        const response = await fetch("/api/auth/mfa/verify", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify({ otp }),
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok || !data.authenticated) {
          throw new Error(data.message || "Verification failed.");
        }

        onAuthenticated();
        return;
      }

      // Explicit SIH prototype mode: accept any six digits.
      // Replace this block with the real MFA endpoint before production.
      onAuthenticated();
    } catch (err) {
      setError(err.message || "Unable to verify the code.");
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
              <h1>Land Acquisition &amp; Management System</h1>
              <p>Real-Time National Monitoring &amp; Decision Support</p>
            </div>
          </div>

          <div className="auth-divider" />

          {step === "credentials" ? (
            <form onSubmit={handleCredentials} className="auth-form">
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
                {loading ? "Authenticating..." : "Continue to Verification"}
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
          ) : (
            <form onSubmit={handleOtp} className="auth-form">
              <div className="auth-heading">
                <div className="auth-step-icon">
                  <CheckCircle2 size={25} />
                </div>
                <h2>Verify your identity</h2>
                <p>
                  Enter the 6-digit verification code from your configured
                  authentication provider.
                </p>
              </div>

              <label htmlFor="otp">Verification code</label>
              <div className="auth-input otp-input">
                <LockKeyhole size={18} />
                <input
                  id="otp"
                  inputMode="numeric"
                  autoComplete="one-time-code"
                  maxLength={6}
                  placeholder="000000"
                  value={otp}
                  onChange={(event) =>
                    setOtp(event.target.value.replace(/\D/g, ""))
                  }
                />
              </div>

              {error && (
                <div className="auth-error" role="alert">
                  <AlertCircle size={17} />
                  <span>{error}</span>
                </div>
              )}

              <button className="auth-submit" disabled={loading}>
                {loading ? "Verifying..." : "Verify & Enter Portal"}
                {!loading && <ArrowRight size={18} />}
              </button>

              <button
                type="button"
                className="auth-back"
                onClick={() => {
                  setStep("credentials");
                  setOtp("");
                  setError("");
                }}
              >
                Back to login
              </button>
            </form>
          )}

          <div className="auth-security">
            <LockKeyhole size={14} />
            <span>Protected session · Authorized access only</span>
          </div>

          {AUTH_MODE === "demo" && (
            <div className="auth-demo">
              <strong>SIH prototype mode</strong>
              <span>
                This screen demonstrates the authentication flow. It is not a
                real government identity verification service.
              </span>
            </div>
          )}
        </section>
      </main>

      <footer className="auth-footer">
        <span>© 2026 National Land Management System</span>
        <span>Government of India · Secure Access</span>
      </footer>
    </div>
  );
}
