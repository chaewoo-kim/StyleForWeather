export type Category =
  | 'TOP'
  | 'INNER'
  | 'BOTTOM'
  | 'OUTER'
  | 'SHOES'
  | 'SOCKS'
  | 'ACCESSORY';

export type Gender = 'MALE' | 'FEMALE' | 'UNISEX';

export type Style = 'CASUAL' | 'FORMAL' | 'SPORTY' | 'STREET';

export type WeatherCondition =
  | 'CLEAR'
  | 'CLOUDS'
  | 'RAIN'
  | 'DRIZZLE'
  | 'THUNDERSTORM'
  | 'SNOW'
  | 'MIST'
  | 'FOG'
  | 'HAZE';

export interface CurrentWeather {
  locationName: string;
  temperature: number;
  condition: WeatherCondition;
}

export interface Recommendation {
  clothesId: number;
  name: string;
  category: Category;
  imageUrl: string | null;
  gender: Gender;
  style: Style;
  priority: number;
}

export interface Clothes {
  id: number;
  name: string;
  category: Category;
  imageUrl: string | null;
  gender: Gender;
  style: Style;
}

export interface DailyForecast {
  date: string;
  temperature: number;
  condition: WeatherCondition;
  recommendations: Recommendation[];
}

export interface WeeklyForecast {
  locationName: string;
  daily: DailyForecast[];
}
