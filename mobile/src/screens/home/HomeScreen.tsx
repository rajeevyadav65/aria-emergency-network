// HomeScreen.tsx — ARIA Dashboard v2
import React, { useEffect, useState, useCallback, useRef } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, RefreshControl, Animated, Platform } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import LinearGradient from 'react-native-linear-gradient';
import { useNavigation } from '@react-navigation/native';
import { Colors, Font, Space, Radius, Shadow, S, getRisk } from '../../utils/theme';
import { EmergencyApi, DisasterApi, AlertsApi, AnalyticsApi, getToken } from '../../services/api';

const ROLE = { USER:{e:'👤',l:'Citizen',c:Colors.blue,bg:Colors.blueBg}, DOCTOR:{e:'🩺',l:'Doctor',c:Colors.green,bg:Colors.greenBg}, POLICE:{e:'👮',l:'Police',c:Colors.purple,bg:Colors.purpleBg}, AMBULANCE:{e:'🚑',l:'Ambulance',c:Colors.amber,bg:Colors.amberBg}, ADMIN:{e:'⚙️',l:'Admin',c:Colors.red,bg:Colors.redBg}, GUEST:{e:'👁',l:'Guest',c:Colors.textTertiary,bg:Colors.surface3} };
const DISASTER_ICONS: Record<string,string> = { EARTHQUAKE:'🌍', FLOOD:'🌊', FIRE:'🔥', CYCLONE:'🌀', TSUNAMI:'🌊', LANDSLIDE:'⛰️', PANDEMIC:'🦠', OTHER:'⚡' };
const getGreeting = () => { const h = new Date().getHours(); return h < 12 ? 'Morning' : h < 17 ? 'Afternoon' : 'Evening'; };
const timeAgo = (d: string) => { const m = Math.floor((Date.now()-new Date(d).getTime())/60000); return m<1?'Just now':m<60?`${m}m ago`:m<1440?`${Math.floor(m/60)}h ago`:`${Math.floor(m/1440)}d ago`; };

