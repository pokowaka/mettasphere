import React, { useState } from 'react'
import { db } from '../../db'

interface ReflectScreenProps {
  onSave: () => void
  onSkip: () => void
}

const HINDRANCES = [
  { id: 'desire', name: 'Sensual Desire (Kāmacchanda)', description: 'The mind "reaching out" or pulling toward pleasant things.' },
  { id: 'aversion', name: 'Ill-Will / Aversion (Vyāpāda)', description: 'The mind "pushing away" or resisting the present moment.' },
  { id: 'sloth', name: 'Sloth and Torpor (Thīna-middha)', description: 'The mind "sinking" into heaviness or lack of clarity.' },
  { id: 'restless', name: 'Restlessness and Regret (Uddhacca-kukkucca)', description: 'The mind "spinning" with excess energy or worry.' },
  { id: 'doubt', name: 'Skeptical Doubt (Vicikicchā)', description: 'The mind "wavering" or paralyzed by uncertainty.' },
]

const PHYSICAL = [
  { id: 'zest', name: 'Zest/Energy (Piti)', description: 'Bubbly, light, or pleasant tingling.' },
  { id: 'comfort', name: 'Deep Comfort (Sukha)', description: 'Cool, calm, steady sense of "okay-ness".' },
  { id: 'light', name: 'Weightlessness', description: 'Body feels transparent or disappeared.' },
  { id: 'heavy', name: 'Heaviness', description: 'Groggy or physically "thick".' },
]

const MENTAL = [
  { id: 'unified', name: 'Unified/Still', description: 'Mind like a still forest pool; not jumping.' },
  { id: 'expansive', name: 'Expansive', description: 'Awareness has no boundaries; fills the room.' },
  { id: 'equanimous', name: 'Equanimous', description: 'Neutral, balanced, remarkably "flat" (in a good way).' },
  { id: 'bright', name: 'Bright', description: 'Internal vision feels very light/bright.' },
]

