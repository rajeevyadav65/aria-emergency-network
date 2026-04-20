// src/screens/map/MapScreen.tsx
import React, { useEffect, useRef, useState, useCallback } from 'react';
import {
  View, Text, StyleSheet, TouchableOpacity, Animated,
  Platform, Dimensions, Alert, StatusBar,
} from 'react-native';
import MapView, {
  Marker, Circle, Polyline, Callout, PROVIDER_GOOGLE,
  AnimatedRegion,
} from 'react-native-maps';
import Geolocation from '@react-native-community/geolocation';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { MMKV } from 'react-native-mmkv';
import { EmergencyAPI, LocationAPI, DisasterAPI } from '../../services/api';
import { WS_URL } from '../../services/api';
import { Colors, Spacing, Radius } from '../../utils/theme';
import Client from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const storage = new MMKV();
const { width, height } = Dimensions.get('window');

const MAP_STYLE = [
  { elementType: 'geometry', stylers: [{ color: '#0a0c14' }] },
  { elementType: 'labels.text.stroke', stylers: [{ color: '#0a0c14' }] },
  { elementType: 'labels.text.fill', stylers: [{ color: '#4a5068' }] },
  { featureType: 'road', elementType: 'geometry', stylers: [{ color: '#1a1e2d' }] },
  { featureType: 'road', elementType: 'geometry.stroke', stylers: [{ color: '#11131e' }] },
  { featureType: 'road.highway', elementType: 'geometry', stylers: [{ color: '#1f2437' }] },
  { featureType: 'water', elementType: 'geometry', stylers: [{ color: '#050a18' }] },
  { featureType: 'poi', stylers: [{ visibility: 'off' }] },
  { featureType: 'transit', stylers: [{ visibility: 'off' }] },
];

type Filter = 'ALL' | 'HIGH' | 'ACTIVE' | 'DISASTERS';

