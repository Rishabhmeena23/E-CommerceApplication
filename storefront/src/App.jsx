import { useEffect, useState } from 'react'
import { Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom'
import { cartApi, wishlistApi } from './api/services'
import { getSession } from './api/client'
import { Header, RoleGuard } from './components/AppHeader'
import { AuthModal, Toast } from './components/feedback'
import ShopPage from './pages/ShopPage'
import { CartPage, CheckoutPage, OrdersPage, ProfilePage, WishlistPage } from './pages/CustomerPages'
import SellerPage from './pages/SellerPage'
import AdminPage from './pages/AdminPage'

const paths = { shop: '/', cart: '/cart', checkout: '/checkout', wishlist: '/wishlist', orders: '/orders', account: '/account', seller: '/seller', admin: '/admin' }

export default function App() {
  const [session, setSession] = useState(getSession)
  const [authOpen, setAuthOpen] = useState(false)
  const [toast, setToast] = useState(null)
  const [cartCount, setCartCount] = useState(0)
  const [wishCount, setWishCount] = useState(0)
  const navigate = useNavigate()
  const location = useLocation()
  const notify = (message, type = 'success') => setToast({ message, type, id: Date.now() })
  const page = Object.entries(paths).find(([, path]) => path === location.pathname)?.[0] || 'shop'
  const setPage = (next) => { navigate(paths[next] || '/'); window.scrollTo({ top: 0, behavior: 'smooth' }) }
  const openAuth = () => setAuthOpen(true)

  useEffect(() => {
    if (!session) { setCartCount(0); setWishCount(0); return }
    Promise.allSettled([cartApi.get(), wishlistApi.get()]).then(([cart, wish]) => {
      if (cart.status === 'fulfilled') setCartCount(cart.value.items?.length || 0)
      if (wish.status === 'fulfilled') setWishCount(wish.value.items?.length || 0)
    })
  }, [session])

  return <>
    <Header session={session} setSession={setSession} page={page} setPage={setPage} onAuth={openAuth} cartCount={cartCount} wishCount={wishCount} notify={notify} />
    <Routes>
      <Route path="/" element={<ShopPage session={session} onAuth={openAuth} notify={notify} setCartCount={setCartCount} setWishCount={setWishCount} />} />
      <Route path="/cart" element={<RoleGuard session={session} onAuth={openAuth}><CartPage notify={notify} setCartCount={setCartCount} goShop={() => setPage('shop')} goCheckout={() => setPage('checkout')} /></RoleGuard>} />
      <Route path="/checkout" element={<RoleGuard session={session} onAuth={openAuth}><CheckoutPage notify={notify} setCartCount={setCartCount} goCart={() => setPage('cart')} goOrders={() => setPage('orders')} /></RoleGuard>} />
      <Route path="/wishlist" element={<RoleGuard session={session} onAuth={openAuth}><WishlistPage notify={notify} setWishCount={setWishCount} setCartCount={setCartCount} goShop={() => setPage('shop')} /></RoleGuard>} />
      <Route path="/orders" element={<RoleGuard session={session} onAuth={openAuth}><OrdersPage notify={notify} /></RoleGuard>} />
      <Route path="/account" element={<RoleGuard session={session} onAuth={openAuth}><ProfilePage session={session} setSession={setSession} notify={notify} /></RoleGuard>} />
      <Route path="/seller" element={<RoleGuard session={session} allowed={['SELLER']} onAuth={openAuth}><SellerPage session={session} notify={notify} /></RoleGuard>} />
      <Route path="/admin" element={<RoleGuard session={session} allowed={['ADMIN']} onAuth={openAuth}><AdminPage notify={notify} /></RoleGuard>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
    <footer><div className="shell footer-inner">
      <div><button className="brand light" onClick={() => setPage('shop')}><span>M</span><b>Shopping</b></button><p>Good things, found here.</p></div>
      <div><b>Marketplace</b><button onClick={() => setPage('shop')}>All products</button><button onClick={() => session ? setPage('account') : openAuth()}>Your account</button></div>
      <div><b>Trust</b><span>Role-aware access</span><span>Verified inventory</span></div><small>© 2026 Shopping</small>
    </div></footer>
    <AuthModal open={authOpen} onClose={() => setAuthOpen(false)} onAuthenticated={setSession} notify={notify} />
    <Toast toast={toast} clear={() => setToast(null)} />
  </>
}

