import { useEffect, useState } from 'react'
import { Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom'
import {
  ArrowRight, BadgeCheck, BarChart3, Box, Check, ChevronDown, CircleUserRound,
  CreditCard, Heart, LayoutDashboard, LogOut, Menu, Package, Pencil, Plus, ReceiptText, Search,
  Settings, ShieldCheck, ShoppingBag, ShoppingCart, SlidersHorizontal, Sparkles,
  Store, Trash2, TrendingUp, Truck, UserRound, Users, X,
} from 'lucide-react'
import { getSession, saveSession } from './api/client'
import {
  adminApi, authApi, cartApi, categoryApi, customerApi, orderApi, paymentApi,
  productApi, sellerApi, subCategoryApi, userApi, wishlistApi,
} from './api/services'

const money = (value) =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 })
    .format(Number(value || 0))

const titleCase = (value = '') =>
  value.toLowerCase().replaceAll('_', ' ').replace(/\b\w/g, (letter) => letter.toUpperCase())

const placeholderColors = [
  'linear-gradient(145deg, #e7efe9 0%, #b9cdbd 100%)',
  'linear-gradient(145deg, #f1e8dc 0%, #d6bda0 100%)',
  'linear-gradient(145deg, #ece7f2 0%, #c9bdd8 100%)',
  'linear-gradient(145deg, #e5edf0 0%, #afc7ce 100%)',
]

function Modal({ open, onClose, title, subtitle, children, wide = false }) {
  useEffect(() => {
    if (!open) return undefined
    const close = (event) => event.key === 'Escape' && onClose()
    document.addEventListener('keydown', close)
    document.body.style.overflow = 'hidden'
    return () => {
      document.removeEventListener('keydown', close)
      document.body.style.overflow = ''
    }
  }, [open, onClose])
  if (!open) return null
  return (
    <div className="modal-backdrop" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <section className={`modal ${wide ? 'modal-wide' : ''}`} role="dialog" aria-modal="true" aria-label={title}>
        <button className="icon-button modal-close" onClick={onClose} aria-label="Close"><X size={19} /></button>
        {title && <h2>{title}</h2>}
        {subtitle && <p className="modal-subtitle">{subtitle}</p>}
        {children}
      </section>
    </div>
  )
}

function Toast({ toast, clear }) {
  useEffect(() => {
    if (!toast) return undefined
    const timer = setTimeout(clear, 3600)
    return () => clearTimeout(timer)
  }, [toast, clear])
  if (!toast) return null
  return <div className={`toast ${toast.type || 'success'}`}>{toast.type !== 'error' && <Check size={18} />}{toast.message}</div>
}

function Loading({ label = 'Loading' }) {
  return <div className="loading"><span className="spinner" />{label}…</div>
}

function Empty({ icon: Icon = Box, title, text, action }) {
  return (
    <div className="empty">
      <div className="empty-icon"><Icon size={27} /></div>
      <h3>{title}</h3><p>{text}</p>{action}
    </div>
  )
}

function StatusPill({ value }) {
  const key = String(value || '').toLowerCase()
  return <span className={`status ${key}`}>{titleCase(value || 'Unknown')}</span>
}

