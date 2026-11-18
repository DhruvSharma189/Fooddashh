const money = n => '₹' + Number(n).toLocaleString('en-IN', { maximumFractionDigits: 2 });
const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const $ = id => document.getElementById(id);
const debounce = (fn, ms = 350) => { let t; return (...a) => { clearTimeout(t); t = setTimeout(() => fn(...a), ms); }; };
const STATUS = { PLACED: 'Placed', PREPARING: 'Preparing', OUT_FOR_DELIVERY: 'Out for delivery', DELIVERED: 'Delivered', CANCELLED: 'Cancelled' };

// ---------- auth tokens (localStorage) + automatic refresh ----------
const TOKEN_KEY = 'fooddash_access_token', REFRESH_KEY = 'fooddash_refresh_token', USER_KEY = 'fooddash_user';
function saveSession(d) {
  localStorage.setItem(TOKEN_KEY, d.token);
  if (d.refreshToken) localStorage.setItem(REFRESH_KEY, d.refreshToken);
  localStorage.setItem(USER_KEY, JSON.stringify({ name: d.name, email: d.email, roles: d.roles }));
}
function clearSession() { [TOKEN_KEY, REFRESH_KEY, USER_KEY].forEach(k => localStorage.removeItem(k)); }
let refreshing = null; // one refresh at a time; parallel requests share it
function refreshSession() {
  if (!refreshing) {
    refreshing = (async () => {
      const rt = localStorage.getItem(REFRESH_KEY);
      if (!rt) return false;
      try {
        const r = await fetch('/api/auth/refresh', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ refreshToken: rt }) });
        if (!r.ok) return false;
        saveSession(await r.json());
        return true;
      } catch { return false; }
    })().finally(() => { refreshing = null; });
  }
  return refreshing;
}
async function authFetch(url, opts = {}) {
  const send = () => {
    const headers = { ...(opts.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }), ...(opts.headers || {}) };
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) headers.Authorization = `Bearer ${token}`;
    return fetch(url, { ...opts, headers });
  };
  let res = await send();
  if (res.status === 401 && await refreshSession()) res = await send(); // expired token: refresh once, retry
  if (res.status === 401) { clearSession(); location.href = 'login.html'; }
  return res;
}
async function api(url, opts = {}) {
  const res = await authFetch(url, opts);
  if (res.status === 204) return null;
  const data = await res.json().catch(() => null);
  if (!res.ok) {
    const err = new Error(res.status === 403 ? 'You do not have permission to do that.' : (data?.message || 'Request failed'));
    err.status = res.status;
    err.errors = data?.errors || {};
    throw err;
  }
  return data;
}

function stars(r) {
  let h = '';
  for (let i = 1; i <= 5; i++) h += `<i class="bi ${r >= i ? 'bi-star-fill' : r >= i - 0.5 ? 'bi-star-half' : 'bi-star'}"></i>`;
  return `<span class="stars">${h}</span> <small class="text-secondary">${Number(r).toFixed(1)}</small>`;
}
const vegMark = v => `<span class="vegmark ${v ? '' : 'non'}" title="${v ? 'Veg' : 'Non-veg'}"></span>`;

function toast(msg, type = 'success') {
  const el = document.createElement('div');
  el.className = `toast align-items-center border-0 text-bg-${type === 'error' ? 'danger' : 'success'}`;
  el.innerHTML = `<div class="d-flex"><div class="toast-body">${esc(msg)}</div><button class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button></div>`;
  $('toasts').appendChild(el);
  el.addEventListener('hidden.bs.toast', () => el.remove());
  new bootstrap.Toast(el, { delay: 2800 }).show();
}

const EMOJI = { Pizza: '🍕', Burgers: '🍔', Biryani: '🍛', Desserts: '🍰', Drinks: '🥤' };
const emo = c => EMOJI[c] || '🍽️';
const FALLBACK = 'data:image/svg+xml;utf8,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 600 450"><rect width="600" height="450" fill="#e8dfcc"/><text x="300" y="270" font-size="120" text-anchor="middle">🍽️</text></svg>');

(function initTheme() {
  const root = document.documentElement;
  const apply = t => {
    root.dataset.theme = t;
    root.setAttribute('data-bs-theme', t === 'night' ? 'dark' : 'light');
    const b = $('themeBtn');
    if (b) b.innerHTML = t === 'night' ? '<i class="bi bi-sun-fill"></i>' : '<i class="bi bi-moon-stars-fill"></i>';
    window.dispatchEvent(new Event('themechange'));
  };
  apply(localStorage.getItem('fooddash_theme') || 'day');
  const b = $('themeBtn');
  if (b) b.addEventListener('click', () => {
    const t = root.dataset.theme === 'night' ? 'day' : 'night';
    localStorage.setItem('fooddash_theme', t);
    apply(t);
  });
})();
