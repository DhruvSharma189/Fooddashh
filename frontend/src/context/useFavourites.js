import { useCallback, useState } from 'react';

const KEY = 'fooddash_favs';
export function useFavourites() {
  const [favs, setFavs] = useState(() => { try { return new Set(JSON.parse(localStorage.getItem(KEY)) || []); } catch { return new Set(); } });
  const toggle = useCallback(id => setFavs(prev => {
    const next = new Set(prev);
    next.has(id) ? next.delete(id) : next.add(id);
    localStorage.setItem(KEY, JSON.stringify([...next]));
    return next;
  }), []);
  return [favs, toggle];
}
