import React, { useState, useEffect, useRef, useCallback } from 'react'
import { type Preset } from '../../db'

interface TimerScreenProps {
  preset: Preset
  onEnd: () => void
}

const soundMap: Record<string, string> = {
  "Singing Bowl": "bowl.wav",
  "Burmese Bell": "burmese_bell.wav",
  "Double Chime": "bell.wav",
  "Woodblock": "woodblock.wav",
  "Gong": "gong.wav"
}

const playSound = (soundName: string) => {
  if (soundName === 'No sound') return
  const fileName = soundMap[soundName]
  if (!fileName) return
  
  console.log(`Playing sound: ${soundName} (${fileName})`)
  const base = import.meta.env.BASE_URL || '/'
  const audio = new Audio(`${base}sounds/${fileName}`)
  audio.play().catch(e => console.error("Audio play failed", e))
}

const TimerScreen: React.FC<TimerScreenProps> = ({ preset, onEnd }) => {
  const [phase, setPhase] = useState<'delay' | 'active' | 'finished'>(preset.delaySeconds > 0 ? 'delay' : 'active')
  const [timeLeft, setTimeLeft] = useState(phase === 'delay' ? preset.delaySeconds : preset.totalMinutes * 60)
  const [isRunning, setIsRunning] = useState(true)
  
  const totalActiveSeconds = preset.totalMinutes * 60
  const intervalSeconds = preset.intervalMinutes * 60
  const lastIntervalRef = useRef(totalActiveSeconds)

  const handlePhaseComplete = useCallback(() => {
    if (phase === 'delay') {
      setPhase('active')
      setTimeLeft(preset.totalMinutes * 60)
      playSound(preset.startSound)
    } else if (phase === 'active') {
      setPhase('finished')
      setIsRunning(false)
      playSound(preset.endSound)
      // Small delay before returning to start or showing reflection
      setTimeout(onEnd, 2000)
    }
  }, [phase, preset, onEnd])

  useEffect(() => {
    let interval: number | undefined
    if (isRunning && timeLeft > 0) {
      interval = window.setInterval(() => {
        setTimeLeft((prev) => prev - 1)
      }, 1000)
    } else if (timeLeft === 0) {
      handlePhaseComplete()
    }
    return () => clearInterval(interval)
  }, [isRunning, timeLeft, handlePhaseComplete])

  // Interval chimes logic
  useEffect(() => {
    if (phase === 'active' && intervalSeconds > 0) {
      const elapsed = totalActiveSeconds - timeLeft
      if (elapsed > 0 && elapsed % intervalSeconds === 0 && timeLeft > 0) {
        // Prevent double-triggering within the same second
        if (lastIntervalRef.current !== timeLeft) {
          playSound(preset.intervalSound)
          lastIntervalRef.current = timeLeft
        }
      }
    }
  }, [timeLeft, phase, intervalSeconds, totalActiveSeconds, preset.intervalSound])

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${mins}:${secs.toString().padStart(2, '0')}`
  }

  const progress = phase === 'active' 
    ? ((totalActiveSeconds - timeLeft) / totalActiveSeconds) * 100 
    : 0
  const strokeDashoffset = 1000 - (progress / 100) * 1000

  return (
    <div className="relative min-h-screen flex flex-col items-center justify-center overflow-hidden animate-in fade-in duration-1000">
      {/* Background Layer */}
      <div className="absolute inset-0 z-0">
        <img 
          className="w-full h-full object-cover opacity-20 transition-all duration-1000"
          alt="preset visual"
          src={preset.visual} 
        />
        <div className="absolute inset-0 bg-gradient-to-b from-surface via-transparent to-surface"></div>
      </div>

      {/* Active Timer Content */}
      <div className="relative z-10 flex flex-col items-center w-full max-w-lg px-8 text-center">
        <div className="mb-12">
          <span className="font-label text-xs uppercase tracking-[0.2rem] text-on-surface-variant opacity-60">
            {phase === 'delay' ? 'Prepare' : 'Deep Presence'}
          </span>
          <h1 className="font-headline text-4xl mt-2 text-on-surface">{preset.name}</h1>
        </div>

        {/* Progress Ring */}
        <div className="relative flex items-center justify-center w-80 h-80 md:w-96 md:h-96">
          <svg className="absolute inset-0 w-full h-full -rotate-90">
            <circle 
              className="text-surface-variant opacity-20" 
              cx="50%" cy="50%" fill="transparent" r="48%" 
              stroke="currentColor" strokeWidth="2" 
            />
            <circle 
              className={`${phase === 'delay' ? 'text-tertiary' : 'text-primary'} transition-all duration-1000 ease-linear`} 
              cx="50%" cy="50%" fill="transparent" r="48%" 
              stroke="currentColor" strokeWidth="2" 
              strokeDasharray="1000" 
              strokeDashoffset={strokeDashoffset}
              strokeLinecap="round" 
            />
          </svg>
          
          {/* Pulsating Inner Circle */}
          <div className={`absolute w-64 h-64 md:w-80 md:h-80 rounded-full bg-gradient-to-br ${phase === 'delay' ? 'from-tertiary-container/20' : 'from-primary-container/20'} to-transparent animate-pulse`}></div>
          
          <div className="relative flex flex-col items-center">
            <span className={`font-headline text-7xl md:text-8xl tracking-tighter font-light ${phase === 'delay' ? 'text-tertiary' : 'text-on-surface'}`}>
              {formatTime(timeLeft)}
            </span>
            <span className="font-label text-sm uppercase tracking-widest text-on-surface-variant mt-2">
              {phase === 'delay' ? 'starting soon' : 'remaining'}
            </span>
          </div>
        </div>

        {/* Controls */}
        <div className="mt-20 flex items-center gap-12">
          <button 
            onClick={() => setIsRunning(!isRunning)}
            className="group flex flex-col items-center gap-3"
          >
            <div className={`w-16 h-16 rounded-full flex items-center justify-center bg-gradient-to-r ${phase === 'delay' ? 'from-tertiary to-tertiary-dim' : 'from-primary to-primary-dim'} text-on-primary shadow-xl transition-transform duration-500 hover:scale-105 active:scale-95`}>
              <span className="material-symbols-outlined text-3xl">
                {isRunning ? 'pause' : 'play_arrow'}
              </span>
            </div>
            <span className="font-label text-[10px] uppercase tracking-widest text-on-surface-variant opacity-60 group-hover:opacity-100 transition-opacity">
              {isRunning ? 'Pause' : 'Resume'}
            </span>
          </button>

          <button 
            onClick={onEnd}
            className="group flex flex-col items-center gap-3"
          >
            <div className="w-16 h-16 rounded-full flex items-center justify-center bg-surface-container-high text-on-surface-variant transition-transform duration-500 hover:scale-105 active:scale-95">
              <span className="material-symbols-outlined text-2xl">stop</span>
            </div>
            <span className="font-label text-[10px] uppercase tracking-widest text-on-surface-variant opacity-60 group-hover:opacity-100 transition-opacity">End Session</span>
          </button>
        </div>

        {/* Breath Indicator */}
        <div className="mt-16 flex flex-col items-center gap-2">
          <div className={`w-3 h-3 rounded-full animate-ping ${phase === 'delay' ? 'bg-tertiary' : 'bg-primary'}`}></div>
          <p className="font-label text-xs italic text-on-surface-variant/70">
            {phase === 'delay' ? 'Find your posture...' : 'Just Smile, Relax, and Observe...'}
          </p>
        </div>
      </div>
    </div>
  )
}

export default TimerScreen
