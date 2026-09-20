import { Activity, AlertTriangle, Gauge, GitCommitVertical } from 'lucide-react'

function StatCard({ icon: Icon, label, value, accent }) {
  return (
    <div className="card p-5 flex items-center gap-4 animate-fadeIn">
      <div className={`w-11 h-11 rounded-xl flex items-center justify-center shrink-0 ${accent}`}>
        <Icon className="w-5 h-5" />
      </div>
      <div className="min-w-0">
        <p className="text-2xl font-bold text-white leading-tight truncate">{value}</p>
        <p className="text-xs text-slate-500 mt-0.5">{label}</p>
      </div>
    </div>
  )
}

export default function StatsCards({ stats }) {
  const latest = stats?.latestAnalysis

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <StatCard
        icon={GitCommitVertical}
        label="Total Analyses"
        value={stats?.totalAnalyses ?? 0}
        accent="bg-accent-500/10 text-accent-400"
      />
      <StatCard
        icon={AlertTriangle}
        label="High Risk Commits"
        value={stats?.highRiskCommits ?? 0}
        accent="bg-risk-high/10 text-risk-high"
      />
      <StatCard
        icon={Gauge}
        label="Average Risk Score"
        value={stats?.averageRiskScore ?? 0}
        accent="bg-indigo-500/10 text-indigo-400"
      />
      <StatCard
        icon={Activity}
        label="Latest Score"
        value={latest ? `${latest.riskScore} (${latest.riskLevel})` : '—'}
        accent="bg-emerald-500/10 text-emerald-400"
      />
    </div>
  )
}
