import { useEffect, useState } from "react";
import "./AdminDashboard.css";

const API = "https://digital-heroes-o9f8.onrender.com/api";
export default function AdminDashboard() {
  const [adminEmail, setAdminEmail] = useState("suhana@gmail.com");
  const [draw, setDraw] = useState(null);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const loadDraw = async () => {
    try {
      const response = await fetch(`${API}/draws/history`);
      if (!response.ok) throw new Error("Failed to load draw");
      const data = await response.json();
      setDraw(data.length > 0 ? data[0] : null);
    } catch (error) {
      console.error(error);
      setMessage("Unable to load draw information.");
    }
  };

  useEffect(() => {
    loadDraw();
  }, []);

  const simulateDraw = async () => {
    setLoading(true);
    setMessage("");

    try {
      const response = await fetch(
        `${API}/draws/simulate?adminEmail=${encodeURIComponent(adminEmail)}`,
        { method: "POST" }
      );

      if (!response.ok) {
        throw new Error("Simulation failed");
      }

      const data = await response.json();
      setDraw(data);
      setMessage("Draw simulated successfully.");
    } catch (error) {
      console.error(error);
      setMessage("Unable to simulate draw.");
    } finally {
      setLoading(false);
    }
  };

  const publishDraw = async () => {
    if (!draw) {
      setMessage("Please simulate a draw first.");
      return;
    }

    setLoading(true);
    setMessage("");

    try {
      const response = await fetch(
        `${API}/draws/${draw.id}/publish?adminEmail=${encodeURIComponent(adminEmail)}`,
        { method: "POST" }
      );

      if (!response.ok) {
        throw new Error("Publish failed");
      }

      const data = await response.json();
      setDraw(data);
      setMessage("Draw published successfully.");
    } catch (error) {
      console.error(error);
      setMessage("Unable to publish draw.");
    } finally {
      setLoading(false);
    }
  };

  const winningNumbers = draw
    ? [
        draw.winningNumber1,
        draw.winningNumber2,
        draw.winningNumber3,
        draw.winningNumber4,
        draw.winningNumber5,
      ]
    : [];

  return (
    <div className="admin-page">
      <header className="admin-header">
        <div>
          <div className="admin-label">DIGITAL HEROES</div>
          <h1>Admin Dashboard</h1>
          <p>Manage monthly draws, prize pools and winners.</p>
        </div>

        <a href="/dashboard" className="dashboard-link">
          ← User Dashboard
        </a>
      </header>

      {message && (
        <div className="admin-message">
          {message}
        </div>
      )}

      <section className="admin-card admin-access">
        <div className="section-title">
          <div className="section-icon">👤</div>
          <div>
            <h2>Administrator Access</h2>
            <p>Use an account with ADMIN role to manage the draw.</p>
          </div>
        </div>

        <label>Admin Email</label>

        <input
          type="email"
          value={adminEmail}
          onChange={(e) => setAdminEmail(e.target.value)}
          placeholder="admin@example.com"
        />

        <small>
          The email must belong to a user whose role is ADMIN.
        </small>
      </section>

      <section className="admin-card draw-card">
        <div className="draw-top">
          <div>
            <div className="section-kicker">DRAW MANAGEMENT</div>
            <h2>Monthly Draw</h2>
            <p>Simulate and publish the monthly Digital Heroes draw.</p>
          </div>

          <span className={draw?.published ? "status published" : "status ready"}>
            {draw?.published ? "PUBLISHED" : "READY"}
          </span>
        </div>

        {draw ? (
          <>
            <div className="draw-summary">
              <div>
                <span>Draw ID</span>
                <strong>#{draw.id}</strong>
              </div>

              <div>
                <span>Draw Month</span>
                <strong>
                  {new Date(draw.drawMonth).toLocaleDateString("en-IN", {
                    month: "long",
                    year: "numeric",
                  })}
                </strong>
              </div>

              <div>
                <span>Prize Pool</span>
                <strong>₹{Number(draw.prizePool).toFixed(2)}</strong>
              </div>

              <div>
                <span>Published</span>
                <strong>{draw.published ? "Yes" : "No"}</strong>
              </div>
            </div>

            <div className="winning-section">
              <div>
                <div className="section-kicker">WINNING NUMBERS</div>
                <h3>Monthly Winning Combination</h3>
              </div>

              <div className="number-list">
                {winningNumbers.map((number, index) => (
                  <span className="number-ball" key={index}>
                    {number}
                  </span>
                ))}
              </div>
            </div>
          </>
        ) : (
          <div className="empty-draw">
            <div className="empty-icon">🎯</div>
            <h3>No draw available</h3>
            <p>Simulate a new monthly draw to generate winning numbers.</p>
          </div>
        )}

        <div className="draw-actions">
          <button
            className="primary-button"
            onClick={simulateDraw}
            disabled={loading}
          >
            {loading ? "Processing..." : "🎲 Simulate Draw"}
          </button>

          <button
            className="secondary-button"
            onClick={publishDraw}
            disabled={loading || !draw || draw.published}
          >
            {draw?.published ? "✓ Draw Published" : "🚀 Publish Draw"}
          </button>
        </div>
      </section>

      <section className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">💰</div>
          <div>
            <span>Prize Pool</span>
            <strong>₹{draw ? Number(draw.prizePool).toFixed(2) : "0.00"}</strong>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">🏆</div>
          <div>
            <span>Jackpot · 5 Match</span>
            <strong>
              ₹{draw ? Number(draw.jackpotAmount).toFixed(2) : "0.00"}
            </strong>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">🥈</div>
          <div>
            <span>4 Match Prize</span>
            <strong>
              ₹{draw ? Number(draw.fourMatchPrize).toFixed(2) : "0.00"}
            </strong>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">🥉</div>
          <div>
            <span>3 Match Prize</span>
            <strong>
              ₹{draw ? Number(draw.threeMatchPrize).toFixed(2) : "0.00"}
            </strong>
          </div>
        </div>
      </section>

      <section className="admin-card winners-card">
        <div className="winners-heading">
          <div>
            <div className="section-kicker">WINNER MANAGEMENT</div>
            <h2>Winners</h2>
            <p>Review, approve, reject and pay draw winners.</p>
          </div>

          <div className="winner-count">0 winners</div>
        </div>

        <div className="empty-winners">
          <div className="trophy">🏆</div>
          <h3>No winners yet</h3>
          <p>
            Winners will appear here when users match the published draw numbers.
          </p>
        </div>
      </section>
    </div>
  );
}