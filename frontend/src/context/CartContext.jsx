import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { useToast } from './ToastContext';

const KEY = 'fooddash_cart';
const CartContext = createContext(null);
export const useCart = () => useContext(CartContext);

const read = () => { try { return JSON.parse(localStorage.getItem(KEY)) || []; } catch { return []; } };

export function CartProvider({ children }) {
  const [items, setItems] = useState(read);
  const [open, setOpen] = useState(false);
  const toast = useToast();
  useEffect(() => localStorage.setItem(KEY, JSON.stringify(items)), [items]);

  const value = useMemo(() => ({
    items, open, setOpen,
    count: items.reduce((s, x) => s + x.qty, 0),
    total: items.reduce((s, x) => s + x.qty * x.price, 0),
    add(m, qty = 1) {
      setItems(c => c.some(x => x.id === m.id)
        ? c.map(x => (x.id === m.id ? { ...x, qty: Math.min(20, x.qty + qty) } : x))
        : [...c, { id: m.id, name: m.name, price: m.price, imageUrl: m.imageUrl, category: m.category, veg: m.veg, qty: Math.min(20, qty) }]);
      toast(`${m.name} added to your cart`);
    },
    change: (id, d) => setItems(c => c.map(x => (x.id === id ? { ...x, qty: Math.min(20, x.qty + d) } : x)).filter(x => x.qty > 0)),
    remove: id => setItems(c => c.filter(x => x.id !== id)),
    clear: () => setItems([]),
  }), [items, open, toast]);

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}
