import { RadialBarChart, RadialBar, PolarAngleAxis } from 'recharts'
import { riskMeta } from '../utils/risk'

export default function RiskScoreGauge({ score, level, size = 220 }) {
  const meta = riskMeta(level)
  const data = [{ name: 'score', value: score, fill: meta.color }]

  return (
    <div className="relative flex items-center justify-center" style={{ width: size, height: size }}>
      <RadialBarChart
        width={size}
        height={size}
        cx="50%"
        cy="50%"
        innerRadius="72%"
        outerRadius="100%"
        barSize={14}
        data={data}
        startAngle={90}
        endAngle={-270}
      >
        <PolarAngleAxis type="number" domain={[0, 100]} angleAxisId={0} tick={false} />
        <RadialBar background={{ fill: '#1a2233' }} dataKey="value" cornerRadius={20} angleAxisId={0} />
      </RadialBarChart>
      <div className="absolute inset-0 flex flex-col items-center justify-center">
        <span className="text-5xl font-extrabold text-white tracking-tight">{score}</span>
        <span className="text-xs text-slate-500 mb-2">/ 100</span>
        <span
          className={`px-3 py-1 rounded-full text-xs font-bold tracking-wide border ${meta.bg} ${meta.border} ${meta.text}`}
        >
          {meta.label}
        </span>
      </div>
    </div>
  )
}
