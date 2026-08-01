# Secured backend revision

This revision changes the API contract to make identity and resource ownership
enforceable across the microservices.

## Required environment variables

Set one strong shared `JWT_SECRET` (at least 32 bytes) and a non-default
`INTERNAL_SERVICE_KEY` before starting any service. The Config Server database
configuration now also requires `AUTH_DB_PASSWORD`, `CUSTOMER_DB_PASSWORD`,
`CART_DB_PASSWORD`, `WISHLIST_DB_PASSWORD`, `SELLER_DB_PASSWORD`, and
`PRODUCT_DB_PASSWORD`, `ORDER_DB_PASSWORD`, and `PAYMENT_DB_PASSWORD`.

For local development, copy `scripts/local-env.example` to `.local-env`, set
the values, and run `source .local-env` in the terminal before starting the
Config Server, Discovery Server, Auth Service, and Gateway. Configure the same
variables in your IDE’s run configuration if you start services from the IDE.

## Updated authenticated workflow

1. `POST /auth/register` creates a CUSTOMER user and immediately returns a JWT.
2. Use that JWT to create the linked profile with `POST /customers`, then use
   `GET|PUT|DELETE /customers/me` for the caller’s own profile.
3. Cart and wishlist are now owner-derived endpoints: `GET /cart`,
   `POST /cart/items`, `PUT|DELETE /cart/items/{productId}`, `DELETE /cart`,
   with identical paths under `/wishlist`.
4. A customer applies to be a seller using `POST /sellers`; the request no
   longer accepts a user ID. An Admin changes the approval state. Once approved,
   assign the `SELLER` role through the Admin user-role endpoint.
5. Product writes require SELLER or ADMIN. Sellers can only change products
   whose `sellerUserId` equals the JWT user ID. Catalog reads are public.
6. Checkout creates a price-verified order with `POST /orders`, then simulates
   payment with `POST /payments`. Customers use `/orders/me` and `/payments/me`
   for their own history; administrators can list and fulfil all orders.

## Security controls added

- Every externally reachable business service verifies JWT signatures and asks
  Auth Service for the user’s current role, active state, and token version.
- Banning a user or changing a role increments token version, immediately
  invalidating existing access tokens.
- Direct service requests no longer bypass authentication.
- Product prices and available stock are verified by Cart Service before an item
  is added or its quantity is changed.
- Gateway routing includes catalog, order, and payment endpoints; CORS supports
  both local frontend origins and all required HTTP methods.
- Admin products and orders are read from their real services. Internal calls
  use `X-Internal-Service-Key` and are not exposed through the Gateway.

## Database migration note

Customer adds a required unique `user_id`, and existing cart/wishlist
`customer_id` columns are now treated as the authenticated Auth Service user ID.
For non-empty databases, migrate or recreate these tables before deployment.
