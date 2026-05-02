import { apiClient } from './client';
import { Gender, Recommendation, Style, WeatherCondition } from '../types';

export async function getRecommendations(params: {
  temp: number;
  condition: WeatherCondition;
  gender: Gender;
  style: Style;
}): Promise<Recommendation[]> {
  const { data } = await apiClient.get<Recommendation[]>('/api/recommend', {
    params,
  });
  return data;
}