function AuthModal({ open, onClose, onAuthenticated, notify }) {
  const [mode, setMode] = useState('login')
  const [loading, setLoading] = useState(false)
  const [form, setForm] = useState({ name: '', email: '', password: '' })
  const change = (event) => setForm({ ...form, [event.target.name]: event.target.value })
  const submit = async (event) => {
    event.preventDefault()
    setLoading(true)
    try {
      const session = mode === 'login'
        ? await authApi.login({ email: form.email, password: form.password })
        : await authApi.register({ ...form, role: 'CUSTOMER' })
      saveSession(session)
      onAuthenticated(session)
      notify(`Welcome${session.name ? `, ${session.name.split(' ')[0]}` : ''}!`)
      onClose()
    } catch (error) {
      notify(error.message, 'error')
    } finally {
      setLoading(false)
    }
  }
  return (
    <Modal open={open} onClose={onClose}>
      <div className="auth-mark"><Sparkles size={21} /></div>
      <h2>{mode === 'login' ? 'Welcome back' : 'Create your account'}</h2>
      <p className="modal-subtitle">
        {mode === 'login' ? 'Sign in to continue your Shoping journey.' : 'Join as a customer. You can apply to sell later.'}
      </p>
      <div className="auth-tabs">
        <button className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>Sign in</button>
        <button className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>Register</button>
      </div>
      <form onSubmit={submit} className="form-stack">
        {mode === 'register' && (
          <label>Full name<input name="name" value={form.name} onChange={change} minLength="3" maxLength="100" placeholder="Aarav Sharma" required /></label>
        )}
        <label>Email address<input name="email" type="email" value={form.email} onChange={change} placeholder="you@example.com" required /></label>
        <label>Password<input name="password" type="password" value={form.password} onChange={change} minLength="6" maxLength="20" placeholder="6–20 characters" required /></label>
        <button type="submit" className="button primary full" disabled={loading}>{loading ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Create account'}<ArrowRight size={17} /></button>
      </form>
      <p className="fine-print">By continuing, you agree to shop responsibly and keep your account secure.</p>
    </Modal>
  )
}

function ProductVisual({ product, className = '' }) {
  const image = product.images?.find((item) => item.primaryImage) || product.images?.[0]
  return (
    <div className={`product-visual ${className}`} style={!image?.url ? { background: placeholderColors[(product.id || 0) % placeholderColors.length] } : undefined}>
      {image?.url ? <img src={image.url} alt={image.altText || product.name} /> : (
        <div className="visual-placeholder"><span>{product.brand || 'Shoping'}</span><Package size={44} strokeWidth={1.2} /></div>
      )}
    </div>
  )
}

function ProductCard({ product, onView, onAdd, onWish, canShop }) {
  const inventory = product.inventory
  const inStock = !inventory || inventory.availableQuantity > 0
  return (
    <article className="product-card">
      <button className="product-image-button" onClick={() => onView(product)}>
        <ProductVisual product={product} />
        {product.categoryName && <span className="category-tag">{product.categoryName}</span>}
      </button>
      <div className="product-card-body">
        <div className="product-brand">{product.brand || 'Independent maker'}</div>
        <button className="product-name" onClick={() => onView(product)}>{product.name}</button>
        <div className="product-bottom">
          <div><strong>{money(product.price)}</strong><small>{inStock ? 'In stock' : 'Out of stock'}</small></div>
          <div className="card-actions">
            <button className="icon-button" onClick={() => onWish(product)} aria-label="Add to wishlist"><Heart size={18} /></button>
            <button className="bag-button" disabled={!inStock} onClick={() => onAdd(product)} aria-label="Add to cart">
              <ShoppingBag size={18} />{canShop && <span>Add</span>}
            </button>
          </div>
        </div>
      </div>
    </article>
  )
}

function ProductDetail({ product, onClose, onAdd, onWish }) {
  const [quantity, setQuantity] = useState(1)
  return (
    <Modal open={Boolean(product)} onClose={onClose} wide>
      {product && <div className="product-detail">
        <ProductVisual product={product} className="detail-visual" />
        <div className="detail-copy">
          <div className="eyebrow">{product.categoryName || 'Curated collection'} · {product.brand}</div>
          <h2>{product.name}</h2>
          <p className="detail-price">{money(product.price)}</p>
          <p className="detail-description">{product.description || 'A thoughtfully selected product from one of our trusted marketplace sellers.'}</p>
          <div className="detail-facts">
            <span><b>SKU</b>{product.sku || '—'}</span>
            <span><b>Availability</b>{product.inventory ? `${product.inventory.availableQuantity} ready to ship` : 'Available'}</span>
          </div>
          <div className="quantity-line">
            <label>Quantity<input type="number" min="1" value={quantity} onChange={(e) => setQuantity(Math.max(1, Number(e.target.value)))} /></label>
            <button className="button primary" onClick={() => onAdd(product, quantity)}><ShoppingBag size={18} />Add to cart</button>
            <button className="button secondary square" onClick={() => onWish(product)} aria-label="Add to wishlist"><Heart size={19} /></button>
          </div>
          <p className="secure-note"><ShieldCheck size={17} />Protected checkout and verified inventory</p>
        </div>
      </div>}
    </Modal>
  )
}

function ShopPage({ session, onAuth, notify, setCartCount, setWishCount }) {
  const [products, setProducts] = useState([])
  const [categories, setCategories] = useState([])
  const [selected, setSelected] = useState(null)
  const [loading, setLoading] = useState(true)
  const [filtersOpen, setFiltersOpen] = useState(false)
  const [filters, setFilters] = useState({ keyword: '', categoryId: '', minPrice: '', maxPrice: '' })

  const load = async (nextFilters = filters) => {
    setLoading(true)
    try {
      const [productResult, categoryResult] = await Promise.all([
        Object.values(nextFilters).some(Boolean) ? productApi.search({ ...nextFilters, size: 40 }) : productApi.list(),
        categoryApi.list(),
      ])
      setProducts(Array.isArray(productResult) ? productResult : productResult.content || [])
      setCategories(categoryResult || [])
    } catch (error) {
      notify(error.message, 'error')
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const requireAuth = () => {
    if (!session) { onAuth(); return false }
    return true
  }
  const add = async (product, quantity = 1) => {
    if (!requireAuth()) return
    try {
      const cart = await cartApi.add(product.id, quantity)
      setCartCount(cart.items?.length || 0)
      notify(`${product.name} added to your cart`)
    } catch (error) { notify(error.message, 'error') }
  }
  const wish = async (product) => {
    if (!requireAuth()) return
    try {
      const wishlist = await wishlistApi.add(product.id)
      setWishCount(wishlist.items?.length || 0)
      notify(`${product.name} saved to your wishlist`)
    } catch (error) { notify(error.message, 'error') }
  }
  const categoryName = categories.find((item) => String(item.id) === String(filters.categoryId))?.name
  return (
    <>
      <section className="hero shell">
        <div className="hero-copy">
          <p className="eyebrow"><Sparkles size={15} />Fresh finds, thoughtfully selected</p>
          <h1>Good things,<br /><em>found here.</em></h1>
          <p>Discover useful, beautiful products from trusted independent sellers across India.</p>
          <div className="hero-actions">
            <button className="button primary" onClick={() => document.getElementById('collection')?.scrollIntoView({ behavior: 'smooth' })}>Explore collection<ArrowRight size={17} /></button>
            {!session && <button className="text-button" onClick={onAuth}>Join the community</button>}
          </div>
        </div>
        <div className="hero-art" aria-hidden="true">
          <div className="art-card art-one"><ShoppingBag size={35} /><span>Everyday</span><b>essentials</b></div>
          <div className="art-card art-two"><Sparkles size={30} /><span>Made with</span><b>intention</b></div>
          <div className="art-orbit"><span>20+</span>curated<br />categories</div>
        </div>
      </section>
      <section className="promise-strip">
        <div className="shell promises">
          <span><BadgeCheck size={18} />Trusted sellers</span>
          <span><ShieldCheck size={18} />Secure accounts</span>
          <span><Package size={18} />Live inventory</span>
          <span><Heart size={18} />Made for you</span>
        </div>
      </section>
      <main className="shell collection" id="collection">
        <div className="section-head">
          <div><p className="eyebrow">The marketplace edit</p><h2>{categoryName || 'Explore all products'}</h2></div>
          <button className="button secondary filter-toggle" onClick={() => setFiltersOpen(!filtersOpen)}><SlidersHorizontal size={17} />Filters</button>
        </div>
        <form className={`filter-bar ${filtersOpen ? 'open' : ''}`} onSubmit={(e) => { e.preventDefault(); load() }}>
          <label className="search-field"><Search size={18} /><input value={filters.keyword} onChange={(e) => setFilters({ ...filters, keyword: e.target.value })} placeholder="Search products or brands" /></label>
          <select value={filters.categoryId} onChange={(e) => setFilters({ ...filters, categoryId: e.target.value })}>
            <option value="">All categories</option>{categories.map((item) => <option value={item.id} key={item.id}>{item.name}</option>)}
          </select>
          <input type="number" min="0" value={filters.minPrice} onChange={(e) => setFilters({ ...filters, minPrice: e.target.value })} placeholder="Min price" />
          <input type="number" min="0" value={filters.maxPrice} onChange={(e) => setFilters({ ...filters, maxPrice: e.target.value })} placeholder="Max price" />
          <button className="button dark">Apply</button>
          {Object.values(filters).some(Boolean) && <button type="button" className="text-button" onClick={() => { const empty = { keyword: '', categoryId: '', minPrice: '', maxPrice: '' }; setFilters(empty); load(empty) }}>Clear</button>}
        </form>
        {loading ? <Loading label="Curating products" /> : products.length ? (
          <div className="product-grid">
            {products.map((product) => <ProductCard key={product.id} product={product} onView={setSelected} onAdd={add} onWish={wish} canShop={Boolean(session)} />)}
          </div>
        ) : <Empty icon={Search} title="No products found" text="Try widening your filters or browsing all categories." />}
      </main>
      <ProductDetail product={selected} onClose={() => setSelected(null)} onAdd={add} onWish={wish} />
    </>
  )
}

function CartPage({ notify, setCartCount, goShop, goCheckout }) {
  const [cart, setCart] = useState(null)
  const [products, setProducts] = useState({})
  const [loading, setLoading] = useState(true)
  const load = async () => {
    setLoading(true)
    try {
      let result
      try { result = await cartApi.get() } catch (error) {
        if (/not found|cart/i.test(error.message)) result = await cartApi.create()
        else throw error
      }
      setCart(result); setCartCount(result.items?.length || 0)
      const pairs = await Promise.all((result.items || []).map(async (item) => {
        try { return [item.productId, await productApi.get(item.productId)] } catch { return [item.productId, null] }
      }))
      setProducts(Object.fromEntries(pairs))
    } catch (error) { notify(error.message, 'error') } finally { setLoading(false) }
  }
  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps
  const update = async (id, quantity) => {
    if (quantity < 1) return
    try { const next = await cartApi.update(id, quantity); setCart(next); notify('Quantity updated') } catch (error) { notify(error.message, 'error') }
  }
  const remove = async (id) => {
    try { await cartApi.remove(id); await load(); notify('Item removed') } catch (error) { notify(error.message, 'error') }
  }
  const clear = async () => {
    if (!window.confirm('Remove all items from your cart?')) return
    try { await cartApi.clear(); setCart({ ...cart, items: [], totalAmount: 0 }); setCartCount(0); notify('Cart cleared') } catch (error) { notify(error.message, 'error') }
  }
  if (loading) return <main className="shell page"><Loading label="Loading your cart" /></main>
  const items = cart?.items || []
  return (
    <main className="shell page">
      <div className="page-title"><div><p className="eyebrow">Your selection</p><h1>Shopping cart</h1></div>{items.length > 0 && <button className="text-button danger" onClick={clear}>Clear cart</button>}</div>
      {!items.length ? <Empty icon={ShoppingCart} title="Your cart is waiting" text="Explore the collection and add something you love." action={<button className="button primary" onClick={goShop}>Start shopping</button>} /> : (
        <div className="cart-layout">
          <section className="line-items">
            {items.map((item) => {
              const product = products[item.productId] || { id: item.productId, name: `Product #${item.productId}`, price: item.price }
              return <article className="line-item" key={item.productId}>
                <ProductVisual product={product} />
                <div className="line-copy"><span>{product.brand || 'Shoping seller'}</span><h3>{product.name}</h3><strong>{money(item.price)}</strong></div>
                <label className="quantity">Qty<input type="number" min="1" value={item.quantity} onChange={(e) => update(item.productId, Number(e.target.value))} /></label>
                <strong className="subtotal">{money(item.subtotal)}</strong>
                <button className="icon-button danger" onClick={() => remove(item.productId)} aria-label="Remove item"><Trash2 size={18} /></button>
              </article>
            })}
          </section>
          <aside className="order-summary">
            <p className="eyebrow">Order summary</p><h2>{money(cart.totalAmount)}</h2>
            <div><span>Items</span><span>{items.length}</span></div>
            <div><span>Delivery</span><span>Calculated later</span></div>
            <hr /><div><b>Total</b><b>{money(cart.totalAmount)}</b></div>
            <button className="button primary full" onClick={goCheckout}>Proceed to checkout</button>
            <p><ShieldCheck size={16} />Cart prices are verified against live product data.</p>
          </aside>
        </div>
      )}
    </main>
  )
}

function CheckoutPage({ notify, setCartCount, goCart, goOrders }) {
  const [cart, setCart] = useState(null)
  const [products, setProducts] = useState({})
  const [shippingAddress, setShippingAddress] = useState('')
  const [paymentMethod, setPaymentMethod] = useState('CARD')
  const [cardNumber, setCardNumber] = useState('4111111111111111')
  const [loading, setLoading] = useState(true)
  const [placing, setPlacing] = useState(false)

  useEffect(() => {
    cartApi.get().then(async (result) => {
      setCart(result)
      const pairs = await Promise.all((result.items || []).map(async (item) => {
        try { return [item.productId, await productApi.get(item.productId)] } catch { return [item.productId, null] }
      }))
      setProducts(Object.fromEntries(pairs))
    }).catch((error) => notify(error.message, 'error')).finally(() => setLoading(false))
  }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const submit = async (event) => {
    event.preventDefault(); setPlacing(true)
    try {
      const order = await orderApi.create({
        shippingAddress,
        items: cart.items.map((item) => ({ productId: item.productId, quantity: item.quantity })),
      })
      const payment = await paymentApi.pay({ orderId: order.id, paymentMethod, cardNumber: paymentMethod === 'CARD' ? cardNumber : '' })
      if (payment.status === 'SUCCESS') {
        await cartApi.clear(); setCartCount(0); notify(`Payment successful · ${payment.paymentReference}`); goOrders()
      } else {
        notify(payment.failureReason || 'Dummy payment was declined', 'error'); goOrders()
      }
    } catch (error) { notify(error.message, 'error') } finally { setPlacing(false) }
  }

  if (loading) return <main className="shell page"><Loading label="Preparing checkout" /></main>
  const items = cart?.items || []
  if (!items.length) return <main className="shell page"><Empty icon={ShoppingCart} title="Your cart is empty" text="Add a product before checking out." action={<button className="button primary" onClick={goCart}>Return to cart</button>} /></main>
  return <main className="shell page">
    <div className="page-title"><div><p className="eyebrow">Secure checkout</p><h1>Delivery & payment</h1></div><span className="admin-lock"><ShieldCheck size={17} />Dummy payment</span></div>
    <form className="checkout-layout" onSubmit={submit}>
      <section className="panel checkout-form">
        <div className="checkout-step"><span>1</span><div><h2>Delivery address</h2><p>Where should this order be delivered?</p></div></div>
        <label>Full delivery address<textarea rows="4" maxLength="500" required value={shippingAddress} onChange={(e) => setShippingAddress(e.target.value)} placeholder="House number, street, city, state and PIN code" /></label>
        <div className="checkout-step"><span>2</span><div><h2>Payment method</h2><p>This is a simulation and never charges real money.</p></div></div>
        <div className="payment-options">
          <button type="button" className={paymentMethod === 'CARD' ? 'active' : ''} onClick={() => setPaymentMethod('CARD')}><CreditCard size={20} /><b>Dummy card</b><small>Instant test payment</small></button>
          <button type="button" className={paymentMethod === 'UPI' ? 'active' : ''} onClick={() => setPaymentMethod('UPI')}><Sparkles size={20} /><b>Dummy UPI</b><small>Always succeeds</small></button>
        </div>
        {paymentMethod === 'CARD' && <label>Dummy card number<input required inputMode="numeric" pattern="[0-9 ]{12,23}" value={cardNumber} onChange={(e) => setCardNumber(e.target.value)} /><small>Use 4111111111111111 for success; a number ending in 0000 is declined.</small></label>}
      </section>
      <aside className="order-summary checkout-summary">
        <p className="eyebrow">Review order</p>
        {items.map((item) => <div className="checkout-item" key={item.productId}><span>{products[item.productId]?.name || `Product #${item.productId}`} × {item.quantity}</span><b>{money(item.subtotal)}</b></div>)}
        <hr /><div><b>Total</b><b>{money(cart.totalAmount)}</b></div>
        <button className="button primary full" disabled={placing}>{placing ? 'Processing…' : `Pay ${money(cart.totalAmount)}`}</button>
        <button type="button" className="text-button" onClick={goCart}>Back to cart</button>
      </aside>
    </form>
  </main>
}

function OrdersPage({ notify }) {
  const [orders, setOrders] = useState([])
  const [payments, setPayments] = useState([])
  const [loading, setLoading] = useState(true)
  const load = async () => {
    setLoading(true)
    const [orderResult, paymentResult] = await Promise.allSettled([orderApi.mine(), paymentApi.mine()])
    if (orderResult.status === 'fulfilled') setOrders(orderResult.value || [])
    else notify(orderResult.reason.message, 'error')
    if (paymentResult.status === 'fulfilled') setPayments(paymentResult.value || [])
    setLoading(false)
  }
  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps
  const cancel = async (order) => {
    if (!window.confirm(`Cancel order #${order.id}?`)) return
    try { await orderApi.cancel(order.id); await load(); notify('Order cancelled') } catch (error) { notify(error.message, 'error') }
  }
  if (loading) return <main className="shell page"><Loading label="Loading your orders" /></main>
  return <main className="shell page">
    <div className="page-title"><div><p className="eyebrow">Purchase history</p><h1>Your orders</h1></div><span>{orders.length} orders</span></div>
    {!orders.length ? <Empty icon={ReceiptText} title="No orders yet" text="Completed checkouts will appear here." /> : <div className="order-list">
      {orders.map((order) => {
        const payment = payments.find((item) => item.orderId === order.id)
        return <article className="panel order-card" key={order.id}>
          <div className="order-card-head"><div><small>Order #{order.id}</small><h2>{money(order.totalAmount)}</h2><span>{new Date(order.createdAt).toLocaleString()}</span></div><StatusPill value={order.orderStatus} /></div>
          <div className="order-items">{(order.items || []).map((item) => <div key={item.id}><span>{item.productName} × {item.quantity}</span><b>{money(item.subtotal)}</b></div>)}</div>
          <div className="order-meta"><span><Truck size={16} />{order.shippingAddress}</span>{payment && <span><CreditCard size={16} />{payment.paymentReference} · {titleCase(payment.status)}</span>}</div>
          {['PENDING_PAYMENT', 'PAYMENT_FAILED'].includes(order.orderStatus) && <button className="text-button danger" onClick={() => cancel(order)}>Cancel order</button>}
        </article>
      })}
    </div>}
  </main>
}

function WishlistPage({ notify, setWishCount, setCartCount, goShop }) {
  const [wishlist, setWishlist] = useState(null)
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const load = async () => {
    setLoading(true)
    try {
      let result
      try { result = await wishlistApi.get() } catch (error) {
        if (/not found|wishlist/i.test(error.message)) result = await wishlistApi.create()
        else throw error
      }
      setWishlist(result); setWishCount(result.items?.length || 0)
      const values = await Promise.all((result.items || []).map(async (item) => {
        try { return await productApi.get(item.productId) } catch { return { id: item.productId, name: `Product #${item.productId}` } }
      }))
      setProducts(values)
    } catch (error) { notify(error.message, 'error') } finally { setLoading(false) }
  }
  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps
  const remove = async (product) => {
    try { await wishlistApi.remove(product.id); await load(); notify('Removed from wishlist') } catch (error) { notify(error.message, 'error') }
  }
  const add = async (product) => {
    try { const cart = await cartApi.add(product.id, 1); setCartCount(cart.items?.length || 0); notify(`${product.name} added to cart`) } catch (error) { notify(error.message, 'error') }
  }
  const clear = async () => {
    if (!window.confirm('Clear your wishlist?')) return
    try { await wishlistApi.clear(); setProducts([]); setWishlist({ ...wishlist, items: [] }); setWishCount(0); notify('Wishlist cleared') } catch (error) { notify(error.message, 'error') }
  }
  return <main className="shell page">
    <div className="page-title"><div><p className="eyebrow">Saved for later</p><h1>Your wishlist</h1></div>{products.length > 0 && <button className="text-button danger" onClick={clear}>Clear wishlist</button>}</div>
    {loading ? <Loading label="Opening your wishlist" /> : !products.length ? <Empty icon={Heart} title="Save what speaks to you" text="Tap the heart on any product to keep it close." action={<button className="button primary" onClick={goShop}>Browse products</button>} /> : (
      <div className="product-grid">{products.map((product) => <ProductCard key={product.id} product={product} onView={() => {}} onAdd={add} onWish={remove} canShop />)}</div>
    )}
  </main>
}

function ProfilePage({ session, setSession, notify }) {
  const [profile, setProfile] = useState(null)
  const [seller, setSeller] = useState(null)
  const [loading, setLoading] = useState(true)
  const [edit, setEdit] = useState(false)
  const [sellerForm, setSellerForm] = useState(false)
  const [form, setForm] = useState({ firstName: '', lastName: '', phone: '', gender: '' })
  const [shop, setShop] = useState({ shopName: '', shopDescription: '', phone: '', gstNumber: '', sellerType: 'INDIVIDUAL' })
  const load = async () => {
    setLoading(true)
    const [customerResult, sellerResult] = await Promise.allSettled([customerApi.me(), sellerApi.me()])
    if (customerResult.status === 'fulfilled') {
      const data = customerResult.value; setProfile(data)
      const names = (data.fullName || '').split(' ')
      setForm({ firstName: names[0] || '', lastName: names.slice(1).join(' '), phone: data.phone || '', gender: data.gender || '' })
    }
    if (sellerResult.status === 'fulfilled') setSeller(sellerResult.value)
    setLoading(false)
  }
  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps
  const saveProfile = async (event) => {
    event.preventDefault()
    try {
      const next = profile ? await customerApi.updateMe(form) : await customerApi.create(form)
      setProfile(next); setEdit(false); notify('Profile saved')
    } catch (error) { notify(error.message, 'error') }
  }
  const applySeller = async (event) => {
    event.preventDefault()
    try { const next = await sellerApi.create(shop); setSeller(next); setSellerForm(false); notify('Seller application submitted') } catch (error) { notify(error.message, 'error') }
  }
  const deleteProfile = async () => {
    if (!window.confirm('Permanently delete your customer profile? Your login account will remain active.')) return
    try { await customerApi.deleteMe(); setProfile(null); notify('Customer profile deleted') } catch (error) { notify(error.message, 'error') }
  }
  if (loading) return <main className="shell page"><Loading label="Loading account" /></main>
  return <main className="shell page account-page">
    <div className="page-title"><div><p className="eyebrow">Account & identity</p><h1>Hello, {session.name?.split(' ')[0] || 'there'}</h1></div><StatusPill value={session.role} /></div>
    <div className="account-grid">
      <section className="panel profile-card">
        <div className="avatar">{session.name?.[0] || <UserRound />}</div>
        <h2>{session.name}</h2><p>{session.email}</p>
        <dl><div><dt>User ID</dt><dd>#{session.userId}</dd></div><div><dt>Access level</dt><dd>{titleCase(session.role)}</dd></div></dl>
        <p className="permission-note"><ShieldCheck size={18} />Your access is enforced by the API Gateway and each service.</p>
      </section>
      <section className="panel account-section">
        <div className="panel-head"><div><p className="eyebrow">Customer profile</p><h2>{profile ? 'Personal details' : 'Finish your profile'}</h2></div>{profile && <button className="button secondary small" onClick={() => setEdit(!edit)}><Pencil size={15} />Edit</button>}</div>
        {(!profile || edit) ? <form className="form-grid" onSubmit={saveProfile}>
          <label>First name<input required value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} /></label>
          <label>Last name<input required value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} /></label>
          <label>Phone<input required pattern="[0-9]{10}" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} placeholder="10-digit number" /></label>
          <label>Gender<select value={form.gender} onChange={(e) => setForm({ ...form, gender: e.target.value })}><option value="">Prefer not to say</option><option>Female</option><option>Male</option><option>Non-binary</option></select></label>
          <div className="form-actions"><button className="button primary">Save profile</button>{edit && <button type="button" className="button secondary" onClick={() => setEdit(false)}>Cancel</button>}</div>
        </form> : <div className="details-list"><div><span>Full name</span><b>{profile.fullName}</b></div><div><span>Email</span><b>{profile.email}</b></div><div><span>Phone</span><b>{profile.phone}</b></div><div><span>Gender</span><b>{profile.gender || 'Not provided'}</b></div><button className="text-button danger" onClick={deleteProfile}>Delete customer profile</button></div>}
      </section>
      <section className="panel account-section seller-application">
        <div className="panel-head"><div><p className="eyebrow">Sell on Shoping</p><h2>{seller ? seller.shopName : 'Open your shop'}</h2></div>{seller && <StatusPill value={seller.approvalStatus} />}</div>
        {seller ? <>
          <p>{seller.shopDescription || 'Your seller application is being managed by the marketplace team.'}</p>
          <div className="seller-steps"><span className="done">1<small>Applied</small></span><i /><span className={seller.approvalStatus === 'APPROVED' ? 'done' : ''}>2<small>Approved</small></span><i /><span className={session.role === 'SELLER' ? 'done' : ''}>3<small>Seller access</small></span></div>
          {seller.approvalStatus === 'APPROVED' && session.role !== 'SELLER' && <p className="callout">Your application is approved. An admin must now assign your SELLER role.</p>}
        </> : sellerForm ? <form className="form-grid" onSubmit={applySeller}>
          <label>Shop name<input required maxLength="150" value={shop.shopName} onChange={(e) => setShop({ ...shop, shopName: e.target.value })} /></label>
          <label>Seller type<select value={shop.sellerType} onChange={(e) => setShop({ ...shop, sellerType: e.target.value })}>{['INDIVIDUAL', 'PROPRIETORSHIP', 'PARTNERSHIP', 'LLP', 'COMPANY'].map((type) => <option key={type}>{type}</option>)}</select></label>
          <label>Phone<input required value={shop.phone} onChange={(e) => setShop({ ...shop, phone: e.target.value })} /></label>
          <label>GST number (optional)<input pattern="[0-9A-Z]{15}" value={shop.gstNumber} onChange={(e) => setShop({ ...shop, gstNumber: e.target.value.toUpperCase() })} /></label>
          <label className="span-two">Description<textarea value={shop.shopDescription} onChange={(e) => setShop({ ...shop, shopDescription: e.target.value })} /></label>
          <div className="form-actions"><button className="button primary">Submit application</button><button type="button" className="button secondary" onClick={() => setSellerForm(false)}>Cancel</button></div>
        </form> : <><p>Turn your craft, collection, or business into a trusted storefront. Every application is reviewed by an administrator.</p><button className="button dark" onClick={() => setSellerForm(true)}><Store size={17} />Apply to become a seller</button></>}
      </section>
    </div>
  </main>
}

function ProductForm({ product, subcategories, onClose, onSaved, notify }) {
  const existingImage = product?.images?.find((item) => item.primaryImage) || product?.images?.[0]
  const [form, setForm] = useState({
    name: product?.name || '', description: product?.description || '', sku: product?.sku || '',
    brand: product?.brand || '', price: product?.price || '', subCategoryId: product?.subCategoryId || '',
    imageUrl: existingImage?.url || '',
  })
  const [saving, setSaving] = useState(false)
  const submit = async (event) => {
    event.preventDefault(); setSaving(true)
    try {
      const { imageUrl, ...productFields } = form
      const payload = { ...productFields, price: Number(form.price), subCategoryId: Number(form.subCategoryId) }
      let value = product ? await productApi.update(product.id, payload) : await productApi.create(payload)
      if (imageUrl.trim()) {
        const imagePayload = { url: imageUrl.trim(), altText: form.name, primaryImage: true, displayOrder: 0 }
        if (existingImage) await productApi.images.update(value.id, existingImage.id, imagePayload)
        else await productApi.images.add(value.id, [imagePayload])
        value = await productApi.get(value.id)
      }
      notify(product ? 'Product updated' : 'Product created')
      onSaved(value); onClose()
    } catch (error) { notify(error.message, 'error') } finally { setSaving(false) }
  }
  return <Modal open onClose={onClose} title={product ? 'Edit product' : 'Add a new product'} subtitle="Product changes are validated by the catalog service." wide>
    <form className="form-grid product-form" onSubmit={submit}>
      <label>Product name<input required maxLength="200" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></label>
      <label>Brand<input required maxLength="100" value={form.brand} onChange={(e) => setForm({ ...form, brand: e.target.value })} /></label>
      <label>SKU<input required maxLength="50" value={form.sku} onChange={(e) => setForm({ ...form, sku: e.target.value })} /></label>
      <label>Price (₹)<input required type="number" min="1" step="0.01" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} /></label>
      <label className="span-two">Subcategory<select required value={form.subCategoryId} onChange={(e) => setForm({ ...form, subCategoryId: e.target.value })}><option value="">Select subcategory</option>{subcategories.map((item) => <option value={item.id} key={item.id}>{item.categoryName} · {item.name}</option>)}</select></label>
      <label className="span-two">Description<textarea maxLength="2000" rows="4" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></label>
      <label className="span-two">Primary image URL<input type="url" maxLength="500" value={form.imageUrl} onChange={(e) => setForm({ ...form, imageUrl: e.target.value })} placeholder="https://example.com/product-image.jpg" /><small>Paste a public HTTPS image URL. It will be displayed on product cards and checkout.</small></label>
      {form.imageUrl && <div className="span-two image-preview"><img src={form.imageUrl} alt="Product preview" onError={(event) => { event.currentTarget.style.display = 'none' }} /><span>Image preview</span></div>}
      <div className="form-actions"><button className="button primary" disabled={saving}>{saving ? 'Saving…' : 'Save product'}</button><button type="button" className="button secondary" onClick={onClose}>Cancel</button></div>
    </form>
  </Modal>
}

