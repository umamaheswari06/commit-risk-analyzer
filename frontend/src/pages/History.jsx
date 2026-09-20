import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Loader2, Search } from 'lucide-react'
import { getHistory } from '../api/client'
import { riskMeta, formatRelativeTime } from '../utils/risk'

export default function History() {
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [query, setQuery] = useState('')
  const [levelFilter, setLevelFilter] = useState('ALL')

  useEffect(() => {
    getHistory()
      .then(setHistory)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  const filtered = history.filter((a) => {
    const matchesQuery = (a.commitMessage || '').toLowerCase().includes(query.toLowerCase())
    const matchesLevel = levelFilter === 'ALL' || a.riskLevel === levelFilter
    return matchesQuery && matchesLevel
  })

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white tracking-tight">Analysis History</h1>
        <p className="text-sm text-slate-500 mt-1">All previously analyzed commits, newest first.</p>
      </div>

      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search by commit message…"
            className="w-full bg-base-900 border border-base-700 rounded-xl pl-10 pr-4 py-2.5 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-accent-500/40"
          />
        </div>
        <select
          value={levelFilter}
          onChange={(e) => setLevelFilter(e.target.value)}
          className="bg-base-900 border border-base-700 rounded-xl px-4 py-2.5 text-sm text-slate-200 focus:outline-none focus:ring-2 focus:ring-accent-500/40"
        >
          <option value="ALL">All levels</option>
          <option value="LOW">Low</option>
          <option value="MEDIUM">Medium</option>
          <option value="HIGH">High</option>
          <option value="CRITICAL">Critical</option>
        </select>
      </div>

      {loading && (
        <div className="flex items-center gap-2 text-slate-500 text-sm py-12 justify-center">
          <Loader2 className="w-4 h-4 animate-spin" /> Loading history…
        </div>
      )}

      {error && <div className="card p-6 text-sm text-red-400 text-center">{error}</div>}

      {!loading && !error && (
        <div className="card divide-y divide-base-700 overflow-hidden">
          {filtered.length === 0 && (
            <p className="text-sm text-slate-500 text-center py-10">No analyses match your filters.</p>
          )}
          {filtered.map((a) => {
            const meta = riskMeta(a.riskLevel)
            return (
              <Link
                key={a.id}
                to={`/history/${a.id}`}
                className="flex items-center justify-between p-4 hover:bg-base-800/60 transition-colors"
              >
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-medium text-slate-200 truncate">
                    {a.commitMessage || `Analysis #${a.id}`}
                  </p>
                  <p className="text-xs text-slate-500 mt-0.5">
                    {a.filesChanged} files changed · {formatRelativeTime(a.createdAt)}
                  </p>
                </div>
                <span className={`text-xs font-bold px-2.5 py-1 rounded-full border shrink-0 ml-4 ${meta.bg} ${meta.border} ${meta.text}`}>
                  {a.riskScore} · {meta.label}
                </span>
              </Link>
            )
          })}
        </div>
      )}
    </div>
  )
}
