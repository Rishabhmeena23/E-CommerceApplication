# Frontend Development Guide

This guide explains the storefront from the outside in. Read the files in the order shown below.

## 1. How the application starts

1. `src/main.jsx` mounts React and enables browser routing.
2. `src/App.jsx` keeps the small amount of state shared by the whole app:
   - signed-in user
   - cart and wishlist counts
   - sign-in dialog
   - success and error messages
3. `src/App.jsx` maps URLs such as `/cart` and `/seller` to page components.
4. A page calls a function from `src/api/services.js`.
5. `src/api/client.js` sends the request and adds the saved login token.

## 2. Folder map

```text
src/
├── api/
│   ├── client.js          Shared HTTP and login-token handling
│   └── services.js        Backend operations grouped by feature
├── components/
│   ├── AppHeader.jsx      Header navigation and route protection
│   ├── feedback.jsx       Dialog, toast, loading, and empty states
│   └── products.jsx       Reusable product display components
├── pages/
│   ├── ShopPage.jsx       Public catalog and product search
│   ├── CustomerPages.jsx  Cart, checkout, orders, wishlist, profile
│   ├── SellerPage.jsx     Seller catalog and inventory tools
│   └── AdminPage.jsx      Admin dashboard and marketplace controls
├── utils/
│   └── format.js          Currency, labels, and placeholder colours
├── App.jsx                Routes and application-wide state
├── main.jsx               React entry point
└── styles.css             Shared visual styles
```

A component is a reusable piece of the interface. A page is a component attached to a URL. API files know how to communicate with the backend, but do not decide how the page looks.

## 3. Follow one feature end to end

Use “add to cart” as an example:

1. `ProductCard` in `components/products.jsx` calls the `onAdd` function it receives.
2. `ShopPage` provides that function and checks whether the visitor is signed in.
3. The page calls `cartApi.add(product.id, quantity)`.
4. `cartApi` in `api/services.js` describes the request.
5. `request` in `api/client.js` sends it to the API Gateway.
6. The page updates the cart count and shows a confirmation toast.

This pattern—component event → page action → service function → HTTP client—is used throughout the storefront.

## 4. How to add a new page

1. Create a component in `src/pages`, for example `HelpPage.jsx`.
2. Import it in `src/App.jsx`.
3. Add its URL to the `paths` object.
4. Add a `<Route>` for it.
5. Add a header link in `components/AppHeader.jsx` when it should be visible in navigation.
6. Add styles to `styles.css`, reusing existing classes where possible.

Keep backend calls in the page or a small page-specific hook. Keep generic buttons, cards, dialogs, and status displays in `components`.

## 5. How to add a backend operation

Add a named function to the correct group in `src/api/services.js`:

```js
export const reviewApi = {
  list: () => request('/reviews'),
  create: (payload) => request('/reviews', { method: 'POST', body: payload }),
}
```

Then import `reviewApi` only in the page that needs it. Do not repeat token, JSON, or error-handling code; `api/client.js` already owns that responsibility.

## 6. A safe development loop

1. Start the backend and frontend with `bash scripts/start-local.sh` from the secured-backend folder.
2. Make one small change in the relevant page or component.
3. Check the guest flow and the affected signed-in role.
4. Run `npm run build` in the storefront folder before finishing.

For checkout, card number `4111111111111111` simulates success. A number ending in `0000` simulates a decline.

