// Single place that talks to the backend.
// - Tokens live in localStorage (same keys the admin dashboard uses, so one login works for both).
// - An expired access token is refreshed automatically and the request is retried, so the user never sees a 401/403.
// - A real 403 ("not allowed") is turned into a friendly message instead of a raw error.

const TOKEN = 'fooddash_access_token';
const REFRESH = 'fooddash_refresh_token';
const USER = 'fooddash_user';

export const session = {
  get access() { return localStorage.getItem(TOKEN); },
  get refresh() { return localStorage.getItem(REFRESH); },
  get user() { try { return JSON.parse(localStorage.getItem(USER)); } catch { return null; } },
  save(d) {
    localStorage.setItem(TOKEN, d.token);
    if (d.refreshToken) localStorage.setItem(REFRESH, d.refreshToken);
    localStorage.setItem(USER, JSON.stringify({ name: d.name, email: d.email, roles: d.roles }));
  },
  clear() { [TOKEN, REFRESH, USER].forEach(k => localStorage.removeItem(k)); },
};

export class ApiError extends Error {
  constructor(message, status, errors) { super(message); this.status = status; this.errors = errors || {}; }
}

function expiresSoon(token, windowMs = 30000) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
    return payload.exp * 1000 - Date.now() < windowMs;
  } catch { return true; }
}

let inflight = null; // parallel requests share one refresh call (refresh tokens are single-use)
// resolves to 'ok' | 'invalid' (refresh token rejected) | 'error' (network/server problem)
function refreshSession() {
  if (!inflight) {
    inflight = (async () => {
      const refreshToken = session.refresh;
      if (!refreshToken) return 'invalid';
      try {
        const res = await fetch('/api/auth/refresh', {
          method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ refreshToken }),
        });
        if (res.ok) { session.save(await res.json()); return 'ok'; }
        return res.status === 401 || res.status === 400 ? 'invalid' : 'error';
      } catch { return 'error'; }
    })().finally(() => { inflight = null; });
  }
  return inflight;
}

function endSession() {
  session.clear();
  window.dispatchEvent(new Event('auth:expired'));
}

async function request(path, { method = 'GET', body, auth = true, retried = false } = {}) {
  // 1) refresh slightly *before* the token dies, so most requests never hit a 401 at all
  if (auth && session.access && session.refresh && expiresSoon(session.access)) {
    if ((await refreshSession()) === 'invalid') endSession();
  }

  const headers = {};
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (auth && session.access) headers.Authorization = `Bearer ${session.access}`;

  let res;
  try {
    res = await fetch(path, { method, headers, body: body !== undefined ? JSON.stringify(body) : undefined });
  } catch {
    throw new ApiError('Cannot reach the server. Check your connection and try again.', 0);
  }

  // 2) token rejected anyway (e.g. clock skew): refresh once and retry the same request
  if (res.status === 401 && auth && !retried && session.refresh) {
    const r = await refreshSession();
    if (r === 'ok') return request(path, { method, body, auth, retried: true });
    if (r === 'invalid') endSession();
  } else if (res.status === 401 && auth && session.access) {
    endSession();
  }

  if (res.status === 204) return null;
  const data = await res.json().catch(() => null);
  if (!res.ok) {
    let message = data?.message || 'Something went wrong. Please try again.';
    if (res.status === 401 && auth) message = 'Your session has ended. Please sign in again.';
    if (res.status === 403) message = data?.message && data.message !== 'Forbidden' ? data.message : "You don't have permission to do that.";
    throw new ApiError(message, res.status, data?.errors);
  }
  return data;
}

export const api = {
  get: (p, o) => request(p, { ...o }),
  post: (p, body, o) => request(p, { method: 'POST', body, ...o }),
  patch: (p, body, o) => request(p, { method: 'PATCH', body, ...o }),
  put: (p, body, o) => request(p, { method: 'PUT', body, ...o }),
  del: (p, o) => request(p, { method: 'DELETE', ...o }),
};
