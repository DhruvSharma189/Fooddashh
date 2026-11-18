# FoodDash — Luxury Himalayan Café

FoodDash is a Spring Boot 3 + MongoDB food-ordering application. The customer storefront and Alpine Luxury admin workspace are served by Spring Boot from `src/main/resources/static`.

## Requirements
- Java 17 or newer (Java 21 is supported)
- Maven 3.8+
- MongoDB local or MongoDB Atlas
- Internet access for CDN-hosted fonts, charts and hero photography

## Configure local development (Windows PowerShell)
Set these environment variables in the VS Code terminal before starting the app. Use your own MongoDB URI; do not commit it to Git.

```powershell
$env:MONGODB_URI = "mongodb+srv://<db-user>:<new-password>@<cluster>/<database>?retryWrites=true&w=majority"
$env:JWT_SECRET = "replace-with-a-random-secret-at-least-32-characters-long"
$env:FOODDASH_ADMIN_EMAIL = "your-admin-email@example.com"
$env:FOODDASH_ADMIN_PASSWORD = "choose-a-unique-password-at-least-12-chars"
$env:FOODDASH_ADMIN_NAME = "FoodDash Admin"
$env:SERVER_PORT = "8098"
```

Optional Cloudinary configuration (only needed for image uploads):

```powershell
$env:CLOUDINARY_CLOUD_NAME = "your-cloud-name"
$env:CLOUDINARY_API_KEY = "your-api-key"
$env:CLOUDINARY_API_SECRET = "your-api-secret"
```

Run from the directory containing `pom.xml`:

```powershell
mvn spring-boot:run
```

Open:
- Storefront: `http://localhost:8098/`
- Admin sign-in: `http://localhost:8098/login.html`
- Admin dashboard: `http://localhost:8098/admin.html` (redirects to login without an admin JWT)

On first startup, `FOODDASH_ADMIN_EMAIL` and `FOODDASH_ADMIN_PASSWORD` create the first administrator if that email is not already registered. The password must be at least 12 characters. Keep the values private. If the variables are omitted, no default admin account is created.

## Authentication and authorization
- `POST /api/auth/register` creates a CUSTOMER account. Public registration cannot assign ADMIN.
- `POST /api/auth/login` verifies BCrypt password hashes and returns a signed short-lived JWT (15 minutes by default).
- Send the token as `Authorization: Bearer <token>` for protected APIs.
- ADMIN-only: menu create/update/delete, image upload, order list/status changes and all `/api/admin/**` endpoints.
- Public customer experience remains guest-friendly: menu browsing, order placement and order tracking by order ID remain available as before. The order list and order status mutation are protected.
- JWT access tokens are stored in `sessionStorage` by the admin UI and cleared on sign out. This implementation does not issue refresh tokens; sign in again when the access token expires.
- Use a unique random `JWT_SECRET` of at least 32 bytes in every environment. Production profile requires `MONGODB_URI` and `JWT_SECRET` to be configured.

## Admin features
- Alpine Luxury responsive admin workspace and split-screen login
- Overview KPIs, revenue trend, live orders and best sellers
- Menu and category management with inventory quantities
- Automatic stock deduction after order placement and low-stock alerts (threshold 5)
- Sales analytics and CSV export
- Admin user creation and enable/disable controls
- MongoDB audit events for sign-ins, registrations, menu changes, order placement/status changes, admin creation and sales exports

## Existing API compatibility
Existing menu, order, category, image and status endpoints are retained. `stockQuantity` is an additive menu field. Existing MongoDB menu documents without that field are treated as having 50 units until edited or ordered; no collection reset or reseed is required. The initial seeder still inserts the existing 17 dishes in the existing five categories only when the menu collection is empty.

## Important security note
Never commit MongoDB credentials, Cloudinary secrets or JWT secrets. If a real MongoDB password was previously included in a project file, terminal log or shared upload, rotate it in MongoDB Atlas and update `MONGODB_URI`.

---

## React frontend (new)

The storefront is now a React app in `frontend/` (Vite + React Router). Pages: **Home** `/`, **Explore** `/explore`, **Our menu** `/menu`, **Our story** `/story`, **Sign in / Register** `/login`, **My orders** `/orders`.

```bash
cd frontend
npm install
npm run dev      # http://localhost:5173  (proxies /api to Spring Boot on :8098)
npm run build    # writes into src/main/resources/static so Spring Boot serves it
```

Photography is all in `frontend/src/images.js` — replace any URL with your own photos.

The admin dashboard (`/admin.html`) is still the original static page; it now shares the same login/token storage.

## Auth: tokens, refresh, 401 vs 403

- Login returns a 15-minute **access token** (`token`) and a 7-day **refresh token** (`refreshToken`, single-use, stored hashed in MongoDB `refresh_tokens`).
- Both are kept in `localStorage` (`fooddash_access_token`, `fooddash_refresh_token`, `fooddash_user`).
- `POST /api/auth/refresh` and `POST /api/auth/logout` take `{ "refreshToken": "..." }`.
- Backend now answers **401** for a missing/expired token and **403** only when a signed-in user lacks permission (before, both were 403).
- `frontend/src/api/client.js` refreshes the token automatically (before it expires, and once more on a 401), retries the request, and shares one refresh between parallel calls. Users only see a message if the refresh token itself is gone.

## Orders per customer

- Placing an order now requires sign-in; the order stores the customer's email.
- `GET /api/orders/mine` returns all of the signed-in customer's orders (newest first). `GET /api/orders/{id}` works only for the owner or an admin.
- Existing orders from before this change have no owner and are visible to admins only.

## Dish images

`DataSeeder` now uses real food photos and, on startup, fills any menu item that has no image (or an old `picsum.photos` placeholder) — this fixes Biryani on existing databases.
