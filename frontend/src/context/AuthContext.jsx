import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { api, session } from '../api/client';
import { useToast } from './ToastContext';

const AuthContext = createContext(null);
export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => (session.access ? session.user : null));
  const toast = useToast();

  // The API client fires this only when the refresh token itself is no longer valid (e.g. after 7 days).
  useEffect(() => {
    const onExpired = () => { setUser(null); toast('Your session ended. Please sign in again.', 'error'); };
    window.addEventListener('auth:expired', onExpired);
    return () => window.removeEventListener('auth:expired', onExpired);
  }, [toast]);

  const finish = data => { session.save(data); setUser(session.user); return data; };
  const login = useCallback(async (email, password) => finish(await api.post('/api/auth/login', { email, password }, { auth: false })), []);
  const register = useCallback(async (name, email, password) => finish(await api.post('/api/auth/register', { name, email, password }, { auth: false })), []);
  const logout = useCallback(async () => {
    const refreshToken = session.refresh;
    if (refreshToken) api.post('/api/auth/logout', { refreshToken }, { auth: false }).catch(() => {});
    session.clear(); setUser(null);
  }, []);

  const value = useMemo(() => ({ user, isAdmin: !!user?.roles?.includes('ADMIN'), login, register, logout }), [user, login, register, logout]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
