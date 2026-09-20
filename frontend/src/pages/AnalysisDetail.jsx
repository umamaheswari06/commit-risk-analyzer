import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { Loader2, ArrowLeft, FileCode2, Plus, Minus, Sparkles } from 'lucide-react'
import { getAnalysisById } from '../api/client'
import RiskScoreGauge from '../components/RiskScoreGauge'
import RiskFactorList from '../components/RiskFactorList'
import RecommendedTests from '../components/RecommendedTests'
import { formatRelativeTime } from '../utils/risk'

export default function AnalysisDetail() {
  const { id } = useParams()
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    setLoading(true)
    getAnalysisById(id)
      .then(setResult)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id])

  if (loading) {
    return (
      <div className="flex items-center gap-2 text-slate-500 text-sm py-20 justify-center">
        <Loader2 className="w-4 h-4 animate-spin" /> Loading analysis…
      </div>
    )
  }

  if (error) {
    return <div className="max-w-3xl mx-auto card p-6 text-sm text-red-400 text-center">{error}</div>
  }

  return (
    <div className="max-w-6xl mx-auto space-y-6 animate-fadeIn">
      <Link to="/history" className="inline-flex items-center gap-1.5 text-sm text-slate-400 hover:text-slate-200">
        <ArrowLeft className="w-4 h-4" /> Back to history
      </Link>

      <div>
        <h1 className="text-2xl font-bold text-white tracking-tight">
          {result.commitMessage || `Analysis #${result.id}`}
        </h1>
        <p className="text-sm text-slate-500 mt-1">{formatRelativeTime(result.createdAt)}</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="card p-6 flex flex-col items-center justify-center">
          <RiskScoreGauge score={result.riskScore} level={result.riskLevel} />
          {result.aiGenerated ? (
            <span className="mt-4 text-[11px] text-accent-400 flex items-center gap-1">
              <Sparkles className="w-3 h-3" /> AI-generated explanation
            </span>
          ) : (
            <span className="mt-4 text-[11px] text-slate-500">Rule-based explanation</span>
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
