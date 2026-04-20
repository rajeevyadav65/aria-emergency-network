// src/screens/chat/ChatScreen.tsx — ARIA AI Chat
import React, { useState, useEffect, useRef, useCallback } from 'react';
import {
  View, Text, StyleSheet, TouchableOpacity, TextInput,
  FlatList, KeyboardAvoidingView, Platform, ActivityIndicator,
  Animated,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import LinearGradient from 'react-native-linear-gradient';
import { useNavigation } from '@react-navigation/native';
import { Colors, Font, Space, Radius, Shadow, S } from '../../utils/theme';
import { ChatApi } from '../../services/api';

interface Message {
  id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  isEmergency?: boolean;
  createdAt: Date;
}

const SESSION_ID = `chat-${Date.now()}`;

const QUICK_PROMPTS = [
  '🆘 I need help urgently',
  '🏥 Find nearest hospital',
  '🩺 I need a doctor',
  '🚑 Call ambulance',
  '📞 Emergency contacts',
  '💊 First aid tips',
  '🔥 There is a fire nearby',
  '🌊 Flood warning near me',
];

export default function ChatScreen({ route }: any) {
  const nav  = useNavigation<any>();
  const user = route?.params?.user ?? {};
  const [messages,  setMessages]  = useState<Message[]>([]);
  const [input,     setInput]     = useState('');
  const [loading,   setLoading]   = useState(false);
  const listRef = useRef<FlatList>(null);
  const inputRef = useRef<TextInput>(null);
  const fadeAnim = useRef(new Animated.Value(0)).current;

  // Load history on mount
  useEffect(() => {
    loadHistory();
    Animated.timing(fadeAnim, { toValue: 1, duration: 400, useNativeDriver: true }).start();
  }, []);

  const loadHistory = async () => {
    try {
      const hist = await ChatApi.getHistory(SESSION_ID) as any[];
      if (hist.length > 0) {
        setMessages(hist.map((m: any) => ({
          id: String(m.id),
          role: m.role as 'user' | 'assistant',
          content: m.content,
          createdAt: new Date(m.createdAt),
        })));
      } else {
        // Welcome message
        setMessages([{
          id: 'welcome',
          role: 'assistant',
          content: "Hi! I'm ARIA, your AI emergency assistant 🤖\n\nI can help you:\n• Report emergencies\n• Find nearby hospitals & doctors\n• Provide first aid guidance\n• Connect you with police or ambulance\n\nHow can I help you today?",
          createdAt: new Date(),
        }]);
      }
    } catch {
      setMessages([{
        id: 'welcome',
        role: 'assistant',
        content: "Hi! I'm ARIA, your AI emergency assistant. How can I help you today?",
        createdAt: new Date(),
      }]);
    }
  };

  const sendMessage = useCallback(async (text?: string) => {
    const msg = (text ?? input).trim();
    if (!msg || loading) return;

    setInput('');
    const userMsg: Message = {
      id: `u-${Date.now()}`,
      role: 'user',
      content: msg,
      createdAt: new Date(),
    };
    setMessages(prev => [...prev, userMsg]);
    setLoading(true);

    setTimeout(() => listRef.current?.scrollToEnd({ animated: true }), 100);

    try {
      const resp = await ChatApi.send(msg, SESSION_ID);
      const aiMsg: Message = {
        id: `a-${Date.now()}`,
        role: 'assistant',
        content: resp.reply,
        isEmergency: resp.isEmergency,
        createdAt: new Date(),
      };
      setMessages(prev => [...prev, aiMsg]);

      if (resp.isEmergency) {
        setTimeout(() => listRef.current?.scrollToEnd({ animated: true }), 100);
      }
    } catch {
      setMessages(prev => [...prev, {
        id: `err-${Date.now()}`,
        role: 'assistant',
        content: 'Sorry, I\'m having trouble connecting right now. For emergencies, please use the SOS button.',
        createdAt: new Date(),
      }]);
    }

    setLoading(false);
    setTimeout(() => listRef.current?.scrollToEnd({ animated: true }), 200);
  }, [input, loading]);

  const clearChat = async () => {
    try {
      await ChatApi.clear(SESSION_ID);
      setMessages([{
        id: 'cleared',
        role: 'assistant',
        content: 'Chat cleared. How can I help you?',
        createdAt: new Date(),
      }]);
    } catch {}
  };

  const renderMessage = ({ item }: { item: Message }) => {
    const isUser = item.role === 'user';
    const isSystem = item.role === 'system';
    const timeStr = item.createdAt.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

    if (isSystem) {
      return (
        <View style={st.systemMsg}>
          <Text style={st.systemMsgText}>{item.content}</Text>
        </View>
      );
    }

    return (
      <View style={[st.msgRow, isUser ? st.msgRowUser : st.msgRowAi]}>
        {!isUser && (
          <View style={st.avatar}>
            <LinearGradient colors={[Colors.blue, Colors.blueDark]} style={st.avatarGrad}>
              <Text style={{ fontSize: 14 }}>🤖</Text>
            </LinearGradient>
          </View>
        )}
        <View style={[
          st.bubble,
          isUser ? st.bubbleUser : st.bubbleAi,
          item.isEmergency && st.bubbleEmergency,
        ]}>
          {item.isEmergency && (
            <View style={st.emergencyBadge}>
              <Text style={st.emergencyBadgeText}>🚨 Emergency Detected</Text>
            </View>
          )}
          <Text style={[st.bubbleText, isUser && st.bubbleTextUser]}>
            {item.content}
          </Text>
          <Text style={[st.bubbleTime, isUser && { color: 'rgba(255,255,255,0.6)' }]}>
            {timeStr}
          </Text>
          {item.isEmergency && (
            <TouchableOpacity
              style={st.sosPromptBtn}
              onPress={() => nav.navigate('SOS', { user })}
              activeOpacity={0.85}
            >
              <Text style={st.sosPromptText}>Tap to send SOS →</Text>
            </TouchableOpacity>
          )}
        </View>
        {isUser && (
          <View style={[st.avatar, st.avatarUser]}>
            <Text style={{ fontSize: 16 }}>{user.role === 'DOCTOR' ? '🩺' : user.role === 'POLICE' ? '👮' : user.role === 'AMBULANCE' ? '🚑' : '👤'}</Text>
          </View>
        )}
      </View>
    );
  };

  return (
    <SafeAreaView style={S.screen} edges={['top']}>
      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        keyboardVerticalOffset={0}
      >
        {/* Header */}
        <View style={st.header}>
          <TouchableOpacity onPress={() => nav.goBack()} style={st.backBtn}>
            <Text style={{ fontSize: 18, color: Colors.text }}>←</Text>
          </TouchableOpacity>
          <View style={st.headerCenter}>
            <LinearGradient colors={[Colors.blue, Colors.blueDark]} style={st.headerAvatar}>
              <Text style={{ fontSize: 18 }}>🤖</Text>
            </LinearGradient>
            <View>
              <Text style={st.headerTitle}>ARIA</Text>
              <View style={st.onlineRow}>
                <View style={st.onlineDot} />
                <Text style={st.onlineText}>AI Emergency Assistant</Text>
              </View>
            </View>
          </View>
          <TouchableOpacity onPress={clearChat} style={st.clearBtn}>
            <Text style={{ fontSize: 13, color: Colors.textSecond }}>Clear</Text>
          </TouchableOpacity>
        </View>

        {/* Messages */}
        <Animated.View style={{ flex: 1, opacity: fadeAnim }}>
          <FlatList
            ref={listRef}
            data={messages}
            renderItem={renderMessage}
            keyExtractor={item => item.id}
            contentContainerStyle={st.msgList}
            showsVerticalScrollIndicator={false}
            onLayout={() => listRef.current?.scrollToEnd()}
            ListFooterComponent={loading ? (
              <View style={[st.msgRowAi, st.msgRow]}>
                <View style={st.avatar}>
                  <LinearGradient colors={[Colors.blue, Colors.blueDark]} style={st.avatarGrad}>
                    <Text style={{ fontSize: 14 }}>🤖</Text>
                  </LinearGradient>
                </View>
                <View style={[st.bubble, st.bubbleAi, { paddingVertical: Space.md }]}>
                  <View style={{ flexDirection: 'row', gap: 4, alignItems: 'center' }}>
                    {[0, 1, 2].map(i => <TypingDot key={i} delay={i * 200} />)}
                  </View>
                </View>
              </View>
            ) : null}
          />
        </Animated.View>

        {/* Quick prompts */}
        <FlatList
          horizontal
          data={QUICK_PROMPTS}
          keyExtractor={item => item}
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={st.quickList}
          renderItem={({ item }) => (
            <TouchableOpacity
              style={st.quickChip}
              onPress={() => sendMessage(item.replace(/^[^\s]+ /, ''))}
              activeOpacity={0.78}
            >
              <Text style={st.quickChipText}>{item}</Text>
            </TouchableOpacity>
          )}
          style={st.quickBar}
        />

        {/* Input bar */}
        <View style={st.inputBar}>
          <View style={st.inputWrap}>
            <TextInput
              ref={inputRef}
              style={st.input}
              placeholder="Ask ARIA anything…"
              placeholderTextColor={Colors.textPlaceholder}
              value={input}
              onChangeText={setInput}
              multiline
              maxLength={500}
              onSubmitEditing={() => sendMessage()}
              returnKeyType="send"
            />
          </View>
          <TouchableOpacity
            style={[st.sendBtn, input.trim() ? st.sendBtnActive : null]}
            onPress={() => sendMessage()}
            disabled={!input.trim() || loading}
            activeOpacity={0.8}
          >
            <LinearGradient
              colors={input.trim() ? [Colors.blue, Colors.blueDark] : [Colors.surface3, Colors.surface3]}
              style={st.sendGrad}
            >
              <Text style={{ fontSize: 18 }}>↑</Text>
            </LinearGradient>
          </TouchableOpacity>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

function TypingDot({ delay }: { delay: number }) {
  const anim = useRef(new Animated.Value(0)).current;
  useEffect(() => {
    const loop = Animated.loop(
      Animated.sequence([
        Animated.delay(delay),
        Animated.timing(anim, { toValue: 1, duration: 400, useNativeDriver: true }),
        Animated.timing(anim, { toValue: 0, duration: 400, useNativeDriver: true }),
      ])
    );
    loop.start();
    return () => loop.stop();
  }, [anim, delay]);

  return (
    <Animated.View style={{
      width: 7, height: 7, borderRadius: 3.5,
      backgroundColor: Colors.textTertiary,
      transform: [{ translateY: anim.interpolate({ inputRange: [0, 1], outputRange: [0, -4] }) }],
    }} />
  );
}

const st = StyleSheet.create({
  header:         { flexDirection: 'row', alignItems: 'center', paddingHorizontal: Space.base, paddingVertical: Space.md, borderBottomWidth: 1, borderBottomColor: Colors.border, gap: Space.md },
  backBtn:        { width: 36, height: 36, borderRadius: 18, backgroundColor: Colors.surface3, alignItems: 'center', justifyContent: 'center' },
  headerCenter:   { flex: 1, flexDirection: 'row', alignItems: 'center', gap: Space.md },
  headerAvatar:   { width: 38, height: 38, borderRadius: 19, alignItems: 'center', justifyContent: 'center' },
  headerTitle:    { fontSize: Font.sizes.md, fontWeight: Font.weight.bold, color: Colors.text },
  onlineRow:      { flexDirection: 'row', alignItems: 'center', gap: 5, marginTop: 2 },
  onlineDot:      { width: 6, height: 6, borderRadius: 3, backgroundColor: Colors.green },
  onlineText:     { fontSize: Font.sizes.xs, color: Colors.textSecond },
  clearBtn:       { padding: Space.sm },
  msgList:        { paddingHorizontal: Space.base, paddingVertical: Space.md, gap: Space.sm },
  msgRow:         { flexDirection: 'row', alignItems: 'flex-end', gap: Space.sm },
  msgRowUser:     { justifyContent: 'flex-end' },
  msgRowAi:       { justifyContent: 'flex-start' },
  avatar:         { width: 32, height: 32, borderRadius: 16, overflow: 'hidden', flexShrink: 0 },
  avatarGrad:     { flex: 1, alignItems: 'center', justifyContent: 'center' },
  avatarUser:     { backgroundColor: Colors.surface3, alignItems: 'center', justifyContent: 'center' },
  bubble:         { maxWidth: '75%', borderRadius: Radius.lg, padding: Space.md },
  bubbleUser:     { backgroundColor: Colors.blue, borderBottomRightRadius: 4 },
  bubbleAi:       { backgroundColor: Colors.surface, borderBottomLeftRadius: 4, borderWidth: 1, borderColor: Colors.border, ...Shadow.xs },
  bubbleEmergency:{ borderWidth: 2, borderColor: Colors.red, backgroundColor: Colors.redBg },
  bubbleText:     { fontSize: Font.sizes.sm, color: Colors.text, lineHeight: 20 },
  bubbleTextUser: { color: Colors.white },
  bubbleTime:     { fontSize: 10, color: Colors.textTertiary, marginTop: 4, alignSelf: 'flex-end' },
  emergencyBadge: { flexDirection: 'row', alignItems: 'center', marginBottom: Space.sm, gap: Space.sm, paddingBottom: Space.sm, borderBottomWidth: 1, borderBottomColor: Colors.redBorder },
  emergencyBadgeText: { fontSize: Font.sizes.xs, fontWeight: Font.weight.bold, color: Colors.red },
  sosPromptBtn:   { marginTop: Space.sm, backgroundColor: Colors.red, borderRadius: Radius.base, paddingVertical: Space.sm, paddingHorizontal: Space.md, alignItems: 'center' },
  sosPromptText:  { fontSize: Font.sizes.xs, color: Colors.white, fontWeight: Font.weight.bold },
  systemMsg:      { alignItems: 'center', paddingVertical: Space.sm },
  systemMsgText:  { fontSize: Font.sizes.xs, color: Colors.textTertiary, backgroundColor: Colors.surface3, paddingHorizontal: Space.md, paddingVertical: Space.sm, borderRadius: Radius.full },
  quickBar:       { maxHeight: 48, borderTopWidth: 1, borderTopColor: Colors.border },
  quickList:      { paddingHorizontal: Space.md, paddingVertical: Space.sm, gap: Space.sm },
  quickChip:      { paddingHorizontal: Space.md, paddingVertical: 8, borderRadius: Radius.full, backgroundColor: Colors.surface3, borderWidth: 1, borderColor: Colors.border },
  quickChipText:  { fontSize: Font.sizes.xs, color: Colors.textSecond, fontWeight: Font.weight.medium },
  inputBar:       { flexDirection: 'row', alignItems: 'flex-end', paddingHorizontal: Space.base, paddingVertical: Space.md, gap: Space.sm, borderTopWidth: 1, borderTopColor: Colors.border, backgroundColor: Colors.surface },
  inputWrap:      { flex: 1, backgroundColor: Colors.bg, borderRadius: Radius.lg, borderWidth: 1, borderColor: Colors.border, paddingHorizontal: Space.base, paddingVertical: Space.sm, minHeight: 44, maxHeight: 120 },
  input:          { fontSize: Font.sizes.sm, color: Colors.text, lineHeight: 20 },
  sendBtn:        { width: 44, height: 44, borderRadius: 22, overflow: 'hidden', flexShrink: 0 },
  sendBtnActive:  { ...Shadow.blue },
  sendGrad:       { width: 44, height: 44, alignItems: 'center', justifyContent: 'center' },
});
