// App.tsx — ARIA Emergency Network Root
import React, { useEffect, useState } from 'react';
import { View, StatusBar, Platform, StyleSheet } from 'react-native';
import { NavigationContainer, DefaultTheme } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { MMKV } from 'react-native-mmkv';
import LinearGradient from 'react-native-linear-gradient';

import AuthScreen       from './src/screens/auth/AuthScreen';
import HomeScreen       from './src/screens/home/HomeScreen';
import SOSScreen        from './src/screens/sos/SOSScreen';
import MapScreen        from './src/screens/map/MapScreen';
import VoiceScreen      from './src/screens/voice/VoiceScreen';
import ChatScreen       from './src/screens/chat/ChatScreen';
import MedicalScreen    from './src/screens/medical/MedicalScreen';
import ProfileScreen    from './src/screens/profile/ProfileScreen';
import AlertsScreen     from './src/screens/alerts/AlertsScreen';
import DispatchScreen   from './src/screens/dispatch/DispatchScreen';
import DisastersScreen  from './src/screens/disasters/DisastersScreen';

import { Colors, Radius } from './src/utils/theme';
// @ts-ignore
const storage = new MMKV();
const Stack = createNativeStackNavigator();
const Tab   = createBottomTabNavigator();

const NAV_THEME = {
  ...DefaultTheme,
  dark: true,
  colors: {
    ...DefaultTheme.colors,
    primary:    Colors.red,
    background: Colors.bg,
    card:       Colors.surface,
    text:       Colors.text,
    border:     Colors.border,
    notification: Colors.red,
  },
};

// ─── Tab Navigator ──────────────────────────────────────────────────────────────
function MainTabs() {
  const role = storage.getString('role') || 'USER';
  const isResponder = ['DOCTOR', 'POLICE', 'AMBULANCE'].includes(role);

  return (
    <Tab.Navigator
        id="MainTabs"
      screenOptions={{
        headerShown: false,
        tabBarStyle: styles.tabBar,
        tabBarActiveTintColor: Colors.red,
        tabBarInactiveTintColor: Colors.textMuted,
        tabBarShowLabel: true,
        tabBarLabelStyle: { fontSize: 10, fontWeight: '600', marginBottom: 2 },
        tabBarBackground: () => (
          <View style={styles.tabBarBg}>
            <View style={styles.tabBarLine} />
          </View>
        ),
      }}
    >
      <Tab.Screen
        name="Home" component={HomeScreen}
        options={{ tabBarIcon: ({ color }) => <TabIcon emoji="🏠" color={color} />, tabBarLabel: 'Home' }}
      />
      <Tab.Screen
        name="Map" component={MapScreen}
        options={{ tabBarIcon: ({ color }) => <TabIcon emoji="🗺️" color={color} />, tabBarLabel: 'Live Map' }}
      />
      <Tab.Screen
        name="SOS" component={SOSScreen}
        options={{
          tabBarIcon: ({ focused }) => (
            <View style={[styles.sosCenterBtn, focused && styles.sosCenterBtnActive]}>
              <LinearGradient colors={[Colors.red, Colors.redDark]} style={styles.sosGrad}>
                <TabIcon emoji="🆘" color="#fff" size={22} />
              </LinearGradient>
            </View>
          ),
          tabBarLabel: '',
          tabBarStyle: { display: 'flex', height: 80 },
        }}
      />
      <Tab.Screen
        name="Voice" component={VoiceScreen}
        options={{ tabBarIcon: ({ color }) => <TabIcon emoji="🎙️" color={color} />, tabBarLabel: 'ARIA' }}
      />
      {isResponder ? (
        <Tab.Screen
          name="Dispatch" component={DispatchScreen}
          options={{ tabBarIcon: ({ color }) => <TabIcon emoji="📡" color={color} />, tabBarLabel: 'Dispatch' }}
        />
      ) : (
        <Tab.Screen
          name="Medical" component={MedicalScreen}
          options={{ tabBarIcon: ({ color }) => <TabIcon emoji="🏥" color={color} />, tabBarLabel: 'Help' }}
        />
      )}
    </Tab.Navigator>
  );
}

function TabIcon({ emoji, color, size = 20 }: any) {
  return (
      <View style={{ opacity: color === Colors.red ? 1 : 0.7, alignItems: 'center', justifyContent: 'center' }}>
        {/* Yahan se View ka fontSize hata diya hai */}
        <TabEmoji emoji={emoji} size={size} />
      </View>
  );
}

function TabEmoji({ emoji, size = 20 }: any) {
  const { Text } = require('react-native');
  return <Text style={{ fontSize: size }}>{emoji}</Text>;
}

// ─── Root Stack ─────────────────────────────────────────────────────────────────
export default function App() {
  const [initialRoute, setInitialRoute] = useState<string | null>(null);

  useEffect(() => {
    const token = storage.getString('token');
    setInitialRoute(token ? 'Main' : 'Auth');
  }, []);

  if (!initialRoute) return null;

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <SafeAreaProvider>
        <NavigationContainer theme={NAV_THEME}>
          <StatusBar barStyle="light-content" backgroundColor={Colors.bg} />
          <Stack.Navigator
              id="RootStack"
            initialRouteName={initialRoute}
            screenOptions={{ headerShown: false, animation: 'slide_from_right' }}
          >
            <Stack.Screen name="Auth"     component={AuthScreen} />
            <Stack.Screen name="Main"     component={MainTabs} />
            <Stack.Screen name="Alerts"   component={AlertsScreen} />
            <Stack.Screen name="Profile"  component={ProfileScreen} />
            <Stack.Screen name="Disasters" component={DisastersScreen} />
            <Stack.Screen name="Chat"     component={ChatScreen} />
          </Stack.Navigator>
        </NavigationContainer>
      </SafeAreaProvider>
    </GestureHandlerRootView>
  );
}

const styles = StyleSheet.create({
  tabBar: {
    backgroundColor: 'transparent',
    borderTopWidth: 0,
    elevation: 0,
    height: Platform.OS === 'ios' ? 88 : 72,
    paddingTop: 6,
  },
    tabBarBg: {
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: Colors.surface,
    },
  tabBarLine: {
    position: 'absolute', top: 0, left: 20, right: 20, height: 1,
    backgroundColor: Colors.border,
  },
  sosCenterBtn: {
    width: 56, height: 56, borderRadius: 28, marginTop: -12,
    borderWidth: 3, borderColor: Colors.bg, overflow: 'hidden',
  },
  sosCenterBtnActive: { borderColor: Colors.red },
  sosGrad: { flex: 1, justifyContent: 'center', alignItems: 'center' },
});
