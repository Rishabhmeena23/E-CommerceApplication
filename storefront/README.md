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
| Checkout and view orders | — | Own | Own | Own/all |
| Run dummy payments | — | Own | Own | Own/all |
| Apply to sell | — | Yes | — | — |
| Create and manage owned products | — | — | Yes | Yes |
| Manage inventory and images | — | — | Owned products | Yes |
| Approve sellers | — | — | — | Yes |
| Manage users and roles | — | — | — | Yes |
| Manage categories | — | — | — | Yes |
| Update fulfilment status | — | — | — | Yes |

Registration always requests the `CUSTOMER` role. A customer becomes a seller
only after an administrator approves the seller application and assigns the
`SELLER` role. Existing tokens are invalidated when a user is banned or their
role changes.

Frontend visibility is for usability only. The API Gateway and individual
services remain responsible for authorization and resource ownership.

## Structure

- `src/api/client.js` — authentication-aware HTTP client and error handling
- `src/api/services.js` — functions for all gateway-exposed backend APIs
- `src/App.jsx` — routed public, checkout, order, seller, and administrator experiences
- `src/styles.css` — responsive visual system

## Checkout and dummy payment

The cart can create an order and complete a simulated payment. Use card number
`4111111111111111` for success or any number ending in `0000` to test a decline.
No real card information is sent to a payment provider or stored.
