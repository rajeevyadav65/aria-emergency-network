// VoiceScreen.tsx — AI Voice Assistant + TTS + Secret SOS Keyword
// Full speech recognition, ARIA AI chat, and voice-triggered silent SOS
import React, { useState, useEffect, useRef, useCallback } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, Animated, TextInput, Alert, Switch, ActivityIndicator, Platform } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Voice from '@react-native-voice/voice';
import Tts from 'react-native-tts';
import LinearGradient from 'react-native-linear-gradient';
import { useNavigation } from '@react-navigation/native';
import { Colors, Font, Space, Radius, Shadow, S } from '../../utils/theme';
import { ChatApi, VoiceApi } from '../../services/api';

const SESSION_ID = `voice-${Date.now()}`;
interface Msg { id:string; role:'user'|'assistant'|'system'; text:string; isEmergency?:boolean; ts:Date; }

export default function VoiceScreen({ route }: any) {
  const nav = useNavigation<any>();
  const user = route?.params?.user ?? {};
  const [listening, setListening] = useState(false);
  const [partial,   setPartial]   = useState('');
  const [msgs,      setMsgs]      = useState<Msg[]>([{ id:'sys-1', role:'system', text:"👋 Hi! I'm ARIA. Say anything or tap 🎙 to talk. I can find help, answer safety questions, or detect emergencies.", ts:new Date() }]);
  const [loading,   setLoading]   = useState(false);
  const [tts,       setTts]       = useState(true);
  const [showKey,   setShowKey]   = useState(false);
  const [keyword,   setKeyword]   = useState('');
  const [hint,      setHint]      = useState('');
  const [keyStatus, setKeyStatus] = useState<any>(null);
  const [savingKey, setSavingKey] = useState(false);
  const pOuter = useRef(new Animated.Value(1)).current;
  const pInner = useRef(new Animated.Value(1)).current;
  const scroll = useRef<ScrollView>(null);

  useEffect(() => {
    Voice.onSpeechStart   = () => setListening(true);
    Voice.onSpeechEnd     = () => setListening(false);
    Voice.onSpeechPartialResults = e => setPartial(e.value?.[0]??'');
    Voice.onSpeechResults = e => { setPartial(''); const t=e.value?.[0]??''; if(t.trim()) sendMsg(t); };
    Voice.onSpeechError   = () => setListening(false);
    loadKeyStatus();
    return () => { Voice.destroy().then(()=>Voice.removeAllListeners()); };
  }, []);

  useEffect(() => {
    if (listening) Animated.loop(Animated.parallel([
      Animated.sequence([Animated.timing(pOuter,{toValue:1.4,duration:800,useNativeDriver:true}),Animated.timing(pOuter,{toValue:1,duration:800,useNativeDriver:true})]),
      Animated.sequence([Animated.timing(pInner,{toValue:1.15,duration:600,useNativeDriver:true}),Animated.timing(pInner,{toValue:1,duration:600,useNativeDriver:true})]),
    ])).start();
    else { pOuter.setValue(1); pInner.setValue(1); }
  }, [listening]);

  const toggleListen = async () => {
    try { if (listening) await Voice.stop(); else await Voice.start('en-IN').catch(()=>Voice.start('en-US')); } catch {}
  };

  const sendMsg = useCallback(async (text: string) => {
    const um: Msg = { id:`${Date.now()}`, role:'user', text, ts:new Date() };
    setMsgs(p=>[...p,um]); setLoading(true);
    try {
      const r = await ChatApi.send(text, SESSION_ID);
      const am: Msg = { id:`${Date.now()+1}`, role:'assistant', text:r.reply, isEmergency:r.isEmergency, ts:new Date() };
      setMsgs(p=>[...p,am]);
      if (tts && r.reply) Tts.speak(r.reply.slice(0,200));
      if (r.isEmergency) setTimeout(()=>Alert.alert('🚨 Emergency Detected','ARIA detected an emergency. Send SOS?',[{text:'No',style:'cancel'},{text:'Yes — SOS',style:'destructive',onPress:()=>nav.navigate('SOS',{user})}]),1500);
    } catch {
      setMsgs(p=>[...p,{ id:`${Date.now()+2}`, role:'assistant', text:"Can't connect. For emergencies, use the SOS button.", ts:new Date() }]);
    }
    setLoading(false);
    setTimeout(()=>scroll.current?.scrollToEnd({animated:true}),100);
  }, [tts, nav, user]);

  const loadKeyStatus = async () => { try { setKeyStatus(await VoiceApi.getStatus()); } catch {} };

  const saveKeyword = async () => {
    if (keyword.trim().length < 4) { Alert.alert('Too Short','At least 4 characters required'); return; }
    setSavingKey(true);
    try {
      await VoiceApi.setKeyword(keyword.trim(), hint.trim());
      setKeyword(''); setHint(''); await loadKeyStatus();
      Alert.alert('✅ Saved',`Say "${keyword.trim()}" at any time to silently send SOS.`);
      setShowKey(false);
    } catch { Alert.alert('Error','Could not save keyword'); }
    setSavingKey(false);
  };

  const QUICK = ['Find nearest hospital','I need an ambulance','Call police','How to do CPR?','I witnessed an accident'];

  return (
    <SafeAreaView style={S.screen} edges={['top']}>
      <View style={st.hdr}>
        <TouchableOpacity onPress={()=>nav.goBack()} style={st.back}><Text style={{fontSize:16}}>←</Text></TouchableOpacity>
        <View style={{flex:1}}><Text style={st.title}>ARIA Voice</Text><Text style={st.sub}>AI Emergency Assistant</Text></View>
        <View style={{flexDirection:'row',alignItems:'center',gap:6}}>
          <Text style={{fontSize:10,color:Colors.textSecond}}>🔊</Text>
          <Switch value={tts} onValueChange={setTts} trackColor={{true:Colors.blue}} />
        </View>
      </View>

      <ScrollView ref={scroll} style={{flex:1}} contentContainerStyle={{padding:Space.base,paddingBottom:Space['2xl']}} showsVerticalScrollIndicator={false} onContentSizeChange={()=>scroll.current?.scrollToEnd()}>
        {msgs.map(m=>(
          <View key={m.id} style={[st.bubble, m.role==='user'?st.bUser:st.bAi, m.isEmergency?st.bEmergency:null]}>
            {m.role!=='user'&&<Text style={st.bLabel}>{m.isEmergency?'🚨 ARIA':'🤖 ARIA'}</Text>}
            <Text style={[st.bText, m.role==='user'?{color:Colors.white}:{color:Colors.text}]}>{m.text}</Text>
            <Text style={st.bTime}>{m.ts.toLocaleTimeString([],{hour:'2-digit',minute:'2-digit'})}</Text>
          </View>
        ))}
        {loading&&<View style={st.bAi}><Text style={st.bLabel}>🤖 ARIA</Text><View style={{flexDirection:'row',gap:4,paddingVertical:4}}>{[0,1,2].map(i=><View key={i} style={{width:7,height:7,borderRadius:4,backgroundColor:Colors.textTertiary}}/>)}</View></View>}
        {partial.length>0&&<View style={[st.bUser,{opacity:0.6}]}><Text style={{color:Colors.white,fontSize:Font.sizes.sm}}>{partial}…</Text></View>}
      </ScrollView>

      <ScrollView horizontal showsHorizontalScrollIndicator={false} style={{maxHeight:44,borderTopWidth:1,borderTopColor:Colors.border}} contentContainerStyle={{padding:8,gap:8}}>
        {QUICK.map(q=>(
          <TouchableOpacity key={q} style={{paddingHorizontal:Space.md,paddingVertical:Space.sm,borderRadius:Radius.full,backgroundColor:Colors.surface3,borderWidth:1,borderColor:Colors.border}} onPress={()=>sendMsg(q)} activeOpacity={0.8}>
            <Text style={{fontSize:Font.sizes.xs,color:Colors.textSecond,fontWeight:Font.weight.medium}}>{q}</Text>
          </TouchableOpacity>
        ))}
      </ScrollView>

      <View style={st.micArea}>
        <TouchableOpacity onPress={()=>setShowKey(!showKey)} style={st.sideBtn} activeOpacity={0.8}>
          <Text style={{fontSize:18}}>🔑</Text>
          <Text style={{fontSize:Font.sizes.xs,color:Colors.purple,fontWeight:Font.weight.medium}}>{keyStatus?.active?'Active ✓':'Keyword'}</Text>
        </TouchableOpacity>
        <View style={{alignItems:'center',justifyContent:'center',position:'relative',width:90,height:90}}>
          <Animated.View style={{position:'absolute',width:90,height:90,borderRadius:45,backgroundColor:listening?'rgba(255,59,48,0.12)':'rgba(0,122,255,0.08)',transform:[{scale:pOuter}]}}/>
          <Animated.View style={{position:'absolute',width:72,height:72,borderRadius:36,backgroundColor:listening?'rgba(255,59,48,0.2)':'rgba(0,122,255,0.12)',transform:[{scale:pInner}]}}/>
          <TouchableOpacity onPress={toggleListen} activeOpacity={0.85}>
            <LinearGradient colors={listening?['#FF3B30','#CC2A20']:[Colors.blue,Colors.blueDark]} style={st.micBtn} start={{x:0,y:0}} end={{x:1,y:1}}>
              <Text style={{fontSize:28}}>{listening?'⏹':'🎙️'}</Text>
            </LinearGradient>
          </TouchableOpacity>
        </View>
        <TouchableOpacity onPress={()=>nav.navigate('SOS',{user})} style={st.sideBtn} activeOpacity={0.8}>
          <Text style={{fontSize:18}}>🆘</Text>
          <Text style={{fontSize:Font.sizes.xs,color:Colors.red,fontWeight:Font.weight.medium}}>SOS</Text>
        </TouchableOpacity>
      </View>
      {listening&&<Text style={{textAlign:'center',fontSize:Font.sizes.sm,color:Colors.red,fontWeight:Font.weight.medium,paddingBottom:Space.sm}}>Listening… speak now</Text>}

      {showKey&&(
        <View style={st.keyPanel}>
          <Text style={{fontSize:Font.sizes.lg,fontWeight:Font.weight.bold,color:Colors.text}}>🔑 Secret Voice Trigger</Text>
          <Text style={{fontSize:Font.sizes.sm,color:Colors.textSecond,lineHeight:18}}>Saying this word silently sends SOS with your location</Text>
          {keyStatus?.active&&<View style={{flexDirection:'row',justifyContent:'space-between',alignItems:'center',padding:Space.md,backgroundColor:Colors.greenBg,borderRadius:Radius.base,borderWidth:1,borderColor:Colors.greenBorder}}>
            <Text style={{fontSize:12,color:Colors.greenDark}}>✓ Active · Hint: "{keyStatus.hint||'—'}" · {keyStatus.triggerCount}x used</Text>
            <TouchableOpacity onPress={async()=>{await VoiceApi.disable();loadKeyStatus();}}><Text style={{fontSize:12,color:Colors.red}}>Disable</Text></TouchableOpacity>
          </View>}
          <View style={{borderWidth:1,borderColor:Colors.border,borderRadius:Radius.base,paddingHorizontal:Space.base,height:48}}>
            <TextInput style={{flex:1,height:'100%',fontSize:Font.sizes.sm,color:Colors.text}} placeholder="Secret keyword (min 4 chars)" secureTextEntry value={keyword} onChangeText={setKeyword} placeholderTextColor={Colors.textPlaceholder}/>
          </View>
          <View style={{borderWidth:1,borderColor:Colors.border,borderRadius:Radius.base,paddingHorizontal:Space.base,height:48}}>
            <TextInput style={{flex:1,height:'100%',fontSize:Font.sizes.sm,color:Colors.text}} placeholder="Hint (e.g. 'starts with H')" value={hint} onChangeText={setHint} placeholderTextColor={Colors.textPlaceholder}/>
          </View>
          <View style={{flexDirection:'row',gap:Space.sm}}>
            <TouchableOpacity style={{flex:1,height:48,borderRadius:Radius.base,backgroundColor:Colors.surface,borderWidth:1,borderColor:Colors.border,alignItems:'center',justifyContent:'center'}} onPress={()=>setShowKey(false)}>
              <Text style={{fontSize:Font.sizes.sm,fontWeight:Font.weight.medium,color:Colors.text}}>Cancel</Text>
            </TouchableOpacity>
            <TouchableOpacity style={{flex:2,height:48,borderRadius:Radius.base,backgroundColor:Colors.purple,alignItems:'center',justifyContent:'center'}} onPress={saveKeyword} disabled={savingKey}>
              <Text style={{fontSize:Font.sizes.sm,fontWeight:Font.weight.semibold,color:Colors.white}}>{savingKey?'Saving…':'Save Keyword'}</Text>
            </TouchableOpacity>
          </View>
        </View>
      )}
    </SafeAreaView>
  );
}

