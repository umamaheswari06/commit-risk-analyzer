import { useState } from 'react'
import { Loader2, Play, Sparkles, FileCode2, Plus, Minus, Info } from 'lucide-react'
import { analyzeCommit } from '../api/client'
import RiskScoreGauge from '../components/RiskScoreGauge'
import RiskFactorList from '../components/RiskFactorList'
import RecommendedTests from '../components/RecommendedTests'

const SAMPLE_DIFF = `diff --git a/src/main/java/com/example/PaymentService.java b/src/main/java/com/example/PaymentService.java
index 123abc..456def 100644
--- a/src/main/java/com/example/PaymentService.java
+++ b/src/main/java/com/example/PaymentService.java
@@ -10,7 +10,12 @@ public class PaymentService {

     public double calculatePrice(double price) {
-        return price;
+        double tax = price * 0.18;
+        double total = price + tax;
+        chargeCard(total);
+        return total;
     }
+
+    private void chargeCard(double amount) {
+        paymentGateway.charge(amount);
+    }
 }
diff --git a/src/main/java/com/example/UserRepository.java b/src/main/java/com/example/UserRepository.java
index 789ghi..012jkl 100644
--- a/src/main/java/com/example/UserRepository.java
+++ b/src/main/java/com/example/UserRepository.java
@@ -5,4 +5,6 @@ public interface UserRepository extends JpaRepository<User, Long> {

     List<User> findByActiveTrue();
+
+    @Query("UPDATE User u SET u.lastLogin = CURRENT_TIMESTAMP WHERE u.id = :id")
+    void updateLastLogin(Long id);
 }
`

export default function Analyze() {
  const [diff, setDiff] = useState('')
  const [commitMessage, setCommitMessage] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [result, setResult] = useState(null)

  async function handleAnalyze() {
    setError(null)
    setResult(null)
    if (!diff.trim()) {
      setError('Please paste a Git diff before analyzing.')
      return
    }
    setLoading(true)
    try {
      const data = await analyzeCommit({ diff, commitMessage })
      setResult(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  function loadSample() {
    setDiff(SAMPLE_DIFF)
    setCommitMessage('Add tax calculation and payment charge to checkout flow')
  }

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white tracking-tight">Analyze Commit</h1>
        <p className="text-sm text-slate-500 mt-1">
          Paste a unified Git diff to estimate its change risk and get AI-assisted testing recommendations.
        </p>
      </div>

      <div className="card p-5 space-y-4">
        <div className="flex items-center justify-between">
          <label className="text-sm font-medium text-slate-300">Commit message (optional)</label>
          <button
            onClick={loadSample}
            className="text-xs text-accent-400 hover:text-accent-300 font-medium flex items-center gap-1"
          >
            <Sparkles className="w-3.5 h-3.5" />
            Load sample diff
          </button>
        </div>
        <input
          type="text"
          value={commitMessage}
          onChange={(e) => setCommitMessage(e.target.value)}
          placeholder="e.g. Fix payment calculation bug"
          className="w-full bg-base-900 border border-base-700 rounded-xl px-4 py-2.5 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-accent-500/40 focus:border-accent-500/40"
        />

        <label className="text-sm font-medium text-slate-300 block">Git diff</label>
        <textarea
          value={diff}
          onChange={(e) => setDiff(e.target.value)}
          placeholder="Paste git diff here..."
          spellCheck={false}
          className="diff-editor w-full h-72 bg-base-950 border border-base-700 rounded-xl px-4 py-3 text-sm text-slate-300 placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-accent-500/40 focus:border-accent-500/40 resize-y"
        />

        {error && (
          <div className="flex items-start gap-2 text-sm text-red-400 bg-red-500/10 border border-red-500/20 rounded-xl px-4 py-3">
            <Info className="w-4 h-4 mt-0.5 shrink-0" />
            {error}
          </div>
        )}

        <button
          onClick={handleAnalyze}
          disabled={loading}
          className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-2.5 rounded-xl bg-accent-500 hover:bg-accent-600 disabled:opacity-60 disabled:cursor-not-allowed text-base-950 font-semibold text-sm transition-colors shadow-glow"
        >
          {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Play className="w-4 h-4" />}
          {loading ? 'Analyzing…' : 'Analyze Commit'}
        </button>
      </div>

      {result && (
        <div className="space-y-6 animate-fadeIn">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="card p-6 flex flex-col items-center justify-center">
              <RiskScoreGauge score={result.riskScore} level={result.riskLevel} />
              {result.aiGenerated ? (
                <span className="mt-4 text-[11px] text-accent-400 flex items-center gap-1">
                  <Sparkles className="w-3 h-3" /> AI-generated explanation
                </span>
              ) : (
                <span className="mt-4 text-[11px] text-slate-500">Rule-based explanation (AI unavailable)</span>
              )}
            </div>

            <div className="lg:col-span-2 grid grid-cols-3 gap-4">
              <MetricTile icon={FileCode2} label="Files Changed" value={result.filesChanged} color="text-indigo-400" />
              <MetricTile icon={Plus} label="Lines Added" value={result.linesAdded} color="text-emerald-400" />
              <MetricTile icon={Minus} label="Lines Deleted" value={result.linesDeleted} color="text-red-400" />

              <div className="col-span-3 card p-5">
                <h3 className="text-sm font-semibold text-slate-200 mb-2">AI Analysis</h3>
                <p className="text-sm text-slate-400 leading-relaxed">{result.aiSummary}</p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div className="card p-5">
              <h3 className="text-sm font-semibold text-slate-200 mb-4">Risk Factors</h3>
              <RiskFactorList factors={result.riskFactors} />
            </div>
            <div className="card p-5">
              <h3 className="text-sm font-semibold text-slate-200 mb-4">Recommended Tests</h3>
              <RecommendedTests tests={result.recommendedTests} />
            </div>
          </div>

          {result.potentialRisks?.length > 0 && (
            <div className="card p-5">
              <h3 className="text-sm font-semibold text-slate-200 mb-3">Potential Risks</h3>
              <ul className="space-y-2">
                {result.potentialRisks.map((risk, idx) => (
                  <li key={idx} className="text-sm text-slate-400 flex gap-2">
                    <span className="text-amber-400">•</span>
                    {risk}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

function MetricTile({ icon: Icon, label, value, color }) {
  return (
    <div className="card p-5 flex flex-col items-center justify-center text-center">
      <Icon className={`w-5 h-5 mb-2 ${color}`} />
      <p className="text-2xl font-bold text-white">{value}</p>
      <p className="text-xs text-slate-500 mt-0.5">{label}</p>
    </div>
  )
}
