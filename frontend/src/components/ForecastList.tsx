import React from 'react';
import {
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { DailyForecast } from '../types';
import { CONDITION_EMOJI } from '../types/conditions';
import { colors } from '../theme/colors';

interface Props {
  daily: DailyForecast[];
  selectedIndex: number;
  onSelect: (index: number) => void;
}

const WEEKDAYS_KR = ['일', '월', '화', '수', '목', '금', '토'];

function formatDate(iso: string): { weekday: string; short: string } {
  const d = new Date(iso + 'T00:00:00');
  return {
    weekday: WEEKDAYS_KR[d.getDay()],
    short: `${d.getMonth() + 1}/${d.getDate()}`,
  };
}

export function ForecastList({ daily, selectedIndex, onSelect }: Props) {
  if (daily.length === 0) return null;

  return (
    <View style={styles.wrapper}>
      <Text style={styles.heading}>주간 예보</Text>
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.row}>
        {daily.map((d, idx) => {
          const { weekday, short } = formatDate(d.date);
          const selected = idx === selectedIndex;
          return (
            <Pressable
              key={d.date}
              onPress={() => onSelect(idx)}
              style={[styles.card, selected && styles.cardSelected]}>
              <Text style={[styles.weekday, selected && styles.textSelected]}>
                {weekday}
              </Text>
              <Text style={[styles.short, selected && styles.textSelected]}>
                {short}
              </Text>
              <Text style={styles.emoji}>{CONDITION_EMOJI[d.condition]}</Text>
              <Text style={[styles.temp, selected && styles.textSelected]}>
                {Math.round(d.temperature)}°
              </Text>
            </Pressable>
          );
        })}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    marginBottom: 16,
  },
  heading: {
    fontSize: 16,
    fontWeight: '600',
    color: colors.text,
    marginBottom: 8,
    paddingHorizontal: 4,
  },
  row: {
    paddingVertical: 4,
    paddingHorizontal: 2,
  },
  card: {
    width: 72,
    paddingVertical: 14,
    backgroundColor: colors.surface,
    borderRadius: 14,
    alignItems: 'center',
    marginRight: 10,
    borderWidth: 2,
    borderColor: 'transparent',
  },
  cardSelected: {
    borderColor: colors.primary,
    backgroundColor: colors.primaryMuted,
  },
  weekday: {
    fontSize: 14,
    fontWeight: '600',
    color: colors.text,
  },
  short: {
    fontSize: 11,
    color: colors.textSecondary,
    marginBottom: 6,
  },
  emoji: {
    fontSize: 24,
    marginVertical: 2,
  },
  temp: {
    fontSize: 16,
    fontWeight: '600',
    color: colors.text,
    marginTop: 4,
  },
  textSelected: {
    color: colors.primary,
  },
});
