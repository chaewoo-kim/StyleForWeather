import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { CurrentWeather } from '../types';
import { CONDITION_LABEL } from '../types/conditions';
import { colors } from '../theme/colors';

interface Props {
  weather: CurrentWeather;
}

export function WeatherCard({ weather }: Props) {
  return (
    <View style={styles.card}>
      <Text style={styles.location}>{weather.locationName}</Text>
      <Text style={styles.temp}>{Math.round(weather.temperature)}°</Text>
      <View style={styles.conditionPill}>
        <Text style={styles.conditionText}>
          {CONDITION_LABEL[weather.condition]}
        </Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: 16,
    padding: 24,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 2 },
    elevation: 2,
  },
  location: {
    fontSize: 16,
    color: colors.textSecondary,
    marginBottom: 8,
  },
  temp: {
    fontSize: 72,
    fontWeight: '300',
    color: colors.text,
    lineHeight: 80,
  },
  conditionPill: {
    alignSelf: 'flex-start',
    backgroundColor: colors.primaryMuted,
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 999,
    marginTop: 8,
  },
  conditionText: {
    color: colors.primary,
    fontSize: 14,
    fontWeight: '500',
  },
});
