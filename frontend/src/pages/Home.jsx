import { Link } from 'react-router-dom';
import { photo } from '../images';
import { Scene } from '../components/Img';

const DOORS = [
  ['/explore', 'Explore the dishes', 'Biryani, pizza, burgers and sweets, by the craving.', photo.cafeTalk],
  ['/menu', 'See the full menu', 'Search, filter, and fill your cart.', photo.coffee],
  ['/story', 'Read our story', 'How a café on a mountain learned to cook.', photo.valley],
];

export default function Home() {
  return (
    <>
      <section className="hero">
        <Scene src={photo.lake} alt="A quiet alpine lake surrounded by forest and mountains" className="hero-bg" />
        <div className="hero-shade" />
        <div className="container hero-content">
          <div className="hero-kicker"><span /> A LITTLE ESCAPE IN EVERY BITE <span /></div>
          <h1>Good food.<br /><i>Higher<br className="hero-break" /> spirits.</i></h1>
          <p>A mountain-inspired menu made for slow mornings, long conversations and cravings worth following.</p>
          <div className="row gap hero-actions">
            <Link className="btn lg" to="/menu">Explore the menu ↗</Link>
            <Link className="btn lg glass" to="/orders">♧ Track your order</Link>
          </div>
          <div className="hero-meta"><span>★ &nbsp;4.8 guest rating</span><b>·</b><span>◷ &nbsp;Freshly made, always</span><b>·</b><span>♙ &nbsp;Inspired by the hills</span></div>
        </div>
        <div className="scroll-cue">SCROLL TO EXPLORE</div>
      </section>
      <section className="container doors" aria-label="Where to next">
        {DOORS.map(([to, title, text, img]) => (
          <Link className="door" to={to} key={to}>
            <Scene src={img} alt="" />
            <div><h2>{title}</h2><p>{text}</p></div>
          </Link>
        ))}
      </section>
      <section className="quote-band"><div className="container"><blockquote>“Come for the food. Stay for the quiet.”</blockquote></div></section>
    </>
  );
}
