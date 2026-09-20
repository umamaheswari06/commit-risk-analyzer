export const RISK_META = {
  LOW: { color: '#22c55e', bg: 'bg-risk-low/10', border: 'border-risk-low/30', text: 'text-risk-low', label: 'LOW' },
  MEDIUM: { color: '#eab308', bg: 'bg-risk-medium/10', border: 'border-risk-medium/30', text: 'text-risk-medium', label: 'MEDIUM' },
  HIGH: { color: '#f97316', bg: 'bg-risk-high/10', border: 'border-risk-high/30', text: 'text-risk-high', label: 'HIGH' },
  CRITICAL: { color: '#ef4444', bg: 'bg-risk-critical/10', border: 'border-risk-critical/30', text: 'text-risk-critical', label: 'CRITICAL' },
}

export function riskMeta(level) {
  return RISK_META[level] || RISK_META.LOW
}

export function formatRelativeTime(isoString) {
  if (!isoString) return ''
  const date = new Date(isoString)
  const diffMs = Date.now() - date.getTime()
  const diffMins = Math.round(diffMs / 60000)
  if (diffMins < 1) return 'just now'
  if (diffMins < 60) return `${diffMins}m ago`
  const diffHours = Math.round(diffMins / 60)
  if (diffHours < 24) return `${diffHours}h ago`
  const diffDays = Math.round(diffHours / 24)
  if (diffDays < 30) return `${diffDays}d ago`
  return date.toLocaleDateString()
}
