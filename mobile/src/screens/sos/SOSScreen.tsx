// SOSScreen.tsx — Emergency SOS + Live Location Sharing with Police & Ambulance
// Full implementation with 3-second countdown, GPS lock, and role-targeted location sharing
import React, { useState, useEffect, useRef, useCallback } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, Vibration, Alert, Platform, Animated, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Geolocation from 'react-native-geolocation-service';
import LinearGradient from 'react-native-linear-gradient';
import { useNavigation } from '@react-navigation/native';
import { Colors, Font, Space, Radius, Shadow, S } from '../../utils/theme';
import { EmergencyApi, LocationApi, MedicalApi, getDeviceId } from '../../services/api';

export default function SOSScreen({ route }: any) {
  const nav  = useNavigation<any>();
  const user = route?.params?.user ?? {};
  const [location,  setLocation]  = useState<{lat:number;lon:number}|null>(null);
  const [locLoad,   setLocLoad]   = useState(true);
  const [sending,   setSending]   = useState(false);
  const [sent,      setSent]      = useState(false);
  const [result,    setResult]    = useState<any>(null);
  const [sharing,   setSharing]   = useState(false);
  const [sessions,  setSessions]  = useState<string[]>([]);
  const [nearby,    setNearby]    = useState({ police:0, ambulance:0 });
  const [countdown, setCountdown] = useState<number|null>(null);
  const cTimer = useRef<any>(null);
  const pulse  = useRef(new Animated.Value(1)).current;

  useEffect(() => {
    Geolocation.getCurrentPosition(
      pos => { setLocation({ lat:pos.coords.latitude, lon:pos.coords.longitude }); setLocLoad(false); loadNearby(pos.coords.latitude, pos.coords.longitude); },
      () => setLocLoad(false),
      { enableHighAccuracy:true, timeout:10000 }
    );
  }, []);

  useEffect(() => {
    if (sending) Animated.loop(Animated.sequence([Animated.timing(pulse,{toValue:1.1,duration:600,useNativeDriver:true}),Animated.timing(pulse,{toValue:1.0,duration:600,useNativeDriver:true})])).start();
    else pulse.setValue(1);
  }, [sending]);

  const loadNearby = async (lat:number, lon:number) => {
    try {
      const [p, a] = await Promise.all([MedicalApi.nearbyPolice(lat,lon), MedicalApi.nearbyAmbulances(lat,lon)]);
      setNearby({ police:p.length, ambulance:a.length });
    } catch {}
  };

  const startCountdown = () => {
    if (sending||sent||countdown!==null) return;
    Vibration.vibrate([0,100,100,100]);
    setCountdown(3);
    cTimer.current = setInterval(() => {
      setCountdown(prev => { if(!prev||prev<=1){ clearInterval(cTimer.current); sendSOS(); return null; } Vibration.vibrate(50); return prev-1; });
    }, 1000);
  };

  const cancelCountdown = () => {
    clearInterval(cTimer.current);
    setCountdown(null);
    Vibration.cancel();
  };

  const sendSOS = async () => {
    if (!location) { Alert.alert('Location Required','Please wait for GPS lock'); return; }
    setSending(true);
    Vibration.vibrate([0,200,100,200,100,300]);
    try {
      const res = await EmergencyApi.report({ latitude:location.lat, longitude:location.lon, movement:'UNKNOWN', userResponse:'NEED_HELP', deviceId: getDeviceId()??'mobile' });
      setResult(res); setSent(true);
      await shareWithTarget('AMBULANCE', res.emergencyId);
    } catch(e:any) {
      Alert.alert('Failed', e?.response?.data?.error??'Could not send SOS. Check connection.');
    } finally { setSending(false); }
  };

  const shareWithTarget = useCallback(async (target:'POLICE'|'AMBULANCE', emergencyId?:number) => {
    if (!location) return;
    setSharing(true);
    try {
      const fn = target==='POLICE' ? LocationApi.shareWithPolice : LocationApi.shareWithAmbulance;
      const session = await fn(location.lat, location.lon, emergencyId);
      if (session?.sessionId) setSessions(p => [...p, session.sessionId]);
    } catch {}
    setSharing(false);
  }, [location]);

  const stopSession = async (sid:string) => {
    try { await LocationApi.stopSharing(sid); setSessions(p => p.filter(s=>s!==sid)); } catch {}
  };

  if (sent && result) {
    return (
      <SafeAreaView style={[S.screen,{backgroundColor:Colors.greenBg}]} edges={['top','bottom']}>
        <ScrollView contentContainerStyle={{padding:Space.base,paddingBottom:100}} showsVerticalScrollIndicator={false}>
          <View style={{alignItems:'center',paddingVertical:Space['2xl']}}>
            <Text style={{fontSize:64}}>✅</Text>
            <Text style={st.sentTitle}>SOS Sent!</Text>
            <Text style={st.sentSub}>Help is on the way</Text>
          </View>
          <View style={[S.card,{marginBottom:Space.base}]}>
            {[['Emergency #', `#${result.emergencyId}`], ['Risk Level', result.riskLevel], ['Nearby Alerted', `${result.nearbyUsersAlerted} users`]].map(([l,v])=>(
              <View key={l} style={{flexDirection:'row',justifyContent:'space-between',paddingVertical:Space.sm,borderBottomWidth:1,borderBottomColor:Colors.border}}>
                <Text style={{fontSize:Font.sizes.sm,color:Colors.textSecond}}>{l}</Text>
                <Text style={{fontSize:Font.sizes.sm,fontWeight:Font.weight.bold,color:Colors.text}}>{v}</Text>
              </View>
            ))}
            {result.aiAction&&<View style={{marginTop:Space.md,padding:Space.md,backgroundColor:Colors.blueBg,borderRadius:Radius.base,borderWidth:1,borderColor:Colors.blueBorder}}>
              <Text style={{fontSize:Font.sizes.xs,color:Colors.textSecond,marginBottom:4}}>🤖 AI Assessment</Text>
              <Text style={{fontSize:Font.sizes.sm,color:Colors.blueDark}}>{result.aiAction}</Text>
            </View>}
          </View>
          <Text style={[S.sectionHeader,{marginHorizontal:0}]}>📍 Share Live Location</Text>
          <View style={st.shareRow}>
            <TouchableOpacity style={[st.shareCard,{backgroundColor:Colors.purpleBg,borderColor:Colors.purpleBorder}]} onPress={()=>shareWithTarget('POLICE',result.emergencyId)} disabled={sharing} activeOpacity={0.8}>
              <Text style={{fontSize:28}}>👮</Text>
              <Text style={[st.shareLabel,{color:Colors.purple}]}>Police</Text>
              {nearby.police>0&&<Text style={st.shareCnt}>{nearby.police} nearby</Text>}
            </TouchableOpacity>
            <TouchableOpacity style={[st.shareCard,{backgroundColor:Colors.redBg,borderColor:Colors.redBorder}]} onPress={()=>shareWithTarget('AMBULANCE',result.emergencyId)} disabled={sharing} activeOpacity={0.8}>
              <Text style={{fontSize:28}}>🚑</Text>
              <Text style={[st.shareLabel,{color:Colors.red}]}>Ambulance</Text>
              {nearby.ambulance>0&&<Text style={st.shareCnt}>{nearby.ambulance} nearby</Text>}
            </TouchableOpacity>
          </View>
          {sessions.length>0&&<View style={st.activeShare}><View style={st.activeDot}/><Text style={st.activeTxt}>Live sharing with {sessions.length} unit{sessions.length>1?'s':''}</Text><TouchableOpacity onPress={()=>sessions.forEach(stopSession)}><Text style={{fontSize:Font.sizes.xs,color:Colors.red,fontWeight:Font.weight.semibold}}>Stop all</Text></TouchableOpacity></View>}
          <TouchableOpacity onPress={()=>Alert.alert('Emergency Call','Call 112?',[{text:'Cancel',style:'cancel'},{text:'📞 Call',style:'destructive',onPress:()=>{}}])} style={{marginBottom:Space.sm}} activeOpacity={0.8}>
            <LinearGradient colors={[Colors.green,Colors.greenDark]} style={st.callGrad} start={{x:0,y:0}} end={{x:1,y:1}}>
              <Text style={{fontSize:22}}>📞</Text>
              <Text style={st.callTxt}>Call 112 (Emergency Services)</Text>
            </LinearGradient>
          </TouchableOpacity>
          <TouchableOpacity style={st.doneBtn} onPress={()=>nav.goBack()} activeOpacity={0.8}>
            <Text style={st.doneTxt}>Done — Back to Home</Text>
          </TouchableOpacity>
        </ScrollView>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={[S.screen,{backgroundColor:Colors.redBg}]} edges={['top']}>
      <ScrollView contentContainerStyle={{paddingBottom:100}} showsVerticalScrollIndicator={false}>
        <View style={st.hdr}>
          <TouchableOpacity onPress={()=>nav.goBack()} style={st.backBtn}><Text style={{fontSize:16,color:Colors.text}}>✕</Text></TouchableOpacity>
          <Text style={st.hdrTitle}>Emergency SOS</Text>
          <View style={{width:40}}/>
        </View>
        <View style={[st.locCard,{borderColor:location?Colors.greenBorder:Colors.amberBorder,backgroundColor:location?Colors.greenBg:Colors.amberBg}]}>
          <Text style={{fontSize:16}}>{location?'📍':'🔄'}</Text>
          <View style={{flex:1}}>
            <Text style={{fontSize:Font.sizes.sm,fontWeight:Font.weight.semibold,color:location?Colors.greenDark:Colors.amberDark}}>{locLoad?'Getting GPS…':location?'Location locked ✓':'GPS unavailable'}</Text>
            {location&&<Text style={{fontSize:Font.sizes.xs,color:Colors.textSecond,fontFamily:'monospace'}}>{location.lat.toFixed(5)}, {location.lon.toFixed(5)}</Text>}
          </View>
        </View>
        <View style={{alignItems:'center',paddingVertical:Space.xl}}>
          {countdown!==null?(
            <TouchableOpacity onPress={cancelCountdown} style={st.cancelOuter} activeOpacity={0.9}>
              <Text style={st.cntNum}>{countdown}</Text>
              <Text style={{fontSize:Font.sizes.sm,color:Colors.amberDark}}>Tap to cancel</Text>
            </TouchableOpacity>
          ):(
            <Animated.View style={{transform:[{scale:pulse}]}}>
              <TouchableOpacity onPress={startCountdown} onLongPress={sendSOS} delayLongPress={500} activeOpacity={0.85} style={st.sosOuter} disabled={sending}>
                <LinearGradient colors={['#FF3B30','#CC2A20']} style={st.sosInner} start={{x:0,y:0}} end={{x:1,y:1}}>
                  {sending?<ActivityIndicator color={Colors.white} size="large"/>:<>
                    <Text style={{fontSize:52}}>🆘</Text>
                    <Text style={st.sosTxt}>SOS</Text>
                    <Text style={st.sosHint}>Tap = 3s · Hold = instant</Text>
                  </>}
                </LinearGradient>
              </TouchableOpacity>
            </Animated.View>
          )}
        </View>
        <TouchableOpacity onPress={()=>Alert.alert('Emergency Call','Call 112?',[{text:'Cancel',style:'cancel'},{text:'📞 Call',style:'destructive',onPress:()=>{}}])} style={{marginHorizontal:Space.base,marginBottom:Space.base}} activeOpacity={0.8}>
          <LinearGradient colors={[Colors.green,Colors.greenDark]} style={st.callGrad} start={{x:0,y:0}} end={{x:1,y:1}}>
            <Text style={{fontSize:22}}>📞</Text><Text style={st.callTxt}>Call 112 (Emergency)</Text>
          </LinearGradient>
        </TouchableOpacity>
        {location&&<>
          <Text style={S.sectionHeader}>📍 Pre-share Location</Text>
          <View style={st.shareRow}>
            <TouchableOpacity style={[st.shareCard,{backgroundColor:Colors.purpleBg,borderColor:Colors.purpleBorder}]} onPress={()=>shareWithTarget('POLICE')} disabled={sharing} activeOpacity={0.8}>
              <Text style={{fontSize:28}}>👮</Text>
              <Text style={[st.shareLabel,{color:Colors.purple}]}>Police</Text>
              {nearby.police>0&&<Text style={st.shareCnt}>{nearby.police} nearby</Text>}
            </TouchableOpacity>
            <TouchableOpacity style={[st.shareCard,{backgroundColor:Colors.redBg,borderColor:Colors.redBorder}]} onPress={()=>shareWithTarget('AMBULANCE')} disabled={sharing} activeOpacity={0.8}>
              <Text style={{fontSize:28}}>🚑</Text>
              <Text style={[st.shareLabel,{color:Colors.red}]}>Ambulance</Text>
              {nearby.ambulance>0&&<Text style={st.shareCnt}>{nearby.ambulance} nearby</Text>}
            </TouchableOpacity>
          </View>
        </>}
        {sessions.length>0&&<View style={st.activeShare}><View style={st.activeDot}/><Text style={st.activeTxt}>Live location active with {sessions.length} unit{sessions.length>1?'s':''}</Text></View>}
      </ScrollView>
    </SafeAreaView>
  );
}

const st = StyleSheet.create({
  hdr:{flexDirection:'row',alignItems:'center',justifyContent:'space-between',paddingHorizontal:Space.base,paddingVertical:Space.md},
  backBtn:{width:40,height:40,borderRadius:20,backgroundColor:Colors.surface,alignItems:'center',justifyContent:'center',...Shadow.sm},
  hdrTitle:{fontSize:Font.sizes.lg,fontWeight:Font.weight.bold,color:Colors.text},
  locCard:{marginHorizontal:Space.base,borderRadius:Radius.base,borderWidth:1,flexDirection:'row',alignItems:'center',gap:Space.md,padding:Space.md,marginBottom:Space.base},
  sosOuter:{width:200,height:200,borderRadius:100,...Platform.select({ios:{shadowColor:'#FF3B30',shadowOffset:{width:0,height:12},shadowOpacity:0.6,shadowRadius:28},android:{elevation:18}})},
  sosInner:{width:200,height:200,borderRadius:100,alignItems:'center',justifyContent:'center',gap:4},
  sosTxt:{fontSize:Font.sizes['3xl'],fontWeight:Font.weight.black,color:Colors.white,letterSpacing:4},
  sosHint:{fontSize:Font.sizes.xs,color:'rgba(255,255,255,0.75)',textAlign:'center'},
  cancelOuter:{width:200,height:200,borderRadius:100,backgroundColor:Colors.amberBg,borderWidth:4,borderColor:Colors.amber,alignItems:'center',justifyContent:'center',gap:Space.sm},
  cntNum:{fontSize:Font.sizes['5xl'],fontWeight:Font.weight.black,color:Colors.red},
  callGrad:{flexDirection:'row',alignItems:'center',justifyContent:'center',gap:Space.sm,height:52,borderRadius:Radius.base},
  callTxt:{fontSize:Font.sizes.md,fontWeight:Font.weight.semibold,color:Colors.white},
  shareRow:{flexDirection:'row',marginHorizontal:Space.base,gap:Space.md,marginBottom:Space.base},
  shareCard:{flex:1,borderRadius:Radius.lg,borderWidth:1.5,padding:Space.base,alignItems:'center',gap:Space.sm},
  shareLabel:{fontSize:Font.sizes.sm,fontWeight:Font.weight.bold},
  shareCnt:{fontSize:Font.sizes.xs,color:Colors.textSecond},
  activeShare:{marginHorizontal:Space.base,flexDirection:'row',alignItems:'center',gap:Space.sm,padding:Space.md,backgroundColor:Colors.greenBg,borderRadius:Radius.base,borderWidth:1,borderColor:Colors.greenBorder,marginBottom:Space.base},
  activeDot:{width:8,height:8,borderRadius:4,backgroundColor:Colors.green},
  activeTxt:{flex:1,fontSize:Font.sizes.sm,color:Colors.greenDark,fontWeight:Font.weight.medium},
  sentTitle:{fontSize:Font.sizes['3xl'],fontWeight:Font.weight.black,color:Colors.greenDark,marginTop:Space.base},
  sentSub:{fontSize:Font.sizes.base,color:Colors.textSecond,marginTop:Space.sm},
  doneBtn:{height:52,borderRadius:Radius.base,backgroundColor:Colors.surface,borderWidth:1.5,borderColor:Colors.border,alignItems:'center',justifyContent:'center'},
  doneTxt:{fontSize:Font.sizes.md,fontWeight:Font.weight.semibold,color:Colors.text},
});
