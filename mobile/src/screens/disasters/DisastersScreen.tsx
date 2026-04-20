// src/screens/disasters/DisastersScreen.tsx — Live Disaster Alerts
import React, { useState, useEffect, useCallback } from 'react';
import {
  View, Text, StyleSheet, ScrollView, TouchableOpacity,
  RefreshControl, Animated,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import LinearGradient from 'react-native-linear-gradient';
import { useNavigation } from '@react-navigation/native';
import Geolocation from 'react-native-geolocation-service';
import { Colors, Font, Space, Radius, Shadow, S } from '../../utils/theme';
import { DisasterApi } from '../../services/api';
import type { DisasterAlert } from '../../services/api';

const ICONS: Record<string, string> = {
  EARTHQUAKE: '🌍', FLOOD: '🌊', FIRE: '🔥', CYCLONE: '🌀',
  TSUNAMI: '🌊', LANDSLIDE: '⛰️', INDUSTRIAL_ACCIDENT: '🏭',
  TERRORIST_ATTACK: '⚠️', PANDEMIC: '🦠', OTHER: '⚡',
};

const SEV_CONFIG: Record<string, { bg: string; text: string; border: string; label: string }> = {
  CRITICAL: { bg: '#FFF0EF', text: Colors.red,       border: '#FFD5D3', label: '🔴 CRITICAL' },
  HIGH:     { bg: '#FFF0EF', text: Colors.redDark,   border: '#FFD5D3', label: '🟠 HIGH' },
  MEDIUM:   { bg: Colors.amberBg,  text: Colors.amberDark, border: Colors.amberBorder, label: '🟡 MEDIUM' },
  LOW:      { bg: Colors.greenBg,  text: Colors.greenDark, border: Colors.greenBorder, label: '🟢 LOW' },
};

export default function DisastersScreen({ route }: any) {
  const nav  = useNavigation<any>();
  const user = route?.params?.user ?? {};

  const [alerts,     setAlerts]     = useState<DisasterAlert[]>([]);
  const [nearby,     setNearby]     = useState<DisasterAlert[]>([]);
  const [refreshing, setRefreshing] = useState(false);
  const [location,   setLocation]   = useState<{lat:number;lon:number}|null>(null);
  const [tab,        setTab]        = useState<'all'|'nearby'>('all');
  const headerAnim = new Animated.Value(0);

  useEffect(() => {
    load();
    Animated.timing(headerAnim, { toValue: 1, duration: 500, useNativeDriver: true }).start();
    Geolocation.getCurrentPosition(
      pos => {
        const loc = { lat: pos.coords.latitude, lon: pos.coords.longitude };
        setLocation(loc);
        loadNearby(loc.lat, loc.lon);
      },
      () => {},
      { enableHighAccuracy: true, timeout: 8000 }
    );
  }, []);

  const load = async () => {
    try {
      const data = await DisasterApi.getActive();
      setAlerts(data);
    } catch {}
  };

  const loadNearby = async (lat: number, lon: number) => {
    try {
      const data = await DisasterApi.getNearby(lat, lon);
      setNearby(data);
    } catch {}
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await load();
    if (location) await loadNearby(location.lat, location.lon);
    setRefreshing(false);
  };

  const displayed = tab === 'nearby' ? nearby : alerts;

  const critical = alerts.filter(a => a.severity === 'CRITICAL' || a.severity === 'HIGH');

  return (
    <SafeAreaView style={S.screen} edges={['top']}>
      {/* Header */}
      <View style={st.header}>
        <TouchableOpacity onPress={() => nav.goBack()} style={st.backBtn}>
          <Text style={{ fontSize: 18, color: Colors.text }}>←</Text>
        </TouchableOpacity>
        <View>
          <Text style={st.title}>Disaster Alerts</Text>
          <Text style={st.subtitle}>Live data · Auto-refreshes every 5min</Text>
        </View>
        <View style={{ width: 36 }} />
      </View>

      {/* Critical banner */}
      {critical.length > 0 && (
        <TouchableOpacity activeOpacity={0.92}>
          <LinearGradient colors={[Colors.red, Colors.redDark]} style={st.criticalBanner} start={{x:0,y:0}} end={{x:1,y:0}}>
            <Text style={{ fontSize: 22 }}>🚨</Text>
            <View style={{ flex: 1 }}>
              <Text style={st.criticalTitle}>{critical.length} Critical Alert{critical.length > 1 ? 's' : ''}</Text>
              <Text style={st.criticalSub} numberOfLines={1}>{critical[0].title}</Text>
            </View>
            <Text style={{ color: 'rgba(255,255,255,0.7)', fontSize: 20 }}>›</Text>
          </LinearGradient>
        </TouchableOpacity>
      )}

      {/* Stats row */}
      <View style={st.statsRow}>
        <View style={[st.statCard, { backgroundColor: Colors.redBg }]}>
          <Text style={[st.statNum, { color: Colors.red }]}>{alerts.length}</Text>
          <Text style={st.statLabel}>Active</Text>
        </View>
        <View style={[st.statCard, { backgroundColor: Colors.amberBg }]}>
          <Text style={[st.statNum, { color: Colors.amberDark }]}>{alerts.filter(a => ['EARTHQUAKE','FLOOD','FIRE'].includes(a.type)).length}</Text>
          <Text style={st.statLabel}>Natural</Text>
        </View>
        <View style={[st.statCard, { backgroundColor: Colors.blueBg }]}>
          <Text style={[st.statNum, { color: Colors.blue }]}>{nearby.length}</Text>
          <Text style={st.statLabel}>Near You</Text>
        </View>
      </View>

      {/* Tabs */}
      <View style={st.tabRow}>
        {(['all', 'nearby'] as const).map(t => (
          <TouchableOpacity key={t} style={[st.tab, tab === t && st.tabActive]} onPress={() => setTab(t)} activeOpacity={0.8}>
            <Text style={[st.tabText, tab === t && st.tabTextActive]}>
              {t === 'all' ? `All Alerts (${alerts.length})` : `Near You (${nearby.length})`}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      <ScrollView
        contentContainerStyle={{ padding: Space.base, gap: Space.md, paddingBottom: Space['3xl'] }}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={Colors.red} />}
        showsVerticalScrollIndicator={false}
      >
        {displayed.length === 0 ? (
          <View style={st.empty}>
            <Text style={{ fontSize: 56 }}>✅</Text>
            <Text style={st.emptyTitle}>No Active Alerts</Text>
            <Text style={st.emptySub}>
              {tab === 'nearby'
                ? location ? 'No disaster alerts affecting your current location.' : 'Enable location to see alerts near you.'
                : 'No active disaster alerts at this time. Data from USGS and GDACS.'}
            </Text>
          </View>
        ) : (
          displayed.map(alert => {
            const sev = SEV_CONFIG[alert.severity] ?? SEV_CONFIG.LOW;
            return (
              <View key={alert.id} style={[st.alertCard, { borderColor: sev.border, borderLeftWidth: 4, borderLeftColor: sev.text }]}>
                {/* Type header */}
                <View style={st.alertHeader}>
                  <View style={[st.alertIconWrap, { backgroundColor: sev.bg }]}>
                    <Text style={{ fontSize: 24 }}>{ICONS[alert.type] ?? '⚡'}</Text>
                  </View>
                  <View style={{ flex: 1 }}>
                    <Text style={st.alertTitle} numberOfLines={2}>{alert.title}</Text>
                    <View style={st.alertTagRow}>
                      <View style={[st.sevPill, { backgroundColor: sev.bg, borderColor: sev.border }]}>
                        <Text style={[st.sevPillText, { color: sev.text }]}>{sev.label}</Text>
                      </View>
                      <View style={st.typePill}>
                        <Text style={st.typePillText}>{alert.type.replace(/_/g, ' ')}</Text>
                      </View>
                    </View>
                  </View>
                </View>

                {/* Description */}
                {alert.description && (
                  <Text style={st.alertDesc} numberOfLines={3}>{alert.description}</Text>
                )}

                {/* Meta row */}
                <View style={st.alertMeta}>
                  {alert.magnitude && (
                    <View style={st.metaChip}>
                      <Text style={st.metaChipText}>M{alert.magnitude.toFixed(1)}</Text>
                    </View>
                  )}
                  {alert.radiusKm && (
                    <View style={st.metaChip}>
                      <Text style={st.metaChipText}>📍 {alert.radiusKm}km radius</Text>
                    </View>
                  )}
                  {alert.epicenterLat && (
                    <View style={st.metaChip}>
                      <Text style={st.metaChipText}>🌐 {alert.epicenterLat.toFixed(2)}, {alert.epicenterLon?.toFixed(2)}</Text>
                    </View>
                  )}
                </View>

                {/* Actions */}
                <View style={st.alertActions}>
                  <TouchableOpacity style={st.actionBtn} onPress={() => nav.navigate('Map', { user })} activeOpacity={0.8}>
                    <Text style={st.actionBtnText}>🗺️ View on Map</Text>
                  </TouchableOpacity>
                  <TouchableOpacity style={[st.actionBtn, { backgroundColor: Colors.redBg, borderColor: Colors.redBorder }]} onPress={() => nav.navigate('SOS', { user })} activeOpacity={0.8}>
                    <Text style={[st.actionBtnText, { color: Colors.red }]}>🆘 Send SOS</Text>
                  </TouchableOpacity>
                </View>
              </View>
            );
          })
        )}

        {/* Source attribution */}
        <View style={st.sourceNote}>
          <Text style={st.sourceText}>📡 Data sourced from USGS Earthquake API and GDACS</Text>
          <Text style={st.sourceText}>Auto-refreshes every 5 minutes · M4.0+ earthquakes</Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const st = StyleSheet.create({
  header:         { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', paddingHorizontal: Space.base, paddingVertical: Space.md, gap: Space.md },
  backBtn:        { width: 36, height: 36, borderRadius: 18, backgroundColor: Colors.surface3, alignItems: 'center', justifyContent: 'center' },
  title:          { fontSize: Font.sizes.lg, fontWeight: Font.weight.bold, color: Colors.text },
  subtitle:       { fontSize: Font.sizes.xs, color: Colors.textSecond },
  criticalBanner: { marginHorizontal: Space.base, marginBottom: Space.md, borderRadius: Radius.lg, padding: Space.base, flexDirection: 'row', alignItems: 'center', gap: Space.md, ...Shadow.red },
  criticalTitle:  { fontSize: Font.sizes.sm, fontWeight: Font.weight.bold, color: Colors.white },
  criticalSub:    { fontSize: Font.sizes.xs, color: 'rgba(255,255,255,0.8)' },
  statsRow:       { flexDirection: 'row', paddingHorizontal: Space.base, gap: Space.md, marginBottom: Space.md },
  statCard:       { flex: 1, borderRadius: Radius.base, padding: Space.md, alignItems: 'center', gap: 2 },
  statNum:        { fontSize: Font.sizes['2xl'], fontWeight: Font.weight.black },
  statLabel:      { fontSize: Font.sizes.xs, color: Colors.textSecond, fontWeight: Font.weight.medium },
  tabRow:         { flexDirection: 'row', marginHorizontal: Space.base, marginBottom: Space.md, backgroundColor: Colors.surface3, borderRadius: Radius.base, padding: 3 },
  tab:            { flex: 1, paddingVertical: Space.sm, borderRadius: Radius.sm, alignItems: 'center' },
  tabActive:      { backgroundColor: Colors.surface, ...Shadow.xs },
  tabText:        { fontSize: Font.sizes.sm, fontWeight: Font.weight.medium, color: Colors.textSecond },
  tabTextActive:  { color: Colors.text, fontWeight: Font.weight.semibold },
  alertCard:      { backgroundColor: Colors.surface, borderRadius: Radius.lg, borderWidth: 1, borderColor: Colors.border, overflow: 'hidden', ...Shadow.sm },
  alertHeader:    { flexDirection: 'row', padding: Space.base, gap: Space.md, alignItems: 'flex-start' },
  alertIconWrap:  { width: 52, height: 52, borderRadius: 26, alignItems: 'center', justifyContent: 'center', flexShrink: 0 },
  alertTitle:     { fontSize: Font.sizes.sm, fontWeight: Font.weight.semibold, color: Colors.text, lineHeight: 20, marginBottom: Space.sm },
  alertTagRow:    { flexDirection: 'row', gap: Space.sm, flexWrap: 'wrap' },
  sevPill:        { paddingHorizontal: Space.sm, paddingVertical: 3, borderRadius: Radius.full, borderWidth: 1 },
  sevPillText:    { fontSize: 10, fontWeight: Font.weight.bold },
  typePill:       { paddingHorizontal: Space.sm, paddingVertical: 3, borderRadius: Radius.full, backgroundColor: Colors.surface3, borderWidth: 1, borderColor: Colors.border },
  typePillText:   { fontSize: 10, color: Colors.textSecond, fontWeight: Font.weight.medium },
  alertDesc:      { fontSize: Font.sizes.sm, color: Colors.textSecond, lineHeight: 20, paddingHorizontal: Space.base, paddingBottom: Space.sm },
  alertMeta:      { flexDirection: 'row', flexWrap: 'wrap', gap: Space.sm, paddingHorizontal: Space.base, paddingBottom: Space.md },
  metaChip:       { backgroundColor: Colors.surface3, borderRadius: Radius.full, paddingHorizontal: Space.sm, paddingVertical: 3 },
  metaChipText:   { fontSize: 10, color: Colors.textSecond, fontFamily: 'monospace' },
  alertActions:   { flexDirection: 'row', borderTopWidth: 1, borderTopColor: Colors.border },
  actionBtn:      { flex: 1, paddingVertical: Space.md, alignItems: 'center', backgroundColor: Colors.surface, borderWidth: 0, borderRightWidth: 0.5, borderColor: Colors.border },
  actionBtnText:  { fontSize: Font.sizes.xs, fontWeight: Font.weight.semibold, color: Colors.blue },
  empty:          { alignItems: 'center', paddingVertical: Space['3xl'] },
  emptyTitle:     { fontSize: Font.sizes.xl, fontWeight: Font.weight.bold, color: Colors.text, marginTop: Space.base },
  emptySub:       { fontSize: Font.sizes.sm, color: Colors.textSecond, textAlign: 'center', marginTop: Space.sm, lineHeight: 20, paddingHorizontal: Space['2xl'] },
  sourceNote:     { alignItems: 'center', paddingVertical: Space.lg, gap: 4 },
  sourceText:     { fontSize: Font.sizes.xs, color: Colors.textTertiary, textAlign: 'center' },
});
