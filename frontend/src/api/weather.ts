import { apiClient } from './client';
import { CurrentWeather } from '../types';

export async function getWeatherByCoords(
  lat: number,
  lon: number,
): Promise<CurrentWeather> {
  const { data } = await apiClient.get<CurrentWeather>('/api/weather', {
    params: { lat, lon },
  });
  return data;
}

export async function getWeatherByCity(city: string): Promise<CurrentWeather> {
  const { data } = await apiClient.get<CurrentWeather>('/api/weather', {
    params: { city },
  });
  return data;
}
