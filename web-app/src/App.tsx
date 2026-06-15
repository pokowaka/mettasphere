import { useState, useEffect } from 'react'
import StartScreen from './components/Practice/StartScreen'
import TimerScreen from './components/Practice/TimerScreen'
import PresetForm from './components/Practice/PresetForm'
import ReflectScreen from './components/Practice/ReflectScreen'
import ProgressScreen from './components/Practice/ProgressScreen'
import HistoryScreen from './components/Practice/HistoryScreen'
import NavShell from './components/Layout/NavShell'
import { Preset, syncDefaultPresets } from './db'

export type Screen = 'start' | 'timer' | 'preset' | 'progress' | 'reflect' | 'history'

function App() {
  const [currentScreen, setCurrentScreen] = useState<Screen>('start')
  const [selectedPreset, setSelectedPreset] = useState<Preset | null>(null)

  useEffect(() => {
    syncDefaultPresets()
  }, [])

  const handleStartTimer = (preset: Preset) => {
    setSelectedPreset(preset)
    setCurrentScreen('timer')
  }

  const handleEditPreset = (preset: Preset) => {
    setSelectedPreset(preset)
    setCurrentScreen('preset')
  }

  const handleCreateNewPreset = () => {
    setSelectedPreset(null)
    setCurrentScreen('preset')
  }

  const renderScreen = () => {
    switch (currentScreen) {
      case 'start':
        return (
          <StartScreen 
            onStartTimer={handleStartTimer} 
            onEditPreset={handleEditPreset}
            onCreatePreset={handleCreateNewPreset} 
          />
        )
      case 'timer':
        return selectedPreset ? (
          <TimerScreen 
            preset={selectedPreset}
            onEnd={() => setCurrentScreen('reflect')} 
          />
        ) : null
      case 'reflect':
        return (
          <ReflectScreen 
            preset={selectedPreset}
            onSave={() => {
              setSelectedPreset(null)
              setCurrentScreen('progress')
            }}
            onSkip={() => {
              setSelectedPreset(null)
              setCurrentScreen('start')
            }}
          />
        )
      case 'history':
        return <HistoryScreen />
      case 'progress':
        return <ProgressScreen />
      case 'preset':
        return (
          <PresetForm 
            preset={selectedPreset}
            onCancel={() => setCurrentScreen('start')} 
            onSave={() => setCurrentScreen('start')} 
          />
        )
      default:
        return (
          <StartScreen 
            onStartTimer={handleStartTimer} 
            onEditPreset={handleEditPreset}
            onCreatePreset={handleCreateNewPreset} 
          />
        )
    }
  }

  return (
    <div className="min-h-screen bg-surface text-on-surface font-body selection:bg-primary-container selection:text-on-primary-container">
      <NavShell currentScreen={currentScreen} onNavigate={setCurrentScreen}>
        {renderScreen()}
      </NavShell>
    </div>
  )
}

export default App
