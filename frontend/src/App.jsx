import { useEffect, useState } from "react";
import axios from "axios";
import AdminDashboard from "./pages/AdminDashboard";

import {
  BrowserRouter,
  Routes,
  Route,
  Link,
  useNavigate,
  useSearchParams
} from "react-router-dom";

const API = "http://localhost:8080/api";

const charities = [
  {
    name: "Golf For Good Foundation",
    description:
      "Supporting young people through sport, education and community programs.",
  },
  {
    name: "Green Earth Foundation",
    description:
      "Supporting environmental projects and sustainable community development.",
  },
  {
    name: "Hope For Children",
    description:
      "Helping children access education, care and essential opportunities.",
  },
  {
    name: "Community Sports Trust",
    description:
      "Making sports and healthy activities accessible to local communities.",
  },
];

const contributionOptions = [10, 15, 20, 25, 30, 50, 75, 100];

function Header() {
  const user = JSON.parse(
    localStorage.getItem("digitalHeroesUser") || "null"
  );

  return (
    <header className="border-b bg-white">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
        <Link
          to="/"
          className="text-2xl font-bold text-slate-900"
        >
          Digital Heroes
        </Link>

        <nav className="flex items-center gap-6 text-sm font-medium">
          <Link to="/" className="hover:text-blue-600">
            Home
          </Link>

          <Link to="/charities" className="hover:text-blue-600">
            Charities
          </Link>

          {user ? (
            <>
              <Link
                to="/dashboard"
                className="hover:text-blue-600"
              >
                Dashboard
              </Link>

              <button
                onClick={() => {
                  localStorage.removeItem("digitalHeroesUser");
                  window.location.href = "/";
                }}
                className="rounded-lg bg-slate-900 px-4 py-2 text-white"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="hover:text-blue-600"
              >
                Login
              </Link>

              <Link
                to="/register"
                className="rounded-lg bg-blue-600 px-4 py-2 text-white"
              >
                Join Now
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}

function Home() {
  return (
    <div>
      <section className="bg-slate-950 px-6 py-24 text-white">
        <div className="mx-auto max-w-7xl">
          <p className="mb-4 text-sm font-semibold uppercase tracking-widest text-blue-400">
            Play. Give. Win.
          </p>

          <h1 className="max-w-3xl text-5xl font-bold leading-tight md:text-6xl">
            Your golf scores can create a bigger impact.
          </h1>

          <p className="mt-6 max-w-2xl text-lg leading-8 text-slate-300">
            Track your Stableford scores, support a charity you care
            about and take part in monthly prize draws.
          </p>

          <div className="mt-8 flex gap-4">
            <Link
              to="/register"
              className="rounded-xl bg-blue-600 px-6 py-3 font-semibold hover:bg-blue-500"
            >
              Become a Digital Hero
            </Link>

            <Link
              to="/charities"
              className="rounded-xl border border-slate-600 px-6 py-3 font-semibold hover:bg-slate-900"
            >
              Explore Charities
            </Link>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-6 py-20">
        <div className="grid gap-6 md:grid-cols-3">
          <Feature
            title="Track Your Game"
            text="Keep your latest five Stableford scores in one simple dashboard."
          />

          <Feature
            title="Support a Cause"
            text="Choose a charity and decide how much of your subscription should contribute."
          />

          <Feature
            title="Monthly Draws"
            text="Use your participation to enter monthly prize draws with multiple prize levels."
          />
        </div>
      </section>
    </div>
  );
}

function Feature({ title, text }) {
  return (
    <div className="rounded-2xl border bg-white p-7 shadow-sm">
      <h3 className="text-xl font-bold text-slate-900">
        {title}
      </h3>

      <p className="mt-3 leading-7 text-slate-600">
        {text}
      </p>
    </div>
  );
}

function Login() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {
    e.preventDefault();

    setError("");
    setLoading(true);

    try {
      const response = await axios.post(
        `${API}/auth/login`,
        form
      );

      localStorage.setItem(
        "digitalHeroesUser",
        JSON.stringify(response.data)
      );

      navigate("/dashboard");
    } catch (err) {
      setError(
        typeof err.response?.data === "string"
          ? err.response.data
          : "Login failed."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout title="Welcome back">
      <form onSubmit={submit} className="space-y-5">
        <Input
          label="Email"
          type="email"
          value={form.email}
          onChange={(e) =>
            setForm({
              ...form,
              email: e.target.value,
            })
          }
          required
        />

        <Input
          label="Password"
          type="password"
          value={form.password}
          onChange={(e) =>
            setForm({
              ...form,
              password: e.target.value,
            })
          }
          required
        />

        {error && <ErrorMessage message={error} />}

        <button
          disabled={loading}
          className="w-full rounded-xl bg-blue-600 px-5 py-3 font-semibold text-white disabled:opacity-50"
        >
          {loading ? "Signing in..." : "Sign In"}
        </button>
      </form>
    </AuthLayout>
  );
}

function Register() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    fullName: "",
    email: "",
    password: "",
    subscriptionPlan: "MONTHLY",
    charityContribution: 10,
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {
    e.preventDefault();

    setError("");
    setLoading(true);

    try {
      const response = await axios.post(
        `${API}/auth/register`,
        form
      );

      localStorage.setItem(
        "digitalHeroesUser",
        JSON.stringify(response.data)
      );

      navigate("/subscription");
    } catch (err) {
      setError(
        typeof err.response?.data === "string"
          ? err.response.data
          : "Registration failed."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout title="Create your Digital Heroes account">
      <form onSubmit={submit} className="space-y-5">
        <Input
          label="Full Name"
          value={form.fullName}
          onChange={(e) =>
            setForm({
              ...form,
              fullName: e.target.value,
            })
          }
          required
        />

        <Input
          label="Email"
          type="email"
          value={form.email}
          onChange={(e) =>
            setForm({
              ...form,
              email: e.target.value,
            })
          }
          required
        />

        <Input
          label="Password"
          type="password"
          value={form.password}
          onChange={(e) =>
            setForm({
              ...form,
              password: e.target.value,
            })
          }
          required
        />

        <div>
          <label className="mb-2 block text-sm font-semibold">
            Subscription Plan
          </label>

          <select
            value={form.subscriptionPlan}
            onChange={(e) =>
              setForm({
                ...form,
                subscriptionPlan: e.target.value,
              })
            }
            className="w-full rounded-xl border px-4 py-3"
          >
            <option value="MONTHLY">Monthly</option>
            <option value="YEARLY">Yearly</option>
          </select>
        </div>

        <div>
          <label className="mb-2 block text-sm font-semibold">
            Charity Contribution
          </label>

          <select
            value={form.charityContribution}
            onChange={(e) =>
              setForm({
                ...form,
                charityContribution: Number(e.target.value),
              })
            }
            className="w-full rounded-xl border px-4 py-3"
          >
            {contributionOptions.map((value) => (
              <option key={value} value={value}>
                {value}%
              </option>
            ))}
          </select>
        </div>

        {error && <ErrorMessage message={error} />}

        <button
          disabled={loading}
          className="w-full rounded-xl bg-blue-600 px-5 py-3 font-semibold text-white disabled:opacity-50"
        >
          {loading ? "Creating account..." : "Create Account"}
        </button>
      </form>
    </AuthLayout>
  );
}

function Subscription() {
  const navigate = useNavigate();

  const user = JSON.parse(
    localStorage.getItem("digitalHeroesUser") || "null"
  );

  const [plan, setPlan] = useState("MONTHLY");
  const [contribution, setContribution] = useState(10);
  const [subscription, setSubscription] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!user?.email) {
      navigate("/login");
      return;
    }

    axios
      .get(`${API}/subscriptions`, {
        params: {
          email: user.email,
        },
      })
      .then((response) => {
        setSubscription(response.data);
        setPlan(response.data.plan);
        setContribution(
          response.data.charityContribution
        );
      })
      .catch(() => {
        // No subscription yet.
      });
  }, []);

  
  const startCheckout = () => {
  navigate(
    `/payment?plan=${plan}&contribution=${contribution}`
  );
};

   

  const cancelSubscription = async () => {
    if (!user?.email) return;

    try {
      const response = await axios.put(
        `${API}/subscriptions/cancel`,
        null,
        {
          params: {
            email: user.email,
          },
        }
      );

      setSubscription(response.data);

      const updatedUser = {
        ...user,
        subscribed: false,
        subscriptionStatus: "CANCELLED",
      };

      localStorage.setItem(
        "digitalHeroesUser",
        JSON.stringify(updatedUser)
      );
    } catch (err) {
      setError(
        typeof err.response?.data === "string"
          ? err.response.data
          : "Unable to cancel subscription."
      );
    }
  };

  return (
    <PageLayout
      title="Subscription"
      subtitle="Choose your plan and decide how much you want to contribute to your chosen cause."
    >
      <div className="mx-auto max-w-3xl">
        {subscription?.status === "ACTIVE" && (
          <div className="mb-8 rounded-2xl border border-green-200 bg-green-50 p-6">
            <h3 className="text-lg font-bold text-green-800">
              Active subscription
            </h3>

            <p className="mt-2 text-green-700">
              {subscription.plan} plan ·{" "}
              {subscription.charityContribution}% charity contribution
            </p>

            <button
              onClick={cancelSubscription}
              className="mt-4 rounded-lg border border-red-300 px-4 py-2 text-sm font-semibold text-red-700"
            >
              Cancel Subscription
            </button>
          </div>
        )}

        <div className="grid gap-5 md:grid-cols-2">
          <PlanCard
            title="Monthly"
            price="$9.99"
            selected={plan === "MONTHLY"}
            onClick={() => setPlan("MONTHLY")}
          />

          <PlanCard
            title="Yearly"
            price="$99.99"
            selected={plan === "YEARLY"}
            onClick={() => setPlan("YEARLY")}
          />
        </div>

        <div className="mt-8 rounded-2xl border bg-white p-6 shadow-sm">
          <label className="mb-2 block font-semibold">
            Charity contribution
          </label>

          <p className="mb-4 text-sm text-slate-500">
            Minimum contribution is 10%.
          </p>

          <select
            value={contribution}
            onChange={(e) =>
              setContribution(Number(e.target.value))
            }
            className="w-full rounded-xl border px-4 py-3"
          >
            {contributionOptions.map((value) => (
              <option key={value} value={value}>
                {value}%
              </option>
            ))}
          </select>

          {error && (
            <div className="mt-4">
              <ErrorMessage message={error} />
            </div>
          )}

          <button
            onClick={startCheckout}
            disabled={loading}
            className="mt-6 w-full rounded-xl bg-blue-600 px-5 py-3 font-semibold text-white disabled:opacity-50"
          >
            {`Continue to Test Payment`}
          </button>
        </div>
      </div>
    </PageLayout>
  );
}
function PaymentSuccess() {
  return (
    <PageLayout
      title="Payment successful"
subtitle="Your test payment was completed successfully."    >
      <div className="mx-auto max-w-2xl rounded-2xl border border-green-200 bg-green-50 p-8 text-center">
        <div className="text-5xl">✓</div>

        <h2 className="mt-4 text-2xl font-bold text-green-800">
          Thank you for joining Digital Heroes
        </h2>

        <p className="mt-3 text-green-700">
          Your payment was received. Subscription activation will
          be confirmed by the backend payment workflow.
        </p>

        <Link
          to="/dashboard"
          className="mt-6 inline-block rounded-xl bg-green-700 px-6 py-3 font-semibold text-white"
        >
          Go to Dashboard
        </Link>
      </div>
    </PageLayout>
  );
}


function Dashboard() {
  const navigate = useNavigate();

  const user = JSON.parse(
    localStorage.getItem("digitalHeroesUser") || "null"
  );

  const [scores, setScores] = useState([]);
  const [subscription, setSubscription] = useState(null);
  const [score, setScore] = useState("");
  const [date, setDate] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [charity, setCharity] = useState(
    user?.selectedCharity || "Golf For Good Foundation"
  );
  const [contribution, setContribution] = useState(
    user?.charityContribution || 10
  );
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (!user?.email) {
      navigate("/login");
      return;
    }

    loadScores();
    loadSubscription();
  }, []);

  const loadScores = async () => {
    try {
      const response = await axios.get(`${API}/scores`, {
        params: {
          email: user.email,
        },
      });

      setScores(response.data);
    } catch (err) {
      setError(
        typeof err.response?.data === "string"
          ? err.response.data
          : "Unable to load scores."
      );
    }
  };

  const loadSubscription = async () => {
    try {
      const response = await axios.get(
        `${API}/subscriptions`,
        {
          params: {
            email: user.email,
          },
        }
      );

      setSubscription(response.data);
    } catch {
      setSubscription(null);
    }
  };

  const saveScore = async (e) => {
    e.preventDefault();

    setMessage("");
    setError("");

    try {
      if (editingId) {
        await axios.put(
          `${API}/scores/${editingId}`,
          {
            stablefordScore: Number(score),
            scoreDate: date,
          },
          {
            params: {
              email: user.email,
            },
          }
        );
      } else {
        await axios.post(
          `${API}/scores`,
          {
            stablefordScore: Number(score),
            scoreDate: date,
          },
          {
            params: {
              email: user.email,
            },
          }
        );
      }

      setScore("");
      setDate("");
      setEditingId(null);
      setMessage("Score saved successfully.");

      await loadScores();
    } catch (err) {
      setError(
        typeof err.response?.data === "string"
          ? err.response.data
          : "Unable to save score."
      );
    }
  };

  const editScore = (item) => {
    setEditingId(item.id);
    setScore(item.stablefordScore);
    setDate(item.scoreDate);
  };

  const deleteScore = async (id) => {
    try {
      await axios.delete(`${API}/scores/${id}`, {
        params: {
          email: user.email,
        },
      });

      await loadScores();
      setMessage("Score deleted.");
    } catch (err) {
      setError(
        typeof err.response?.data === "string"
          ? err.response.data
          : "Unable to delete score."
      );
    }
  };

  const saveCharity = async () => {
    setMessage("");
    setError("");

    try {
      const response = await axios.put(
        `${API}/auth/charity`,
        {
          selectedCharity: charity,
          charityContribution: contribution,
        },
        {
          params: {
            email: user.email,
          },
        }
      );

      localStorage.setItem(
        "digitalHeroesUser",
        JSON.stringify(response.data)
      );

      setMessage("Charity preferences saved.");
    } catch (err) {
      setError(
        typeof err.response?.data === "string"
          ? err.response.data
          : "Unable to save charity preferences."
      );
    }
  };

  return (
    <PageLayout
      title={`Welcome, ${user?.fullName || "Digital Hero"}`}
      subtitle="Manage your scores, subscription and charity impact."
    >
      <div className="grid gap-6 lg:grid-cols-3">
        <div className="rounded-2xl border bg-white p-6 shadow-sm lg:col-span-2">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-bold">
              Latest Golf Scores
            </h2>

            <span className="rounded-full bg-blue-50 px-3 py-1 text-sm text-blue-700">
              {scores.length}/5
            </span>
          </div>

          <form
            onSubmit={saveScore}
            className="mt-6 grid gap-4 md:grid-cols-3"
          >
            <Input
              label="Stableford Score"
              type="number"
              min="1"
              max="45"
              value={score}
              onChange={(e) => setScore(e.target.value)}
              required
            />

            <Input
              label="Score Date"
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              required
            />

            <div className="flex items-end">
              <button className="w-full rounded-xl bg-slate-900 px-5 py-3 font-semibold text-white">
                {editingId ? "Update Score" : "Add Score"}
              </button>
            </div>
          </form>

          {editingId && (
            <button
              onClick={() => {
                setEditingId(null);
                setScore("");
                setDate("");
              }}
              className="mt-3 text-sm text-slate-500 underline"
            >
              Cancel editing
            </button>
          )}

          <div className="mt-6 space-y-3">
            {scores.length === 0 ? (
              <p className="text-slate-500">
                No scores added yet.
              </p>
            ) : (
              scores.map((item) => (
                <div
                  key={item.id}
                  className="flex items-center justify-between rounded-xl border p-4"
                >
                  <div>
                    <p className="font-bold">
                      {item.stablefordScore} points
                    </p>

                    <p className="text-sm text-slate-500">
                      {item.scoreDate}
                    </p>
                  </div>

                  <div className="flex gap-3">
                    <button
                      onClick={() => editScore(item)}
                      className="text-sm font-semibold text-blue-600"
                    >
                      Edit
                    </button>

                    <button
                      onClick={() => deleteScore(item.id)}
                      className="text-sm font-semibold text-red-600"
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        <div className="space-y-6">
          <div className="rounded-2xl border bg-white p-6 shadow-sm">
            <h2 className="text-xl font-bold">
              Subscription
            </h2>

            {subscription ? (
              <div className="mt-4 space-y-2 text-sm">
                <p>
                  Plan:{" "}
                  <strong>{subscription.plan}</strong>
                </p>

                <p>
                  Status:{" "}
                  <strong>{subscription.status}</strong>
                </p>

                <p>
                  Charity:{" "}
                  <strong>
                    {subscription.charityContribution}%
                  </strong>
                </p>

                <Link
                  to="/subscription"
                  className="mt-4 inline-block font-semibold text-blue-600"
                >
                  Manage subscription
                </Link>
              </div>
            ) : (
              <div>
                <p className="mt-3 text-sm text-slate-500">
                  No active subscription.
                </p>

                <Link
                  to="/subscription"
                  className="mt-4 inline-block rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white"
                >
                  Subscribe
                </Link>
              </div>
            )}
          </div>

          <div className="rounded-2xl border bg-white p-6 shadow-sm">
            <h2 className="text-xl font-bold">
              Charity Impact
            </h2>

            <div className="mt-4 space-y-4">
              <div>
                <label className="mb-2 block text-sm font-semibold">
                  Charity
                </label>

                <select
                  value={charity}
                  onChange={(e) =>
                    setCharity(e.target.value)
                  }
                  className="w-full rounded-xl border px-3 py-2"
                >
                  {charities.map((item) => (
                    <option
                      key={item.name}
                      value={item.name}
                    >
                      {item.name}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="mb-2 block text-sm font-semibold">
                  Contribution
                </label>

                <select
                  value={contribution}
                  onChange={(e) =>
                    setContribution(Number(e.target.value))
                  }
                  className="w-full rounded-xl border px-3 py-2"
                >
                  {contributionOptions.map((value) => (
                    <option key={value} value={value}>
                      {value}%
                    </option>
                  ))}
                </select>
              </div>

              <button
                onClick={saveCharity}
                className="w-full rounded-xl bg-slate-900 px-4 py-3 font-semibold text-white"
              >
                Save Charity Preferences
              </button>
            </div>
          </div>
        </div>
      </div>

      {message && (
        <div className="mt-6 rounded-xl bg-green-50 p-4 text-green-700">
          {message}
        </div>
      )}

      {error && (
        <div className="mt-6">
          <ErrorMessage message={error} />
        </div>
      )}
    </PageLayout>
  );
}

function Charities() {
  return (
    <PageLayout
      title="Our Charities"
      subtitle="Choose a cause that matters to you."
    >
      <div className="grid gap-6 md:grid-cols-2">
        {charities.map((charity) => (
          <div
            key={charity.name}
            className="rounded-2xl border bg-white p-7 shadow-sm"
          >
            <h2 className="text-xl font-bold">
              {charity.name}
            </h2>

            <p className="mt-3 leading-7 text-slate-600">
              {charity.description}
            </p>

            <Link
              to="/register"
              className="mt-5 inline-block font-semibold text-blue-600"
            >
              Support this cause →
            </Link>
          </div>
        ))}
      </div>
    </PageLayout>
  );
}

function Payment() {
  const [searchParams] = useSearchParams();

  const email =
    localStorage.getItem("digitalHeroesUser")
      ? JSON.parse(localStorage.getItem("digitalHeroesUser")).email
      : "";

  const plan = searchParams.get("plan") || "MONTHLY";
  const contribution =
    Number(searchParams.get("contribution")) || 10;

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const planName =
    plan === "YEARLY"
      ? "Yearly Subscription"
      : "Monthly Subscription";

  const amount =
    plan === "YEARLY"
      ? "$99.99 / year"
      : "$9.99 / month";

  const completePayment = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await axios.post(
        `${API}/subscriptions/mock-payment?email=${encodeURIComponent(email)}`,
        {
          plan,
          charityContribution: contribution,
        }
      );

      if (response.data?.paymentStatus === "SUCCESS") {
        localStorage.setItem(
          "digitalHeroesUser",
          JSON.stringify({
            ...JSON.parse(
              localStorage.getItem("digitalHeroesUser")
            ),
            subscribed: true,
            subscriptionPlan: plan,
            subscriptionStatus: "ACTIVE",
            charityContribution: contribution,
          })
        );

        window.location.href = "/payment-success";
      }
    } catch (err) {
      setError(
        err.response?.data ||
          "Payment could not be completed. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  if (!email) {
    return (
      <PageLayout
        title="Payment"
        subtitle="Please log in before continuing."
      >
        <div className="mx-auto max-w-2xl rounded-2xl border bg-white p-8 text-center shadow-sm">
          <p className="text-slate-600">
            You need to log in before completing your subscription.
          </p>

          <Link
            to="/login"
            className="mt-6 inline-block rounded-xl bg-blue-600 px-6 py-3 font-semibold text-white"
          >
            Login
          </Link>
        </div>
      </PageLayout>
    );
  }

  return (
    <PageLayout
      title="Complete Your Subscription"
      subtitle="This is a test payment flow for the Digital Heroes project."
    >
      <div className="mx-auto max-w-2xl rounded-2xl border bg-white p-8 shadow-sm">

        <div className="rounded-xl bg-slate-50 p-6">
          <h2 className="text-xl font-bold text-slate-900">
            {planName}
          </h2>

          <p className="mt-2 text-2xl font-bold text-blue-600">
            {amount}
          </p>

          <div className="mt-5 border-t pt-5">
            <p className="text-sm text-slate-500">
              Charity contribution
            </p>

            <p className="mt-1 text-lg font-semibold text-slate-900">
              {contribution}%
            </p>
          </div>
        </div>

        <div className="mt-6 rounded-xl border border-dashed p-5">
          <p className="font-semibold text-slate-900">
            Test Payment
          </p>

          <p className="mt-2 text-sm text-slate-600">
            No real money will be charged. Clicking the button
            below simulates a successful subscription payment.
          </p>
        </div>

        {error && (
          <div className="mt-5 rounded-xl bg-red-50 p-4 text-sm text-red-700">
            {error}
          </div>
        )}

        <button
          onClick={completePayment}
          disabled={loading}
          className="mt-6 w-full rounded-xl bg-blue-600 px-6 py-3 font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {loading
            ? "Processing Test Payment..."
            : "Complete Test Payment"}
        </button>

        <Link
          to="/subscription"
          className="mt-4 block text-center text-sm font-medium text-slate-600 hover:text-blue-600"
        >
          ← Back to Subscription
        </Link>
      </div>
    </PageLayout>
  );
}


function AuthLayout({ title, children }) {
  return (
    <>
      <Header />

      <main className="min-h-screen bg-slate-50 px-6 py-16">
        <div className="mx-auto max-w-md">
          <div className="mb-8 text-center">
            <h1 className="text-3xl font-bold text-slate-900">
              {title}
            </h1>
          </div>

          <div className="rounded-2xl border bg-white p-7 shadow-sm">
            {children}
          </div>
        </div>
      </main>
    </>
  );
}

function PageLayout({ title, subtitle, children }) {
  return (
    <>
      <Header />

      <main className="min-h-screen bg-slate-50 px-6 py-12">
        <div className="mx-auto max-w-7xl">
          <div className="mb-10">
            <h1 className="text-4xl font-bold text-slate-900">
              {title}
            </h1>

            <p className="mt-3 max-w-2xl text-slate-600">
              {subtitle}
            </p>
          </div>

          {children}
        </div>
      </main>
    </>
  );
}

function Input({
  label,
  type = "text",
  value,
  onChange,
  required = false,
  min,
  max,
}) {
  return (
    <div>
      <label className="mb-2 block text-sm font-semibold text-slate-700">
        {label}
      </label>

      <input
        type={type}
        value={value}
        onChange={onChange}
        required={required}
        min={min}
        max={max}
        className="w-full rounded-xl border px-4 py-3 outline-none focus:border-blue-500"
      />
    </div>
  );
}

function ErrorMessage({ message }) {
  return (
    <div className="rounded-xl bg-red-50 p-4 text-sm text-red-700">
      {message}
    </div>
  );
}

function PlanCard({
  title,
  price,
  selected,
  onClick,
}) {
  return (
    <button
      onClick={onClick}
      className={`rounded-2xl border p-6 text-left transition ${
        selected
          ? "border-blue-600 bg-blue-50 ring-2 ring-blue-200"
          : "bg-white hover:border-slate-400"
      }`}
    >
      <p className="text-sm font-semibold uppercase tracking-wide text-slate-500">
        {title}
      </p>

      <p className="mt-3 text-3xl font-bold">
        {price}
      </p>

      <p className="mt-1 text-sm text-slate-500">
        {title === "Monthly"
          ? "per month"
          : "per year"}
      </p>
    </button>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/admin" element={<AdminDashboard />} />
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route
          path="/subscription"
          element={<Subscription />}
        />
        <Route path="/payment" element={<Payment />} />
        <Route
          path="/payment-success"
          element={<PaymentSuccess />}
        />
        <Route
          path="/dashboard"
          element={<Dashboard />}
        />
        <Route
          path="/charities"
          element={<Charities />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;