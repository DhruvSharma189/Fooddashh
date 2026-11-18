import { Link } from 'react-router-dom';
import { photo } from '../images';
import { Scene } from '../components/Img';

export default function Story() {
  return (
    <>
      <section className="hero story-hero">
        <Scene src={photo.snowRange} alt="A range of snow peaks above the clouds" className="hero-bg" />
        <div className="hero-shade" />
        <div className="container hero-content">
          <h1>Some mornings<br /><i>are meant to be slow.</i></h1>
          <p>Up here, the clouds sit lower than the cups on the table, and nobody is in a hurry.</p>
        </div>
      </section>

      <section className="container chapter">
        <Scene src={photo.cafeFriends} alt="Friends laughing together around a wooden café table" className="chapter-img" />
        <div className="chapter-text">
          <h2>A table with a view</h2>
          <p>We opened with six wooden tables and one big window. The window did most of the work. People came in from the cold, sat down facing the valley, and forgot to check the time.</p>
          <p>Since then, the best seat in the house has never been empty for long.</p>
        </div>
      </section>

      <section className="pano">
        <Scene src={photo.valley} alt="Morning mist rolling through a mountain valley" />
        <blockquote><p>Good food tastes better with a view, and better still with someone to share it.</p></blockquote>
      </section>

      <section className="container chapter flip">
        <Scene src={photo.forest} alt="Sunlight filtering through a pine forest" className="chapter-img" />
        <div className="chapter-text">
          <h2>Cooked the slow way</h2>
          <p>The mountains teach patience. Our biryani rests in a sealed handi until the steam does its work. Our chai is simmered, never rushed. Every plate is a small trail: it starts with something simple and ends somewhere you didn’t expect.</p>
        </div>
      </section>

      <section className="container mosaic" aria-label="Moments from the café">
        <Scene src={photo.cafeInterior} alt="Warm café interior with wooden seating" />
        <Scene src={photo.lake} alt="A calm mountain lake at dawn" />
        <Scene src={photo.cafeTalk} alt="Two friends talking over coffee by the window" />
        <Scene src={photo.cafeCosy} alt="Guests sharing a meal at a cosy table" />
      </section>

      <section className="container chapter">
        <Scene src={photo.heroMountain} alt="A single snow peak glowing in the evening light" className="chapter-img" />
        <div className="chapter-text">
          <h2>Come as you are</h2>
          <p>Muddy boots, windswept hair, a long day behind you. Pull up a chair. The kettle is on, the kitchen is warm, and there is always room for one more.</p>
          <Link className="btn lg" to="/menu">Find your next favourite</Link>
        </div>
      </section>
    </>
  );
}
