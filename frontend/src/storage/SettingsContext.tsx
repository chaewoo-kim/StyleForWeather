import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
} from 'react';
import {
  DEFAULT_SETTINGS,
  UserSettings,
  loadSettings,
  saveSettings,
} from './settings';

interface SettingsContextValue {
  settings: UserSettings;
  loaded: boolean;
  update: (next: Partial<UserSettings>) => Promise<void>;
}

const SettingsContext = createContext<SettingsContextValue | null>(null);

export function SettingsProvider({ children }: { children: React.ReactNode }) {
  const [settings, setSettings] = useState<UserSettings>(DEFAULT_SETTINGS);
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    loadSettings().then(s => {
      setSettings(s);
      setLoaded(true);
    });
  }, []);

  const update = useCallback(
    async (next: Partial<UserSettings>) => {
      const merged = { ...settings, ...next };
      setSettings(merged);
      await saveSettings(merged);
    },
    [settings],
  );

  return (
    <SettingsContext.Provider value={{ settings, loaded, update }}>
      {children}
    </SettingsContext.Provider>
  );
}

export function useSettings(): SettingsContextValue {
  const ctx = useContext(SettingsContext);
  if (!ctx) {
    throw new Error('useSettings must be used within SettingsProvider');
  }
  return ctx;
}