const ReflectScreen: React.FC<ReflectScreenProps> = ({ onSave, onSkip }) => {
  const [radiance, setRadiance] = useState(5)
  const [hindrances, setHindrances] = useState<string[]>([])
  const [recognition, setRecognition] = useState(1)
  const [release, setRelease] = useState<'suppression' | 'analytical' | 'passive' | '6r'>('6r')
  const [relaxation, setRelaxation] = useState(1)
  const [smileQuality, setSmileQuality] = useState(1)
  const [smileDuration, setSmileDuration] = useState('vanished')
  const [physical, setPhysical] = useState<string[]>([])
  const [mental, setMental] = useState<string[]>([])
  const [tendency, setTendency] = useState<'fight' | 'flow'>('flow')

  const handleSave = async () => {
    await db.reflections.add({
      timestamp: new Date(),
      mettaRadiance: radiance,
      hindrances,
      recognitionLevel: recognition,
      releaseType: release,
      relaxationLevel: relaxation,
      smileQuality,
      smileDuration,
      physicalSensations: physical,
      mentalStates: mental,
      tendency,
    })
    onSave()
  }

  const toggleItem = (list: string[], setList: (l: string[]) => void, id: string) => {
    if (list.includes(id)) {
      setList(list.filter(i => i !== id))
    } else {
      setList([...list, id])
    }
  }

  return (
    <div className="pt-8 pb-32 px-6 max-w-2xl mx-auto space-y-16 animate-in slide-in-from-bottom duration-700">
      <section className="text-center space-y-4">
        <h2 className="font-headline text-4xl md:text-5xl font-bold tracking-tight text-primary">Stillness</h2>
        <p className="text-on-surface-variant font-body max-w-xs mx-auto opacity-80">Take a moment to weave your practice into the fabric of your day.</p>
      </section>

      {/* 1. Metta Radiance */}
      <section className="space-y-8">
        <div className="flex items-center gap-3">
          <span className="w-12 h-[1px] bg-outline-variant"></span>
          <span className="font-label text-[10px] tracking-widest uppercase text-outline">The Starting Point</span>
        </div>
        <div className="bg-surface-container-low rounded-3xl p-8 space-y-8 shadow-sm">
          <div className="space-y-4">
            <h3 className="font-headline text-2xl text-primary leading-tight">Initial Feeling</h3>
            <p className="text-on-surface-variant text-sm">How would you rate the warm 'glow' or feeling of Metta in the center of your chest?</p>
          </div>
          <div className="relative pt-6">
            <input 
              type="range" min="1" max="10" value={radiance}
              onChange={(e) => setRadiance(parseInt(e.target.value))}
              className="w-full h-1 bg-surface-variant rounded-full appearance-none cursor-pointer accent-primary" 
            />
            <div className="flex justify-between mt-6 px-1">
              <span className="text-[10px] font-label text-outline uppercase tracking-wider">Subtle</span>
              <span className="text-[10px] font-label text-outline uppercase tracking-wider">Radiant</span>
            </div>
            {/* Radiant Bloom Effect */}
            <div className={`absolute -top-12 left-1/2 -translate-x-1/2 w-32 h-32 bg-primary-container/30 blur-3xl rounded-full -z-10 transition-opacity duration-1000 ${radiance > 5 ? 'opacity-100 animate-pulse' : 'opacity-0'}`}></div>
          </div>
        </div>
      </section>

      {/* 2. Hindrances */}
      <section className="space-y-8">
        <div className="flex items-center gap-3">
          <span className="w-12 h-[1px] bg-outline-variant"></span>
          <span className="font-label text-[10px] tracking-widest uppercase text-outline">The 6R Process</span>
        </div>
        <div className="bg-surface-container-lowest p-8 rounded-3xl shadow-sm space-y-6">
          <div className="flex items-center gap-3">
            <span className="material-symbols-outlined text-primary">cloud_off</span>
            <h4 className="font-headline text-lg text-on-surface">The Five Hindrances (Nivarana)</h4>
          </div>
          <p className="text-sm text-on-surface-variant">Check any that you observed during your session. These are impersonal mental states.</p>
          <div className="space-y-3">
            {HINDRANCES.map(h => (
              <label 
                key={h.id}
                className={`flex items-start gap-4 p-4 rounded-2xl border transition-all cursor-pointer ${hindrances.includes(h.id) ? 'border-primary bg-primary-container/20' : 'border-outline-variant hover:border-primary'}`}
              >
                <input 
                  type="checkbox" 
                  checked={hindrances.includes(h.id)}
                  onChange={() => toggleItem(hindrances, setHindrances, h.id)}
                  className="mt-1 rounded border-outline-variant text-primary focus:ring-primary/20 w-5 h-5" 
                />
                <div className="space-y-1">
                  <span className={`block text-[10px] font-label tracking-widest uppercase ${hindrances.includes(h.id) ? 'text-primary' : 'text-outline'}`}>Hindrance</span>
                  <p className={`text-sm ${hindrances.includes(h.id) ? 'text-on-surface' : 'text-on-surface-variant'}`}>
                    <span className="font-bold">{h.name}:</span> {h.description}
                  </p>
                </div>
              </label>
            ))}
          </div>
        </div>
      </section>

      {/* 3. Recognition */}
      <section className="bg-surface-container-lowest p-8 rounded-3xl shadow-sm space-y-6">
        <div className="flex items-center gap-3">
          <span className="material-symbols-outlined text-primary">visibility</span>
          <h4 className="font-headline text-lg text-on-surface">Recognition</h4>
        </div>
        <p className="text-sm text-on-surface-variant">At what stage did you catch the distraction before returning to Metta?</p>
        <div className="space-y-3">
          {[
            { level: 1, label: 'Post-event', desc: 'I was lost for a while before I noticed' },
            { level: 2, label: 'During the event', desc: 'I caught the thought while it was active' },
            { level: 3, label: 'At the start', desc: 'I caught the thought as soon as it began' },
            { level: 4, label: 'Pre-thought', desc: 'I felt the "tightness" before the thought formed' },
            { level: 5, label: 'The Flicker', desc: 'I saw the intention to move before any tension arose' },
          ].map(r => (
            <button 
              key={r.level}
              onClick={() => setRecognition(r.level)}
              className={`w-full text-left p-4 rounded-2xl border transition-all ${recognition === r.level ? 'border-primary bg-primary-container/20 ring-2 ring-primary/10' : 'border-outline-variant hover:border-primary'}`}
            >
              <div className="flex justify-between items-center mb-1">
                <span className={`text-[10px] font-label tracking-widest uppercase ${recognition === r.level ? 'text-primary' : 'text-outline'}`}>Level {r.level}</span>
                <span className={`text-[10px] font-label italic ${recognition === r.level ? 'text-primary/60' : 'text-outline/50'}`}>{r.label}</span>
              </div>
              <p className={`text-sm ${recognition === r.level ? 'text-on-surface' : 'text-on-surface-variant'}`}>{r.desc}</p>
            </button>
          ))}
        </div>
      </section>

      {/* 4. Release Step */}
      <section className="bg-surface-container-lowest p-8 rounded-3xl shadow-sm space-y-6">
        <div className="flex items-center gap-3">
          <span className="material-symbols-outlined text-primary">leak_remove</span>
          <h4 className="font-headline text-lg text-on-surface">The Release Step</h4>
        </div>
        <p className="text-sm text-on-surface-variant">How did you handle the distraction once you recognized it?</p>
        <div className="space-y-3">
          {[
            { id: 'suppression', label: 'Suppression (The Push)', desc: 'I tried to "kick" the thought out because it was "bad".' },
            { id: 'analytical', label: 'Analytical (The Story)', desc: 'I started thinking about why the distraction happened.' },
            { id: 'passive', label: 'Passive (The Drift)', desc: 'I let the thought stay and just sat with it.' },
            { id: '6r', label: 'The 6R Release (The Let-Go)', desc: 'I allowed the thought to be there, but stopped "giving it fuel".' },
          ].map(r => (
            <label 
              key={r.id}
              className={`flex items-start gap-4 p-4 rounded-2xl border transition-all cursor-pointer ${release === r.id ? 'border-primary bg-primary-container/20' : 'border-outline-variant hover:border-primary'}`}
            >
              <input 
                type="radio" 
                checked={release === r.id}
                onChange={() => setRelease(r.id as any)}
                className="mt-1 rounded border-outline-variant text-primary focus:ring-primary/20 w-5 h-5" 
              />
              <div className="space-y-1">
                <span className={`block text-[10px] font-label tracking-widest uppercase ${release === r.id ? 'text-primary' : 'text-outline'}`}>Method</span>
                <p className={`text-sm ${release === r.id ? 'text-on-surface' : 'text-on-surface-variant'}`}>
                  <span className="font-bold">{r.label}:</span> {r.desc}
                </p>
              </div>
            </label>
          ))}
        </div>
      </section>

      {/* 5. Relaxation */}
      <section className="bg-surface-container-lowest p-8 rounded-3xl shadow-sm space-y-6">
        <div className="flex items-center gap-3">
          <span className="material-symbols-outlined text-primary">spa</span>
          <h4 className="font-headline text-lg text-on-surface">Relaxation</h4>
        </div>
        <div className="space-y-3">
          {[
            { level: 1, label: 'Conceptual', desc: "I thought 'relax' but didn't feel physical change" },
            { level: 2, label: 'Surface', desc: 'I relaxed my shoulders, jaw, or brow' },
            { level: 3, label: 'Deep', desc: "I felt a release of 'pressure' inside my head" },
            { level: 4, label: 'Total', desc: 'The whole body felt dropped into profound ease' },
          ].map(r => (
            <button 
              key={r.level}
              onClick={() => setRelaxation(r.level)}
              className={`w-full text-left p-4 rounded-2xl border transition-all ${relaxation === r.level ? 'border-primary bg-primary-container/20' : 'border-outline-variant hover:border-primary'}`}
            >
              <div className="flex justify-between items-center mb-1">
                <span className={`text-[10px] font-label tracking-widest uppercase ${relaxation === r.level ? 'text-primary' : 'text-outline'}`}>Level {r.level}</span>
                <span className={`text-[10px] font-label italic ${relaxation === r.level ? 'text-primary/60' : 'text-outline/50'}`}>{r.label}</span>
              </div>
              <p className={`text-sm ${relaxation === r.level ? 'text-on-surface' : 'text-on-surface-variant'}`}>{r.desc}</p>
            </button>
          ))}
        </div>
      </section>

      {/* 6. The Smile */}
      <section className="bg-surface-container-high rounded-3xl p-8 space-y-8 shadow-sm">
        <div className="space-y-2">
          <h4 className="font-headline text-2xl text-primary">The Smile</h4>
          <p className="text-sm text-on-surface-variant italic">What was the quality of your "Re-smile"?</p>
        </div>
        <div className="grid grid-cols-1 gap-3">
          {[
            { level: 1, label: 'Mechanical', desc: "Lips moved, but felt 'fake'; mood unchanged." },
            { level: 2, label: 'Friendly', desc: 'Felt like seeing a friend; meditation felt lighter.' },
            { level: 3, label: 'Radiant', desc: 'Eyes felt like they were smiling; mind became sunny.' },
            { level: 4, label: 'Jhanic Joy', desc: 'Triggered tingling, cool energy, or zest (Piti).' },
          ].map(r => (
            <button 
              key={r.level}
              onClick={() => setSmileQuality(r.level)}
              className={`text-left p-4 rounded-2xl bg-surface-container-lowest border transition-all ${smileQuality === r.level ? 'border-primary shadow-md' : 'border-transparent hover:border-primary/30'}`}
            >
              <span className={`block text-[10px] font-label tracking-widest uppercase mb-1 ${smileQuality === r.level ? 'text-primary' : 'text-outline'}`}>Level {r.level}: {r.label}</span>
              <p className={`text-sm ${smileQuality === r.level ? 'text-on-surface' : 'text-on-surface-variant'}`}>{r.desc}</p>
            </button>
          ))}
        </div>
        <div className="pt-4 space-y-4">
          <p className="text-sm font-headline text-primary">How long did the "Smile" last?</p>
          <div className="flex flex-col gap-2">
            {[
              { id: 'vanished', label: 'It vanished as I returned to the Metta' },
              { id: 'stayed', label: 'It stayed as a background glow' },
            ].map(d => (
              <label key={d.id} className="flex items-center gap-3 p-4 bg-surface-container-lowest/50 rounded-xl cursor-pointer hover:bg-surface-container-lowest transition-all">
                <input 
                  type="radio" 
                  checked={smileDuration === d.id}
                  onChange={() => setSmileDuration(d.id)}
                  className="w-4 h-4 text-primary focus:ring-primary/20 border-outline-variant" 
                />
                <span className={`text-sm ${smileDuration === d.id ? 'text-on-surface font-semibold' : 'text-on-surface-variant'}`}>{d.label}</span>
              </label>
            ))}
          </div>
        </div>
      </section>

      {/* 7. Weather Report */}
      <section className="space-y-8">
        <div className="flex items-center gap-3">
          <span className="w-12 h-[1px] bg-outline-variant"></span>
          <span className="font-label text-[10px] tracking-widest uppercase text-outline">Observation</span>
        </div>
        <div className="bg-surface-container-lowest p-8 rounded-3xl shadow-sm space-y-12">
          <div className="space-y-2">
            <h4 className="font-headline text-2xl text-primary">Weather Report</h4>
            <p className="text-sm text-on-surface-variant italic">How do you feel right now?</p>
          </div>

          <div className="space-y-6">
            <h5 className="font-label text-xs font-bold tracking-widest uppercase text-outline">Physical Sensations</h5>
            <div className="grid grid-cols-1 gap-3">
              {PHYSICAL.map(p => (
                <label key={p.id} className={`flex items-start gap-4 p-4 rounded-2xl border transition-all cursor-pointer ${physical.includes(p.id) ? 'border-primary bg-primary-container/20' : 'border-outline-variant hover:border-primary'}`}>
                  <input 
                    type="checkbox" 
                    checked={physical.includes(p.id)}
                    onChange={() => toggleItem(physical, setPhysical, p.id)}
                    className="mt-1 rounded border-outline-variant text-primary focus:ring-primary/20 w-5 h-5" 
                  />
                  <div>
                    <span className={`block text-sm font-bold ${physical.includes(p.id) ? 'text-primary' : 'text-on-surface'}`}>{p.name}</span>
                    <p className="text-xs text-on-surface-variant leading-relaxed">{p.description}</p>
                  </div>
                </label>
              ))}
            </div>
          </div>

          <div className="space-y-6">
            <h5 className="font-label text-xs font-bold tracking-widest uppercase text-outline">Mental States</h5>
            <div className="grid grid-cols-1 gap-3">
              {MENTAL.map(m => (
                <label key={m.id} className={`flex items-start gap-4 p-4 rounded-2xl border transition-all cursor-pointer ${mental.includes(m.id) ? 'border-primary bg-primary-container/20' : 'border-outline-variant hover:border-primary'}`}>
                  <input 
                    type="checkbox" 
                    checked={mental.includes(m.id)}
                    onChange={() => toggleItem(mental, setMental, m.id)}
                    className="mt-1 rounded border-outline-variant text-primary focus:ring-primary/20 w-5 h-5" 
                  />
                  <div>
                    <span className={`block text-sm font-bold ${mental.includes(m.id) ? 'text-primary' : 'text-on-surface'}`}>{m.name}</span>
                    <p className="text-xs text-on-surface-variant leading-relaxed">{m.description}</p>
                  </div>
                </label>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* 8. Tendency (Fight vs Flow) */}
      <section className="bg-surface-container-low rounded-3xl p-2 flex gap-2">
        <button 
          onClick={() => setTendency('fight')}
          className={`flex-1 py-6 px-4 rounded-2xl text-center transition-all duration-500 ${tendency === 'fight' ? 'bg-surface-container-lowest shadow-sm' : 'opacity-50 hover:opacity-100'}`}
        >
          <span className="block font-label text-[10px] tracking-widest uppercase text-outline mb-1">Tendency</span>
          <span className={`block font-headline text-2xl ${tendency === 'fight' ? 'text-error' : 'text-on-surface-variant'}`}>Fight</span>
          <p className="text-[10px] text-outline italic">effortful & striving</p>
        </button>
        <button 
          onClick={() => setTendency('flow')}
          className={`flex-1 py-6 px-4 rounded-2xl text-center transition-all duration-500 ${tendency === 'flow' ? 'bg-primary-container shadow-sm' : 'opacity-50 hover:opacity-100'}`}
        >
          <span className="block font-label text-[10px] tracking-widest uppercase text-on-primary-container/60 mb-1">Tendency</span>
          <span className={`block font-headline text-2xl font-bold ${tendency === 'flow' ? 'text-primary' : 'text-on-surface-variant'}`}>Flow</span>
          <p className="text-[10px] text-on-primary-container/60 italic">gentle let-go</p>
        </button>
      </section>

      {/* Actions */}
      <div className="pt-12 flex flex-col items-center gap-6">
        <button 
          onClick={handleSave}
          className="w-full py-5 bg-gradient-to-r from-primary to-primary-dim text-on-primary font-body font-bold text-lg rounded-full shadow-xl hover:shadow-primary/20 transition-all duration-500 active:scale-[0.98]"
        >
          Save Reflection
        </button>
        <button 
          onClick={onSkip}
          className="text-on-surface-variant font-label text-sm border-b-2 border-primary/20 pb-1 hover:border-primary transition-all"
        >
          Skip for now
        </button>
      </div>
    </div>
  )
}

export default ReflectScreen
