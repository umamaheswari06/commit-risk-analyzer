import { AlertOctagon } from 'lucide-react'

export default function RiskFactorList({ factors = [] }) {
  if (factors.length === 0) {
    return (
      <div className="text-sm text-slate-500 py-6 text-center">
        No specific risk factors were detected for this commit.
      </div>
    )
  }

  const sorted = [...factors].sort((a, b) => b.impact - a.impact)
  const maxImpact = Math.max(...sorted.map((f) => f.impact), 1)

  return (
    <div className="space-y-3">
      {sorted.map((factor, idx) => (
        <div key={idx} className="flex items-start gap-3 p-3 rounded-xl bg-base-800/60 border border-base-700">
          <AlertOctagon className="w-4 h-4 text-amber-400 mt-0.5 shrink-0" />
          <div className="flex-1 min-w-0">
            <div className="flex items-center justify-between gap-3">
              <p className="text-sm font-semibold text-slate-200">{factor.name}</p>
              <span className="text-xs font-bold text-amber-400 shrink-0">+{factor.impact}</span>
            </div>
            <p className="text-xs text-slate-500 mt-0.5">{factor.description}</p>
            <div className="mt-2 h-1.5 rounded-full bg-base-700 overflow-hidden">
              <div
                className="h-full bg-gradient-to-r from-amber-500 to-orange-500 rounded-full"
                style={{ width: `${(factor.impact / maxImpact) * 100}%` }}
              />
            </div>
          </div>
        </div>
      ))}
    </div>
  )
}
