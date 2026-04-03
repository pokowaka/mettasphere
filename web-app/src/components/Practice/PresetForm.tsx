import React, { useState, useEffect } from 'react'
import { db, type Preset } from '../../db'

interface PresetFormProps {
  preset?: Preset | null
  onCancel: () => void
  onSave: () => void
}

const VISUALS = [
  { id: '1', url: 'https://images.unsplash.com/photo-1506744038136-46273834b3fb', alt: 'Serene mountain lake' },
  { id: '2', url: 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e', alt: 'Sunlight through trees' },
  { id: '3', url: 'https://images.unsplash.com/photo-1500674425917-06385469493a', alt: 'Misty coastal cliff' },
  { id: '4', url: 'https://images.unsplash.com/photo-1501785888041-af3ef285b470', alt: 'Sunset over water' },
]

const PresetForm: React.FC<PresetFormProps> = ({ preset, onCancel, onSave }) => {
  const [name, setName] = useState(preset?.name || 'New Stillness')
  const [visual, setVisual] = useState(preset?.visual || VISUALS[0].url)
  const [delay, setDelay] = useState(preset?.delaySeconds || 0)
  const [interval, setIntervalVal] = useState(preset?.intervalMinutes || 5)
  const [duration, setDuration] = useState(preset?.totalMinutes || 20)
  
  const [startSound, setStartSound] = useState(preset?.startSound || 'Deep Bowl')
  const [intervalSound, setIntervalSound] = useState(preset?.intervalSound || 'Singing Bowl')
  const [endSound, setEndSound] = useState(preset?.endSound || 'Soft Gong')

  useEffect(() => {
    if (preset) {
      setName(preset.name)
      setVisual(preset.visual)
      setDelay(preset.delaySeconds)
      setIntervalVal(preset.intervalMinutes)
      setDuration(preset.totalMinutes)
      setStartSound(preset.startSound)
      setIntervalSound(preset.intervalSound)
      setEndSound(preset.endSound)
    }
  }, [preset])

  const handleSave = async () => {
    const newPreset: Preset = {
      name,
      visual,
      delaySeconds: delay,
      intervalMinutes: interval,
      totalMinutes: duration,
      startSound,
      intervalSound,
      endSound,
    }

    if (preset?.id) {
      await db.presets.update(preset.id, newPreset)
    } else {
      await db.presets.add(newPreset)
    }
    onSave()
  }

  const handleDelete = async () => {
    if (preset?.id) {
      await db.presets.delete(preset.id)
    }
    onCancel()
  }

  return (
    <div className="pt-8 pb-32 px-6 max-w-2xl mx-auto space-y-12 animate-in slide-in-from-bottom duration-500">
      {/* Visual Selection */}
      <section className="space-y-4">
        <label className="block font-label text-[10px] uppercase tracking-widest font-semibold text-on-surface-variant px-1">
          Choose Visual
        </label>
        <div className="flex gap-4 overflow-x-auto pb-4 -mx-6 px-6 no-scrollbar">
          {VISUALS.map((v) => (
            <div 
              key={v.id}
              onClick={() => setVisual(v.url)}
              className={`flex-none w-40 aspect-square rounded-2xl overflow-hidden relative cursor-pointer transition-all duration-300 ${visual === v.url ? 'ring-4 ring-primary ring-offset-4 ring-offset-surface scale-95 shadow-lg' : 'opacity-70 hover:opacity-100'}`}
            >
              <img src={v.url} alt={v.alt} className="w-full h-full object-cover" />
              {visual === v.url && (
                <div className="absolute top-2 right-2 bg-primary text-on-primary rounded-full p-1 shadow-md">
                  <span className="material-symbols-outlined text-sm leading-none" style={{ fontSize: '16px' }}>check</span>
                </div>
              )}
            </div>
          ))}
        </div>
      </section>

      <section className="space-y-4">
        <label className="block font-label text-[10px] uppercase tracking-widest font-semibold text-on-surface-variant px-1">
          Preset Name
        </label>
        <input 
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="w-full bg-surface-container border-none rounded-2xl px-6 py-5 text-xl font-headline focus:ring-2 focus:ring-primary/20 focus:bg-surface-container-lowest transition-all duration-500 text-primary outline-none"
          placeholder="Morning Stillness"
        />
      </section>

      <div className="space-y-12">
        {/* Delay Selection */}
        <div className="space-y-6">
          <div className="flex justify-between items-end">
            <div className="space-y-1">
              <label className="block font-label text-[10px] uppercase tracking-widest font-semibold text-on-surface-variant">
                Delay Before Start
              </label>
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-primary text-sm">hourglass_top</span>
                <span className="font-headline text-2xl text-primary">
                  {delay} <span className="text-xs font-body tracking-normal text-on-surface-variant italic">sec</span>
                </span>
              </div>
            </div>
            <select 
              value={startSound}
              onChange={(e) => setStartSound(e.target.value)}
              className="bg-surface-container-low border-none rounded-lg text-xs font-medium py-2 focus:ring-0 text-on-surface-variant outline-none"
            >
              <option>No sound</option>
              <option>Deep Bowl</option>
              <option>Singing Bowl</option>
              <option>Soft Gong</option>
            </select>
          </div>
          <input 
            type="range" min="0" max="60" value={delay}
            onChange={(e) => setDelay(parseInt(e.target.value))}
            className="w-full h-1 bg-surface-variant rounded-full appearance-none cursor-pointer accent-primary" 
          />
        </div>

        {/* Interval Selection */}
        <div className="space-y-6">
          <div className="flex justify-between items-end">
            <div className="space-y-1">
              <label className="block font-label text-[10px] uppercase tracking-widest font-semibold text-on-surface-variant">
                Interval Chimes
              </label>
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-primary text-sm">timer</span>
                <span className="font-headline text-2xl text-primary">
                  {interval} <span className="text-xs font-body tracking-normal text-on-surface-variant italic">min</span>
                </span>
              </div>
            </div>
            <select 
              value={intervalSound}
              onChange={(e) => setIntervalSound(e.target.value)}
              className="bg-surface-container-low border-none rounded-lg text-xs font-medium py-2 focus:ring-0 text-on-surface-variant outline-none"
            >
              <option>No sound</option>
              <option>Deep Bowl</option>
              <option>Singing Bowl</option>
              <option>Soft Gong</option>
            </select>
          </div>
          <input 
            type="range" min="0" max="30" step="1" value={interval}
            onChange={(e) => setIntervalVal(parseInt(e.target.value))}
            className="w-full h-1 bg-surface-variant rounded-full appearance-none cursor-pointer accent-primary" 
          />
        </div>

        {/* Duration Selection */}
        <div className="space-y-6">
          <div className="flex justify-between items-end">
            <div className="space-y-1">
              <label className="block font-label text-[10px] uppercase tracking-widest font-semibold text-on-surface-variant">
                Total Duration
              </label>
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-primary text-sm">schedule</span>
                <span className="font-headline text-2xl text-primary">
                  {duration} <span className="text-xs font-body tracking-normal text-on-surface-variant italic">min</span>
                </span>
              </div>
            </div>
            <select 
              value={endSound}
              onChange={(e) => setEndSound(e.target.value)}
              className="bg-surface-container-low border-none rounded-lg text-xs font-medium py-2 focus:ring-0 text-on-surface-variant outline-none"
            >
              <option>No sound</option>
              <option>Deep Bowl</option>
              <option>Singing Bowl</option>
              <option>Soft Gong</option>
            </select>
          </div>
          <input 
            type="range" min="5" max="120" step="5" value={duration}
            onChange={(e) => setDuration(parseInt(e.target.value))}
            className="w-full h-1 bg-surface-variant rounded-full appearance-none cursor-pointer accent-primary" 
          />
        </div>
      </div>

      <footer className="flex flex-col gap-4 pt-12">
        <button 
          onClick={handleSave}
          className="w-full py-5 rounded-full bg-gradient-to-r from-primary to-primary-dim text-on-primary font-bold tracking-wide shadow-md active:scale-95 transition-all duration-300"
        >
          {preset?.id ? 'Update Preset' : 'Save Preset'}
        </button>
        {preset?.id && (
          <button 
            onClick={handleDelete}
            className="w-full py-5 rounded-full text-error font-semibold hover:bg-error-container/10 transition-colors flex items-center justify-center gap-2"
          >
            <span className="material-symbols-outlined">delete</span>
            Delete Preset
          </button>
        )}
        <button 
          onClick={onCancel}
          className="w-full py-5 rounded-full text-on-surface/50 font-semibold hover:bg-surface-container transition-colors"
        >
          Cancel
        </button>
      </footer>
    </div>
  )
}

export default PresetForm
