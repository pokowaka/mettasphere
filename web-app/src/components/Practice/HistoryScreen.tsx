import React from 'react'
import { useLiveQuery } from 'dexie-react-hooks'
import { db } from '../../db'

const HistoryScreen: React.FC = () => {
  const reflections = useLiveQuery(() => db.reflections.orderBy('timestamp').reverse().toArray())

  if (!reflections) return null

  return (
    <div className="max-w-4xl mx-auto px-6 pt-12 space-y-12 animate-in fade-in duration-700">
      <header className="space-y-2">
        <h2 className="font-headline text-4xl text-primary">Your Journal</h2>
        <p className="text-on-surface-variant italic">A record of your path toward tranquil awareness.</p>
      </header>

      <div className="space-y-4">
        {reflections.map((r) => (
          <div key={r.id} className="bg-surface-container-low rounded-3xl p-6 shadow-sm border border-outline-variant/10 flex justify-between items-center group hover:bg-surface-container transition-all">
            <div className="space-y-1">
              <p className="font-label text-[10px] uppercase tracking-widest text-outline font-bold">
                {r.timestamp.toLocaleDateString()} • {r.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
              </p>
              <h3 className="font-headline text-xl text-on-surface">6R Reflection</h3>
              <div className="flex gap-2 mt-2">
                {r.hindrances.map(h => (
                  <span key={h} className="px-2 py-0.5 bg-secondary-container text-on-secondary-container rounded-full text-[10px] uppercase font-bold tracking-tighter">
                    {h}
                  </span>
                ))}
              </div>
            </div>
            <div className="text-right">
              <div className="flex items-center gap-1 justify-end text-primary">
                <span className="material-symbols-outlined text-sm">sunny</span>
                <span className="font-headline text-lg">{r.mettaRadiance}</span>
              </div>
              <p className="text-[10px] font-label text-outline uppercase tracking-widest">Radiance</p>
            </div>
          </div>
        ))}

        {reflections.length === 0 && (
          <div className="text-center py-24 space-y-4">
            <div className="w-20 h-20 bg-surface-container-high rounded-full flex items-center justify-center mx-auto text-on-surface-variant/30">
              <span className="material-symbols-outlined text-4xl">menu_book</span>
            </div>
            <p className="text-on-surface-variant italic">Your first journal entry is waiting for you at the end of a sit.</p>
          </div>
        )}
      </div>
    </div>
  )
}

export default HistoryScreen
