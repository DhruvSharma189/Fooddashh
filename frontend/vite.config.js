import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Dev:   npm run dev      -> http://localhost:5173 (calls to /api are proxied to Spring Boot)
// Build: npm run build    -> writes straight into Spring Boot's static folder
export default defineConfig({
  plugins: [react()],
  server: { port: 5174, proxy: { '/api': process.env.VITE_API_TARGET || 'http://localhost:8098' } },
  build: { outDir: '../src/main/resources/static', emptyOutDir: false },
});