export default function MapScreen() {
  const insets = useSafeAreaInsets();
  const mapRef = useRef<MapView>(null);
  const [myLocation, setMyLocation] = useState<{ lat: number; lon: number } | null>(null);
  const [emergencies, setEmergencies] = useState<any[]>([]);
  const [disasters, setDisasters] = useState<any[]>([]);
  const [responders, setResponders] = useState<any[]>([]);
  const [filter, setFilter] = useState<Filter>('ALL');
  const [shareActive, setShareActive] = useState(false);
  const [tracking, setTracking] = useState<string | null>(null);
  const stompRef = useRef<any>(null);
  const watchRef = useRef<any>(null);
  const deviceId = storage.getString('deviceId') || 'unknown';
  const role = storage.getString('role') || 'USER';

  useEffect(() => {
    startLocationTracking();
    loadData();
    connectWebSocket();
    return () => {
      watchRef.current && Geolocation.clearWatch(watchRef.current);
      stompRef.current?.deactivate();
    };
  }, []);

  const startLocationTracking = () => {
    watchRef.current = Geolocation.watchPosition(
      pos => {
        const { latitude, longitude } = pos.coords;
        setMyLocation({ lat: latitude, lon: longitude });
        if (shareActive) {
          LocationAPI.update(deviceId, latitude, longitude, pos.coords.accuracy);
        }
      },
      err => console.warn(err),
      { enableHighAccuracy: true, distanceFilter: 5, interval: 3000 }
    );
  };

  const loadData = async () => {
    try {
      const [em, dis] = await Promise.all([
        EmergencyAPI.getActive(),
        DisasterAPI.active(),
      ]);
      setEmergencies(em.data || []);
      setDisasters(dis.data?.filter((d: any) => d.alertStatus === 'ACTIVE') || []);
    } catch {}
  };

  const connectWebSocket = () => {
    try {
      const token = storage.getString('token');
      const client = new Client({
        webSocketFactory: () => new SockJS(`${WS_URL}/ws`),
        connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
        onConnect: () => {
          // Subscribe to live emergency updates
          client.subscribe('/topic/emergency/broadcast', (msg) => {
            const data = JSON.parse(msg.body);
            setEmergencies(prev => {
              const exists = prev.find(e => e.id === data.id);
              return exists ? prev.map(e => e.id === data.id ? data : e) : [data, ...prev];
            });
          });
          // Subscribe to responder locations
          client.subscribe(`/topic/responder/${deviceId}`, (msg) => {
            const data = JSON.parse(msg.body);
            if (data.type === 'RESPONDER_LOCATION') {
              setResponders(prev => {
                const exists = prev.find(r => r.responderId === data.responderId);
                return exists ? prev.map(r => r.responderId === data.responderId ? data : r)
                              : [...prev, data];
              });
            }
          });
          // Subscribe to location updates of tracked victim
          if (tracking) {
            client.subscribe(`/topic/location/${tracking}`, (msg) => {
              const data = JSON.parse(msg.body);
              // Update victim marker
            });
          }
        },
        reconnectDelay: 5000,
      });
      client.activate();
      stompRef.current = client;
    } catch {}
  };

  const toggleLocationSharing = async () => {
    if (!myLocation) { Alert.alert('GPS not available'); return; }
    setShareActive(s => !s);
    if (!shareActive) {
      await LocationAPI.update(deviceId, myLocation.lat, myLocation.lon);
    }
  };

  const centerOnMe = () => {
    if (!myLocation) return;
    mapRef.current?.animateToRegion({
      latitude: myLocation.lat, longitude: myLocation.lon,
      latitudeDelta: 0.01, longitudeDelta: 0.01,
    }, 500);
  };

  const filtered = emergencies.filter(e => {
    if (filter === 'HIGH')   return e.riskLevel === 'HIGH';
    if (filter === 'ACTIVE') return e.status === 'ACTIVE';
    return true;
  });

  const riskColor: any = { HIGH: Colors.red, MEDIUM: Colors.amber, LOW: Colors.green };
  const roleIcon: any  = { DOCTOR: '🩺', POLICE: '👮', AMBULANCE: '🚑' };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />

      <MapView
        ref={mapRef}
        provider={PROVIDER_GOOGLE}
        style={StyleSheet.absoluteFillObject}
        customMapStyle={MAP_STYLE}
        showsUserLocation
        showsMyLocationButton={false}
        initialRegion={{
          latitude: myLocation?.lat ?? 27.1767,
          longitude: myLocation?.lon ?? 78.0081,
          latitudeDelta: 0.05,
          longitudeDelta: 0.05,
        }}
      >
        {/* Emergency markers */}
        {filtered.map(e => e.latitude && (
          <React.Fragment key={e.id}>
            <Marker
              coordinate={{ latitude: e.latitude, longitude: e.longitude }}
              title={e.message || 'Emergency'}
              description={`${e.riskLevel} • ${e.status}`}
            >
              <View style={[styles.emMarker, { backgroundColor: riskColor[e.riskLevel] || Colors.textSub }]}>
                <Text style={styles.emMarkerText}>
                  {e.riskLevel === 'HIGH' ? '🚨' : e.riskLevel === 'MEDIUM' ? '⚠️' : '⭕'}
                </Text>
              </View>
            </Marker>
            {/* Radius circle for HIGH risk */}
            {e.riskLevel === 'HIGH' && (
              <Circle
                center={{ latitude: e.latitude, longitude: e.longitude }}
                radius={500}
                fillColor={Colors.red + '15'}
                strokeColor={Colors.red + '40'}
                strokeWidth={1}
              />
            )}
          </React.Fragment>
        ))}

        {/* Disaster circles */}
        {filter !== 'HIGH' && disasters.map(d => d.epicenterLat && (
          <React.Fragment key={d.id}>
            <Marker coordinate={{ latitude: d.epicenterLat, longitude: d.epicenterLon }}>
              <View style={styles.disMarker}>
                <Text>⚡</Text>
              </View>
            </Marker>
            <Circle
              center={{ latitude: d.epicenterLat, longitude: d.epicenterLon }}
              radius={(d.radiusKm || 10) * 1000}
              fillColor={Colors.amber + '10'}
              strokeColor={Colors.amber + '50'}
              strokeWidth={1.5}
            />
          </React.Fragment>
        ))}

        {/* Responder markers */}
        {responders.map(r => r.latitude && (
          <Marker key={r.responderId} coordinate={{ latitude: r.latitude, longitude: r.longitude }}>
            <View style={[styles.respMarker, { backgroundColor: Colors.blue }]}>
              <Text style={{ fontSize: 16 }}>{roleIcon[r.role] || '🚒'}</Text>
            </View>
          </Marker>
        ))}
      </MapView>

      {/* Top controls */}
      <View style={[styles.topControls, { top: insets.top + 8 }]}>
        <View style={styles.filterRow}>
          {(['ALL', 'HIGH', 'ACTIVE', 'DISASTERS'] as Filter[]).map(f => (
            <TouchableOpacity
              key={f}
              style={[styles.filterBtn, filter === f && styles.filterBtnActive]}
              onPress={() => setFilter(f)}
            >
              <Text style={[styles.filterText, filter === f && styles.filterTextActive]}>{f}</Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {/* Bottom controls */}
      <View style={[styles.bottomControls, { bottom: insets.bottom + 90 }]}>
        <View style={styles.statsBar}>
          <View style={styles.statItem}>
            <Text style={[styles.statNum, { color: Colors.red }]}>{filtered.length}</Text>
            <Text style={styles.statLbl}>On Map</Text>
          </View>
          <View style={styles.statItem}>
            <Text style={[styles.statNum, { color: Colors.amber }]}>{disasters.length}</Text>
            <Text style={styles.statLbl}>Disasters</Text>
          </View>
          <View style={styles.statItem}>
            <Text style={[styles.statNum, { color: Colors.blue }]}>{responders.length}</Text>
            <Text style={styles.statLbl}>Responders</Text>
          </View>
        </View>

        <View style={styles.btnRow}>
          <TouchableOpacity style={styles.mapBtn} onPress={centerOnMe}>
            <Text style={styles.mapBtnText}>📍 My Location</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.mapBtn, shareActive && { backgroundColor: Colors.green + '20', borderColor: Colors.green }]}
            onPress={toggleLocationSharing}
          >
            <Text style={[styles.mapBtnText, shareActive && { color: Colors.green }]}>
              {shareActive ? '📡 Sharing Live' : '📡 Share Location'}
            </Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.mapBtn} onPress={loadData}>
            <Text style={styles.mapBtnText}>↺ Refresh</Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.bg },
  topControls: { position: 'absolute', left: 0, right: 0, paddingHorizontal: Spacing.md },
  filterRow: { flexDirection: 'row', gap: 6 },
  filterBtn: { backgroundColor: Colors.overlay, borderRadius: Radius.full, paddingHorizontal: 12, paddingVertical: 6, borderWidth: 1, borderColor: Colors.border },
  filterBtnActive: { backgroundColor: Colors.red, borderColor: Colors.red },
  filterText: { fontSize: 11, color: Colors.textSub, fontWeight: '600' },
  filterTextActive: { color: '#fff' },

  emMarker: { width: 36, height: 36, borderRadius: 18, justifyContent: 'center', alignItems: 'center', borderWidth: 2, borderColor: '#fff' },
  emMarkerText: { fontSize: 16 },
  disMarker: { width: 32, height: 32, borderRadius: 16, backgroundColor: Colors.amber + '30', borderWidth: 1, borderColor: Colors.amber, justifyContent: 'center', alignItems: 'center' },
  respMarker: { width: 40, height: 40, borderRadius: 20, justifyContent: 'center', alignItems: 'center', borderWidth: 2, borderColor: '#fff' },

  bottomControls: { position: 'absolute', left: 0, right: 0, paddingHorizontal: Spacing.md },
  statsBar: { flexDirection: 'row', backgroundColor: Colors.overlay, borderRadius: Radius.lg, borderWidth: 1, borderColor: Colors.border, padding: 12, marginBottom: 8, justifyContent: 'space-around' },
  statItem: { alignItems: 'center', gap: 2 },
  statNum: { fontSize: 20, fontWeight: '800' },
  statLbl: { fontSize: 10, color: Colors.textMuted },
  btnRow: { flexDirection: 'row', gap: 8 },
  mapBtn: { flex: 1, backgroundColor: Colors.overlay, borderRadius: Radius.md, borderWidth: 1, borderColor: Colors.border, padding: 10, alignItems: 'center' },
  mapBtnText: { fontSize: 11, color: Colors.textSub, fontWeight: '600' },
});
