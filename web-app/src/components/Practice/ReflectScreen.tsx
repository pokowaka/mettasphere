import React, { useState } from 'react'
import { db } from '../../db'

import { type Preset } from '../../db'

interface ReflectScreenProps {
  preset: Preset | null
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

const RADIANCE_LEVELS = [
  { level: 1, title: "Subtle/None", quote: "I don't feel it right now.", companion: "A quiet space. Even without physical warmth, the simple intention of goodwill is planting seeds of peace in your mind. It is enough just to sit." },
  { level: 2, title: "Heart Glow", quote: "I felt a soft glow in my heart.", companion: "A gentle spark. A warm center of friendliness is settling in the heart space. Rest your awareness here." },
  { level: 3, title: "Body Ease", quote: "I felt it flow through my whole body.", companion: "Filling the vessel. The warm sensation of comfort spreads outward, soaking the limbs and torso in calm ease. It might feel like the body and limbs are starting to disappear." },
  { level: 4, title: "Head Center", quote: "I felt balanced in my head, body gone.", companion: "A quiet stillness. The warmth settles in the head space, and it feels like the physical body is not really there anymore." },
  { level: 5, title: "Boundless", quote: "I felt boundless, radiating in all directions.", companion: "Boundless presence. Goodwill radiates outward infinitely, dissolving boundaries in unlimited friendliness for all beings." }
]

const ReflectScreen: React.FC<ReflectScreenProps> = ({ preset, onSave, onSkip }) => {
  const [step, setStep] = useState(0)
  const [radiance, setRadiance] = useState(5)
  const [hindrances, setHindrances] = useState<string[]>([])
  const [recognition, setRecognition] = useState(1)
  const [release, setRelease] = useState<'suppression' | 'analytical' | 'passive' | '6r'>('6r')
  const [relaxation, setRelaxation] = useState(1)
  const [smileQuality, setSmileQuality] = useState(1)
  const [flowLevel, setFlowLevel] = useState(1)

  const showDetailed = preset ? preset.showDetailedReflection : true
  const totalSteps = showDetailed ? 7 : 3

  const handleSave = async () => {
    await db.reflections.add({
      timestamp: new Date(),
      mettaRadiance: radiance,
      hindrances,
      recognitionLevel: recognition,
      releaseType: release,
      relaxationLevel: relaxation,
      smileQuality: smileQuality,
      flowLevel: flowLevel,
      smileDuration: 'vanished',
      physicalSensations: [],
      mentalStates: [],
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

  const nextStep = () => {
    if (step < totalSteps - 1) {
      setStep(step + 1)
      window.scrollTo(0, 0)
    }
  }

  const prevStep = () => {
    if (step > 0) {
      setStep(step - 1)
      window.scrollTo(0, 0)
    }
  }

  const renderStep = () => {
    switch (step) {
      case 0: {
        const currentLevel = RADIANCE_LEVELS.find(l => l.level === radiance) || RADIANCE_LEVELS[0]
        return (
          <section className="space-y-8 animate-in fade-in slide-in-from-right duration-500">
            <div className="text-center space-y-4 mb-8">
              <h2 className="font-headline text-4xl font-bold text-primary">Initial Feeling</h2>
              <p className="text-on-surface-variant text-sm">How would you rate the warm 'glow' or feeling of Metta in the center of your chest?</p>
            </div>
            
            {/* 5 Circle Buttons */}
            <div className="flex justify-center gap-4 py-4">
              {[1, 2, 3, 4, 5].map((i) => {
                const isSelected = radiance === i
                return (
                  <button
                    key={i}
                    onClick={() => setRadiance(i)}
                    className={`w-14 h-14 rounded-full flex items-center justify-center font-headline text-xl font-bold border transition-all ${
                      isSelected 
                        ? 'bg-primary text-on-primary border-primary shadow-lg ring-4 ring-primary/5' 
                        : 'bg-surface-container-low border-outline-variant/30 text-on-surface-variant hover:border-primary/50'
                    }`}
                  >
                    {i}
                  </button>
                )
              })}
            </div>

            {/* Description Card */}
            <div className="bg-primary-container/20 rounded-3xl p-8 border border-outline-variant/10 text-center space-y-4 transition-all duration-300">
              <p className="font-headline text-lg font-bold text-on-surface">"{currentLevel.quote}"</p>
              <p className="text-sm text-primary italic leading-relaxed">{currentLevel.companion}</p>
            </div>
          </section>
        )
      }
      case 1:
        return (
          <section className="space-y-8 animate-in fade-in slide-in-from-right duration-500">
            <div className="text-center space-y-4 mb-12">
              <h2 className="font-headline text-4xl font-bold text-primary">Hindrances</h2>
              <p className="text-sm text-on-surface-variant">Check any that you observed during your session. These are impersonal mental states.</p>
            </div>
            <div className="space-y-3">
              {HINDRANCES.map(h => (
                <label 
                  key={h.id}
                  className={`flex items-start gap-4 p-5 rounded-3xl border transition-all cursor-pointer ${hindrances.includes(h.id) ? 'border-primary bg-primary-container/20' : 'border-outline-variant hover:border-primary bg-surface-container-lowest'}`}
                >
                  <input 
                    type="checkbox" 
                    checked={hindrances.includes(h.id)}
                    onChange={() => toggleItem(hindrances, setHindrances, h.id)}
                    className="mt-1 rounded border-outline-variant text-primary focus:ring-primary/20 w-6 h-6" 
                  />
                  <div className="space-y-1">
                    <p className={`text-base font-bold ${hindrances.includes(h.id) ? 'text-primary' : 'text-on-surface'}`}>{h.name}</p>
                    <p className="text-sm text-on-surface-variant">{h.description}</p>
                  </div>
                </label>
              ))}
            </div>
          </section>
        )
      case 2:
        return (
          <section className="space-y-8 animate-in fade-in slide-in-from-right duration-500">
            <div className="text-center space-y-4 mb-12">
              <h2 className="font-headline text-4xl font-bold text-primary">Recognition</h2>
              <p className="text-sm text-on-surface-variant">At what stage did you catch the distraction before returning to Metta?</p>
            </div>
            <div className="space-y-4">
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
                  className={`w-full text-left p-6 rounded-3xl border transition-all ${recognition === r.level ? 'border-primary bg-primary-container/20 ring-4 ring-primary/5 shadow-md' : 'border-outline-variant hover:border-primary bg-surface-container-lowest'}`}
                >
                  <div className="flex justify-between items-center mb-1">
                    <span className={`text-[10px] font-label tracking-widest uppercase font-bold ${recognition === r.level ? 'text-primary' : 'text-outline'}`}>Level {r.level}</span>
                    <span className={`text-[10px] font-label italic ${recognition === r.level ? 'text-primary/60' : 'text-outline/50'}`}>{r.label}</span>
                  </div>
                  <p className={`text-base ${recognition === r.level ? 'text-on-surface font-semibold' : 'text-on-surface-variant'}`}>{r.desc}</p>
                </button>
              ))}
            </div>
          </section>
        )
      case 3:
        return (
          <section className="space-y-8 animate-in fade-in slide-in-from-right duration-500">
            <div className="text-center space-y-4 mb-12">
              <h2 className="font-headline text-4xl font-bold text-primary">The Release</h2>
              <p className="text-sm text-on-surface-variant">How did you handle the distraction once you recognized it?</p>
            </div>
            <div className="space-y-4">
              {[
                { id: 'suppression', label: 'Suppression (The Push)', desc: 'I tried to "kick" the thought out because it was "bad".' },
                { id: 'analytical', label: 'Analytical (The Story)', desc: 'I started thinking about why the distraction happened.' },
                { id: 'passive', label: 'Passive (The Drift)', desc: 'I let the thought stay and just sat with it.' },
                { id: '6r', label: 'The 6R Release (The Let-Go)', desc: 'I allowed the thought to be there, but stopped "giving it fuel".' },
              ].map(r => (
                <label 
                  key={r.id}
                  className={`flex items-start gap-4 p-6 rounded-3xl border transition-all cursor-pointer ${release === r.id ? 'border-primary bg-primary-container/20 ring-4 ring-primary/5 shadow-md' : 'border-outline-variant hover:border-primary bg-surface-container-lowest'}`}
                >
                  <input 
                    type="radio" name="release"
                    checked={release === r.id}
                    onChange={() => setRelease(r.id as any)}
                    className="mt-1 rounded-full border-outline-variant text-primary focus:ring-primary/20 w-6 h-6" 
                  />
                  <div className="space-y-1">
                    <p className={`text-base font-bold ${release === r.id ? 'text-on-surface' : 'text-on-surface-variant'}`}>{r.label}</p>
                    <p className="text-sm text-on-surface-variant">{r.desc}</p>
                  </div>
                </label>
              ))}
            </div>
          </section>
        )
      case 4:
        return (
          <section className="space-y-8 animate-in fade-in slide-in-from-right duration-500">
            <div className="text-center space-y-4 mb-12">
              <h2 className="font-headline text-4xl font-bold text-primary">Relaxation</h2>
              <p className="text-sm text-on-surface-variant">When you recognized a distraction, how did you "Relax"?</p>
            </div>
            <div className="space-y-4">
              {[
                { level: 1, label: 'No Relaxation', desc: "I forgot to relax" },
                { level: 2, label: 'Surface', desc: 'I relaxed my shoulders, jaw, or brow' },
                { level: 3, label: 'Deep', desc: "I felt a release of 'pressure' inside my head" },
                { level: 4, label: 'Total', desc: 'The whole body felt dropped into profound ease' },
              ].map(r => (
                <button 
                  key={r.level}
                  onClick={() => setRelaxation(r.level)}
                  className={`w-full text-left p-6 rounded-3xl border transition-all ${relaxation === r.level ? 'border-primary bg-primary-container/20 ring-4 ring-primary/5 shadow-md' : 'border-outline-variant hover:border-primary bg-surface-container-lowest'}`}
                >
                  <div className="flex justify-between items-center mb-1">
                    <span className={`text-[10px] font-label tracking-widest uppercase font-bold ${relaxation === r.level ? 'text-primary' : 'text-outline'}`}>Level {r.level}</span>
                    <span className={`text-[10px] font-label italic ${relaxation === r.level ? 'text-primary/60' : 'text-outline/50'}`}>{r.label}</span>
                  </div>
                  <p className={`text-base ${relaxation === r.level ? 'text-on-surface font-semibold' : 'text-on-surface-variant'}`}>{r.desc}</p>
                </button>
              ))}
            </div>
          </section>
        )
      case 5:
        return (
          <section className="space-y-12 animate-in fade-in slide-in-from-right duration-500">
            <div className="text-center space-y-4 mb-12">
              <h2 className="font-headline text-4xl font-bold text-primary">The Smile</h2>
              <p className="text-sm text-on-surface-variant">What was the quality of your "Re-smile"?</p>
            </div>
            <div className="grid grid-cols-1 gap-4">
              {[
                { level: 1, label: 'No Smile', desc: "I forgot to smile" },
                { level: 2, label: 'Friendly', desc: 'Felt like seeing a friend; meditation felt lighter.' },
                { level: 3, label: 'Radiant', desc: "Eyes felt like they were smiling; mind became sunny." },
              ].map(r => (
                <button 
                  key={r.level}
                  onClick={() => setSmileQuality(r.level)}
                  className={`text-left p-6 rounded-3xl bg-surface-container-lowest border transition-all ${smileQuality === r.level ? 'border-primary shadow-lg ring-4 ring-primary/5' : 'border-outline-variant/30 hover:border-primary/50'}`}
                >
                  <span className={`block text-[10px] font-label tracking-widest uppercase mb-1 font-bold ${smileQuality === r.level ? 'text-primary' : 'text-outline'}`}>Level {r.level}: {r.label}</span>
                  <p className={`text-base ${smileQuality === r.level ? 'text-on-surface font-semibold' : 'text-on-surface-variant'}`}>{r.desc}</p>
                </button>
              ))}
            </div>
          </section>
        )
      case 6:
        return (
          <section className="space-y-12 animate-in fade-in slide-in-from-right duration-500">
            <div className="text-center space-y-4 mb-12">
              <h2 className="font-headline text-4xl font-bold text-primary">The Flow</h2>
              <p className="text-sm text-on-surface-variant">How did the 6R steps feel as you practiced them?</p>
            </div>
            <div className="grid grid-cols-1 gap-4">
              {[
                { level: 1, label: 'Mechanical', desc: "I had to actively recall each step; the process felt separate and manual." },
                { level: 2, label: 'Structured', desc: 'I knew the steps, but had to move deliberately from one to the next.' },
                { level: 3, label: 'Harmonious', desc: 'The transition between steps felt smooth and natural, requiring little effort.' },
                { level: 4, label: 'Automatic', desc: "I didn't have to do anything; the mind naturally followed the steps on its own." },
              ].map(r => (
                <button 
                   key={r.level}
                   onClick={() => setFlowLevel(r.level)}
                   className={`text-left p-6 rounded-3xl bg-surface-container-lowest border transition-all ${flowLevel === r.level ? 'border-primary shadow-lg ring-4 ring-primary/5' : 'border-outline-variant/30 hover:border-primary/50'}`}
                >
                  <span className={`block text-[10px] font-label tracking-widest uppercase mb-1 font-bold ${flowLevel === r.level ? 'text-primary' : 'text-outline'}`}>Level {r.level}: {r.label}</span>
                  <p className={`text-base ${flowLevel === r.level ? 'text-on-surface font-semibold' : 'text-on-surface-variant'}`}>{r.desc}</p>
                </button>
              ))}
            </div>
          </section>
        )
      default:
        return null
    }
  }

  return (
    <div className="pt-4 pb-32 px-6 max-w-2xl mx-auto flex flex-col min-h-[80vh]">
      {/* Progress Bar */}
      <div className="w-full bg-surface-variant/30 h-1.5 rounded-full mb-12 overflow-hidden">
        <div 
          className="bg-primary h-full transition-all duration-500 ease-out"
          style={{ width: `${((step + 1) / totalSteps) * 100}%` }}
        ></div>
      </div>

      <div className="flex-grow">
        {renderStep()}
      </div>

      {/* Navigation Actions */}
      <div className="mt-16 space-y-4">
        <div className="flex gap-4">
          {step > 0 && (
            <button 
              onClick={prevStep}
              className="flex-1 py-5 bg-surface-container-high text-on-surface font-bold rounded-full transition-all active:scale-[0.98]"
            >
              Previous
            </button>
          )}
          {step < totalSteps - 1 ? (
            <button 
              onClick={nextStep}
              className="flex-[2] py-5 bg-primary text-on-primary font-bold rounded-full shadow-lg active:scale-[0.98] transition-all"
            >
              Next
            </button>
          ) : (
            <button 
              onClick={handleSave}
              className="flex-[2] py-5 bg-gradient-to-r from-primary to-primary-dim text-on-primary font-bold rounded-full shadow-xl active:scale-[0.98] transition-all"
            >
              Save Reflection
            </button>
          )}
        </div>
        
        {step === 0 && (
          <button 
            onClick={onSkip}
            className="w-full py-4 text-on-surface-variant font-label text-sm border-b-2 border-transparent hover:border-primary/20 transition-all opacity-60"
          >
            Skip for now
          </button>
        )}
      </div>
    </div>
  )
}

export default ReflectScreen
