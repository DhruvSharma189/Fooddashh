import { useState } from 'react';
import { categoryPhoto, defaultDishPhoto } from '../images';

const EMPTY = 'data:image/svg+xml;utf8,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 450"><rect width="600" height="450" fill="#d9e4df"/><text x="300" y="270" font-size="110" text-anchor="middle">🍽️</text></svg>');

/** Dish photo that never shows a broken icon: own image -> category photo -> neutral placeholder. */
export function DishImage({ item, className }) {
  const chain = [item.imageUrl, categoryPhoto[item.category] || defaultDishPhoto, EMPTY].filter(Boolean);
  const [i, setI] = useState(0);
  return <img className={className} src={chain[i]} alt={item.name} loading="lazy" onError={() => setI(n => Math.min(n + 1, chain.length - 1))} />;
}

/** Scenic photo on a soft gradient: if the URL ever fails, the gradient stays instead of a broken image. */
export function Scene({ src, alt, className = '' }) {
  const [ok, setOk] = useState(true);
  return <div className={`scene ${className}`}>{ok && <img src={src} alt={alt} loading="lazy" onError={() => setOk(false)} />}</div>;
}
