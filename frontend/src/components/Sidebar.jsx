import { NavLink } from 'react-router-dom'
import { GitCommitHorizontal, LayoutDashboard, History, ShieldCheck } from 'lucide-react'

const navItems = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/analyze', label: 'Analyze', icon: GitCommitHorizontal },
  { to: '/history', label: 'History', icon: History },
]

export default function Sidebar() {
  return (
    <aside className="hidden md:flex md:w-64 flex-col border-r border-base-700 bg-base-900/60 backdrop-blur-sm px-4 py-6">
      <div className="flex items-center gap-2 px-2 mb-8">
        <div className="w-9 h-9 rounded-xl bg-accent-500/15 border border-accent-500/30 flex items-center justify-center">
          <ShieldCheck className="w-5 h-5 text-accent-400" />
        </div>
        <div>
          <p className="font-bold text-white leading-tight tracking-tight">CommitRisk</p>
          <p className="text-[11px] text-slate-500 leading-tight">AI Risk Analyzer</p>
        </div>
      </div>

      <nav className="flex flex-col gap-1">
        {navItems.map(({ to, label, icon: Icon, end }) => (
          <NavLink
            key={to}
            to={to}
            end={end}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-accent-500/10 text-accent-400 border border-accent-500/20'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-base-800 border border-transparent'
              }`
            }
          >
            <Icon className="w-4 h-4" />
            {label}
          </NavLink>
        ))}
      </nav>

      <div className="mt-auto px-3 py-4 rounded-xl bg-base-850 border border-base-700 text-xs text-slate-500 leading-relaxed">
        <p className="text-slate-400 font-medium mb-1">About this tool</p>
        Estimates commit <span className="text-slate-300">change risk</span> from measurable
        characteristics. It does not detect actual bugs.
      </div>
    </aside>
  )
}
