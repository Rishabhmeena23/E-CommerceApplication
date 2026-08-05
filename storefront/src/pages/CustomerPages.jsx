import { useEffect, useState } from 'react'
import { ArrowRight, BadgeCheck, BarChart3, Box, Check, ChevronDown, CircleUserRound,
  CreditCard, Heart, LayoutDashboard, LogOut, Menu, Package, Pencil, Plus, ReceiptText, Search,
  Settings, ShieldCheck, ShoppingBag, ShoppingCart, SlidersHorizontal, Sparkles,
  Store, Trash2, TrendingUp, Truck, UserRound, Users, X } from 'lucide-react'
import { adminApi, authApi, cartApi, categoryApi, customerApi, orderApi, paymentApi,
  productApi, sellerApi, subCategoryApi, userApi, wishlistApi } from '../api/services'
import { getSession, saveSession } from '../api/client'
import { money, titleCase, placeholderColors } from '../utils/format'
import { Empty, Loading, Modal, StatusPill } from '../components/feedback'
import { ProductCard, ProductDetail, ProductVisual } from '../components/products'

export function CartPage({ notify, setCartCount, goShop, goCheckout }) {
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
                <div className="line-copy"><span>{product.brand || 'Shopping seller'}</span><h3>{product.name}</h3><strong>{money(item.price)}</strong></div>
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

export function CheckoutPage({ notify, setCartCount, goCart, goOrders }) {
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

export function OrdersPage({ notify }) {
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

export function WishlistPage({ notify, setWishCount, setCartCount, goShop }) {
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

export function ProfilePage({ session, setSession, notify }) {
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
        <div className="panel-head"><div><p className="eyebrow">Sell on Shopping</p><h2>{seller ? seller.shopName : 'Open your shop'}</h2></div>{seller && <StatusPill value={seller.approvalStatus} />}</div>
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


