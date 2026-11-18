import { useEffect, useState } from 'react';
import { Link, NavLink, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';

const LINKS = [['/', 'Home'], ['/explore', 'Explore'], ['/menu', 'Our menu'], ['/story', 'Our story']];

export default function Navbar() {
  const { user, isAdmin, logout } = useAuth();
  const cart = useCart();
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);
  const [theme, setTheme] = useState(document.documentElement.dataset.theme || 'day');
  useEffect(() => setMenuOpen(false), [pathname]);
  const flip = () => {
    const t = theme === 'night' ? 'day' : 'night';
    document.documentElement.dataset.theme = t;
    localStorage.setItem('fooddash_theme', t);
    setTheme(t);
  };

  return (
    <header className="nav">
      <div className="container nav-row">
        <Link to="/" className="brand" aria-label="FoodDash home">
          <span className="brand-mark" aria-hidden="true" />
          <span className="brand-copy"><b>FoodDash</b><small>THE MOUNTAIN CAFÉ</small></span>
        </Link>
        <nav className={`nav-links ${menuOpen ? 'open' : ''}`} aria-label="Main">
          {LINKS.map(([to, label]) => <NavLink key={to} to={to} end={to === '/'}>{label}</NavLink>)}
          {user && <NavLink to="/orders">My orders</NavLink>}
          {isAdmin && <a href="/admin.html">Admin</a>}
        </nav>
        <div className="nav-tools">
          <button className="icon-btn nav-search" onClick={() => navigate('/explore')} aria-label="Explore menu">⌕</button>
          <button className="icon-btn nav-heart" onClick={() => navigate('/explore')} aria-label="Explore favourites">♡</button>
          <Link className="track-link" to={user ? '/orders' : '/login'}>♧ <span>Track order</span></Link>
          <button className="icon-btn" onClick={flip} aria-label="Toggle theme">{theme === 'night' ? '☀' : '☾'}</button>
          <button className="icon-btn nav-grid" onClick={() => { window.location.href = '/login.html'; }} aria-label="Admin control room">▦</button>
          {user && <button className="icon-btn" onClick={() => { logout(); navigate('/'); }} aria-label="Sign out">↪</button>}
          <button className="cart-btn" onClick={() => cart.setOpen(true)} aria-label={`Open cart, ${cart.count} items`}><span className="cart-bag">♧</span><span className="cart-count">{cart.count}</span></button>
          <button className="icon-btn menu-toggle" onClick={() => setMenuOpen(o => !o)} aria-label="Toggle navigation" aria-expanded={menuOpen}>☰</button>
        </div>
      </div>
    </header>
  );
}