export default function HomeScreen({ route }: any) {
  const nav = useNavigation<any>();
  const user = route?.params?.user ?? {};
  const role = user.role ?? 'GUEST';
  const cfg = ROLE[role as keyof typeof ROLE] ?? ROLE.GUEST;
  const [emergencies, setEmergencies] = useState<any[]>([]);
  const [disasters, setDisasters] = useState<any[]>([]);
  const [unread, setUnread] = useState(0);
  const [trend, setTrend] = useState<any>(null);
  const [refreshing, setRefreshing] = useState(false);
  const pulse = useRef(new Animated.Value(1)).current;

  useEffect(() => {
    Animated.loop(Animated.sequence([
      Animated.timing(pulse, { toValue:1.06, duration:900, useNativeDriver:true }),
      Animated.timing(pulse, { toValue:1.00, duration:900, useNativeDriver:true }),
    ])).start();
  }, [pulse]);

  const load = useCallback(async () => {
    try {
      const [em, dis, tr] = await Promise.all([EmergencyApi.getActive(), DisasterApi.getActive(), AnalyticsApi.trend()]);
      setEmergencies(em.slice(0,5)); setDisasters(dis.slice(0,3)); setTrend(tr);
      if (getToken()) { const u = await AlertsApi.getUnread(); setUnread(u.unreadCount ?? 0); }
    } catch {}
  }, []);

  useEffect(() => { load(); }, [load]);
  const onRefresh = async () => { setRefreshing(true); await load(); setRefreshing(false); };

  const QUICK = [
    { id:'map',  e:'🗺️', l:'Live Map',   s:'Map',      bg:Colors.blueBg,   c:Colors.blue   },
    { id:'voice',e:'🎙️', l:'Voice AI',   s:'Voice',    bg:Colors.purpleBg, c:Colors.purple },
    { id:'med',  e:'🩺', l:'Find Help',  s:'Medical',  bg:Colors.greenBg,  c:Colors.green  },
    { id:'chat', e:'💬', l:'ARIA Chat',  s:'Chat',     bg:Colors.amberBg,  c:Colors.amber  },
    { id:'dis',  e:'🌍', l:'Disasters',  s:'Disasters',bg:Colors.redBg,    c:Colors.red    },
    { id:'prof', e:'👤', l:'Profile',    s:'Profile',  bg:Colors.surface3, c:Colors.textSecond },
  ];

  return (
    <SafeAreaView style={S.screen} edges={['top']}>
      <ScrollView contentContainerStyle={{ paddingBottom: 100 }} refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={Colors.red} />} showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View style={st.hdr}>
          <View style={{ flex:1 }}>
            <Text style={st.greet}>Good {getGreeting()}</Text>
            <Text style={st.name} numberOfLines={1}>{user.name ?? 'ARIA User'} {cfg.e}</Text>
          </View>
          <TouchableOpacity style={st.bell} onPress={() => nav.navigate('Alerts')}>
            <Text style={{ fontSize:20 }}>🔔</Text>
            {unread > 0 && <View style={st.badge}><Text style={st.badgeT}>{unread > 9 ? '9+' : unread}</Text></View>}
          </TouchableOpacity>
        </View>

        {/* Role badge */}
        <View style={[st.roleB, { backgroundColor: cfg.bg }]}>
          <Text style={[st.roleT, { color: cfg.c }]}>{cfg.e} {cfg.l} Mode</Text>
          {trend && <Text style={st.stat}>{trend.thisWeek} emergencies this week{trend.changePercent !== 0 ? ` · ${trend.changePercent>0?'↑':'↓'}${Math.abs(trend.changePercent)}%` : ''}</Text>}
        </View>

        {/* SOS Button */}
        <TouchableOpacity activeOpacity={0.85} onPress={() => nav.navigate('SOS', { user })} style={st.sosWrap}>
          <Animated.View style={{ transform: [{ scale: pulse }] }}>
            <LinearGradient colors={['#FF3B30','#FF6B6B']} style={st.sosBtn} start={{x:0,y:0}} end={{x:1,y:1}}>
              <Text style={{ fontSize: 38, marginBottom: 2 }}>🆘</Text>
              <Text style={st.sosT}>EMERGENCY SOS</Text>
              <Text style={st.sosSub}>Tap for immediate help</Text>
            </LinearGradient>
          </Animated.View>
        </TouchableOpacity>

        {/* Quick actions */}
        <View style={st.grid}>
          {QUICK.map(a => (
            <TouchableOpacity key={a.id} style={[st.qCard, { backgroundColor: a.bg }]} onPress={() => nav.navigate(a.s, { user })} activeOpacity={0.78}>
              <Text style={{ fontSize: 26, marginBottom: 4 }}>{a.e}</Text>
              <Text style={[st.qLabel, { color: a.c }]}>{a.l}</Text>
            </TouchableOpacity>
          ))}
        </View>

        {/* Disaster alerts */}
        {disasters.length > 0 && <>
          <Text style={S.sectionHeader}>⚠️ Disaster Alerts</Text>
          {disasters.map(d => (
            <TouchableOpacity key={d.id} style={st.disCard} onPress={() => nav.navigate('Disasters')} activeOpacity={0.8}>
              <Text style={{ fontSize: 22, width: 36 }}>{DISASTER_ICONS[d.type] ?? '⚡'}</Text>
              <View style={{ flex: 1 }}>
                <Text style={st.disTitle} numberOfLines={1}>{d.title}</Text>
                <Text style={st.disSub}>{d.severity} · {d.type.replace(/_/g,' ')}</Text>
              </View>
              <Text style={{ fontSize: 20, color: Colors.amber }}>›</Text>
            </TouchableOpacity>
          ))}
        </>}

        {/* Active emergencies */}
        {emergencies.length > 0 && <>
          <Text style={S.sectionHeader}>🚨 Active Nearby</Text>
          {emergencies.map(e => {
            const r = getRisk(e.riskLevel);
            return (
              <TouchableOpacity key={e.id} style={st.emCard} activeOpacity={0.8} onPress={() => nav.navigate('EmergencyDetail', { emergency: e, user })}>
                <View style={[st.emIcon, { backgroundColor: r.bg }]}><Text style={{ fontSize:18 }}>{r.icon}</Text></View>
                <View style={{ flex:1 }}>
                  <Text style={st.emTitle} numberOfLines={2}>{e.message || 'Emergency reported'}</Text>
                  <View style={{ flexDirection:'row', gap:8, alignItems:'center', marginTop:4 }}>
                    <View style={[st.rPill, { backgroundColor:r.bg, borderColor:r.border }]}><Text style={[st.rPillT, { color:r.text }]}>{e.riskLevel}</Text></View>
                    {e.createdAt && <Text style={st.emTime}>{timeAgo(e.createdAt)}</Text>}
                  </View>
                </View>
                <Text style={{ fontSize:20, color:Colors.textTertiary }}>›</Text>
              </TouchableOpacity>
            );
          })}
          <TouchableOpacity style={{ alignItems:'center', paddingVertical: Space.md }} onPress={() => nav.navigate('Alerts')}>
            <Text style={{ fontSize: Font.sizes.sm, color: Colors.blue, fontWeight: Font.weight.medium }}>See all emergencies →</Text>
          </TouchableOpacity>
        </>}

        {emergencies.length === 0 && disasters.length === 0 && (
          <View style={{ alignItems:'center', paddingVertical: Space['3xl'], paddingHorizontal: Space['2xl'] }}>
            <Text style={{ fontSize: 56 }}>✅</Text>
            <Text style={{ fontSize: Font.sizes.xl, fontWeight: Font.weight.bold, color: Colors.text, marginTop: Space.base }}>All Clear</Text>
            <Text style={{ fontSize: Font.sizes.sm, color: Colors.textSecond, textAlign:'center', marginTop: Space.sm, lineHeight: 20 }}>No active emergencies or disaster alerts in your area</Text>
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const st = StyleSheet.create({
  hdr:   { flexDirection:'row', alignItems:'center', paddingHorizontal:Space.base, paddingTop:Space.md, paddingBottom:Space.sm },
  greet: { fontSize:Font.sizes.sm, color:Colors.textSecond, fontWeight:Font.weight.medium },
  name:  { fontSize:Font.sizes.xl, fontWeight:Font.weight.bold, color:Colors.text },
  bell:  { width:44, height:44, borderRadius:22, backgroundColor:Colors.surface, alignItems:'center', justifyContent:'center', ...Shadow.sm, position:'relative' },
  badge: { position:'absolute', top:2, right:2, backgroundColor:Colors.red, borderRadius:9, minWidth:18, height:18, alignItems:'center', justifyContent:'center', paddingHorizontal:3 },
  badgeT: { fontSize:10, fontWeight:Font.weight.bold, color:Colors.white },
  roleB: { marginHorizontal:Space.base, borderRadius:Radius.base, padding:Space.md, flexDirection:'row', alignItems:'center', justifyContent:'space-between', marginBottom:Space.base },
  roleT: { fontSize:Font.sizes.sm, fontWeight:Font.weight.semibold },
  stat:  { fontSize:Font.sizes.xs, color:Colors.textSecond },
  sosWrap: { paddingHorizontal:Space.base, marginBottom:Space.lg },
  sosBtn: { borderRadius:Radius.xl, height:130, alignItems:'center', justifyContent:'center', gap:2, ...Platform.select({ ios:{shadowColor:'#FF3B30',shadowOffset:{width:0,height:10},shadowOpacity:0.5,shadowRadius:24}, android:{elevation:12} }) },
  sosT:  { fontSize:Font.sizes.xl, fontWeight:Font.weight.black, color:Colors.white, letterSpacing:2 },
  sosSub: { fontSize:Font.sizes.sm, color:'rgba(255,255,255,0.85)' },
  grid:  { flexDirection:'row', flexWrap:'wrap', paddingHorizontal:Space.md, gap:Space.sm, marginBottom:Space.sm },
  qCard: { width:'30.5%', borderRadius:Radius.base, padding:Space.md, alignItems:'center' },
  qLabel: { fontSize:Font.sizes.xs, fontWeight:Font.weight.semibold, textAlign:'center' },
  disCard: { marginHorizontal:Space.base, backgroundColor:Colors.amberBg, borderRadius:Radius.base, borderWidth:1, borderColor:Colors.amberBorder, flexDirection:'row', alignItems:'center', padding:Space.md, marginBottom:Space.sm, gap:Space.sm },
  disTitle: { fontSize:Font.sizes.sm, fontWeight:Font.weight.semibold, color:Colors.amberDark },
  disSub: { fontSize:Font.sizes.xs, color:Colors.amber, marginTop:2 },
  emCard: { marginHorizontal:Space.base, backgroundColor:Colors.surface, borderRadius:Radius.base, borderWidth:1, borderColor:Colors.border, flexDirection:'row', alignItems:'center', padding:Space.md, marginBottom:Space.sm, gap:Space.sm, ...Shadow.xs },
  emIcon: { width:44, height:44, borderRadius:22, alignItems:'center', justifyContent:'center', flexShrink:0 },
  emTitle: { fontSize:Font.sizes.sm, fontWeight:Font.weight.medium, color:Colors.text, lineHeight:18 },
  emTime: { fontSize:Font.sizes.xs, color:Colors.textTertiary },
  rPill: { paddingHorizontal:8, paddingVertical:2, borderRadius:Radius.full, borderWidth:1 },
  rPillT: { fontSize:10, fontWeight:Font.weight.semibold },
});
