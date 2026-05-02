import axios from 'axios';
import { Platform } from 'react-native';

const baseURL = Platform.select({
  ios: 'http://localhost:8080',
  android: 'http://10.0.2.2:8080',
  default: 'http://localhost:8080',
});

export const apiClient = axios.create({
  baseURL,
  timeout: 10000,
});
