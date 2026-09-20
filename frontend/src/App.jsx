import { Routes, Route, Link } from 'react-router-dom'
import { Menu } from 'lucide-react'
import { useState } from 'react'
import Sidebar from './components/Sidebar'
import Dashboard from './pages/Dashboard'
import Analyze from './pages/Analyze'
import History from './pages/History'
import AnalysisDetail from './pages/AnalysisDetail'

export default function App() {
  const [mobileNavOpen, setMobileNavOpen] = useState(false)

  return (
    <div className="flex h-screen w-full text-slate-200">
      <Sidebar />

      {/* Mobile top bar */}
      <div className="md:hidden fixed top-0 left-0 right-0 z-20 bg-base-900/90 backdrop-blur border-b border-base-700 px-4 py-3 flex items-center justify-between">
        <span className="font-bold text-white">CommitRisk</span>
        <button onClick={() => setMobileNavOpen((v) => !v)} className="text-slate-300">
          <Menu className="w-5 h-5" />
        </button>
      </div>
      {mobileNavOpen && (
        <div className="md:hidden fixed top-12 left-0 right-0 z-20 bg-base-900 border-b border-base-700 px-4 py-3 flex gap-4">
          <Link to="/" onClick={() => setMobileNavOpen(false)} className="text-sm text-slate-300">Dashboard</Link>
          <Link to="/analyze" onClick={() => setMobileNavOpen(false)} className="text-sm text-slate-300">Analyze</Link>
          <Link to="/history" onClick={() => setMobileNavOpen(false)} className="text-sm text-slate-300">History</Link>
        </div>
      )}

      <main className="flex-1 overflow-y-auto px-4 sm:px-8 py-8 md:py-8 mt-12 md:mt-0">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/analyze" element={<Analyze />} />
          <Route path="/history" element={<History />} />
          <Route path="/history/:id" element={<AnalysisDetail />} />
        </Routes>
      </main>
    </div>
  )
}
