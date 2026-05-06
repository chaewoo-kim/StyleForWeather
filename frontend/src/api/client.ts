import axios from 'axios';
import { Platform } from 'react-native';

export const baseURL = Platform.select({
  ios: 'http://localhost:8080',
  android: 'http://10.0.2.2:8080',
  default: 'http://localhost:8080',
}) as string;

export function absoluteUrl(path: string | null | undefined): string | null {
  if (!path) return null;
  if (/^https?:\/\//.test(path)) return path;
  return baseURL + path;
}

export const apiClient = axios.create({
  baseURL,
  timeout: 10000,
});
