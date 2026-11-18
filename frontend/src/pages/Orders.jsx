import { useCallback, useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { api } from '../api/client';
import { ACTIVE, STATUS, STEPS, formatDate, money, shortId } from '../api/format';
import { useToast } from '../context/ToastContext';

export default function Orders() {
  const [orders, setOrders] = useState(null);
  const toast = useToast();
  const { state } = useLocation();

  const load = useCallback(() => api.get('/api/orders/mine').then(setOrders).catch(e => { toast(e.message, 'error'); setOrders(o => o || []); }), [toast]);
  useEffect(() => { load(); }, [load]);

  // keep live statuses fresh while any order is still on its way
  const anyActive = orders?.some(o => ACTIVE.includes(o.status));
  useEffect(() => { if (!anyActive) return; const t = setInterval(load, 10000); return () => clearInterval(t); }, [anyActive, load]);

  return (
    <div className="container page-pad narrow-wide">
      <header className="page-head"><h1>My orders</h1><p>Everything you’ve ordered, newest first.</p></header>
      {!orders && <p className="muted">Loading your orders…</p>}
      {orders?.length === 0 && <div className="empty"><p>You haven’t ordered yet.</p><Link className="btn" to="/menu">Browse the menu</Link></div>}
      <div className="orders">
        {orders?.map(o => <OrderCard key={o.id} o={o} fresh={state?.placed === o.id} />)}
      </div>
    </div>
  );
}

function OrderCard({ o, fresh }) {
  const idx = STEPS.indexOf(o.status);
  return (
    <article className={`order ${fresh ? 'fresh' : ''}`}>
      <div className="row between wrap">
        <div><strong>Order #{shortId(o.id)}</strong><span className="muted"> · {formatDate(o.createdAt)}</span></div>
        <span className={`pill ${o.status}`}>{STATUS[o.status]}</span>
      </div>
      {o.status === 'CANCELLED' ? <p className="muted">This order was cancelled.</p> : (
        <ol className="steps" aria-label="Order progress">
          {STEPS.map((s, i) => <li key={s} className={`${i <= idx ? 'done' : ''} ${i === idx && idx < 3 ? 'now' : ''}`}>{STATUS[s]}</li>)}
        </ol>
      )}
      <div className="order-items">{o.items.map((i, k) => <div className="row between" key={k}><span>{i.quantity} × {i.name}</span><span>{money(i.price * i.quantity)}</span></div>)}</div>
      <div className="row between total"><span className="muted">{o.address}</span><b>{money(o.total)}</b></div>
    </article>
  );
}
