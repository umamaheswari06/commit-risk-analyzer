import { CheckCircle2 } from 'lucide-react'

const PRIORITY_STYLES = {
  HIGH: 'text-risk-high border-risk-high/30 bg-risk-high/10',
  MEDIUM: 'text-risk-medium border-risk-medium/30 bg-risk-medium/10',
  LOW: 'text-risk-low border-risk-low/30 bg-risk-low/10',
}

export default function RecommendedTests({ tests = [] }) {
  if (tests.length === 0) {
    return <div className="text-sm text-slate-500 py-6 text-center">No specific tests were recommended.</div>
  }

  return (
    <ul className="space-y-2">
      {tests.map((test, idx) => (
        <li
          key={idx}
          className="flex items-center gap-3 p-3 rounded-xl bg-base-800/60 border border-base-700"
        >
          <CheckCircle2 className="w-4 h-4 text-accent-400 shrink-0" />
          <span className="text-sm text-slate-300 flex-1">{test.description}</span>
          {test.priority && (
            <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border shrink-0 ${PRIORITY_STYLES[test.priority] || ''}`}>
              {test.priority}
            </span>
          )}
        </li>
      ))}
    </ul>
  )
}
