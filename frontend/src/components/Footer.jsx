import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container footer-row">
        <div><strong>FoodDash</strong><p>Mountain café kitchen, at your door.</p></div>
        <nav aria-label="Footer"><Link to="/explore">Explore</Link><Link to="/menu">Our menu</Link><Link to="/story">Our story</Link></nav>
        <small>© 2026 FoodDash</small>
      </div>
    </footer>
  );
}
