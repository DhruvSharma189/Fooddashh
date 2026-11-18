import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Field } from '../components/CartDrawer';

export default function Login({ register: startAsRegister = false }) {
  const { login, register, user, isAdmin } = useAuth();
  const navigate = useNavigate();
  const loc = useLocation();
  const [signUp, setSignUp] = useState(startAsRegister);
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [busy, setBusy] = useState(false);
  const set = k => e => setForm(f => ({ ...f, [k]: e.target.value }));

  if (user) {
    return (
      <div className="container page-pad narrow">
        <h1>Hi, {user.name}</h1>
        <p className="muted">You’re signed in.</p>
        <div className="row gap"><Link className="btn" to="/menu">Browse the menu</Link><Link className="btn ghost" to="/orders">My orders</Link>{isAdmin && <a className="btn ghost" href="/admin.html">Admin dashboard</a>}</div>
      </div>
    );
  }

  const submit = async e => {
    e.preventDefault();
    setBusy(true); setErrors({});
    try {
      const data = signUp ? await register(form.name.trim(), form.email.trim(), form.password) : await login(form.email.trim(), form.password);
      navigate(data.roles?.includes('ADMIN') && !loc.state?.from ? '/' : loc.state?.from || '/menu', { replace: true });
    } catch (err) {
      setErrors({ ...(err.errors || {}), form: err.message });
    } finally { setBusy(false); }
  };

  return (
    <div className="container page-pad narrow">
      <h1>{signUp ? 'Create your account' : 'Welcome back'}</h1>
      {loc.state?.notice && <p className="notice">{loc.state.notice}</p>}
      <form className="form" onSubmit={submit} noValidate>
        {signUp && <Field label="Name" error={errors.name}><input value={form.name} onChange={set('name')} autoComplete="name" /></Field>}
        <Field label="Email" error={errors.email}><input type="email" value={form.email} onChange={set('email')} autoComplete="username" /></Field>
        <Field label={signUp ? 'Password (8+ characters)' : 'Password'} error={errors.password}><input type="password" value={form.password} onChange={set('password')} autoComplete={signUp ? 'new-password' : 'current-password'} /></Field>
        {errors.form && <p className="form-error" role="alert">{errors.form}</p>}
        <button className="btn lg block" disabled={busy}>{busy ? 'Please wait…' : signUp ? 'Create account' : 'Sign in'}</button>
      </form>
      <p className="muted">{signUp ? 'Already have an account?' : 'New here?'} <button className="link-btn" onClick={() => { setSignUp(s => !s); setErrors({}); }}>{signUp ? 'Sign in' : 'Create an account'}</button></p>
    </div>
  );
}
