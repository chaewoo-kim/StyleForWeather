import React, { useState } from 'react';
import {
  Keyboard,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import { CurrentWeather, WeeklyForecast } from '../types';
import { getForecastByCity } from '../api/forecast';
import { WeatherCard } from '../components/WeatherCard';
import { ForecastList } from '../components/ForecastList';
import { RecommendList } from '../components/RecommendList';
import { useSettings } from '../storage/SettingsContext';
import { colors } from '../theme/colors';

export function SearchScreen() {
  const { settings } = useSettings();
  const [query, setQuery] = useState('');
  const [forecast, setForecast] = useState<WeeklyForecast | null>(null);
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const onSearch = async () => {
    const city = query.trim();
    if (!city) return;
    Keyboard.dismiss();
    setLoading(true);
    setError(null);
    try {
      const f = await getForecastByCity(city, settings.gender, settings.style);
      setForecast(f);
      setSelectedIndex(0);
    } catch (e: any) {
      setForecast(null);
      const status = e?.response?.status;
      if (status === 404) {
        setError('도시를 찾을 수 없습니다.');
      } else {
        setError(e?.message ?? '검색에 실패했습니다.');
      }
    } finally {
      setLoading(false);
    }
  };

  const selected = forecast?.daily[selectedIndex];
  const cardWeather: CurrentWeather | null =
    forecast && selected
      ? {
          locationName: forecast.locationName,
          temperature: selected.temperature,
          condition: selected.condition,
        }
      : null;

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
      keyboardShouldPersistTaps="handled">
      <View style={styles.searchRow}>
        <TextInput
          value={query}
          onChangeText={setQuery}
          placeholder="도시명을 영문으로 입력 (예: Seoul)"
          placeholderTextColor={colors.textSecondary}
          style={styles.input}
          autoCapitalize="words"
          autoCorrect={false}
          returnKeyType="search"
          onSubmitEditing={onSearch}
        />
        <TouchableOpacity
          onPress={onSearch}
          style={[styles.button, loading && styles.buttonDisabled]}
          disabled={loading}>
          <Text style={styles.buttonText}>
            {loading ? '검색 중' : '검색'}
          </Text>
        </TouchableOpacity>
      </View>

      {error ? <Text style={styles.error}>{error}</Text> : null}
      {cardWeather ? <WeatherCard weather={cardWeather} /> : null}
      {forecast ? (
        <ForecastList
          daily={forecast.daily}
          selectedIndex={selectedIndex}
          onSelect={setSelectedIndex}
        />
      ) : null}
      {forecast ? (
        <RecommendList items={selected?.recommendations ?? []} />
      ) : null}
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
  searchRow: {
    flexDirection: 'row',
    gap: 8,
    marginBottom: 16,
  },
  input: {
    flex: 1,
    backgroundColor: colors.surface,
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 15,
    color: colors.text,
    borderWidth: 1,
    borderColor: colors.border,
  },
  button: {
    backgroundColor: colors.primary,
    paddingHorizontal: 18,
    justifyContent: 'center',
    borderRadius: 10,
  },
  buttonDisabled: {
    opacity: 0.6,
  },
  buttonText: {
    color: '#fff',
    fontSize: 15,
    fontWeight: '500',
  },
  error: {
    color: colors.danger,
    fontSize: 14,
    marginBottom: 12,
  },
});