function InventoryModal({ product, onClose, notify, onUpdated }) {
  const inventory = product.inventory
  const [mode, setMode] = useState(inventory ? 'update' : 'create')
  const [quantity, setQuantity] = useState(inventory?.quantity || 0)
  const [reserved, setReserved] = useState(inventory?.reservedQuantity || 0)
  const submit = async (event) => {
    event.preventDefault()
    try {
      let next
      if (mode === 'increase') next = await productApi.inventory.increase(product.id, Number(quantity))
      else if (mode === 'decrease') next = await productApi.inventory.decrease(product.id, Number(quantity))
      else if (mode === 'create') next = await productApi.inventory.create(product.id, { quantity: Number(quantity), reservedQuantity: Number(reserved) })
      else next = await productApi.inventory.update(product.id, { quantity: Number(quantity), reservedQuantity: Number(reserved) })
      notify('Inventory updated'); onUpdated(next); onClose()
    } catch (error) { notify(error.message, 'error') }
  }
  return <Modal open onClose={onClose} title={`Inventory · ${product.name}`} subtitle="Stock changes are checked by the product service.">
    <div className="segmented">{['update', 'increase', 'decrease'].map((item) => <button type="button" key={item} disabled={!inventory && item !== 'update'} className={mode === item || (!inventory && mode === 'create' && item === 'update') ? 'active' : ''} onClick={() => setMode(!inventory && item === 'update' ? 'create' : item)}>{titleCase(item)}</button>)}</div>
    <form className="form-stack" onSubmit={submit}>
      <label>{mode === 'increase' || mode === 'decrease' ? 'Adjustment quantity' : 'Total quantity'}<input type="number" min="0" required value={quantity} onChange={(e) => setQuantity(e.target.value)} /></label>
      {(mode === 'update' || mode === 'create') && <label>Reserved quantity<input type="number" min="0" required value={reserved} onChange={(e) => setReserved(e.target.value)} /></label>}
      <button className="button primary full">Update inventory</button>
    </form>
  </Modal>
}

