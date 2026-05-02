import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { HomeScreen } from '../screens/HomeScreen';
import { SearchScreen } from '../screens/SearchScreen';
import { SettingsScreen } from '../screens/SettingsScreen';
import { colors } from '../theme/colors';

const Tab = createBottomTabNavigator();

export function RootNavigator() {
  return (
    <NavigationContainer>
      <Tab.Navigator
        screenOptions={{
          tabBarActiveTintColor: colors.tabActive,
          tabBarInactiveTintColor: colors.tabInactive,
          tabBarShowLabel: true,
          tabBarLabelStyle: { fontSize: 13, fontWeight: '500' },
          tabBarStyle: { paddingTop: 6, height: 60 },
          tabBarIconStyle: { display: 'none' },
          headerStyle: { backgroundColor: colors.surface },
          headerTitleStyle: { color: colors.text, fontWeight: '600' },
        }}>
        <Tab.Screen
          name="Home"
          component={HomeScreen}
          options={{ title: '홈' }}
        />
        <Tab.Screen
          name="Search"
          component={SearchScreen}
          options={{ title: '검색' }}
        />
        <Tab.Screen
          name="Settings"
          component={SettingsScreen}
          options={{ title: '설정' }}
        />
      </Tab.Navigator>
    </NavigationContainer>
  );
}
