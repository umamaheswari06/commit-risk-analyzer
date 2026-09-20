import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts'
import { ArrowRight, Loader2 } from 'lucide-react'
import { getDashboardStats, getHistory } from '../api/client'
import StatsCards from '../components/StatsCards'
import { riskMeta, formatRelativeTime } from '../utils/risk'

export default function Dashboard() {
  const [stats, setStats] = useState(null)
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let cancelled = false
    async function load() {
      try {
        const [statsData, historyData] = await Promise.all([getDashboardStats(), getHistory()])
        if (!cancelled) {
          setStats(statsData)
          setHistory(historyData)
        }
      } catch (err) {
        if (!cancelled) setError(err.message)
      } finally {
        if (!cancelled) setLoading(false)
      }
    }
    load()
    return () => { cancelled = true }
  }, [])

  const chartData = [...history]
    .slice(0, 10)
    .reverse()
    .map((a) => ({ name: `#${a.id}`, score: a.riskScore, level: a.riskLevel }))

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Dashboard</h1>
          <p className="text-sm text-slate-500 mt-1">Overview of your commit risk analysis activity.</p>
        </div>
        <Link
          to="/analyze"
          className="hidden sm:inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-accent-500 hover:bg-accent-600 text-base-950 text-sm font-semibold transition-colors"
        >
          Analyze a commit <ArrowRight className="w-4 h-4" />
        </Link>
      </div>

      {loading && (
        <div className="flex items-center gap-2 text-slate-500 text-sm py-12 justify-center">
          <Loader2 className="w-4 h-4 animate-spin" /> Loading dashboard…
        </div>
      )}

      {error && (
        <div className="card p-6 text-sm text-red-400 text-center">{error}</div>
      )}

      {!loading && !error && (
        <>
          <StatsCards stats={stats} />

          <div className="card p-6">
            <h3 className="text-sm font-semibold text-slate-200 mb-4">Recent Risk Scores</h3>
            {chartData.length === 0 ? (
              <p className="text-sm text-slate-500 text-center py-10">
                No analyses yet — <Link to="/analyze" className="text-accent-400 hover:underline">analyze your first commit</Link>.
              </p>
            ) : (
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#232c40" vertical={false} />
                  <XAxis dataKey="name" stroke="#64748b" fontSize={12} tickLine={false} axisLine={false} />
                  <YAxis stroke="#64748b" fontSize={12} domain={[0, 100]} tickLine={false} axisLine={false} />
                  <Tooltip
                    contentStyle={{ background: '#141a29', border: '1px solid #232c40', borderRadius: 12, fontSize: 12 }}
                    labelStyle={{ color: '#94a3b8' }}
                  />
                  <Bar dataKey="score" radius={[6, 6, 0, 0]}>
                    {chartData.map((entry, idx) => (
                      <Cell key={idx} fill={riskMeta(entry.level).color} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            )}
          </div>

          <div className="card p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-sm font-semibold text-slate-200">Latest Analyses</h3>
              <Link to="/history" className="text-xs text-accent-400 hover:text-accent-300 font-medium">
                View all
              </Link>
            </div>
            <div className="space-y-2">
              {history.slice(0, 5).map((a) => {
                const meta = riskMeta(a.riskLevel)
                return (
                  <Link
                    key={a.id}
                    to={`/history/${a.id}`}
                    className="flex items-center justify-between p-3 rounded-xl bg-base-800/60 border border-base-700 hover:border-accent-500/30 transition-colors"
                  >
                    <div className="min-w-0">
                      <p className="text-sm text-slate-200 truncate">{a.commitMessage || `Analysis #${a.id}`}</p>
                      <p className="text-xs text-slate-500">{a.filesChanged} files · {formatRelativeTime(a.createdAt)}</p>
                    </div>
                    <span className={`text-xs font-bold px-2.5 py-1 rounded-full border shrink-0 ${meta.bg} ${meta.border} ${meta.text}`}>
                      {a.riskScore} · {meta.label}
                    </span>
                  </Link>
                )
              })}
              {history.length === 0 && (
                <p className="text-sm text-slate-500 text-center py-6">No analyses yet.</p>
              )}
            </div>
          </div>
        </>
      )}
    </div>
  )
}
