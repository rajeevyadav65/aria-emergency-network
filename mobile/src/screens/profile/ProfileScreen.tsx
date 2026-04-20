// src/screens/profile/ProfileScreen.tsx — User Profile & Settings
import React, { useState, useEffect } from 'react';
import {
  View, Text, StyleSheet, ScrollView, TouchableOpacity,
  Switch, Alert, TextInput, ActivityIndicator,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import AsyncStorage from '@react-native-async-storage/async-storage';
import LinearGradient from 'react-native-linear-gradient';
import { useNavigation } from '@react-navigation/native';
import { Colors, Font, Space, Radius, Shadow, S } from '../../utils/theme';
import { DispatchApi, VoiceApi, clearAuth, getToken } from '../../services/api';

const ROLE_INFO: Record<string, { emoji: string; label: string; color: string; bg: string }> = {
  USER:      { emoji: '👤', label: 'Normal User',   color: Colors.blue,        bg: Colors.blueBg },
  DOCTOR:    { emoji: '🩺', label: 'Doctor',        color: Colors.green,       bg: Colors.greenBg },
  POLICE:    { emoji: '👮', label: 'Police Officer', color: Colors.purple,     bg: Colors.purpleBg },
  AMBULANCE: { emoji: '🚑', label: 'Ambulance',     color: Colors.amber,       bg: Colors.amberBg },
  ADMIN:     { emoji: '⚙️', label: 'Admin',         color: Colors.red,         bg: Colors.redBg },
  GUEST:     { emoji: '👁',  label: 'Guest',         color: Colors.textSecond,  bg: Colors.surface3 },
};

export default function ProfileScreen({ route }: any) {
  const nav = useNavigation<any>();
  const [user,     setUser]     = useState<any>(route?.params?.user ?? {});
  const [onDuty,   setOnDuty]   = useState(false);
  const [saving,   setSaving]   = useState(false);
  const [keyStatus, setKeyStatus] = useState<any>(null);
  const role = user.role ?? 'GUEST';
  const cfg  = ROLE_INFO[role] ?? ROLE_INFO.GUEST;
  const isResponder = ['DOCTOR', 'POLICE', 'AMBULANCE'].includes(role);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const stored = await AsyncStorage.getItem('user');
      if (stored) setUser(JSON.parse(stored));
      if (getToken()) {
        const ks = await VoiceApi.getStatus();
        setKeyStatus(ks);
      }
    } catch {}
  };

  const toggleDuty = async (val: boolean) => {
    setSaving(true);
    try {
      await DispatchApi.setDuty(val, val);
      setOnDuty(val);
    } catch { Alert.alert('Error', 'Could not update duty status'); }
    setSaving(false);
  };

  const logout = () => {
    Alert.alert(
      'Sign Out',
      'Are you sure you want to sign out?',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Sign Out',
          style: 'destructive',
          onPress: async () => {
            clearAuth();
            await AsyncStorage.multiRemove(['token', 'user', 'deviceId']);
            nav.reset({ index: 0, routes: [{ name: 'Auth' }] });
          },
        },
      ]
    );
  };

  const MENU_ITEMS = [
    { emoji: '🗺️', label: 'Live Map',        action: () => nav.navigate('Map') },
    { emoji: '🔔', label: 'My Alerts',        action: () => nav.navigate('Alerts') },
    { emoji: '🌍', label: 'Disaster Alerts',  action: () => nav.navigate('Disasters') },
    { emoji: '💬', label: 'ARIA Chat',        action: () => nav.navigate('Chat') },
    { emoji: '🏥', label: 'Find Help Nearby', action: () => nav.navigate('Medical') },
  ];

  return (
    <SafeAreaView style={S.screen} edges={['top']}>
      <ScrollView contentContainerStyle={{ paddingBottom: 100 }} showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View style={st.header}>
          <TouchableOpacity onPress={() => nav.goBack()} style={st.backBtn}>
            <Text style={{ fontSize: 16 }}>←</Text>
          </TouchableOpacity>
          <Text style={st.title}>Profile</Text>
          <View style={{ width: 36 }} />
        </View>

        {/* Avatar card */}
        <LinearGradient
          colors={[Colors.red, Colors.redDark]}
          style={st.avatarCard}
          start={{ x: 0, y: 0 }} end={{ x: 1, y: 1 }}
        >
          <View style={st.avatarCircle}>
            <Text style={{ fontSize: 36 }}>{cfg.emoji}</Text>
          </View>
          <Text style={st.avatarName}>{user.name || 'ARIA User'}</Text>
          <Text style={st.avatarEmail}>{user.email || 'Guest session'}</Text>
          <View style={st.roleBadge}>
            <Text style={st.roleBadgeText}>{cfg.emoji} {cfg.label}</Text>
          </View>
        </LinearGradient>

        {/* Duty toggle (responders only) */}
        {isResponder && (
          <>
            <Text style={S.sectionHeader}>Duty Status</Text>
            <View style={[S.card, { marginHorizontal: Space.base }]}>
              <View style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }}>
                <View style={{ flex: 1 }}>
                  <Text style={{ fontSize: Font.sizes.md, fontWeight: Font.weight.semibold, color: Colors.text }}>
                    {onDuty ? '🟢 On Duty' : '⚫ Off Duty'}
                  </Text>
                  <Text style={{ fontSize: Font.sizes.sm, color: Colors.textSecond, marginTop: 4 }}>
                    {onDuty
                      ? 'You are receiving emergency dispatch alerts'
                      : 'Toggle to start receiving alerts'}
                  </Text>
                </View>
                {saving
                  ? <ActivityIndicator color={Colors.green} />
                  : <Switch
                      value={onDuty}
                      onValueChange={toggleDuty}
                      trackColor={{ false: Colors.surface3, true: Colors.green }}
                      thumbColor={Colors.white}
                    />
                }
              </View>
            </View>
          </>
        )}

        {/* Voice keyword status */}
        {getToken() && (
          <>
            <Text style={S.sectionHeader}>Security</Text>
            <TouchableOpacity
              style={[S.card, { marginHorizontal: Space.base, flexDirection: 'row', alignItems: 'center', gap: Space.md }]}
              onPress={() => nav.navigate('Voice', { user })}
              activeOpacity={0.8}
            >
              <View style={{ width: 44, height: 44, borderRadius: 22, backgroundColor: Colors.purpleBg, alignItems: 'center', justifyContent: 'center' }}>
                <Text style={{ fontSize: 22 }}>🔑</Text>
              </View>
              <View style={{ flex: 1 }}>
                <Text style={{ fontSize: Font.sizes.sm, fontWeight: Font.weight.semibold, color: Colors.text }}>
                  Secret Voice Trigger
                </Text>
                <Text style={{ fontSize: Font.sizes.xs, color: keyStatus?.active ? Colors.green : Colors.textSecond, marginTop: 2 }}>
                  {keyStatus?.active
                    ? `Active · "${keyStatus.hint || 'hint hidden'}" · ${keyStatus.triggerCount}x triggered`
                    : 'Not set — tap to configure'}
                </Text>
              </View>
              <Text style={{ fontSize: 18, color: Colors.textTertiary }}>›</Text>
            </TouchableOpacity>
          </>
        )}

        {/* Navigation menu */}
        <Text style={S.sectionHeader}>Navigate</Text>
        <View style={{ marginHorizontal: Space.base, borderRadius: Radius.lg, overflow: 'hidden', borderWidth: 1, borderColor: Colors.border }}>
          {MENU_ITEMS.map((item, idx) => (
            <React.Fragment key={item.label}>
              <TouchableOpacity
                style={{ flexDirection: 'row', alignItems: 'center', padding: Space.base, backgroundColor: Colors.surface, gap: Space.md }}
                onPress={item.action}
                activeOpacity={0.7}
              >
                <Text style={{ fontSize: 20, width: 30 }}>{item.emoji}</Text>
                <Text style={{ flex: 1, fontSize: Font.sizes.sm, fontWeight: Font.weight.medium, color: Colors.text }}>{item.label}</Text>
                <Text style={{ fontSize: 18, color: Colors.textTertiary }}>›</Text>
              </TouchableOpacity>
              {idx < MENU_ITEMS.length - 1 && <View style={S.separator} />}
            </React.Fragment>
          ))}
        </View>

        {/* App info */}
        <Text style={S.sectionHeader}>About</Text>
        <View style={[S.card, { marginHorizontal: Space.base }]}>
          {[
            ['App', 'ARIA Emergency Network'],
            ['Version', '2.0.0'],
            ['Platform', 'iOS & Android'],
            ['Backend', 'Spring Boot 3.2'],
          ].map(([label, value]) => (
            <View key={label} style={{ flexDirection: 'row', justifyContent: 'space-between', paddingVertical: Space.sm, borderBottomWidth: 1, borderBottomColor: Colors.border }}>
              <Text style={{ fontSize: Font.sizes.sm, color: Colors.textSecond }}>{label}</Text>
              <Text style={{ fontSize: Font.sizes.sm, color: Colors.text, fontWeight: Font.weight.medium }}>{value}</Text>
            </View>
          ))}
        </View>

        {/* Sign out */}
        <View style={{ paddingHorizontal: Space.base, marginTop: Space.lg }}>
          <TouchableOpacity onPress={logout} style={st.logoutBtn} activeOpacity={0.8}>
            <Text style={st.logoutText}>Sign Out</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const st = StyleSheet.create({
  header:       { flexDirection:'row', alignItems:'center', justifyContent:'space-between', paddingHorizontal:Space.base, paddingVertical:Space.md },
  backBtn:      { width:36, height:36, borderRadius:18, backgroundColor:Colors.surface3, alignItems:'center', justifyContent:'center' },
  title:        { fontSize:Font.sizes.lg, fontWeight:Font.weight.bold, color:Colors.text },
  avatarCard:   { marginHorizontal:Space.base, borderRadius:Radius.xl, padding:Space['2xl'], alignItems:'center', gap:Space.md, ...Shadow.lg },
  avatarCircle: { width:80, height:80, borderRadius:40, backgroundColor:'rgba(255,255,255,0.2)', alignItems:'center', justifyContent:'center' },
  avatarName:   { fontSize:Font.sizes.xl, fontWeight:Font.weight.bold, color:Colors.white },
  avatarEmail:  { fontSize:Font.sizes.sm, color:'rgba(255,255,255,0.8)' },
  roleBadge:    { paddingHorizontal:Space.base, paddingVertical:6, borderRadius:Radius.full, backgroundColor:'rgba(255,255,255,0.25)' },
  roleBadgeText:{ fontSize:Font.sizes.sm, fontWeight:Font.weight.semibold, color:Colors.white },
  logoutBtn:    { height:52, borderRadius:Radius.base, backgroundColor:Colors.redBg, borderWidth:1.5, borderColor:Colors.redBorder, alignItems:'center', justifyContent:'center' },
  logoutText:   { fontSize:Font.sizes.md, fontWeight:Font.weight.semibold, color:Colors.red },
});
