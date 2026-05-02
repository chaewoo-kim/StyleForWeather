import { WeatherCondition } from './index';

export const CONDITION_LABEL: Record<WeatherCondition, string> = {
  CLEAR: '맑음',
  CLOUDS: '구름',
  RAIN: '비',
  DRIZZLE: '이슬비',
  THUNDERSTORM: '뇌우',
  SNOW: '눈',
  MIST: '옅은 안개',
  FOG: '안개',
  HAZE: '연무',
};

export const CONDITION_EMOJI: Record<WeatherCondition, string> = {
  CLEAR: '☀️',
  CLOUDS: '☁️',
  RAIN: '🌧️',
  DRIZZLE: '🌦️',
  THUNDERSTORM: '⛈️',
  SNOW: '❄️',
  MIST: '🌫️',
  FOG: '🌫️',
  HAZE: '🌫️',
};

export const CATEGORY_LABEL: Record<string, string> = {
  TOP: '상의',
  INNER: '이너',
  BOTTOM: '하의',
  OUTER: '아우터',
  SHOES: '신발',
  SOCKS: '양말',
  ACCESSORY: '액세서리',
};

export const CATEGORY_EMOJI: Record<string, string> = {
  TOP: '👕',
  INNER: '🩱',
  BOTTOM: '👖',
  OUTER: '🧥',
  SHOES: '👟',
  SOCKS: '🧦',
  ACCESSORY: '🧢',
};
