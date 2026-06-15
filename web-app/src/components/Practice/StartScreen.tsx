import React from 'react'
import { useLiveQuery } from 'dexie-react-hooks'
import { db, type Preset } from '../../db'

interface StartScreenProps {
  onStartTimer: (preset: Preset) => void
  onEditPreset: (preset: Preset) => void
  onCreatePreset: () => void
}

const StartScreen: React.FC<StartScreenProps> = ({ onStartTimer, onEditPreset, onCreatePreset }) => {
  const presets = useLiveQuery(() => db.presets.toArray())

  return (
    <div className="px-6 max-w-6xl mx-auto animate-in fade-in duration-700">
      {/* Hero Section */}
      <section className="mb-16 mt-8">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
          <div className="space-y-6">
            <span className="text-on-surface-variant font-label text-xs uppercase tracking-widest font-semibold">Quick Start</span>
            <h2 className="text-5xl md:text-6xl font-headline leading-tight text-on-surface">
              Find your <br /><span className="italic">stillness</span>.
            </h2>
            <p className="text-on-surface-variant text-lg max-w-md leading-relaxed">
              Select a curated atmosphere for your practice or design a new space for your breath.
            </p>
          </div>
          <div className="relative aspect-square flex items-center justify-center">
            <div className="absolute inset-0 bg-primary/20 rounded-full scale-110 blur-3xl animate-pulse"></div>
            <div className="w-56 h-56 rounded-3xl overflow-hidden shadow-2xl z-10 border-4 border-surface bg-surface flex items-center justify-center p-4">
              <img 
                alt="MettaSphere Logo" 
                className="w-full h-full object-contain"
                src={`${import.meta.env.BASE_URL || '/'}logo.png`}
              />
            </div>
          </div>
        </div>
      </section>

      {/* Bento Grid Presets */}
      <section className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {presets?.map((preset, index) => (
          <div 
            key={preset.id}
            className={`group relative overflow-hidden rounded-3xl shadow-sm transition-all duration-500 hover:-translate-y-1 hover:shadow-xl ${index === 0 ? 'md:col-span-2' : ''} ${preset.name === 'Quick Rest' ? 'bg-secondary-container' : 'bg-surface-container-lowest'}`}
          >
            <div className="absolute inset-0 opacity-20 group-hover:opacity-30 transition-opacity">
              <img 
                alt={preset.name} 
                className="w-full h-full object-cover"
                src={preset.visual} 
              />
            </div>
            <div className="relative p-8 h-80 flex flex-col justify-between">
              <div className="flex justify-between items-start">
                <button 
                  onClick={() => onEditPreset(preset)}
                  className="p-3 bg-surface/80 backdrop-blur rounded-full text-on-surface shadow-sm hover:bg-primary hover:text-on-primary transition-colors"
                >
                  <span className="material-symbols-outlined">settings</span>
                </button>
                <span className="font-label text-xs font-bold px-3 py-1 bg-surface/80 backdrop-blur rounded-full">
                  {preset.totalMinutes} MIN
                </span>
              </div>
              <div>
                <h3 className={`text-3xl font-headline mb-2 ${preset.name === 'Quick Rest' ? 'text-on-secondary-container' : 'text-on-surface'}`}>{preset.name}</h3>
                <p className={`max-w-xs text-sm ${preset.name === 'Quick Rest' ? 'text-on-secondary-container/70' : 'text-on-surface-variant'}`}>
                  {preset.totalMinutes} minutes session with {preset.intervalMinutes > 0 ? `${preset.intervalMinutes} min intervals` : 'no intervals'}.
                </p>
              </div>
            </div>
            <button 
              onClick={() => onStartTimer(preset)}
              className="absolute bottom-6 right-6 w-12 h-12 flex items-center justify-center rounded-full bg-primary text-on-primary shadow-lg transition-transform group-hover:scale-110 active:scale-95"
            >
              <span className="material-symbols-outlined">play_arrow</span>
            </button>
          </div>
        ))}

        {/* Create New Preset Card */}
        <div 
          onClick={onCreatePreset}
          className="group relative md:col-span-1 overflow-hidden rounded-3xl bg-transparent border-2 border-dashed border-outline-variant hover:border-primary transition-all duration-500 flex items-center justify-center h-80 cursor-pointer"
        >
          <div className="text-center p-8">
            <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-surface-container-low text-primary mb-6 transition-all duration-500 group-hover:bg-primary-container group-hover:scale-105">
              <span className="material-symbols-outlined text-4xl">add</span>
            </div>
            <h3 className="text-xl font-headline text-on-surface mb-2">Create New Preset</h3>
            <p className="text-on-surface-variant text-sm max-w-xs">Customize duration, soundscape, and focus for your unique practice.</p>
          </div>
        </div>
      </section>
    </div>
  )
}

export default StartScreen
