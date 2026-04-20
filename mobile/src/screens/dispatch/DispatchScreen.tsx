// src/screens/dispatch/DispatchScreen.tsx
import React, { useState, useEffect, useRef } from 'react';
import {
  View, Text, StyleSheet, ScrollView, TouchableOpacity,
  Alert, Switch, StatusBar, Animated,
} from 'react-native';
import Geolocation from '@react-native-community/geolocation';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { MMKV } from 'react-native-mmkv';
import { DispatchAPI, EmergencyAPI, LocationAPI } from '../../services/api';
import { Colors, Spacing, Radius } from '../../utils/theme';
import LinearGradient from 'react-native-linear-gradient';
import { WS_URL } from '../../services/api';
import Client from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const storage = new MMKV();

export default function DispatchScreen() {
  const insets = useSafeAreaInsets();
  const [onDuty, setOnDuty]         = useState(false);
  const [loading, setLoading]       = useState(false);
  const [emergencies, setEmergencies] = useState<any[]>([]);
  const [responding, setResponding] = useState<Set<number>>(new Set());
  const [myLoc, setMyLoc]           = useState<{ lat: number; lon: number } | null>(null);
  const pulseAnim = useRef(new Animated.Value(1)).current;
  const role      = storage.getString('role') || 'POLICE';
  const deviceId  = storage.getString('deviceId') || 'unknown';
  const userId    = parseInt(storage.getString('userId') || '0');

  const ROLE_INFO: any = {
    DOCTOR:    { icon: '🩺', color: Colors.green, label: 'Medical Officer' },
    POLICE:    { icon: '👮', color: Colors.blue,  label: 'Police Officer' },
    AMBULANCE: { icon: '🚑', color: Colors.red,   label: 'Ambulance Driver' },
  };
  const info = ROLE_INFO[role] || ROLE_INFO.POLICE;

  useEffect(() => {
    loadEmergencies();
    startLocationTracking();
    connectWS();
    pulseLoop();
  }, []);

  const pulseLoop = () => {
    Animated.loop(
      Animated.sequence([
        Animated.timing(pulseAnim, { toValue: 1.08, duration: 800, useNativeDriver: true }),
        Animated.timing(pulseAnim, { toValue: 1, duration: 800, useNativeDriver: true }),
      ])
    ).start();
  };

  const startLocationTracking = () => {
    Geolocation.watchPosition(
      pos => {
        setMyLoc({ lat: pos.coords.latitude, lon: pos.coords.longitude });
        if (onDuty) {
          LocationAPI.update(deviceId, pos.coords.latitude, pos.coords.longitude, pos.coords.accuracy);
        }
      },
      () => {},
      { enableHighAccuracy: true, distanceFilter: 10 }
    );
  };

  const loadEmergencies = async () => {
    try {
      const res = await EmergencyAPI.getActive();
      setEmergencies((res.data || []).filter((e: any) => e.riskLevel === 'HIGH' || role === 'AMBULANCE'));
    } catch {}
  };

  const connectWS = () => {
    try {
      const token = storage.getString('token');
      const client = new Client({
        webSocketFactory: () => new SockJS(`${WS_URL}/ws`),
        connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
        onConnect: () => {
          client.subscribe(`/topic/alerts/${deviceId}`, (msg) => {
            const data = JSON.parse(msg.body);
            if (data.type === 'CONSULTATION_REQUEST') {
              Alert.alert('🩺 New Consultation', `Patient: ${data.patientName}`, [
                { text: 'Accept', onPress: () => acceptConsultation(data.consultationId) },
                { text: 'Later', style: 'cancel' },
              ]);
            }
          });
          client.subscribe('/topic/emergency/broadcast', (msg) => {
            const em = JSON.parse(msg.body);
            setEmergencies(prev => {
              const exists = prev.find(e => e.id === em.id);
              return exists ? prev : [em, ...prev].slice(0, 20);
            });
          });
        },
      });
      client.activate();
    } catch {}
  };

  const toggleDuty = async () => {
    setLoading(true);
    try {
      await DispatchAPI.toggleDuty(!onDuty, !onDuty);
      setOnDuty(d => !d);
    } catch { Alert.alert('Error', 'Could not update duty status'); }
    setLoading(false);
  };

  const respondToEmergency = async (emergency: any) => {
    if (!myLoc) { Alert.alert('GPS required'); return; }

    Alert.prompt(
      '🚨 Respond to Emergency',
      'Enter your estimated arrival time (minutes):',
      async (eta) => {
        if (!eta) return;
        try {
          await DispatchAPI.respond(emergency.id, eta, emergency.reportedByDeviceId || '');
          setResponding(prev => new Set([...prev, emergency.id]));

          // Share my location with the victim
          if (userId && emergency.reportedByDeviceId) {
            await LocationAPI.shareWithResponder(deviceId, userId, myLoc.lat, myLoc.lon);
          }

          Alert.alert('✅ Dispatched', `You are responding. ETA: ${eta} minutes.\nYour location is now shared with the victim.`);
        } catch { Alert.alert('Error', 'Could not respond'); }
      },
      'plain-text', '5'
    );
  };

  const acceptConsultation = async (id: number) => {
    try {
      const { MedicalAPI } = require('../../services/api');
      await MedicalAPI.acceptConsultation(id);
      Alert.alert('✅ Consultation accepted', 'The patient has been notified');
    } catch {}
  };

  const riskColor: any = { HIGH: Colors.red, MEDIUM: Colors.amber, LOW: Colors.green };

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <StatusBar barStyle="light-content" />

      {/* Duty toggle header */}
      <View style={styles.header}>
        <View>
          <Text style={styles.roleLabel}>{info.icon} {info.label}</Text>
          <Text style={styles.subtitle}>Emergency Dispatch</Text>
        </View>
        <View style={styles.dutyRow}>
          <Text style={[styles.dutyText, onDuty && { color: Colors.green }]}>
            {onDuty ? 'On Duty' : 'Off Duty'}
          </Text>
          <Switch
            value={onDuty}
            onValueChange={toggleDuty}
            trackColor={{ false: Colors.border, true: Colors.green + '60' }}
            thumbColor={onDuty ? Colors.green : Colors.textMuted}
            disabled={loading}
          />
        </View>
      </View>

      {/* Status banner */}
      {onDuty && (
        <Animated.View style={[styles.activeBanner, { transform: [{ scale: pulseAnim }] }]}>
          <LinearGradient colors={[Colors.green + '20', Colors.green + '08']} style={styles.bannerGrad}>
            <View style={styles.activeDot} />
            <Text style={styles.activeBannerText}>ACTIVE — Receiving emergency alerts</Text>
          </LinearGradient>
        </Animated.View>
      )}

      {!onDuty && (
        <View style={styles.offBanner}>
          <Text style={styles.offText}>⭕ Go on duty to receive emergency dispatch alerts</Text>
        </View>
      )}

      <ScrollView contentContainerStyle={{ padding: Spacing.md, gap: 10 }}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>🆘 ACTIVE EMERGENCIES</Text>
          <TouchableOpacity onPress={loadEmergencies}>
            <Text style={styles.refresh}>↺ Refresh</Text>
          </TouchableOpacity>
        </View>

        {emergencies.length === 0 ? (
          <View style={styles.empty}>
            <Text style={{ fontSize: 36 }}>✅</Text>
            <Text style={styles.emptyText}>No active emergencies</Text>
          </View>
        ) : (
          emergencies.map((em: any) => (
            <View key={em.id} style={[styles.emCard, { borderLeftColor: riskColor[em.riskLevel] || Colors.textSub }]}>
              <View style={styles.emTop}>
                <View style={[styles.riskDot, { backgroundColor: riskColor[em.riskLevel] || Colors.textSub }]} />
                <Text style={styles.emId}>#{em.id}</Text>
                <View style={[styles.riskBadge, { backgroundColor: (riskColor[em.riskLevel] || Colors.textSub) + '20' }]}>
                  <Text style={[styles.riskText, { color: riskColor[em.riskLevel] || Colors.textSub }]}>{em.riskLevel}</Text>
                </View>
              </View>
              <Text style={styles.emMsg} numberOfLines={2}>{em.message || 'Emergency reported'}</Text>
              {em.aiAction && <Text style={styles.emAi}>🤖 {em.aiAction}</Text>}
              {em.latitude && (
                <Text style={styles.emLoc}>📍 {parseFloat(em.latitude).toFixed(4)}, {parseFloat(em.longitude).toFixed(4)}</Text>
              )}
              <TouchableOpacity
                style={[styles.respondBtn, responding.has(em.id) && styles.respondBtnDone]}
                onPress={!responding.has(em.id) ? () => respondToEmergency(em) : undefined}
              >
                <Text style={styles.respondBtnText}>
                  {responding.has(em.id) ? '✓ Responding' : `${info.icon} Respond`}
                </Text>
              </TouchableOpacity>
            </View>
          ))
        )}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.bg },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: Spacing.md, paddingVertical: Spacing.sm, borderBottomWidth: 1, borderBottomColor: Colors.border },
  roleLabel: { fontSize: 18, fontWeight: '700', color: Colors.text },
  subtitle: { fontSize: 12, color: Colors.textMuted, marginTop: 2 },
  dutyRow: { flexDirection: 'row', alignItems: 'center', gap: 10 },
  dutyText: { fontSize: 13, fontWeight: '600', color: Colors.textMuted },
  activeBanner: { marginHorizontal: Spacing.md, marginTop: Spacing.sm, borderRadius: Radius.md, overflow: 'hidden' },
  bannerGrad: { flexDirection: 'row', alignItems: 'center', gap: 10, padding: 14, borderRadius: Radius.md, borderWidth: 1, borderColor: Colors.green + '30' },
  activeDot: { width: 8, height: 8, borderRadius: 4, backgroundColor: Colors.green },
  activeBannerText: { fontSize: 13, color: Colors.green, fontWeight: '600' },
  offBanner: { margin: Spacing.md, backgroundColor: Colors.card, borderRadius: Radius.md, borderWidth: 1, borderColor: Colors.border, padding: 14 },
  offText: { fontSize: 13, color: Colors.textMuted, textAlign: 'center' },
  sectionHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 4 },
  sectionTitle: { fontSize: 11, fontWeight: '700', color: Colors.textMuted, letterSpacing: 1.2 },
  refresh: { fontSize: 13, color: Colors.blue },
  empty: { alignItems: 'center', gap: 10, padding: 32 },
  emptyText: { fontSize: 14, color: Colors.textMuted },
  emCard: { backgroundColor: Colors.card, borderRadius: Radius.lg, borderWidth: 1, borderColor: Colors.border, borderLeftWidth: 3, padding: Spacing.md, gap: 8 },
  emTop: { flexDirection: 'row', alignItems: 'center', gap: 8 },
  riskDot: { width: 8, height: 8, borderRadius: 4 },
  emId: { fontSize: 12, color: Colors.textMuted, fontFamily: 'monospace' },
  riskBadge: { paddingHorizontal: 8, paddingVertical: 2, borderRadius: Radius.full },
  riskText: { fontSize: 10, fontWeight: '700' },
  emMsg: { fontSize: 14, fontWeight: '600', color: Colors.text },
  emAi: { fontSize: 12, color: Colors.textSub, backgroundColor: Colors.surface, borderRadius: Radius.sm, padding: 8 },
  emLoc: { fontSize: 11, color: Colors.textMuted, fontFamily: 'monospace' },
  respondBtn: { backgroundColor: Colors.red, borderRadius: Radius.md, padding: 12, alignItems: 'center' },
  respondBtnDone: { backgroundColor: Colors.green },
  respondBtnText: { color: '#fff', fontWeight: '700', fontSize: 14 },
});
