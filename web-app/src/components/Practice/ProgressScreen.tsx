import React from 'react'
import { useLiveQuery } from 'dexie-react-hooks'
import { db } from '../../db'

const BezierTrendGraph: React.FC<{ points: number[]; minY: number; maxY: number; lineColor: string; fillColor: string }> = ({ points, minY, maxY, lineColor, fillColor }) => {
  if (points.length === 0) return null
  
  const width = 500
  const height = 180
  const padding = 10
  
  const valueRange = maxY - minY || 1
  const stepX = points.length > 1 ? (width - padding * 2) / (points.length - 1) : width - padding * 2
  
  const coords = points.map((val, idx) => {
    const x = padding + idx * stepX
    const y = height - padding - ((val - minY) / valueRange) * (height - padding * 2)
    return { x, y }
  })
  
  let pathD = ""
  if (coords.length > 0) {
    pathD = `M ${coords[0].x} ${coords[0].y}`
    for (let i = 0; i < coords.length - 1; i++) {
      const p0 = coords[i]
      const p1 = coords[i + 1]
      const cpX1 = p0.x + (p1.x - p0.x) / 3
      const cpY1 = p0.y
      const cpX2 = p0.x + 2 * (p1.x - p0.x) / 3
      const cpY2 = p1.y
      pathD += ` C ${cpX1} ${cpY1}, ${cpX2} ${cpY2}, ${p1.x} ${p1.y}`
    }
  }
  
  const fillD = coords.length > 0 
    ? `${pathD} L ${coords[coords.length - 1].x} ${height} L ${coords[0].x} ${height} Z` 
    : ""

  return (
    <svg className="w-full h-full" viewBox={`0 0 ${width} ${height}`} preserveAspectRatio="none">
      {[0, 0.25, 0.5, 0.75, 1].map((ratio, i) => {
        const y = padding + ratio * (height - padding * 2)
        return (
          <line key={i} x1="0" y1={y} x2={width} y2={y} stroke="#e9e8e1" strokeWidth="1" strokeDasharray="4 4" />
        )
      })}
      {coords.length > 1 && (
        <>
          <path d={fillD} fill={fillColor} />
          <path d={pathD} fill="none" stroke={lineColor} strokeWidth="3" strokeLinecap="round" />
        </>
      )}
      {coords.map((c, i) => (
        <circle key={i} cx={c.x} cy={c.y} r="5" fill={lineColor} stroke="#fff" strokeWidth="2" />
      ))}
    </svg>
  )
}

