    import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  base: '/CustomNPC-DBC-Addon/',
  build: {
    outDir: '../../.github/pages-dist',
    emptyOutDir: true,
  }
})
