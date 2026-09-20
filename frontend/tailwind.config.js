/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        base: {
          950: '#0a0e14',
          900: '#0f1420',
          850: '#141a29',
          800: '#1a2233',
          700: '#232c40',
          600: '#2f3a52',
        },
        accent: {
          400: '#5eead4',
          500: '#2dd4bf',
          600: '#14b8a6',
        },
        risk: {
          low: '#22c55e',
          medium: '#eab308',
          high: '#f97316',
          critical: '#ef4444',
        },
      },
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'ui-monospace', 'SFMono-Regular', 'monospace'],
      },
      boxShadow: {
        glow: '0 0 40px -10px rgba(45, 212, 191, 0.35)',
      },
      keyframes: {
        fadeIn: { '0%': { opacity: 0, transform: 'translateY(6px)' }, '100%': { opacity: 1, transform: 'translateY(0)' } },
        pulseSoft: { '0%,100%': { opacity: 1 }, '50%': { opacity: .6 } },
      },
      animation: {
        fadeIn: 'fadeIn .35s ease-out',
        pulseSoft: 'pulseSoft 2s ease-in-out infinite',
      },
    },
  },
  plugins: [],
}