const ProgressScreen: React.FC = () => {
  const reflections = useLiveQuery(() => db.reflections.orderBy('timestamp').toArray())

  const streakDays = React.useMemo(() => {
    const days = new Array(14).fill(false)
    if (!reflections) return days
    const now = new Date()
    const dayMillis = 24 * 60 * 60 * 1000
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime()
    
    for (let i = 0; i < 14; i++) {
      const targetDayStart = today - (13 - i) * dayMillis
      const targetDayEnd = targetDayStart + dayMillis
      days[i] = reflections.some(r => {
        const t = new Date(r.timestamp).getTime()
        return t >= targetDayStart && t < targetDayEnd
      })
    }
    return days
  }, [reflections])

  const streakCount = React.useMemo(() => {
    let current = 0
    for (let i = 13; i >= 0; i--) {
      if (streakDays[i]) current++
      else break
    }
    return current
  }, [streakDays])

  if (!reflections) return null

  const sessionCount = reflections.length
  const displayReflections = reflections.slice(-10)

  const avgRadiance = sessionCount > 0 ? (reflections.reduce((acc, r) => acc + r.mettaRadiance, 0) / sessionCount).toFixed(1) : '---'
  const precisionRelease = sessionCount > 0 ? ((reflections.filter(r => r.releaseType === '6r').length / sessionCount) * 100).toFixed(0) : '0'

  const hindranceFrequencies = React.useMemo(() => {
    const map: Record<string, number> = { desire: 0, aversion: 0, sloth: 0, restless: 0, doubt: 0 }
    displayReflections.forEach(ref => {
      ref.hindrances.forEach(h => {
        if (h in map) {
          map[h]++
        }
      })
    })
    return map
  }, [displayReflections])

  return (
    <div className="max-w-4xl mx-auto px-6 pt-12 pb-24 space-y-12 animate-in fade-in duration-700">
      {/* Title */}
      <section className="space-y-2">
        <p className="font-label text-xs uppercase tracking-widest text-primary font-bold">Insights</p>
        <h2 className="font-headline text-4xl md:text-5xl font-light text-on-surface leading-tight">
          {sessionCount > 0 ? (
            <>
              The habit is deepening. <br />
              <span className="italic text-primary">Awareness finds its own space.</span>
            </>
          ) : (
            <>
              Your journey begins <br />
              <span className="italic text-primary">with a single smile.</span>
            </>
          )}
        </h2>
      </section>

      {/* 14-Day Consistency */}
      <section className="space-y-4">
        <h3 className="font-label text-[10px] font-bold uppercase tracking-widest text-on-surface-variant/70">Practice Consistency (14 Days)</h3>
        <div className="bg-surface-container-low rounded-3xl p-6 shadow-sm border border-outline-variant/10">
          <div className="flex justify-between items-center mb-4">
            <span className="text-base font-bold text-on-surface">
              {streakCount > 0 ? `${streakCount}-Day Streak` : 'No active streak'}
            </span>
          </div>
          <div className="flex justify-between gap-1">
            {streakDays.map((isSat, i) => (
              <div 
                key={i} 
                className={`w-4 h-4 rounded-full transition-all ${isSat ? 'bg-primary' : 'bg-outline-variant/30'}`}
              />
            ))}
          </div>
          <div className="flex justify-between text-[10px] text-outline mt-2 font-medium">
            <span>14d ago</span>
            <span className="font-bold text-on-surface">Today</span>
          </div>
        </div>
      </section>

      {/* Sittings & Metrics Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-gradient-to-br from-primary to-primary-dim rounded-3xl p-6 text-on-primary flex flex-col justify-between h-40 shadow-md">
          <span className="text-[10px] font-bold tracking-widest uppercase opacity-80">Total Sittings</span>
          <div>
            <div className="text-5xl font-light">{sessionCount}</div>
            <span className="text-xs opacity-80">Completed sessions</span>
          </div>
        </div>

        <div className="bg-surface-container-low rounded-3xl p-6 flex flex-col justify-between h-40 border border-outline-variant/10 shadow-sm">
          <span className="text-[10px] font-bold tracking-widest uppercase text-outline">Avg Radiance</span>
          <div>
            <div className="text-4xl font-headline text-primary">{avgRadiance}/10</div>
            <span className="text-xs text-on-surface-variant">Chest warmth index</span>
          </div>
        </div>

        <div className="bg-surface-container-low rounded-3xl p-6 flex flex-col justify-between h-40 border border-outline-variant/10 shadow-sm">
          <span className="text-[10px] font-bold tracking-widest uppercase text-outline">6R Precision</span>
          <div>
            <div className="text-4xl font-headline text-primary">{precisionRelease}%</div>
            <span className="text-xs text-on-surface-variant">6R cycle release rate</span>
          </div>
        </div>
      </div>

      {sessionCount === 0 ? (
        /* Empty State Cards */
        <section className="grid grid-cols-1 md:grid-cols-3 gap-6 pt-6">
          <div className="bg-surface-container-low rounded-3xl p-6 space-y-4 border border-outline-variant/10">
            <div className="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center text-primary">
              <span className="material-symbols-outlined">self_improvement</span>
            </div>
            <h4 className="font-headline text-lg font-bold text-on-surface">Your Practice Space</h4>
            <p className="text-sm text-on-surface-variant leading-relaxed">
              Ready to sit? Choose an atmosphere on the Start tab and begin.
            </p>
          </div>

          <div className="bg-surface-container-low rounded-3xl p-6 space-y-4 border border-outline-variant/10">
            <div className="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center text-primary">
              <span className="material-symbols-outlined">menu_book</span>
            </div>
            <h4 className="font-headline text-lg font-bold text-on-surface">Why we reflect</h4>
            <p className="text-sm text-on-surface-variant leading-relaxed">
              Reflections help you understand the patterns of hindrances and celebrate moments of smooth flow.
            </p>
          </div>

          <div className="bg-surface-container-low rounded-3xl p-6 space-y-4 border border-outline-variant/10">
            <div className="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center text-primary">
              <span className="material-symbols-outlined">star_rate</span>
            </div>
            <h4 className="font-headline text-lg font-bold text-on-surface">First Milestone</h4>
            <p className="text-sm text-on-surface-variant leading-relaxed">
              Complete 3 sittings to unlock detailed trends and sensitivity tracking.
            </p>
          </div>
        </section>
      ) : (
        /* Trend Graphs and Patterns */
        <div className="space-y-10 pt-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {/* Radiance Trend */}
            <div className="bg-surface-container-low rounded-3xl p-6 border border-outline-variant/10 shadow-sm space-y-4">
              <div>
                <h4 className="font-headline text-lg font-bold text-on-surface">Initial Feeling</h4>
                <p className="text-xs text-on-surface-variant">Chest glow rating on scale of 1 to 10.</p>
              </div>
              <div className="h-44 w-full">
                <BezierTrendGraph 
                  points={displayReflections.map(r => r.mettaRadiance)}
                  minY={1}
                  maxY={10}
                  lineColor="var(--color-primary)"
                  fillColor="rgba(var(--color-primary-rgb, 142, 60, 48), 0.08)"
                />
              </div>
            </div>

            {/* Recognition Trend */}
            <div className="bg-surface-container-low rounded-3xl p-6 border border-outline-variant/10 shadow-sm space-y-4">
              <div>
                <h4 className="font-headline text-lg font-bold text-on-surface">Recognition Speed</h4>
                <p className="text-xs text-on-surface-variant">Proactive detection of distractions (scale 1 to 5).</p>
              </div>
              <div className="h-44 w-full">
                <BezierTrendGraph 
                  points={displayReflections.map(r => r.recognitionLevel)}
                  minY={1}
                  maxY={5}
                  lineColor="var(--color-outline)"
                  fillColor="rgba(var(--color-outline-rgb, 122, 100, 95), 0.08)"
                />
              </div>
            </div>
          </div>

          {/* Distraction Patterns */}
          <section className="bg-surface-container-low rounded-3xl p-6 border border-outline-variant/10 shadow-sm space-y-6">
            <div>
              <h4 className="font-headline text-lg font-bold text-on-surface">Distraction Patterns</h4>
              <p className="text-xs text-on-surface-variant">Relative frequency of distractions, sorted from most common.</p>
            </div>
            
            <div className="space-y-4">
              {Object.entries(hindranceFrequencies)
                .sort((a, b) => b[1] - a[1])
                .map(([hId, count]) => {
                  const labelMap: Record<string, string> = {
                    desire: "Sensual Desire",
                    aversion: "Ill-Will / Aversion",
                    sloth: "Sloth & Torpor",
                    restless: "Restlessness",
                    doubt: "Doubt"
                  }
                  const label = labelMap[hId] || hId
                  const percentage = displayReflections.length > 0 
                    ? Math.round((count / displayReflections.length) * 100) 
                    : 0
                  
                  return (
                    <div key={hId} className="space-y-2">
                      <div className="flex justify-between items-center text-sm font-semibold">
                        <span className="text-on-surface">{label}</span>
                        <span className="text-outline">{percentage}% of sittings</span>
                      </div>
                      <div className="w-full bg-outline-variant/30 h-2 rounded-full overflow-hidden">
                        <div 
                          className="bg-primary h-full rounded-full transition-all duration-500"
                          style={{ width: `${percentage}%` }}
                        />
                      </div>
                    </div>
                  )
                })}
            </div>
          </section>
        </div>
      )}
    </div>
  )
}

export default ProgressScreen
