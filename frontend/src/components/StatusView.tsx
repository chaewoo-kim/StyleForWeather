import React from 'react';
import {
  ActivityIndicator,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { colors } from '../theme/colors';

export function LoadingView({ message }: { message?: string }) {
  return (
    <View style={styles.center}>
      <ActivityIndicator size="large" color={colors.primary} />
      {message ? <Text style={styles.msg}>{message}</Text> : null}
    </View>
  );
}

export function ErrorView({
  message,
  onRetry,
}: {
  message: string;
  onRetry?: () => void;
}) {
  return (
    <View style={styles.center}>
      <Text style={styles.errMsg}>{message}</Text>
      {onRetry ? (
        <TouchableOpacity onPress={onRetry} style={styles.retry}>
          <Text style={styles.retryText}>다시 시도</Text>
        </TouchableOpacity>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  center: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
  },
  msg: {
    marginTop: 12,
    color: colors.textSecondary,
    fontSize: 14,
  },
  errMsg: {
    color: colors.danger,
    fontSize: 14,
    textAlign: 'center',
    marginBottom: 12,
  },
  retry: {
    backgroundColor: colors.primary,
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 8,
  },
  retryText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '500',
  },
});
