import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { SvgUri } from 'react-native-svg';
import { Recommendation } from '../types';
import { CATEGORY_LABEL } from '../types/conditions';
import { colors } from '../theme/colors';
import { absoluteUrl } from '../api/client';

interface Props {
  items: Recommendation[];
}

const STYLE_LABEL: Record<string, string> = {
  CASUAL: '캐주얼',
  FORMAL: '포멀',
  SPORTY: '스포티',
  STREET: '스트릿',
};

export function RecommendList({ items }: Props) {
  if (items.length === 0) {
    return (
      <View style={styles.emptyBox}>
        <Text style={styles.emptyText}>
          이 조건에 맞는 추천이 아직 없어요.
        </Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.heading}>오늘의 추천</Text>
      {items.map((item, idx) => {
        const uri = absoluteUrl(item.imageUrl);
        return (
          <View
            key={item.clothesId}
            style={[styles.row, idx === items.length - 1 && styles.rowLast]}>
            <View style={styles.thumb}>
              {uri ? (
                <SvgUri uri={uri} width="100%" height="100%" />
              ) : (
                <Text style={styles.thumbFallback}>
                  {CATEGORY_LABEL[item.category]}
                </Text>
              )}
            </View>
            <View style={styles.body}>
              <Text style={styles.name}>{item.name}</Text>
              <Text style={styles.meta}>
                {CATEGORY_LABEL[item.category]} · {STYLE_LABEL[item.style] ?? item.style}
              </Text>
            </View>
          </View>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: colors.surface,
    borderRadius: 16,
    padding: 16,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 2 },
    elevation: 2,
  },
  heading: {
    fontSize: 18,
    fontWeight: '600',
    color: colors.text,
    marginBottom: 12,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border,
  },
  rowLast: {
    borderBottomWidth: 0,
  },
  thumb: {
    width: 64,
    height: 64,
    borderRadius: 12,
    marginRight: 14,
    overflow: 'hidden',
    backgroundColor: colors.primaryMuted,
    alignItems: 'center',
    justifyContent: 'center',
  },
  thumbFallback: {
    fontSize: 12,
    color: colors.primary,
    fontWeight: '600',
  },
  body: {
    flex: 1,
  },
  name: {
    fontSize: 16,
    color: colors.text,
    fontWeight: '500',
  },
  meta: {
    fontSize: 13,
    color: colors.textSecondary,
    marginTop: 2,
  },
  emptyBox: {
    backgroundColor: colors.surface,
    borderRadius: 16,
    padding: 24,
    alignItems: 'center',
  },
  emptyText: {
    color: colors.textSecondary,
    fontSize: 14,
  },
});
