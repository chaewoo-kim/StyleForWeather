import React, { useCallback, useEffect, useState } from 'react';
import {
  RefreshControl,
  ScrollView,
  StyleSheet,
} from 'react-native';
import Geolocation from '@react-native-community/geolocation';
import { useFocusEffect } from '@react-navigation/native';
import { CurrentWeather, Recommendation } from '../types';
import { getWeatherByCoords } from '../api/weather';
import { getRecommendations } from '../api/recommend';
import { WeatherCard } from '../components/WeatherCard';
import { RecommendList } from '../components/RecommendList';
import { ErrorView, LoadingView } from '../components/StatusView';
import { useSettings } from '../storage/SettingsContext';
import { colors } from '../theme/colors';

interface Coords {
  lat: number;
  lon: number;
}

function getCurrentPosition(): Promise<Coords> {
  return new Promise((resolve, reject) => {
    Geolocation.getCurrentPosition(
      pos =>
        resolve({ lat: pos.coords.latitude, lon: pos.coords.longitude }),
      err => reject(err),
      { enableHighAccuracy: false, timeout: 15000, maximumAge: 60_000 },
    );
  });
}

export function HomeScreen() {
  const { settings, loaded } = useSettings();
  const [weather, setWeather] = useState<CurrentWeather | null>(null);
  const [items, setItems] = useState<Recommendation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [refreshing, setRefreshing] = useState(false);

  const load = useCallback(async () => {
    setError(null);
    try {
      const coords = await getCurrentPosition();
      const w = await getWeatherByCoords(coords.lat, coords.lon);
      setWeather(w);
      const recs = await getRecommendations({
        temp: Math.round(w.temperature),
        condition: w.condition,
        gender: settings.gender,
        style: settings.style,
      });
      setItems(recs);
    } catch (e: any) {
      const msg =
        e?.code === 1 || e?.PERMISSION_DENIED
          ? '위치 권한이 필요합니다. 설정에서 권한을 허용해주세요.'
          : e?.message ?? '데이터를 불러오지 못했습니다.';
      setError(msg);
    }
  }, [settings.gender, settings.style]);

  useFocusEffect(
    useCallback(() => {
      if (!loaded) return;
      let cancelled = false;
      setLoading(true);
      load().finally(() => {
        if (!cancelled) setLoading(false);
      });
      return () => {
        cancelled = true;
      };
    }, [load, loaded]),
  );

  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    await load();
    setRefreshing(false);
  }, [load]);

  if (loading && !weather) {
    return <LoadingView message="날씨 정보를 불러오는 중..." />;
  }

  if (error && !weather) {
    return (
      <ErrorView
        message={error}
        onRetry={() => {
          setLoading(true);
          load().finally(() => setLoading(false));
        }}
      />
    );
  }

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
      }>
      {weather ? <WeatherCard weather={weather} /> : null}
      <RecommendList items={items} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  content: {
    padding: 16,
  },
});
