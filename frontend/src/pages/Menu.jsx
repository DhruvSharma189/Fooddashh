import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { api } from '../api/client';
import { useCart } from '../context/CartContext';
import { useToast } from '../context/ToastContext';
import { useFavourites } from '../context/useFavourites';
import DishCard from '../components/DishCard';
import DishModal from '../components/DishModal';

export default function Menu() {
  const [params, setParams] = useSearchParams();
  const category = params.get('category') || '';
  const veg = params.get('veg') || '';
  const sort = params.get('sort') || '';
  const [q, setQ] = useState(params.get('q') || '');
  const [categories, setCategories] = useState([]);
  const [dishes, setDishes] = useState(null);
  const [open, setOpen] = useState(null);
  const [favs, toggleFav] = useFavourites();
  const cart = useCart();
  const toast = useToast();

  const update = (k, v) => setParams(p => { const n = new URLSearchParams(p); v ? n.set(k, v) : n.delete(k); return n; }, { replace: true });
  const favOnly = category === '__fav';

  useEffect(() => { api.get('/api/menu/categories', { auth: false }).then(setCategories).catch(() => {}); }, []);
  useEffect(() => { const t = setTimeout(() => update('q', q.trim()), 350); return () => clearTimeout(t); }, [q]);

  const search = params.get('q') || '';
  useEffect(() => {
    setDishes(null);
    const qs = new URLSearchParams();
    if (category && !favOnly) qs.set('category', category);
    if (veg) qs.set('veg', veg);
    if (sort) qs.set('sort', sort);
    if (search) qs.set('search', search);
    api.get('/api/menu?' + qs, { auth: false }).then(setDishes).catch(e => { toast(e.message, 'error'); setDishes([]); });
  }, [category, veg, sort, search, favOnly, toast]);

  const shown = useMemo(() => (dishes || []).filter(d => !favOnly || favs.has(d.id)), [dishes, favOnly, favs]);

  return (
    <div className="container page-pad">
      <header className="page-head">
        <h1>Our menu</h1>
        <p>{dishes ? `${shown.length} dish${shown.length === 1 ? '' : 'es'} ready` : 'Loading…'}</p>
      </header>

      <div className="filters">
        <input type="search" value={q} onChange={e => setQ(e.target.value)} placeholder="Search dishes" aria-label="Search dishes" />
        <div className="seg" role="group" aria-label="Diet">
          {[['', 'All'], ['true', 'Veg'], ['false', 'Non-veg']].map(([v, l]) => <button key={l} className={veg === v ? 'on' : ''} onClick={() => update('veg', v)}>{l}</button>)}
        </div>
        <select value={sort} onChange={e => update('sort', e.target.value)} aria-label="Sort">
          <option value="">Top rated</option><option value="price_asc">Price: low to high</option><option value="price_desc">Price: high to low</option><option value="newest">Newest</option>
        </select>
      </div>
      <div className="chips" role="group" aria-label="Category">
        {[['', 'All'], ['__fav', '♥ Favourites'], ...categories.map(c => [c, c])].map(([v, l]) => <button key={v || 'all'} className={category === v ? 'on' : ''} onClick={() => update('category', v)}>{l}</button>)}
      </div>

      {dishes && shown.length === 0 && <p className="empty">{favOnly ? 'No favourites yet. Tap the heart on a dish to save it here.' : 'No dishes found. Try another search or filter.'}</p>}
      <div className="grid four">
        {!dishes && Array.from({ length: 8 }, (_, i) => <div key={i} className="skeleton" />)}
        {shown.map(d => <DishCard key={d.id} dish={d} onAdd={cart.add} onOpen={setOpen} fav={favs.has(d.id)} onFav={toggleFav} />)}
      </div>
      {open && <DishModal dish={open} onClose={() => setOpen(null)} onAdd={cart.add} />}
    </div>
  );
}
