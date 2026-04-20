// src/screens/medical/MedicalScreen.tsx
import React, { useState, useEffect } from 'react';
import {
  View, Text, StyleSheet, ScrollView, TouchableOpacity,
  Alert, ActivityIndicator, StatusBar,
} from 'react-native';
import Geolocation from '@react-native-community/geolocation';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { MMKV } from 'react-native-mmkv';
import { MedicalAPI, LocationAPI } from '../../services/api';
import { Colors, Spacing, Radius } from '../../utils/theme';
import LinearGradient from 'react-native-linear-gradient';

const storage = new MMKV();

type ServiceType = 'doctors' | 'ambulances' | 'police';

export default function MedicalScreen() {
  const insets = useSafeAreaInsets();
  const [tab, setTab]             = useState<ServiceType>('doctors');
  const [services, setServices]   = useState<Record<ServiceType, any[]>>({ doctors: [], ambulances: [], police: [] });
  const [loading, setLoading]     = useState(false);
  const [loc, setLoc]             = useState<{ lat: number; lon: number } | null>(null);
  const [sharing, setSharing]     = useState<Record<number, boolean>>({});
  const deviceId = storage.getString('deviceId') || 'unknown';

  useEffect(() => {
    Geolocation.getCurrentPosition(
      pos => setLoc({ lat: pos.coords.latitude, lon: pos.coords.longitude }),
      () => {},
      { enableHighAccuracy: true, timeout: 8000 }
    );
  }, []);

  useEffect(() => { if (loc) loadServices(); }, [loc]);

  const loadServices = async () => {
    if (!loc) return;
    setLoading(true);
    try {
      const [docs, ambs, pol] = await Promise.all([
        MedicalAPI.nearbyDoctors(loc.lat, loc.lon),
        MedicalAPI.nearbyAmbulances(loc.lat, loc.lon),
        MedicalAPI.nearbyPolice(loc.lat, loc.lon),
      ]);
      setServices({
        doctors:    docs.data || [],
        ambulances: ambs.data || [],
        police:     pol.data || [],
      });
    } catch {}
    setLoading(false);
  };

  const shareLocationWith = async (userId: number, role: string) => {
    if (!loc) { Alert.alert('GPS not available'); return; }
    setSharing(prev => ({ ...prev, [userId]: true }));
    try {
      await LocationAPI.shareWithResponder(deviceId, userId, loc.lat, loc.lon);
      Alert.alert(`📍 Location Shared`, `Your live location is now shared with this ${role.toLowerCase()}. They can track you in real time.`);
    } catch {
      Alert.alert('Error', 'Could not share location');
    }
  };

  const requestConsultation = async (doctorId: number, doctorName: string) => {
    Alert.alert(
      `Request Consultation`,
      `Request a video consultation with ${doctorName}?`,
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Request', onPress: async () => {
            try {
              const res = await MedicalAPI.requestConsultation(doctorId);
              Alert.alert('✅ Request Sent', `Consultation requested. Room: ${res.data.roomId}`);
              // Share location with doctor too
              await shareLocationWith(doctorId, 'Doctor');
            } catch { Alert.alert('Error', 'Could not request consultation'); }
          }
        }
      ]
    );
  };

  const TABS: { key: ServiceType; label: string; icon: string; color: string }[] = [
    { key: 'doctors',    label: 'Doctors',    icon: '🩺', color: Colors.green },
    { key: 'ambulances', label: 'Ambulances', icon: '🚑', color: Colors.red },
    { key: 'police',     label: 'Police',     icon: '👮', color: Colors.blue },
  ];

  const items = services[tab];

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <StatusBar barStyle="light-content" />

      <View style={styles.header}>
        <Text style={styles.title}>🏥 Nearby Emergency Services</Text>
        <Text style={styles.sub}>Within 10km of your location</Text>
      </View>

      {/* Tabs */}
      <View style={styles.tabRow}>
        {TABS.map(t => (
          <TouchableOpacity
            key={t.key}
            style={[styles.tab, tab === t.key && { borderColor: t.color, backgroundColor: t.color + '12' }]}
            onPress={() => setTab(t.key)}
          >
            <Text style={{ fontSize: 18 }}>{t.icon}</Text>
            <Text style={[styles.tabLabel, tab === t.key && { color: t.color }]}>
              {t.label}
            </Text>
            <View style={[styles.tabCount, { backgroundColor: t.color + '20' }]}>
              <Text style={[styles.tabCountText, { color: t.color }]}>
                {services[t.key].length}
              </Text>
            </View>
          </TouchableOpacity>
        ))}
      </View>

      {loading ? (
        <View style={styles.center}><ActivityIndicator size="large" color={Colors.red} /></View>
      ) : !loc ? (
        <View style={styles.center}>
          <Text style={{ fontSize: 36, marginBottom: 12 }}>📍</Text>
          <Text style={styles.emptyText}>Waiting for GPS location…</Text>
        </View>
      ) : items.length === 0 ? (
        <View style={styles.center}>
          <Text style={{ fontSize: 36, marginBottom: 12 }}>🔍</Text>
          <Text style={styles.emptyText}>No {tab} found within 10km</Text>
          <TouchableOpacity style={styles.refreshBtn} onPress={loadServices}>
            <Text style={styles.refreshText}>↺ Refresh</Text>
          </TouchableOpacity>
        </View>
      ) : (
        <ScrollView contentContainerStyle={{ padding: Spacing.md, gap: 10 }}>
          {items.map((item: any) => (
            <ServiceCard
              key={item.id}
              item={item}
              type={tab}
              isSharing={!!sharing[item.id]}
              onShare={() => shareLocationWith(item.id, tab)}
              onConsult={tab === 'doctors' ? () => requestConsultation(item.id, item.name) : undefined}
            />
          ))}
        </ScrollView>
      )}
    </View>
  );
}

