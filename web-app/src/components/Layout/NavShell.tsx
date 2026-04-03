import React from 'react'
import { Screen } from '../../App'

interface NavShellProps {
  children: React.ReactNode
  currentScreen: Screen
  onNavigate: (screen: Screen) => void
}

const NavShell: React.FC<NavShellProps> = ({ children, currentScreen, onNavigate }) => {
  const isTimerActive = currentScreen === 'timer'

  return (
    <div className="flex flex-col min-h-screen">
      {/* Main Content */}
      <main className="flex-grow pt-8 pb-32">
        {children}
      </main>

      {/* BottomNavBar - Hidden during active timer for focus */}
      {!isTimerActive && (
        <nav className="fixed bottom-0 left-0 w-full flex justify-around items-center px-4 pb-8 pt-2 glass-panel rounded-t-3xl shadow-lg">
          <button 
            onClick={() => onNavigate('start')}
            className={`flex flex-col items-center justify-center px-6 py-2 transition-all ${currentScreen === 'start' ? 'bg-primary-container text-primary rounded-full' : 'text-on-surface/50'}`}
          >
            <span className="material-symbols-outlined">self_improvement</span>
            <span className="font-label text-[10px] uppercase tracking-widest font-semibold mt-1">Practice</span>
          </button>

          <button 
            onClick={() => onNavigate('progress' as Screen)}
            className={`flex flex-col items-center justify-center px-6 py-2 transition-all ${currentScreen === 'progress' ? 'bg-primary-container text-primary rounded-full' : 'text-on-surface/50'}`}
          >
            <span className="material-symbols-outlined">auto_graph</span>
            <span className="font-label text-[10px] uppercase tracking-widest font-semibold mt-1">Insights</span>
          </button>

          <button 
            onClick={() => onNavigate('history' as Screen)}
            className={`flex flex-col items-center justify-center px-6 py-2 transition-all ${currentScreen === 'history' ? 'bg-primary-container text-primary rounded-full' : 'text-on-surface/50'}`}
          >
            <span className="material-symbols-outlined">menu_book</span>
            <span className="font-label text-[10px] uppercase tracking-widest font-semibold mt-1">Journal</span>
          </button>
        </nav>
      )}
    </div>
  )
}

export default NavShell
