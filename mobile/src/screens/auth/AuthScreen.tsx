// src/screens/auth/AuthScreen.tsx
import React, { useState, useRef, useEffect } from 'react';
import {
  View, Text, TextInput, TouchableOpacity, StyleSheet,
  Animated, Dimensions, KeyboardAvoidingView, Platform,
  ScrollView, Alert, StatusBar, ActivityIndicator,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import { MMKV } from 'react-native-mmkv';
import DeviceInfo from 'react-native-device-info';
import { AuthAPI } from '../../services/api';
import { Colors, Spacing, Radius, Typography } from '../../utils/theme';

const { width, height } = Dimensions.get('window');
const storage = new MMKV();

type Tab = 'login' | 'register' | 'guest';
type Role = 'USER' | 'DOCTOR' | 'POLICE' | 'AMBULANCE';

const ROLES = [
  { key: 'USER',      label: 'Normal User',    icon: '👤', desc: 'Send SOS, find help' },
  { key: 'DOCTOR',    label: 'Doctor',         icon: '🩺', desc: 'Provide medical consult' },
  { key: 'POLICE',    label: 'Police Officer', icon: '👮', desc: 'Respond to emergencies' },
  { key: 'AMBULANCE', label: 'Ambulance',      icon: '🚑', desc: 'Emergency dispatch' },
];

export default function AuthScreen({ navigation }: any) {
  const [tab, setTab] = useState<Tab>('login');
  const [loading, setLoading] = useState(false);
  const [selectedRole, setSelectedRole] = useState<Role>('USER');
  const [form, setForm] = useState({
    name: '', email: '', password: '',
    licenseNumber: '', specialization: '', vehicleId: '',
  });

  // Animation values
  const pulseAnim = useRef(new Animated.Value(1)).current;
  const slideAnim = useRef(new Animated.Value(0)).current;
  const fadeAnim  = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    // Entrance animation
    Animated.parallel([
      Animated.spring(slideAnim, { toValue: 1, useNativeDriver: true, tension: 60, friction: 10 }),
      Animated.timing(fadeAnim, { toValue: 1, duration: 800, useNativeDriver: true }),
    ]).start();

    // Pulse the emergency dot
    Animated.loop(
      Animated.sequence([
        Animated.timing(pulseAnim, { toValue: 1.4, duration: 800, useNativeDriver: true }),
        Animated.timing(pulseAnim, { toValue: 1, duration: 800, useNativeDriver: true }),
      ])
    ).start();
  }, []);

  const set = (field: string) => (value: string) =>
    setForm(prev => ({ ...prev, [field]: value }));

  const handleSubmit = async () => {
    if (!form.email && tab !== 'guest') return;
    setLoading(true);
    try {
      let res: any;
      const deviceId = await DeviceInfo.getUniqueId();

      if (tab === 'login') {
        res = await AuthAPI.login(form.email, form.password);
      } else if (tab === 'register') {
        res = await AuthAPI.register({
          ...form, role: selectedRole, deviceId,
        });
      } else {
        res = await AuthAPI.guest(deviceId);
      }

      const { token, role, name, email: userEmail, deviceId: did } = res.data;
      storage.set('token', token);
      storage.set('role', role || 'GUEST');
      storage.set('name', name || 'User');
      storage.set('email', userEmail || '');
      storage.set('deviceId', did || deviceId);

      navigation.replace('Main');
    } catch (err: any) {
      Alert.alert('Error', err.response?.data?.error || err.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  const translateY = slideAnim.interpolate({
    inputRange: [0, 1],
    outputRange: [60, 0],
  });

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="transparent" translucent />

      {/* Ambient background glow */}
      <View style={styles.glow1} />
      <View style={styles.glow2} />

      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      >
        <ScrollView
          contentContainerStyle={styles.scroll}
          showsVerticalScrollIndicator={false}
          keyboardShouldPersistTaps="handled"
        >
          {/* Logo */}
          <Animated.View style={[styles.logoWrap, { opacity: fadeAnim, transform: [{ translateY }] }]}>
            <View style={styles.logoIcon}>
              <Animated.View style={[styles.logoPulse, { transform: [{ scale: pulseAnim }] }]} />
              <Text style={styles.logoSiren}>🚨</Text>
            </View>
            <Text style={styles.logoTitle}>ARIA</Text>
            <Text style={styles.logoSub}>AI Emergency Network</Text>
          </Animated.View>

          {/* Card */}
          <Animated.View style={[styles.card, { opacity: fadeAnim, transform: [{ translateY }] }]}>

            {/* Tabs */}
            <View style={styles.tabs}>
              {(['login', 'register', 'guest'] as Tab[]).map(t => (
                <TouchableOpacity
                  key={t}
                  style={[styles.tab, tab === t && styles.tabActive]}
                  onPress={() => setTab(t)}
                  activeOpacity={0.8}
                >
                  <Text style={[styles.tabText, tab === t && styles.tabTextActive]}>
                    {t === 'login' ? 'Sign In' : t === 'register' ? 'Register' : 'Guest'}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>

            {/* Register: Role selector */}
            {tab === 'register' && (
              <>
                <Text style={styles.fieldLabel}>SELECT YOUR ROLE</Text>
                <View style={styles.roleGrid}>
                  {ROLES.map(r => (
                    <TouchableOpacity
                      key={r.key}
                      style={[styles.roleCard, selectedRole === r.key && styles.roleCardActive]}
                      onPress={() => setSelectedRole(r.key as Role)}
                      activeOpacity={0.8}
                    >
                      <Text style={styles.roleIcon}>{r.icon}</Text>
                      <Text style={[styles.roleLabel, selectedRole === r.key && { color: Colors.red }]}>
                        {r.label}
                      </Text>
                      <Text style={styles.roleDesc}>{r.desc}</Text>
                    </TouchableOpacity>
                  ))}
                </View>
                <Field label="FULL NAME" value={form.name} onChange={set('name')}
                  placeholder="Your name" />
              </>
            )}

            {/* Email + Password */}
            {tab !== 'guest' && (
              <>
                <Field label="EMAIL ADDRESS" value={form.email} onChange={set('email')}
                  placeholder="you@example.com" keyboardType="email-address" autoCapitalize="none" />
                <Field label="PASSWORD" value={form.password} onChange={set('password')}
                  placeholder="••••••••" secureTextEntry />
              </>
            )}

            {/* Role-specific fields */}
            {tab === 'register' && selectedRole === 'DOCTOR' && (
              <>
                <Field label="SPECIALIZATION" value={form.specialization}
                  onChange={set('specialization')} placeholder="e.g. Emergency Medicine" />
                <Field label="MEDICAL LICENSE NO." value={form.licenseNumber}
                  onChange={set('licenseNumber')} placeholder="e.g. MCI-12345" />
              </>
            )}
            {tab === 'register' && selectedRole === 'POLICE' && (
              <Field label="BADGE NUMBER" value={form.licenseNumber}
                onChange={set('licenseNumber')} placeholder="e.g. UP-POLICE-9876" />
            )}
            {tab === 'register' && selectedRole === 'AMBULANCE' && (
              <Field label="VEHICLE ID" value={form.vehicleId}
                onChange={set('vehicleId')} placeholder="e.g. AMB-UP-001" />
            )}

            {/* Guest info */}
            {tab === 'guest' && (
              <View style={styles.guestInfo}>
                <Text style={styles.guestIcon}>👤</Text>
                <Text style={styles.guestText}>
                  Continue without an account. You can still send emergency alerts,
                  view nearby help, and receive disaster notifications.
                </Text>
              </View>
            )}

            {/* Submit */}
            <TouchableOpacity
              style={styles.submitBtn}
              onPress={handleSubmit}
              disabled={loading}
              activeOpacity={0.85}
            >
              <LinearGradient
                colors={[Colors.red, Colors.redDark]}
                start={{ x: 0, y: 0 }} end={{ x: 1, y: 0 }}
                style={styles.submitGradient}
              >
                {loading
                  ? <ActivityIndicator color="#fff" />
                  : <Text style={styles.submitText}>
                      {tab === 'login' ? 'Sign In' : tab === 'register' ? 'Create Account' : 'Continue as Guest'}
                    </Text>
                }
              </LinearGradient>
            </TouchableOpacity>

            {/* Demo hint */}
            <Text style={styles.demoHint}>
              Demo: alice@demo.com / demo123
            </Text>
          </Animated.View>
        </ScrollView>
      </KeyboardAvoidingView>
    </View>
  );
}

function Field({ label, value, onChange, placeholder, secureTextEntry, keyboardType, autoCapitalize }: any) {
  return (
    <View style={styles.fieldWrap}>
      <Text style={styles.fieldLabel}>{label}</Text>
      <TextInput
        style={styles.input}
        value={value}
        onChangeText={onChange}
        placeholder={placeholder}
        placeholderTextColor={Colors.textMuted}
        secureTextEntry={secureTextEntry}
        keyboardType={keyboardType || 'default'}
        autoCapitalize={autoCapitalize || 'none'}
        autoCorrect={false}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.bg },
  glow1: {
    position: 'absolute', top: -80, left: -60, width: 300, height: 300,
    borderRadius: 150, backgroundColor: Colors.redGlow, opacity: 0.4,
  },
  glow2: {
    position: 'absolute', bottom: 100, right: -40, width: 200, height: 200,
    borderRadius: 100, backgroundColor: Colors.blueGlow, opacity: 0.3,
  },
  scroll: { flexGrow: 1, justifyContent: 'center', padding: Spacing.md, paddingTop: 60 },

  logoWrap: { alignItems: 'center', marginBottom: Spacing.xl },
  logoIcon: {
    width: 80, height: 80, borderRadius: 24, backgroundColor: Colors.redFaint,
    borderWidth: 1, borderColor: Colors.red + '40',
    justifyContent: 'center', alignItems: 'center', marginBottom: 16, position: 'relative',
  },
  logoPulse: {
    position: 'absolute', width: 80, height: 80, borderRadius: 24,
    borderWidth: 1, borderColor: Colors.red + '60',
  },
  logoSiren: { fontSize: 36 },
  logoTitle: {
    fontSize: 40, color: Colors.text, letterSpacing: 8, fontWeight: '800',
    fontFamily: 'RussoOne-Regular',
  },
  logoSub: { fontSize: 14, color: Colors.textSub, marginTop: 4, letterSpacing: 2 },

  card: {
    backgroundColor: Colors.card, borderRadius: Radius.xl,
    borderWidth: 1, borderColor: Colors.border, padding: Spacing.lg,
  },
  tabs: { flexDirection: 'row', backgroundColor: Colors.surface, borderRadius: Radius.md, padding: 3, marginBottom: 20 },
  tab: { flex: 1, paddingVertical: 10, borderRadius: Radius.md - 2, alignItems: 'center' },
  tabActive: { backgroundColor: Colors.red },
  tabText: { fontSize: 13, color: Colors.textMuted, fontWeight: '600' },
  tabTextActive: { color: Colors.text },

  roleGrid: { flexDirection: 'row', flexWrap: 'wrap', gap: 8, marginBottom: 16 },
  roleCard: {
    width: (width - Spacing.md * 2 - Spacing.lg * 2 - 8) / 2,
    backgroundColor: Colors.surface, borderRadius: Radius.md,
    borderWidth: 1, borderColor: Colors.border, padding: 12, alignItems: 'center',
  },
  roleCardActive: { borderColor: Colors.red, backgroundColor: Colors.redFaint },
  roleIcon: { fontSize: 22, marginBottom: 6 },
  roleLabel: { fontSize: 12, fontWeight: '700', color: Colors.text, textAlign: 'center' },
  roleDesc: { fontSize: 10, color: Colors.textMuted, marginTop: 2, textAlign: 'center' },

  fieldWrap: { marginBottom: 14 },
  fieldLabel: { fontSize: 10, color: Colors.textMuted, fontWeight: '700', letterSpacing: 1.2, marginBottom: 6 },
  input: {
    backgroundColor: Colors.surface, borderRadius: Radius.md, borderWidth: 1,
    borderColor: Colors.border, padding: 14, fontSize: 15, color: Colors.text,
  },

  guestInfo: {
    backgroundColor: Colors.blueFaint, borderRadius: Radius.md, borderWidth: 1,
    borderColor: Colors.blue + '30', padding: 16, alignItems: 'center', marginBottom: 16,
  },
  guestIcon: { fontSize: 32, marginBottom: 10 },
  guestText: { fontSize: 13, color: Colors.textSub, textAlign: 'center', lineHeight: 20 },

  submitBtn: { borderRadius: Radius.md, overflow: 'hidden', marginTop: 4 },
  submitGradient: { paddingVertical: 16, alignItems: 'center', borderRadius: Radius.md },
  submitText: { fontSize: 16, fontWeight: '700', color: Colors.text, letterSpacing: 0.5 },

  demoHint: { textAlign: 'center', fontSize: 11, color: Colors.textMuted, marginTop: 14 },
});
