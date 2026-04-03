import React from 'react'
import { useLiveQuery } from 'dexie-react-hooks'
import { db } from '../../db'

const ProgressScreen: React.FC = () => {
  const reflections = useLiveQuery(() => db.reflections.orderBy('timestamp').toArray())

  if (!reflections) return null

  const sessionCount = reflections.length
  const avgRadiance = sessionCount > 0 ? (reflections.reduce((acc, r) => acc + r.mettaRadiance, 0) / sessionCount).toFixed(1) : 0
  const precisionRelease = sessionCount > 0 ? ((reflections.filter(r => r.releaseType === '6r').length / sessionCount) * 100).toFixed(0) : 0

  return (
    <div className="max-w-6xl mx-auto px-6 pt-12 space-y-16 animate-in fade-in duration-700">
      {/* Hero Section */}
      <section className="space-y-4">
        <p className="font-label text-sm uppercase tracking-[0.1rem] text-primary">Monthly Observation</p>
        <h2 className="font-headline text-5xl md:text-7xl font-light text-on-surface leading-tight max-w-3xl">
          {sessionCount > 0 ? 'The habit is deepening. Awareness finds its own space.' : 'Your journey begins with a single smile.'}
        </h2>
        <div className="h-1 w-24 bg-primary-container rounded-full mt-8"></div>
      </section>

      {/* Bento Grid */}
      <div className="grid grid-cols-1 md:grid-cols-12 gap-8">
        {/* Sati Sensitivity Trend (Wide) */}
        <section className="md:col-span-8 bg-surface-container-lowest rounded-3xl p-8 shadow-sm space-y-8">
          <div className="flex justify-between items-start">
            <div>
              <h3 className="font-label text-xs uppercase tracking-wider text-on-surface-variant font-semibold">Sati Sensitivity Trend</h3>
              <p className="font-headline text-2xl text-on-surface mt-1">Recognition Speed</p>
            </div>
            <div className="bg-primary-container px-4 py-1.5 rounded-full">
              <span className="text-on-primary-container text-xs font-bold tracking-tight">LEVEL {sessionCount > 0 ? (reflections.reduce((acc, r) => acc + r.recognitionLevel, 0) / sessionCount).toFixed(1) : '---'}</span>
            </div>
          </div>
          
          {/* Visualizing Recognition Levels (Bar Chart) */}
          <div className="relative h-64 w-full flex items-end gap-2 px-4 border-b border-surface-variant">
            <div className="absolute inset-0 flex flex-col justify-between py-2">
              <span className="text-[10px] text-outline-variant font-medium">Level 5</span>
              <span className="text-[10px] text-outline-variant font-medium">Level 3</span>
              <span className="text-[10px] text-outline-variant font-medium">Level 1</span>
            </div>
            {reflections.slice(-7).map((r, i) => (
              <div 
                key={i} 
                className="flex-1 bg-primary-container/40 rounded-t-lg transition-all hover:bg-primary"
                style={{ height: `${(r.recognitionLevel / 5) * 100}%` }}
              ></div>
            ))}
            {reflections.length === 0 && (
              <div className="absolute inset-0 flex items-center justify-center text-on-surface-variant/30 font-label italic">Waiting for your first reflections...</div>
            )}
          </div>
          
          <div className="p-6 bg-surface-container-low rounded-2xl italic text-on-surface-variant text-sm">
            {sessionCount > 5 ? "Your mindfulness is becoming more proactive as you catch thoughts earlier." : "Continue practicing the 6R cycle to sharpen your awareness of early tension."}
          </div>
        </section>

        {/* Daily Awareness Score */}
        <section className="md:col-span-4 bg-primary bg-gradient-to-br from-primary to-primary-dim rounded-3xl p-8 flex flex-col justify-between text-on-primary shadow-xl">
          <div className="space-y-1">
            <h3 className="font-label text-xs uppercase tracking-widest opacity-80 font-bold">Session Count</h3>
            <p className="font-headline text-xl">Total Sittings</p>
          </div>
          <div className="py-12 flex flex-col items-center justify-center space-y-4">
            <div className="text-8xl font-light tracking-tighter">{sessionCount}</div>
            <div className="font-label text-xs tracking-widest uppercase opacity-70">Meditative Moments</div>
          </div>
          <div className="bg-on-primary/10 rounded-2xl p-4 text-xs leading-relaxed">
            The mind maintains its poise through consistency. Every sit counts toward liberation.
          </div>
        </section>

        {/* Stats Cards */}
        <section className="md:col-span-12 grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-surface-container-low rounded-3xl p-8 space-y-1 shadow-sm border border-outline-variant/10">
            <p className="text-[10px] font-bold uppercase tracking-widest text-outline">Avg Radiance</p>
            <p className="font-headline text-2xl text-primary">{avgRadiance}/10</p>
          </div>
          <div className="bg-surface-container-low rounded-3xl p-8 space-y-1 shadow-sm border border-outline-variant/10">
            <p className="text-[10px] font-bold uppercase tracking-widest text-outline">6R Precision</p>
            <p className="font-headline text-2xl text-primary">{precisionRelease}%</p>
          </div>
          <div className="bg-surface-container-low rounded-3xl p-8 space-y-1 shadow-sm border border-outline-variant/10">
            <p className="text-[10px] font-bold uppercase tracking-widest text-outline">Smile Power</p>
            <p className="font-headline text-2xl text-primary">
              {sessionCount > 0 ? (reflections.reduce((acc, r) => acc + r.smileQuality, 0) / sessionCount).toFixed(1) : '---'}
            </p>
          </div>
        </section>
      </div>

      {/* Encouragement */}
      <section className="flex flex-col items-center text-center space-y-6 pb-12">
        <p className="text-on-surface-variant max-w-lg italic">
          "The mind is becoming more transparent. Continue the gentle effort."
        </p>
      </section>
    </div>
  )
}

export default ProgressScreen
