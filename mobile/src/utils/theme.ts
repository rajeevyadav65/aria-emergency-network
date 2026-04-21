// src/utils/theme.ts — ARIA Design System v2
import { Platform, StyleSheet, Dimensions } from 'react-native';

const { width: W, height: H } = Dimensions.get('window');
export const SCREEN = { W, H };

// ── Color Palette ──────────────────────────────────────────────────────────────
export const Colors = {
  // Primary (emergency red)
  red:          '#FF3B30',
  redDark:      '#CC2A20',
  redLight:     '#FF6B6B',
  redBg:        '#FFF2F1',
  redBorder:    '#FFD5D3',

  // Blue
  blue:         '#007AFF',
  blueDark:     '#0055B3',
  blueBg:       '#EAF3FF',
  blueBorder:   '#B3D4FF',

  // Green
  green:        '#34C759',
  greenDark:    '#248A3D',
  greenBg:      '#EDFBF2',
  greenBorder:  '#B3EEC6',

  // Amber
  amber:        '#FF9500',
  amberDark:    '#C97300',
  amberBg:      '#FFF6E5',
  amberBorder:  '#FFD9A0',

  // Purple
  purple:       '#AF52DE',
  purpleBg:     '#F7EFFE',
  purpleBorder: '#DFB8F7',

  textMuted: '#888888',

  // Neutrals (iOS-accurate)
  white:        '#FFFFFF',
  bg:           '#F2F2F7',
  bgAlt:        '#FAFAFA',
  surface:      '#FFFFFF',
  surface2:     '#F8F8FA',
  surface3:     '#F0F0F5',
  border:       '#E5E5EA',
  borderLight:  '#F0F0F5',
  divider:      '#EBEBF0',

  // Text
  text:         '#1C1C1E',
  textSecond:   '#636366',
  textTertiary: '#AEAEB2',
  textPlaceholder: '#C7C7CC',
  textInverse:  '#FFFFFF',

  // Gradients (start, end)
  gradRed:      ['#FF3B30', '#FF6B6B'] as const,
  gradBlue:     ['#007AFF', '#5AC8FA'] as const,
  gradGreen:    ['#34C759', '#30D158'] as const,
  gradDark:     ['#1C1C1E', '#3A3A3C'] as const,
  gradSunset:   ['#FF6B6B', '#FF9500'] as const,

  // Special
  overlay:      'rgba(0,0,0,0.55)',
  overlayLight: 'rgba(0,0,0,0.08)',
  overlayBlue:  'rgba(0,122,255,0.12)',
  mapDark:      '#1a1a2e',
};

// ── Typography ─────────────────────────────────────────────────────────────────
export const Font = {
  family:  Platform.select({ ios: 'System', android: 'Roboto', default: 'System' }),
  mono:    Platform.select({ ios: 'Courier New', android: 'monospace', default: 'monospace' }),
  sizes:   { xs:10, sm:12, base:14, md:16, lg:18, xl:20, '2xl':24, '3xl':30, '4xl':36, '5xl':48 },
  weight:  { regular:'400' as const, medium:'500' as const, semibold:'600' as const, bold:'700' as const, heavy:'800' as const, black:'900' as const },
  leading: { tight:1.1, snug:1.2, normal:1.4, relaxed:1.6, loose:1.8 },
};

// ── Spacing ────────────────────────────────────────────────────────────────────
export const Space = { xs:4, sm:8, md:12, base:16, lg:20, xl:24, '2xl':32, '3xl':48, '4xl':64 };

// ── Radii ──────────────────────────────────────────────────────────────────────
export const Radius = { sm:6, md:10, base:14, lg:18, xl:24, '2xl':32, full:9999 };

