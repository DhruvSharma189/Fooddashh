import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { money } from '../api/format';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import { useToast } from '../context/ToastContext';
import { DishImage } from './Img';
import Modal from './Modal';

export default function CartDrawer() {
  const cart = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [checkout, setCheckout] = useState(false);

  useEffect(() => {
    if (!cart.open) return;
    const onKey = e => e.key === 'Escape' && cart.setOpen(false);
    document.addEventListener('keydown', onKey);
    return () => document.removeEventListener('keydown', onKey);
  }, [cart.open]);

  const startCheckout = () => {
    cart.setOpen(false);
    if (!user) return navigate('/login', { state: { from: '/menu', notice: 'Sign in to place your order. Your cart will be waiting.' } });
    setCheckout(true);
  };

  return (
    <>
      <div className={`scrim ${cart.open ? 'show' : ''}`} onClick={() => cart.setOpen(false)} />
      <aside className={`drawer ${cart.open ? 'open' : ''}`} aria-label="Your cart" aria-hidden={!cart.open}>
        <div className="drawer-head"><h2>Your cart</h2><button className="icon-btn" onClick={() => cart.setOpen(false)} aria-label="Close cart">×</button></div>
        <div className="drawer-items">
          {cart.items.length === 0 && <p className="empty">Nothing here yet. Pick something warm from the menu.</p>}
          {cart.items.map(x => (
            <div className="cart-line" key={x.id}>
              <DishImage item={x} />
              <div>
                <strong>{x.name}</strong>
                <span className="muted">{money(x.price)}</span>
                <div className="qty small"><button onClick={() => cart.change(x.id, -1)} aria-label={`Less ${x.name}`}>−</button><span>{x.qty}</span><button onClick={() => cart.change(x.id, 1)} aria-label={`More ${x.name}`}>+</button></div>
              </div>
              <div className="cart-line-end"><b>{money(x.price * x.qty)}</b><button className="link-btn" onClick={() => cart.remove(x.id)}>Remove</button></div>
            </div>
          ))}
        </div>
        <div className="drawer-foot">
          <div className="row between"><span>Total</span><b className="price big">{money(cart.total)}</b></div>
          <button className="btn block" disabled={!cart.items.length} onClick={startCheckout}>{user ? 'Place order' : 'Sign in to order'}</button>
        </div>
      </aside>
      {checkout && <CheckoutModal onClose={() => setCheckout(false)} />}
    </>
  );
}

function CheckoutModal({ onClose }) {
  const cart = useCart();
  const { user } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const [form, setForm] = useState({ customerName: user?.name || '', phone: '', address: '' });
  const [errors, setErrors] = useState({});
  const [busy, setBusy] = useState(false);
  const set = k => e => setForm(f => ({ ...f, [k]: e.target.value }));

  const submit = async e => {
    e.preventDefault();
    setBusy(true); setErrors({});
    try {
      const order = await api.post('/api/orders', {
        customerName: form.customerName.trim(), phone: form.phone.trim(), address: form.address.trim(),
        items: cart.items.map(x => ({ menuItemId: x.id, quantity: x.qty })),
      });
      cart.clear(); onClose();
      toast('Order placed. The kitchen is on it.');
      navigate('/orders', { state: { placed: order.id } });
    } catch (err) {
      setErrors(err.errors || {});
      toast(err.errors?.items || err.message, 'error');
    } finally { setBusy(false); }
  };

  return (
    <Modal title="Delivery details" onClose={onClose}>
      <form onSubmit={submit} noValidate className="form">
        <h2>Delivery details</h2>
        <div className="summary">
          {cart.items.map(x => <div className="row between" key={x.id}><span>{x.qty} × {x.name}</span><span>{money(x.qty * x.price)}</span></div>)}
          <div className="row between total"><b>Total</b><b>{money(cart.total)}</b></div>
        </div>
        <Field label="Name" error={errors.customerName}><input value={form.customerName} onChange={set('customerName')} autoComplete="name" /></Field>
        <Field label="Phone (10 digits)" error={errors.phone}><input value={form.phone} onChange={set('phone')} inputMode="numeric" maxLength={10} autoComplete="tel" /></Field>
        <Field label="Delivery address" error={errors.address}><textarea rows={2} value={form.address} onChange={set('address')} autoComplete="street-address" /></Field>
        <div className="row end"><button type="button" className="btn ghost" onClick={onClose}>Cancel</button><button className="btn" disabled={busy}>{busy ? 'Placing…' : 'Confirm order'}</button></div>
      </form>
    </Modal>
  );
}

export const Field = ({ label, error, children }) => (
  <label className={`field ${error ? 'bad' : ''}`}><span>{label}</span>{children}{error && <em role="alert">{error}</em>}</label>
);