const st = StyleSheet.create({
  hdr:{flexDirection:'row',alignItems:'center',paddingHorizontal:Space.base,paddingVertical:Space.md,borderBottomWidth:1,borderBottomColor:Colors.border,gap:Space.md},
  back:{width:36,height:36,borderRadius:18,backgroundColor:Colors.surface3,alignItems:'center',justifyContent:'center'},
  title:{fontSize:Font.sizes.md,fontWeight:Font.weight.bold,color:Colors.text},
  sub:{fontSize:Font.sizes.xs,color:Colors.textSecond},
  bubble:{maxWidth:'85%',borderRadius:Radius.lg,padding:Space.md,marginBottom:Space.sm},
  bUser:{alignSelf:'flex-end',backgroundColor:Colors.blue,borderBottomRightRadius:4},
  bAi:{alignSelf:'flex-start',backgroundColor:Colors.surface,borderBottomLeftRadius:4,borderWidth:1,borderColor:Colors.border,...Shadow.xs},
  bEmergency:{borderWidth:2,borderColor:Colors.redBorder,backgroundColor:Colors.redBg},
  bLabel:{fontSize:Font.sizes.xs,fontWeight:Font.weight.semibold,color:Colors.textSecond,marginBottom:4},
  bText:{fontSize:Font.sizes.sm,lineHeight:20},
  bTime:{fontSize:9,marginTop:4,opacity:0.6,color:Colors.textSecond,alignSelf:'flex-end'},
  micArea:{flexDirection:'row',alignItems:'center',justifyContent:'space-between',paddingHorizontal:Space.xl,paddingVertical:Space.lg,borderTopWidth:1,borderTopColor:Colors.border},
  micBtn:{width:64,height:64,borderRadius:32,alignItems:'center',justifyContent:'center',...Shadow.blue},
  sideBtn:{alignItems:'center',gap:4,padding:Space.sm,width:60},
  keyPanel:{position:'absolute',bottom:0,left:0,right:0,backgroundColor:Colors.surface,borderTopLeftRadius:Radius['2xl'],borderTopRightRadius:Radius['2xl'],padding:Space.base,paddingBottom:Space['2xl'],...Shadow.xl,gap:Space.md},
});
