// AlertsScreen.tsx — Full personal alerts inbox with emergency detail
import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity, RefreshControl, Animated } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { Colors, Font, Space, Radius, Shadow, S, getRisk } from '../../utils/theme';
import { AlertsApi, EmergencyApi } from '../../services/api';

function timeAgo(d: string) { const m=Math.floor((Date.now()-new Date(d).getTime())/60000); return m<1?'Just now':m<60?`${m}m ago`:m<1440?`${Math.floor(m/60)}h ago`:`${Math.floor(m/1440)}d ago`; }

export default function AlertsScreen({ route }: any) {
  const nav = useNavigation<any>();
  const [alerts,     setAlerts]     = useState<any[]>([]);
  const [emergencies,setEmergencies]= useState<any[]>([]);
  const [refreshing, setRefreshing] = useState(false);
  const [tab,        setTab]        = useState<'alerts'|'feed'>('alerts');

  const load = useCallback(async () => {
    try {
      const [a, e] = await Promise.all([AlertsApi.getMine(), EmergencyApi.getAll()]);
      if (Array.isArray(a)) setAlerts(a);
      if (Array.isArray(e)) setEmergencies(e);
    } catch {}
  }, []);

  useEffect(() => { load(); }, [load]);
  const onRefresh = async () => { setRefreshing(true); await load(); setRefreshing(false); };

  const ack = async (id: number) => {
    try { await AlertsApi.acknowledge(id); setAlerts(prev => prev.map(a => a.id===id ? {...a, status:'ACKNOWLEDGED'} : a)); } catch {}
  };

  const unread = alerts.filter(a => a.status === 'SENT').length;

  const renderAlert = ({ item: a }: any) => {
    const r = getRisk(a.emergency?.riskLevel ?? 'LOW');
    const isUnread = a.status === 'SENT';
    return (
      <TouchableOpacity style={[st.card, isUnread && st.cardUnread]} activeOpacity={0.8} onPress={() => isUnread && ack(a.id)}>
        <View style={[st.riskDot, { backgroundColor: r.text }]} />
        <View style={{ flex: 1 }}>
          <Text style={st.cardTitle} numberOfLines={2}>{a.message || `Emergency #${a.emergency?.id} nearby`}</Text>
          <View style={{ flexDirection:'row', alignItems:'center', gap:8, marginTop:4, flexWrap:'wrap' }}>
            {a.emergency?.riskLevel && <View style={[S.pill,{backgroundColor:r.bg,borderColor:r.border}]}><Text style={[S.pillText,{color:r.text}]}>{a.emergency.riskLevel}</Text></View>}
            <Text style={st.meta}>{a.status}</Text>
            {a.distanceMeters && <Text style={st.meta}>📍 {Math.round(a.distanceMeters)}m away</Text>}
            {a.sentAt && <Text style={st.meta}>{timeAgo(a.sentAt)}</Text>}
          </View>
        </View>
        {isUnread && (
          <TouchableOpacity style={st.ackBtn} onPress={() => ack(a.id)} activeOpacity={0.8}>
            <Text style={st.ackTxt}>Read</Text>
          </TouchableOpacity>
        )}
      </TouchableOpacity>
    );
  };

  const renderEmergency = ({ item: e }: any) => {
    const r = getRisk(e.riskLevel);
    return (
      <TouchableOpacity style={st.card} activeOpacity={0.8}>
        <View style={[st.emIcon, { backgroundColor: r.bg }]}><Text style={{ fontSize: 16 }}>{r.icon}</Text></View>
        <View style={{ flex: 1 }}>
          <Text style={st.cardTitle} numberOfLines={2}>{e.message || 'Emergency reported'}</Text>
          <View style={{ flexDirection:'row', alignItems:'center', gap:8, marginTop:4 }}>
            <View style={[S.pill,{backgroundColor:r.bg,borderColor:r.border}]}><Text style={[S.pillText,{color:r.text}]}>{e.riskLevel}</Text></View>
            <Text style={[st.meta, { color: e.status==='ACTIVE'?Colors.red:Colors.textSecond }]}>{e.status}</Text>
            {e.createdAt && <Text style={st.meta}>{timeAgo(e.createdAt)}</Text>}
          </View>
        </View>
      </TouchableOpacity>
    );
  };

  return (
    <SafeAreaView style={S.screen} edges={['top']}>
      {/* Header */}
      <View style={st.hdr}>
        <TouchableOpacity onPress={() => nav.goBack()} style={st.back}><Text style={{ fontSize:16 }}>←</Text></TouchableOpacity>
        <View style={{ flex:1 }}>
          <Text style={st.title}>Emergency Feed</Text>
          {unread > 0 && <Text style={{ fontSize:Font.sizes.xs, color:Colors.red, fontWeight:Font.weight.semibold }}>{unread} unread alert{unread>1?'s':''}</Text>}
        </View>
        <TouchableOpacity onPress={load} style={st.back}><Text style={{ fontSize:16 }}>↺</Text></TouchableOpacity>
      </View>

      {/* Tabs */}
      <View style={st.tabs}>
        {(['alerts','feed'] as const).map(t => (
          <TouchableOpacity key={t} style={[st.tab, tab===t && st.tabActive]} onPress={() => setTab(t)} activeOpacity={0.8}>
            <Text style={[st.tabTxt, tab===t && st.tabTxtActive]}>
              {t==='alerts' ? `🔔 My Alerts${unread>0?` (${unread})`:''}` : `📡 All Emergencies (${emergencies.length})`}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {tab === 'alerts' ? (
        <FlatList data={alerts} keyExtractor={a=>String(a.id)} renderItem={renderAlert}
          contentContainerStyle={{ padding:Space.base, gap:Space.md, paddingBottom:100 }}
          refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={Colors.red}/>}
          showsVerticalScrollIndicator={false}
          ListEmptyComponent={<View style={st.empty}><Text style={{ fontSize:48 }}>🔕</Text><Text style={st.emptyTxt}>No alerts yet</Text><Text style={{ fontSize:Font.sizes.sm, color:Colors.textSecond, textAlign:'center', marginTop:8 }}>You'll receive alerts when emergencies happen near you</Text></View>}
        />
      ) : (
        <FlatList data={emergencies} keyExtractor={e=>String(e.id)} renderItem={renderEmergency}
          contentContainerStyle={{ padding:Space.base, gap:Space.md, paddingBottom:100 }}
          refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={Colors.red}/>}
          showsVerticalScrollIndicator={false}
          ListEmptyComponent={<View style={st.empty}><Text style={{ fontSize:48 }}>✅</Text><Text style={st.emptyTxt}>No emergencies</Text></View>}
        />
      )}
    </SafeAreaView>
  );
}

const st = StyleSheet.create({
  hdr:   { flexDirection:'row', alignItems:'center', paddingHorizontal:Space.base, paddingVertical:Space.md, gap:Space.md },
  back:  { width:36, height:36, borderRadius:18, backgroundColor:Colors.surface3, alignItems:'center', justifyContent:'center' },
  title: { fontSize:Font.sizes.lg, fontWeight:Font.weight.bold, color:Colors.text },
  tabs:  { flexDirection:'row', marginHorizontal:Space.base, marginBottom:Space.base, backgroundColor:Colors.surface3, borderRadius:Radius.base, padding:3 },
  tab:   { flex:1, paddingVertical:8, alignItems:'center', borderRadius:Radius.md },
  tabActive: { backgroundColor:Colors.surface, ...Shadow.xs },
  tabTxt: { fontSize:Font.sizes.xs, fontWeight:Font.weight.medium, color:Colors.textSecond },
  tabTxtActive: { color:Colors.text, fontWeight:Font.weight.semibold },
  card:  { backgroundColor:Colors.surface, borderRadius:Radius.base, borderWidth:1, borderColor:Colors.border, padding:Space.base, flexDirection:'row', alignItems:'flex-start', gap:Space.md, ...Shadow.xs },
  cardUnread: { borderColor:Colors.redBorder, backgroundColor:Colors.redBg },
  riskDot: { width:10, height:10, borderRadius:5, marginTop:4, flexShrink:0 },
  emIcon: { width:36, height:36, borderRadius:18, alignItems:'center', justifyContent:'center', flexShrink:0 },
  cardTitle: { fontSize:Font.sizes.sm, fontWeight:Font.weight.medium, color:Colors.text, lineHeight:18 },
  meta:  { fontSize:Font.sizes.xs, color:Colors.textSecond },
  ackBtn: { paddingHorizontal:Space.sm, paddingVertical:5, borderRadius:Radius.base, backgroundColor:Colors.redBg, borderWidth:1, borderColor:Colors.redBorder },
  ackTxt: { fontSize:Font.sizes.xs, fontWeight:Font.weight.semibold, color:Colors.red },
  empty: { alignItems:'center', paddingVertical:60, paddingHorizontal:Space['2xl'] },
  emptyTxt: { fontSize:Font.sizes.lg, fontWeight:Font.weight.bold, color:Colors.text, marginTop:Space.base },
});
