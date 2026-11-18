import { useState } from 'react';
import { money } from '../api/format';
import { DishImage } from './Img';
import Modal from './Modal';
import { VegMark } from './DishCard';

export default function DishModal({ dish, onClose, onAdd }) {
  const [qty, setQty] = useState(1);
  return (
    <Modal title={dish.name} onClose={onClose} wide>
      <div className="dish-modal">
        <DishImage item={dish} className="dish-modal-img" />
        <div className="dish-modal-body">
          <small>{dish.category} · <VegMark veg={dish.veg} /> {dish.veg ? 'Veg' : 'Non-veg'}</small>
          <h2>{dish.name}</h2>
          <p>{dish.description}</p>
          <div className="price big">{money(dish.price)}</div>
          {dish.available ? (
            <>
              <div className="qty"><button onClick={() => setQty(q => Math.max(1, q - 1))} aria-label="Less">−</button><span>{qty}</span><button onClick={() => setQty(q => Math.min(20, q + 1))} aria-label="More">+</button></div>
              <button className="btn block" onClick={() => { onAdd(dish, qty); onClose(); }}>Add {qty} to cart · {money(dish.price * qty)}</button>
            </>
          ) : <p className="muted">Sold out for now.</p>}
        </div>
      </div>
    </Modal>
  );
}
