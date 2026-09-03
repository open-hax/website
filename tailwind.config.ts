// @ts-nocheck
import { monokai, colors as uiColors } from '@open-hax/uxx/tokens'

function hexToRgbChannels(hex: string): string {
  const normalized = hex.replace('#', '')
  const expanded = normalized.length === 3
    ? normalized.split('').map((char) => char + char).join('')
    : normalized
  const value = Number.parseInt(expanded, 16)
  const r = (value >> 16) & 255
  const g = (value >> 8) & 255
  const b = value & 255
  return `${r} ${g} ${b}`
}

function withAlpha(hex: string) {
  const rgb = hexToRgbChannels(hex)
  return ({ opacityValue }: { opacityValue?: string }) =>
    opacityValue === undefined ? `rgb(${rgb})` : `rgb(${rgb} / ${opacityValue})`
}

const config = {
  darkMode: 'class',
  // The HTML shells are generated from src/cljc (domain/html_document.cljc),
  // so there is no index.html at the repository root to scan.
  content: ['./src/**/*.{ts,tsx,cljs,clj,cljc}'],
  theme: {
    extend: {
      colors: {
        slate: {
          50: withAlpha(monokai.fg.bright),
          100: withAlpha(monokai.fg.default),
          200: withAlpha(monokai.fg.panel),
          300: withAlpha(monokai.fg.soft),
          400: withAlpha(monokai.fg.muted),
          500: withAlpha(monokai.fg.muted),
          600: withAlpha(monokai.fg.muted),
          700: withAlpha(monokai.bg.selection),
          800: withAlpha(monokai.bg.lighter),
          900: withAlpha(monokai.bg.default),
          950: withAlpha(monokai.bg.darker),
        },
        surface: withAlpha(monokai.bg.lighter),
        card: withAlpha(monokai.bg.tabInactive),
        accent: withAlpha(monokai.accent.green),
        ink: withAlpha(monokai.fg.default),
      },
      boxShadow: {
        panel: `0 1px 2px ${uiColors.alpha.shadowLight}, 0 10px 30px ${uiColors.alpha.shadow}`,
      },
    },
  },
  plugins: [],
}

export default config