function ServiceCard({ item, type, isSharing, onShare, onConsult }: any) {
  const icons: any    = { doctors: '🩺', ambulances: '🚑', police: '👮' };
  const colors: any   = { doctors: Colors.green, ambulances: Colors.red, police: Colors.blue };
  const color = colors[type];

  return (
    <View style={[styles.card, { borderLeftColor: color, borderLeftWidth: 3 }]}>
      <View style={styles.cardTop}>
        <View style={[styles.cardIcon, { backgroundColor: color + '15' }]}>
          <Text style={{ fontSize: 24 }}>{icons[type]}</Text>
        </View>
        <View style={{ flex: 1 }}>
          <Text style={styles.cardName}>{item.name || 'Unknown'}</Text>
          <Text style={styles.cardSub}>
            {item.specialization || item.vehicleId || item.licenseNumber || '—'}
          </Text>
          <View style={styles.cardMeta}>
            {item.isOnDuty  && <StatusBadge label="On Duty"   color={Colors.green} />}
            {item.isAvailable && <StatusBadge label="Available" color={Colors.blue} />}
            {!item.isOnDuty && !item.isAvailable && <StatusBadge label="Off Duty" color={Colors.textMuted} />}
          </View>
        </View>
      </View>
      <View style={styles.cardBtns}>
        <TouchableOpacity
          style={[styles.shareBtn, isSharing && { backgroundColor: Colors.green + '20', borderColor: Colors.green }]}
          onPress={onShare}
        >
          <Text style={[styles.shareBtnText, isSharing && { color: Colors.green }]}>
            {isSharing ? '📡 Sharing Live' : '📍 Share Location'}
          </Text>
        </TouchableOpacity>
        {onConsult && (
          <TouchableOpacity style={styles.consultBtn} onPress={onConsult}>
            <LinearGradient colors={[Colors.green, '#1a8a3e']} style={styles.consultGrad}>
              <Text style={styles.consultText}>🎥 Consult</Text>
            </LinearGradient>
          </TouchableOpacity>
        )}
      </View>
    </View>
  );
}

function StatusBadge({ label, color }: any) {
  return (
    <View style={[styles.badge, { backgroundColor: color + '15', borderColor: color + '30' }]}>
      <View style={[styles.badgeDot, { backgroundColor: color }]} />
      <Text style={[styles.badgeText, { color }]}>{label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.bg },
  header: { paddingHorizontal: Spacing.md, paddingVertical: Spacing.sm },
  title: { fontSize: 20, fontWeight: '700', color: Colors.text },
  sub: { fontSize: 12, color: Colors.textMuted, marginTop: 2 },
  tabRow: { flexDirection: 'row', gap: 8, paddingHorizontal: Spacing.md, marginBottom: Spacing.sm },
  tab: { flex: 1, backgroundColor: Colors.card, borderRadius: Radius.md, borderWidth: 1, borderColor: Colors.border, padding: 10, alignItems: 'center', gap: 4 },
  tabLabel: { fontSize: 11, fontWeight: '600', color: Colors.textMuted },
  tabCount: { borderRadius: Radius.full, paddingHorizontal: 6, paddingVertical: 2 },
  tabCountText: { fontSize: 10, fontWeight: '700' },
  center: { flex: 1, justifyContent: 'center', alignItems: 'center', gap: 8 },
  emptyText: { fontSize: 14, color: Colors.textMuted },
  refreshBtn: { backgroundColor: Colors.card, borderRadius: Radius.md, borderWidth: 1, borderColor: Colors.border, paddingHorizontal: 16, paddingVertical: 10 },
  refreshText: { color: Colors.textSub, fontSize: 13 },
  card: { backgroundColor: Colors.card, borderRadius: Radius.lg, borderWidth: 1, borderColor: Colors.border, padding: Spacing.md, gap: 12 },
  cardTop: { flexDirection: 'row', gap: 12 },
  cardIcon: { width: 52, height: 52, borderRadius: 26, justifyContent: 'center', alignItems: 'center' },
  cardName: { fontSize: 15, fontWeight: '700', color: Colors.text },
  cardSub: { fontSize: 12, color: Colors.textMuted, marginTop: 2 },
  cardMeta: { flexDirection: 'row', gap: 6, marginTop: 6 },
  cardBtns: { flexDirection: 'row', gap: 8 },
  shareBtn: { flex: 1, backgroundColor: Colors.surface, borderRadius: Radius.md, borderWidth: 1, borderColor: Colors.border, padding: 10, alignItems: 'center' },
  shareBtnText: { fontSize: 12, fontWeight: '600', color: Colors.textSub },
  consultBtn: { flex: 1, borderRadius: Radius.md, overflow: 'hidden' },
  consultGrad: { padding: 10, alignItems: 'center' },
  consultText: { fontSize: 12, fontWeight: '700', color: '#fff' },
  badge: { flexDirection: 'row', alignItems: 'center', gap: 4, borderRadius: Radius.full, paddingHorizontal: 8, paddingVertical: 3, borderWidth: 1 },
  badgeDot: { width: 5, height: 5, borderRadius: 3 },
  badgeText: { fontSize: 10, fontWeight: '600' },
});
