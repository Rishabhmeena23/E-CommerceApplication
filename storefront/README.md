# Mercato Storefront

A role-aware React and JavaScript frontend for the secured e-commerce
microservices backend.

## Run locally

From the backend directory, the recommended local startup command is:

```bash
bash scripts/start-local.sh
```

It starts the infrastructure-facing Spring services in dependency order,
validates every expected port, starts the frontend, and keeps all processes
supervised in one terminal. Press `Ctrl+C` once to stop the full stack. Runtime
logs are written to `/private/tmp/mercato-local-logs`.

To start only the backend stack:

```bash
START_FRONTEND=false bash scripts/start-local.sh
```

Alternatively, start the backend services and API Gateway manually. The gateway
must be available at `http://localhost:8080`.

Then:

```bash
npm install
npm run dev
```

Open `http://localhost:5173`.

The Vite development server proxies `/api` to the gateway. For another gateway
URL, copy `.env.example` to `.env` and change `VITE_API_URL`.

## Production build

```bash
npm run build
npm run preview
```

## Authority model

| Capability | Guest | Customer | Seller | Admin |
|---|---:|---:|---:|---:|
| Browse and search the catalog | Yes | Yes | Yes | Yes |
| Manage customer profile | — | Own | Own | Own |
| Manage cart and wishlist | — | Own | Own | Own |
| Apply to sell | — | Yes | — | — |
| Create and manage owned products | — | — | Yes | Yes |
| Manage inventory and images | — | — | Owned products | Yes |
| Approve sellers | — | — | — | Yes |
| Manage users and roles | — | — | — | Yes |
| Manage categories | — | — | — | Yes |

Registration always requests the `CUSTOMER` role. A customer becomes a seller
only after an administrator approves the seller application and assigns the
`SELLER` role. Existing tokens are invalidated when a user is banned or their
role changes.

Frontend visibility is for usability only. The API Gateway and individual
services remain responsible for authorization and resource ownership.

## Structure

- `src/api/client.js` — authentication-aware HTTP client and error handling
- `src/api/services.js` — functions for all gateway-exposed backend APIs
- `src/App.jsx` — public, customer, seller, and administrator experiences
- `src/styles.css` — responsive visual system
