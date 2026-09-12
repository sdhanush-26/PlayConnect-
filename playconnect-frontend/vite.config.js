import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // sockjs-client (Day 53) expects Node's `global` object, which
  // doesn't exist in browsers. Vite doesn't polyfill this automatically
  // the way older bundlers (webpack/CRA) did, so we alias it to
  // `globalThis` — the standard browser equivalent.
  define: {
    global: 'globalThis',
  },
})