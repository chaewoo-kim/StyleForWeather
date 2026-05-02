import AsyncStorage from '@react-native-async-storage/async-storage';
import { Gender, Style } from '../types';

const KEY = 'user_settings_v1';

export interface UserSettings {
  gender: Gender;
  style: Style;
}

export const DEFAULT_SETTINGS: UserSettings = {
  gender: 'UNISEX',
  style: 'CASUAL',
};

export async function loadSettings(): Promise<UserSettings> {
  const raw = await AsyncStorage.getItem(KEY);
  if (!raw) {
    return DEFAULT_SETTINGS;
  }
  try {
    return { ...DEFAULT_SETTINGS, ...JSON.parse(raw) };
  } catch {
    return DEFAULT_SETTINGS;
  }
}

export async function saveSettings(settings: UserSettings): Promise<void> {
  await AsyncStorage.setItem(KEY, JSON.stringify(settings));
}
