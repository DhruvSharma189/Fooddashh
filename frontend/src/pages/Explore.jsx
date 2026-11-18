import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import { categoryNote, categoryPhoto } from '../images';
import { useCart } from '../context/CartContext';
import { useToast } from '../context/ToastContext';
import { Scene } from '../components/Img';
import DishCard from '../components/DishCard';
import DishModal from '../components/DishModal';

export default function Explore() {
  const [dishes, setDishes] = useState(null);
  const [open, setOpen] = useState(null);
  const cart = useCart();
  const toast = useToast();

  useEffect(() => { api.get('/api/menu', { auth: false }).then(setDishes).catch(e => { toast(e.message, 'error'); setDishes([]); }); }, [toast]);

  const groups = useMemo(() => {
    const map = new Map();
    (dishes || []).forEach(d => map.set(d.category, [...(map.get(d.category) || []), d]));
    return [...map].map(([name, list]) => ({ name, count: list.length, picks: [...list].sort((a, b) => b.rating - a.rating).slice(0, 3) }));
  }, [dishes]);

  return (
    <div className="container page-pad">
      <header className="page-head">
        <h1>What are you craving?</h1>
        <p>Start with a mood. Each shelf shows the three dishes our guests love most.</p>
      </header>

      {!dishes && <p className="muted">Loading the shelves…</p>}

      <div className="cat-tiles">
        {groups.map(g => (
          <Link key={g.name} to={`/menu?category=${encodeURIComponent(g.name)}`} className="cat-tile">
            <Scene src={categoryPhoto[g.name] || categoryPhoto.Drinks} alt="" />
            <div><h2>{g.name}</h2><span>{g.count} dish{g.count === 1 ? '' : 'es'}</span></div>
          </Link>
        ))}
      </div>

      {groups.map(g => (
        <section key={g.name} className="shelf">
          <div className="shelf-head">
            <div><h2>{g.name}</h2><p>{categoryNote[g.name] || 'Made fresh, every order.'}</p></div>
            <Link className="link-btn" to={`/menu?category=${encodeURIComponent(g.name)}`}>See all {g.name.toLowerCase()}</Link>
          </div>
          <div className="grid three">
            {g.picks.map(d => <DishCard key={d.id} dish={d} onAdd={cart.add} onOpen={setOpen} />)}
          </div>
        </section>
      ))}
      {open && <DishModal dish={open} onClose={() => setOpen(null)} onAdd={cart.add} />}
    </div>
  );
}