function SellerPage({ session, notify }) {
  const [seller, setSeller] = useState(null)
  const [products, setProducts] = useState([])
  const [subcategories, setSubcategories] = useState([])
  const [editing, setEditing] = useState(undefined)
  const [inventoryProduct, setInventoryProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const load = async () => {
    setLoading(true)
    try {
      const [sellerResult, productsResult, subs] = await Promise.all([sellerApi.me(), productApi.list(), subCategoryApi.list()])
      setSeller(sellerResult); setProducts(productsResult || []); setSubcategories(subs || [])
    } catch (error) { notify(error.message, 'error') } finally { setLoading(false) }
  }
  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps
  const remove = async (product) => {
    if (!window.confirm(`Delete “${product.name}”?`)) return
    try { await productApi.remove(product.id); setProducts(products.filter((item) => item.id !== product.id)); notify('Product deleted') } catch (error) { notify(error.message, 'error') }
  }
  const deactivate = async () => {
    if (!window.confirm('Deactivate your seller profile?')) return
    try { await sellerApi.deactivate(seller.sellerId); notify('Seller profile deactivated') } catch (error) { notify(error.message, 'error') }
  }
  if (loading) return <main className="shell page"><Loading label="Opening seller studio" /></main>
  return <main className="shell page">
    <div className="seller-hero">
      <div><p className="eyebrow">Seller studio</p><h1>{seller?.shopName || `${session.name}'s shop`}</h1><p>{seller?.shopDescription || 'Manage your catalog and inventory.'}</p></div>
      <div className="seller-badge"><Store size={23} /><span>{seller?.sellerType ? titleCase(seller.sellerType) : 'Seller'}</span><StatusPill value={seller?.approvalStatus || 'APPROVED'} /></div>
    </div>
    <div className="mini-stats"><div><Package /><span><b>{products.length}</b>Products</span></div><div><TrendingUp /><span><b>{products.filter((p) => (p.inventory?.availableQuantity || 0) > 0).length}</b>In stock</span></div><div><ShieldCheck /><span><b>{titleCase(seller?.approvalStatus || 'APPROVED')}</b>Shop status</span></div></div>
    <section className="panel table-panel">
      <div className="panel-head"><div><p className="eyebrow">Catalog manager</p><h2>Marketplace products</h2><small className="block">Only products you created can be changed; ownership is enforced by the product service.</small></div><button className="button primary small" onClick={() => setEditing(null)}><Plus size={17} />Add product</button></div>
      {!products.length ? <Empty icon={Package} title="Build your first listing" text="Add a product to start your storefront." /> : <div className="table-wrap"><table><thead><tr><th>Product</th><th>Category</th><th>Price</th><th>Available</th><th>Actions</th></tr></thead><tbody>{products.map((product) => <tr key={product.id}>
        <td><div className="table-product"><ProductVisual product={product} /><span><b>{product.name}</b><small>{product.sku}</small></span></div></td>
        <td>{product.categoryName || '—'}</td><td>{money(product.price)}</td><td>{product.inventory?.availableQuantity ?? 'Not set'}</td>
        <td><div className="row-actions"><button className="icon-button" title="Inventory" onClick={() => setInventoryProduct(product)}><Box size={17} /></button><button className="icon-button" title="Edit" onClick={() => setEditing(product)}><Pencil size={17} /></button><button className="icon-button danger" title="Delete" onClick={() => remove(product)}><Trash2 size={17} /></button></div></td>
      </tr>)}</tbody></table></div>}
    </section>
    {seller && <button className="text-button danger deactivate" onClick={deactivate}>Deactivate seller profile</button>}
    {editing !== undefined && <ProductForm product={editing} subcategories={subcategories} onClose={() => setEditing(undefined)} notify={notify} onSaved={(value) => setProducts(editing ? products.map((p) => p.id === value.id ? value : p) : [value, ...products])} />}
    {inventoryProduct && <InventoryModal product={inventoryProduct} notify={notify} onClose={() => setInventoryProduct(null)} onUpdated={(inventory) => setProducts(products.map((p) => p.id === inventoryProduct.id ? { ...p, inventory } : p))} />}
  </main>
}

function CategoryManager({ notify }) {
  const [categories, setCategories] = useState([])
  const [subs, setSubs] = useState([])
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [subName, setSubName] = useState('')
  const [categoryId, setCategoryId] = useState('')
  const load = async () => {
    try { const [c, s] = await Promise.all([categoryApi.list(), subCategoryApi.list()]); setCategories(c || []); setSubs(s || []) } catch (error) { notify(error.message, 'error') }
  }
  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps
  const addCategory = async (event) => {
    event.preventDefault(); try { await categoryApi.create({ name, description }); setName(''); setDescription(''); await load(); notify('Category created') } catch (error) { notify(error.message, 'error') }
  }
  const addSub = async (event) => {
    event.preventDefault(); try { await subCategoryApi.create({ name: subName, categoryId: Number(categoryId), description: '' }); setSubName(''); await load(); notify('Subcategory created') } catch (error) { notify(error.message, 'error') }
  }
  const renameCategory = async (item) => {
    const next = window.prompt('Category name', item.name); if (!next) return
    try { await categoryApi.update(item.id, { name: next, description: item.description || '' }); await load(); notify('Category updated') } catch (error) { notify(error.message, 'error') }
  }
  const removeCategory = async (item) => {
    if (!window.confirm(`Delete “${item.name}”?`)) return
    try { await categoryApi.remove(item.id); await load(); notify('Category deleted') } catch (error) { notify(error.message, 'error') }
  }
  const renameSub = async (item) => {
    const next = window.prompt('Subcategory name', item.name); if (!next) return
    try { await subCategoryApi.update(item.id, { name: next, description: item.description || '', categoryId: item.categoryId }); await load(); notify('Subcategory updated') } catch (error) { notify(error.message, 'error') }
  }
  const removeSub = async (item) => {
    if (!window.confirm(`Delete “${item.name}”?`)) return
    try { await subCategoryApi.remove(item.id); await load(); notify('Subcategory deleted') } catch (error) { notify(error.message, 'error') }
  }
  return <div className="admin-two-col">
    <section className="panel"><div className="panel-head"><div><p className="eyebrow">Taxonomy</p><h2>Categories</h2></div></div>
      <form className="inline-form" onSubmit={addCategory}><input required value={name} onChange={(e) => setName(e.target.value)} placeholder="New category" /><input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Description" /><button className="button dark small"><Plus size={16} />Add</button></form>
      <div className="compact-list">{categories.map((item) => <div key={item.id}><span><b>{item.name}</b><small>{item.description || 'No description'}</small></span><div className="row-actions"><button className="icon-button" onClick={() => renameCategory(item)}><Pencil size={15} /></button><button className="icon-button danger" onClick={() => removeCategory(item)}><Trash2 size={15} /></button></div></div>)}</div>
    </section>
    <section className="panel"><div className="panel-head"><div><p className="eyebrow">Catalog structure</p><h2>Subcategories</h2></div></div>
      <form className="inline-form" onSubmit={addSub}><input required value={subName} onChange={(e) => setSubName(e.target.value)} placeholder="New subcategory" /><select required value={categoryId} onChange={(e) => setCategoryId(e.target.value)}><option value="">Parent category</option>{categories.map((item) => <option value={item.id} key={item.id}>{item.name}</option>)}</select><button className="button dark small"><Plus size={16} />Add</button></form>
      <div className="compact-list">{subs.map((item) => <div key={item.id}><span><b>{item.name}</b><small>{item.categoryName}</small></span><div className="row-actions"><button className="icon-button" onClick={() => renameSub(item)}><Pencil size={15} /></button><button className="icon-button danger" onClick={() => removeSub(item)}><Trash2 size={15} /></button></div></div>)}</div>
    </section>
  </div>
}

function AdminPage({ notify }) {
  const [tab, setTab] = useState('overview')
  const [dashboard, setDashboard] = useState({})
  const [users, setUsers] = useState([])
  const [sellers, setSellers] = useState([])
  const [products, setProducts] = useState([])
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const load = async () => {
    setLoading(true)
    const results = await Promise.allSettled([adminApi.dashboard(), adminApi.users(), sellerApi.list(), adminApi.products(), adminApi.orders(), adminApi.health()])
    if (results[0].status === 'fulfilled') setDashboard(results[0].value)
    if (results[1].status === 'fulfilled') setUsers(results[1].value || [])
    if (results[2].status === 'fulfilled') setSellers(results[2].value || [])
    if (results[3].status === 'fulfilled') setProducts(results[3].value || [])
    if (results[4].status === 'fulfilled') setOrders(results[4].value || [])
    const rejected = results.find((item) => item.status === 'rejected')
    if (rejected) notify(rejected.reason.message, 'error')
    setLoading(false)
  }
  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps
  const setActive = async (user) => {
    try {
      if (user.active) { const reason = window.prompt('Reason for banning this user?'); if (!reason) return; await adminApi.banUser(user.id, reason) }
      else await adminApi.unbanUser(user.id)
      await load(); notify(user.active ? 'User banned and sessions invalidated' : 'User restored')
    } catch (error) { notify(error.message, 'error') }
  }
  const role = async (user, nextRole) => {
    try { await adminApi.assignRole(user.id, nextRole); await load(); notify(`Role changed to ${titleCase(nextRole)}. Existing sessions were invalidated.`) } catch (error) { notify(error.message, 'error') }
  }
  const status = async (seller, next) => {
    try {
      await sellerApi.updateStatus(seller.sellerId, next); await load()
      notify(`Seller application ${titleCase(next)}`)
    } catch (error) { notify(error.message, 'error') }
  }
  const updateOrderStatus = async (order, next) => {
    try { await orderApi.updateStatus(order.id, next); await load(); notify(`Order #${order.id} marked ${titleCase(next)}`) }
    catch (error) { notify(error.message, 'error') }
  }
  const tabs = [['overview', LayoutDashboard, 'Overview'], ['users', Users, 'Users'], ['sellers', Store, 'Sellers'], ['orders', ReceiptText, 'Orders'], ['catalog', Package, 'Catalog'], ['categories', Box, 'Categories']]
  if (loading) return <main className="shell page"><Loading label="Loading admin control room" /></main>
  return <main className="shell page admin-page">
    <div className="page-title"><div><p className="eyebrow">Protected administration</p><h1>Marketplace control room</h1></div><span className="admin-lock"><ShieldCheck size={17} />Admin only</span></div>
    <nav className="admin-tabs">{tabs.map(([key, Icon, label]) => <button className={tab === key ? 'active' : ''} key={key} onClick={() => setTab(key)}><Icon size={17} />{label}</button>)}</nav>
    {tab === 'overview' && <>
      <div className="metric-grid">
        <div><span><Users /></span><p>Total users</p><b>{dashboard.totalUsers ?? users.length}</b><small>Registered accounts</small></div>
        <div><span><Package /></span><p>Total products</p><b>{dashboard.totalProducts ?? products.length}</b><small>Across the marketplace</small></div>
        <div><span><ShoppingBag /></span><p>Total orders</p><b>{dashboard.totalOrders ?? orders.length}</b><small>Live order service</small></div>
        <div><span><ShieldCheck /></span><p>Pending disputes</p><b>{dashboard.pendingDisputes ?? 0}</b><small>Awaiting review</small></div>
      </div>
      <div className="admin-two-col">
        <section className="panel"><div className="panel-head"><div><p className="eyebrow">Needs attention</p><h2>Seller applications</h2></div><button className="text-button" onClick={() => setTab('sellers')}>View all</button></div>{sellers.filter((s) => s.approvalStatus === 'PENDING').slice(0, 4).map((seller) => <div className="approval-row" key={seller.sellerId}><span><b>{seller.shopName}</b><small>{titleCase(seller.sellerType)}</small></span><button className="button primary small" onClick={() => status(seller, 'APPROVED')}>Approve</button></div>)}{!sellers.some((s) => s.approvalStatus === 'PENDING') && <Empty icon={BadgeCheck} title="All caught up" text="There are no pending seller applications." />}</section>
        <section className="panel"><div className="panel-head"><div><p className="eyebrow">Marketplace health</p><h2>Authority model</h2></div></div><ul className="authority-list"><li><span>C</span><div><b>Customers</b><small>Shop, manage profile, cart and wishlist</small></div></li><li><span>S</span><div><b>Sellers</b><small>Customer access plus owned catalog writes</small></div></li><li><span>A</span><div><b>Administrators</b><small>Users, approvals, roles and taxonomy</small></div></li></ul></section>
      </div>
    </>}
    {tab === 'users' && <section className="panel table-panel"><div className="panel-head"><div><p className="eyebrow">Identity & access</p><h2>Users</h2></div><span>{users.length} accounts</span></div><div className="table-wrap"><table><thead><tr><th>User</th><th>Role</th><th>Status</th><th>Created</th><th>Actions</th></tr></thead><tbody>{users.map((user) => <tr key={user.id}><td><b>{user.name}</b><small className="block">{user.email}</small></td><td><select className="table-select" value={user.role} onChange={(e) => role(user, e.target.value)}><option>CUSTOMER</option><option>SELLER</option><option>ADMIN</option></select></td><td><StatusPill value={user.active ? 'ACTIVE' : 'BANNED'} /></td><td>{user.createdAt ? new Date(user.createdAt).toLocaleDateString() : '—'}</td><td><button className={`button small ${user.active ? 'danger-outline' : 'secondary'}`} onClick={() => setActive(user)}>{user.active ? 'Ban' : 'Restore'}</button></td></tr>)}</tbody></table></div></section>}
    {tab === 'sellers' && <section className="panel table-panel"><div className="panel-head"><div><p className="eyebrow">Trust & approvals</p><h2>Seller applications</h2></div></div><div className="table-wrap"><table><thead><tr><th>Shop</th><th>Owner</th><th>Type</th><th>Status</th><th>Decision</th></tr></thead><tbody>{sellers.map((seller) => <tr key={seller.sellerId}><td><b>{seller.shopName}</b><small className="block">{seller.gstNumber || 'No GST number'}</small></td><td>User #{seller.userId}</td><td>{titleCase(seller.sellerType)}</td><td><StatusPill value={seller.approvalStatus} /></td><td><select className="table-select" value={seller.approvalStatus} onChange={(e) => status(seller, e.target.value)}><option>PENDING</option><option>APPROVED</option><option>REJECTED</option><option>SUSPENDED</option></select></td></tr>)}</tbody></table></div></section>}
    {tab === 'orders' && <section className="panel table-panel"><div className="panel-head"><div><p className="eyebrow">Fulfilment</p><h2>All orders</h2></div><span>{orders.length} orders</span></div>{!orders.length ? <Empty icon={ReceiptText} title="No orders yet" text="Customer checkouts will appear here." /> : <div className="table-wrap"><table><thead><tr><th>Order</th><th>Customer</th><th>Total</th><th>Status</th><th>Fulfilment</th></tr></thead><tbody>{orders.map((order) => <tr key={order.id}><td><b>#{order.id}</b></td><td>User #{order.userId}</td><td>{money(order.totalAmount)}</td><td><StatusPill value={order.orderStatus} /></td><td><select className="table-select" value={order.orderStatus} onChange={(e) => updateOrderStatus(order, e.target.value)}><option>PENDING_PAYMENT</option><option>PAID</option><option>PAYMENT_FAILED</option><option>PROCESSING</option><option>SHIPPED</option><option>DELIVERED</option><option>CANCELLED</option></select></td></tr>)}</tbody></table></div>}</section>}
    {tab === 'catalog' && <section className="panel table-panel"><div className="panel-head"><div><p className="eyebrow">Catalog oversight</p><h2>All products</h2></div></div><div className="table-wrap"><table><thead><tr><th>Product</th><th>Category</th><th>Price</th><th>Status</th></tr></thead><tbody>{products.map((product) => <tr key={product.id}><td><b>{product.name}</b><small className="block">{product.description}</small></td><td>{product.category || '—'}</td><td>{money(product.price)}</td><td><StatusPill value={product.status || 'ACTIVE'} /></td></tr>)}</tbody></table></div></section>}
    {tab === 'categories' && <CategoryManager notify={notify} />}
  </main>
}

function Header({ session, setSession, page, setPage, onAuth, cartCount, wishCount, notify }) {
  const [menu, setMenu] = useState(false)
  const logout = () => { saveSession(null); setSession(null); setPage('shop'); notify('Signed out safely') }
  const nav = [{ key: 'shop', label: 'Shop', roles: null }]
  if (session) nav.push({ key: 'wishlist', label: 'Wishlist', roles: null }, { key: 'cart', label: 'Cart', roles: null }, { key: 'orders', label: 'Orders', roles: null }, { key: 'account', label: 'Account', roles: null })
  if (session?.role === 'SELLER') nav.push({ key: 'seller', label: 'Seller studio', roles: ['SELLER'] })
  if (session?.role === 'ADMIN') nav.push({ key: 'admin', label: 'Admin', roles: ['ADMIN'] })
  return <header className="site-header"><div className="shell header-inner">
    <button className="brand" onClick={() => setPage('shop')} aria-label="Shoping home"><span>M</span><b>Shoping</b></button>
    <nav className={`main-nav ${menu ? 'open' : ''}`}>{nav.map((item) => <button key={item.key} className={page === item.key ? 'active' : ''} onClick={() => { setPage(item.key); setMenu(false) }}>{item.label}{item.key === 'cart' && cartCount > 0 && <small>{cartCount}</small>}{item.key === 'wishlist' && wishCount > 0 && <small>{wishCount}</small>}</button>)}</nav>
    <div className="header-actions">
      {session ? <div className="user-menu"><button className="user-chip" onClick={() => setPage('account')}><span>{session.name?.[0] || 'U'}</span><div><small>{titleCase(session.role)}</small><b>{session.name?.split(' ')[0]}</b></div></button><button className="icon-button" onClick={logout} title="Sign out"><LogOut size={18} /></button></div> : <button className="button dark small" onClick={onAuth}><CircleUserRound size={17} />Sign in</button>}
      <button className="icon-button mobile-menu" aria-label="Open navigation menu" onClick={() => setMenu(!menu)}><Menu size={21} /></button>
    </div>
  </div></header>
}

function RoleGuard({ session, allowed, onAuth, children }) {
  if (!session) return <main className="shell page"><Empty icon={ShieldCheck} title="Sign in required" text="This area belongs to your personal account." action={<button className="button primary" onClick={onAuth}>Sign in</button>} /></main>
  if (allowed && !allowed.includes(session.role)) return <main className="shell page"><Empty icon={ShieldCheck} title="Access limited" text={`This area is available to ${allowed.map(titleCase).join(' or ')} accounts.`} /></main>
  return children
}

export default function App() {
  const [session, setSession] = useState(getSession)
  const navigate = useNavigate()
  const location = useLocation()
  const [authOpen, setAuthOpen] = useState(false)
  const [toast, setToast] = useState(null)
  const [cartCount, setCartCount] = useState(0)
  const [wishCount, setWishCount] = useState(0)
  const notify = (message, type = 'success') => setToast({ message, type, id: Date.now() })
  const paths = { shop: '/', cart: '/cart', checkout: '/checkout', wishlist: '/wishlist', orders: '/orders', account: '/account', seller: '/seller', admin: '/admin' }
  const page = Object.entries(paths).find(([, path]) => path === location.pathname)?.[0] || 'shop'
  const setPage = (next) => { navigate(paths[next] || '/'); window.scrollTo({ top: 0, behavior: 'smooth' }) }
  useEffect(() => {
    if (!session) { setCartCount(0); setWishCount(0); return }
    Promise.allSettled([cartApi.get(), wishlistApi.get()]).then(([cart, wish]) => {
      if (cart.status === 'fulfilled') setCartCount(cart.value.items?.length || 0)
      if (wish.status === 'fulfilled') setWishCount(wish.value.items?.length || 0)
    })
  }, [session])
  return <>
    <Header session={session} setSession={setSession} page={page} setPage={setPage} onAuth={() => setAuthOpen(true)} cartCount={cartCount} wishCount={wishCount} notify={notify} />
    <Routes>
      <Route path="/" element={<ShopPage session={session} onAuth={() => setAuthOpen(true)} notify={notify} setCartCount={setCartCount} setWishCount={setWishCount} />} />
      <Route path="/cart" element={<RoleGuard session={session} onAuth={() => setAuthOpen(true)}><CartPage notify={notify} setCartCount={setCartCount} goShop={() => setPage('shop')} goCheckout={() => setPage('checkout')} /></RoleGuard>} />
      <Route path="/checkout" element={<RoleGuard session={session} onAuth={() => setAuthOpen(true)}><CheckoutPage notify={notify} setCartCount={setCartCount} goCart={() => setPage('cart')} goOrders={() => setPage('orders')} /></RoleGuard>} />
      <Route path="/wishlist" element={<RoleGuard session={session} onAuth={() => setAuthOpen(true)}><WishlistPage notify={notify} setWishCount={setWishCount} setCartCount={setCartCount} goShop={() => setPage('shop')} /></RoleGuard>} />
      <Route path="/orders" element={<RoleGuard session={session} onAuth={() => setAuthOpen(true)}><OrdersPage notify={notify} /></RoleGuard>} />
      <Route path="/account" element={<RoleGuard session={session} onAuth={() => setAuthOpen(true)}><ProfilePage session={session} setSession={setSession} notify={notify} /></RoleGuard>} />
      <Route path="/seller" element={<RoleGuard session={session} allowed={['SELLER']} onAuth={() => setAuthOpen(true)}><SellerPage session={session} notify={notify} /></RoleGuard>} />
      <Route path="/admin" element={<RoleGuard session={session} allowed={['ADMIN']} onAuth={() => setAuthOpen(true)}><AdminPage notify={notify} /></RoleGuard>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
    <footer><div className="shell footer-inner"><div><button className="brand light" onClick={() => setPage('shop')}><span>M</span><b>Shoping</b></button><p>Good things, found here.</p></div><div><b>Marketplace</b><button onClick={() => setPage('shop')}>All products</button><button onClick={() => session ? setPage('account') : setAuthOpen(true)}>Your account</button></div><div><b>Trust</b><span>Role-aware access</span><span>Verified inventory</span></div><small>© 2026 Shoping</small></div></footer>
    <AuthModal open={authOpen} onClose={() => setAuthOpen(false)} onAuthenticated={setSession} notify={notify} />
    <Toast toast={toast} clear={() => setToast(null)} />
  </>
}