// ── Shadows ────────────────────────────────────────────────────────────────────
export const Shadow = {
  none: {},
  xs: Platform.select({ ios: { shadowColor:'#000', shadowOffset:{width:0,height:1}, shadowOpacity:0.04, shadowRadius:3 }, android:{elevation:1} })!,
  sm: Platform.select({ ios: { shadowColor:'#000', shadowOffset:{width:0,height:2}, shadowOpacity:0.07, shadowRadius:6 }, android:{elevation:2} })!,
  md: Platform.select({ ios: { shadowColor:'#000', shadowOffset:{width:0,height:4}, shadowOpacity:0.10, shadowRadius:12 }, android:{elevation:4} })!,
  lg: Platform.select({ ios: { shadowColor:'#000', shadowOffset:{width:0,height:8}, shadowOpacity:0.14, shadowRadius:20 }, android:{elevation:8} })!,
  xl: Platform.select({ ios: { shadowColor:'#000', shadowOffset:{width:0,height:16}, shadowOpacity:0.18, shadowRadius:32 }, android:{elevation:16} })!,
  red: Platform.select({ ios: { shadowColor:'#FF3B30', shadowOffset:{width:0,height:6}, shadowOpacity:0.42, shadowRadius:16 }, android:{elevation:10} })!,
  blue: Platform.select({ ios: { shadowColor:'#007AFF', shadowOffset:{width:0,height:4}, shadowOpacity:0.28, shadowRadius:12 }, android:{elevation:6} })!,
};

// ── Risk helpers ───────────────────────────────────────────────────────────────
export const RiskStyle = {
  HIGH:         { bg: Colors.redBg,    text: Colors.red,       border: Colors.redBorder,    icon: '🚨' },
  MEDIUM:       { bg: Colors.amberBg,  text: Colors.amberDark, border: Colors.amberBorder,  icon: '⚠️' },
  LOW:          { bg: Colors.greenBg,  text: Colors.greenDark, border: Colors.greenBorder,  icon: '💚' },
  FALSE_ALARM:  { bg: Colors.surface3, text: Colors.textSecond,border: Colors.border,        icon: '✅' },
};

export const getRisk = (level: string) => RiskStyle[level as keyof typeof RiskStyle] ?? RiskStyle.LOW;

// ── Common styles ──────────────────────────────────────────────────────────────
export const S = StyleSheet.create({
  screen:     { flex:1, backgroundColor: Colors.bg },
  center:     { alignItems:'center', justifyContent:'center' },
  row:        { flexDirection:'row', alignItems:'center' },
  flex1:      { flex:1 },
  card: {
    backgroundColor: Colors.surface, borderRadius: Radius.lg,
    padding: Space.base, ...Shadow.sm,
  },
  cardBorder: {
    backgroundColor: Colors.surface, borderRadius: Radius.lg,
    padding: Space.base, borderWidth: 1, borderColor: Colors.border,
  },
  sectionHeader: {
    fontSize: Font.sizes.sm, fontWeight: Font.weight.semibold,
    color: Colors.textTertiary, textTransform:'uppercase',
    letterSpacing:0.6, paddingHorizontal: Space.base,
    paddingTop: Space.lg, paddingBottom: Space.sm,
  },
  pill: {
    paddingHorizontal: Space.md, paddingVertical: 4,
    borderRadius: Radius.full, borderWidth:1,
  },
  pillText: { fontSize: Font.sizes.xs, fontWeight: Font.weight.semibold },
  inputContainer: {
    backgroundColor: Colors.surface, borderRadius: Radius.base,
    borderWidth:1, borderColor: Colors.border,
    flexDirection:'row', alignItems:'center',
    paddingHorizontal: Space.base, height: 52,
  },
  input: {
    flex:1, fontSize: Font.sizes.md, color: Colors.text,
    height:'100%',
  },
  btn: {
    height:52, borderRadius: Radius.base,
    alignItems:'center', justifyContent:'center',
    flexDirection:'row', gap: Space.sm,
  },
  btnText: { fontSize: Font.sizes.md, fontWeight: Font.weight.semibold },
  btnPrimary: {
    height:52, borderRadius: Radius.base,
    alignItems:'center', justifyContent:'center',
    backgroundColor: Colors.red, ...Shadow.red,
  },
  btnSecondary: {
    height:52, borderRadius: Radius.base,
    alignItems:'center', justifyContent:'center',
    backgroundColor: Colors.surface, borderWidth:1.5, borderColor: Colors.red,
  },
  avatar: {
    width:40, height:40, borderRadius:20,
    alignItems:'center', justifyContent:'center',
    backgroundColor: Colors.surface3,
  },
  separator: { height:1, backgroundColor: Colors.divider, marginLeft: Space.base },
  tag: {
    paddingHorizontal: Space.sm, paddingVertical:3, borderRadius: Radius.full,
    backgroundColor: Colors.surface3,
  },
  tagText: { fontSize: Font.sizes.xs, color: Colors.textSecond, fontWeight: Font.weight.medium },
});
