import { money } from '../api/format';
import { DishImage } from './Img';

export const VegMark = ({ veg }) => <span className={`vegmark ${veg ? '' : 'non'}`} title={veg ? 'Veg' : 'Non-veg'} aria-label={veg ? 'Vegetarian' : 'Non-vegetarian'} />;

export default function DishCard({ dish, onAdd, onOpen, fav, onFav }) {
  return (
    <article className={`dish ${dish.available ? '' : 'soldout'}`}>
      <button className="dish-photo" onClick={() => onOpen?.(dish)} aria-label={`View ${dish.name}`}><DishImage item={dish} /></button>
      {onFav && <button className={`heart ${fav ? 'on' : ''}`} onClick={() => onFav(dish.id)} aria-label={fav ? 'Remove from favourites' : 'Save to favourites'} aria-pressed={!!fav}>{fav ? '♥' : '♡'}</button>}
      <div className="dish-body">
        <div className="dish-top"><span>{dish.category}</span><VegMark veg={dish.veg} /></div>
        <h3>{dish.name}</h3>
        <p>{dish.description}</p>
        <div className="dish-foot">
          <span className="price">{money(dish.price)}</span>
          <span className="rating" aria-label={`Rated ${Number(dish.rating).toFixed(1)} out of 5`}>★ {Number(dish.rating).toFixed(1)}</span>
          <button className="btn small" disabled={!dish.available} onClick={() => onAdd(dish)}>{dish.available ? 'Add' : 'Sold out'}</button>
        </div>
      </div>
    </article>
  );
}
