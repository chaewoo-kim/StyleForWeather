import { apiClient } from './client';
import { Gender, Style, WeeklyForecast } from '../types';

export async function getForecastByCoords(
  lat: number,
  lon: number,
  gender: Gender,
  style: Style,
): Promise<WeeklyForecast> {
  const { data } = await apiClient.get<WeeklyForecast>('/api/forecast', {
    params: { lat, lon, gender, style },
  });
  return data;
}

export async function getForecastByCity(
  city: string,
  gender: Gender,
  style: Style,
): Promise<WeeklyForecast> {
  const { data } = await apiClient.get<WeeklyForecast>('/api/forecast', {
    params: { city, gender, style },
  });
  return data;
}
