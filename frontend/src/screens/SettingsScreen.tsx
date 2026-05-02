import React from 'react';
import {
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { Gender, Style } from '../types';
import { useSettings } from '../storage/SettingsContext';
import { colors } from '../theme/colors';

const GENDER_OPTIONS: { value: Gender; label: string }[] = [
  { value: 'UNISEX', label: '공용' },
  { value: 'MALE', label: '남성' },
  { value: 'FEMALE', label: '여성' },
];

const STYLE_OPTIONS: { value: Style; label: string }[] = [
  { value: 'CASUAL', label: '캐주얼' },
  { value: 'FORMAL', label: '포멀' },
];

export function SettingsScreen() {
  const { settings, update } = useSettings();

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}>
      <Section title="성별">
        <View style={styles.group}>
          {GENDER_OPTIONS.map(opt => (
            <Option
              key={opt.value}
              label={opt.label}
              selected={settings.gender === opt.value}
              onPress={() => update({ gender: opt.value })}
            />
          ))}
        </View>
      </Section>

      <Section title="스타일">
        <View style={styles.group}>
          {STYLE_OPTIONS.map(opt => (
            <Option
              key={opt.value}
              label={opt.label}
              selected={settings.style === opt.value}
              onPress={() => update({ style: opt.value })}
            />
          ))}
        </View>
      </Section>

      <Text style={styles.note}>
        설정은 자동으로 저장되며 홈/검색 화면에 반영됩니다.
      </Text>
    </ScrollView>
  );
}

function Section({
  title,
  children,
}: {
  title: string;
  children: React.ReactNode;
}) {
  return (
    <View style={styles.section}>
      <Text style={styles.sectionTitle}>{title}</Text>
      {children}
    </View>
  );
}

function Option({
  label,
  selected,
  onPress,
}: {
  label: string;
  selected: boolean;
  onPress: () => void;
}) {
  return (
    <TouchableOpacity
      onPress={onPress}
      style={[styles.option, selected && styles.optionSelected]}>
      <Text style={[styles.optionText, selected && styles.optionTextSelected]}>
        {label}
      </Text>
    </TouchableOpacity>
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
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: colors.textSecondary,
    marginBottom: 8,
  },
  group: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  option: {
    backgroundColor: colors.surface,
    borderRadius: 10,
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderWidth: 1,
    borderColor: colors.border,
  },
  optionSelected: {
    backgroundColor: colors.primaryMuted,
    borderColor: colors.primary,
  },
  optionText: {
    color: colors.text,
    fontSize: 15,
  },
  optionTextSelected: {
    color: colors.primary,
    fontWeight: '600',
  },
  note: {
    color: colors.textSecondary,
    fontSize: 13,
    marginTop: 8,
  },
});
