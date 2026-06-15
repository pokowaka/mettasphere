import Dexie, { type EntityTable } from 'dexie';

export interface Preset {
  id?: number;
  name: string;
  visual: string;
  delaySeconds: number;
  intervalMinutes: number;
  totalMinutes: number;
  startSound: string;
  intervalSound: string;
  endSound: string;
}

export interface Reflection {
  id?: number;
  sessionId?: number;
  timestamp: Date;
  mettaRadiance: number; // 1-10
  hindrances: string[]; // ids of active Nivarana
  recognitionLevel: number; // 1-5
  releaseType: string; // 'suppression' | 'analytical' | 'passive' | '6r'
  relaxationLevel: number; // 1-4
  smileQuality: number; // 1-4
  flowLevel: number; // 1-4
  smileDuration: string; // 'vanished' | 'stayed'
  physicalSensations: string[]; // ids of sensations
  mentalStates: string[]; // ids of mental states
}

const db = new Dexie('MettasphereDB') as Dexie & {
  presets: EntityTable<Preset, 'id'>;
  reflections: EntityTable<Reflection, 'id'>;
};

db.version(1).stores({
  presets: '++id, name',
  reflections: '++id, sessionId, timestamp'
});

// Seed some initial presets if none exist
db.on('populate', () => {
  db.presets.bulkAdd([
    {
      name: 'Morning Metta',
      visual: 'https://images.unsplash.com/photo-1506744038136-46273834b3fb',
      delaySeconds: 0,
      intervalMinutes: 5,
      totalMinutes: 20,
      startSound: 'Deep Bowl',
      intervalSound: 'Singing Bowl',
      endSound: 'Soft Gong'
    },
    {
      name: 'Quick Rest',
      visual: 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e',
      delaySeconds: 0,
      intervalMinutes: 0,
      totalMinutes: 10,
      startSound: 'Singing Bowl',
      intervalSound: 'No sound',
      endSound: 'Soft Gong'
    },
    {
      name: 'Quick Test',
      visual: 'https://images.unsplash.com/photo-1500674425917-06385469493a',
      delaySeconds: 0,
      intervalMinutes: 0,
      totalMinutes: 0.1, // 6 seconds
      startSound: 'Deep Bowl',
      intervalSound: 'No sound',
      endSound: 'Soft Gong'
    }
  ]);
});

export { db };
