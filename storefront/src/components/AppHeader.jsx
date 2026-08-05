import { useEffect, useState } from 'react'
import { ArrowRight, BadgeCheck, BarChart3, Box, Check, ChevronDown, CircleUserRound,
  CreditCard, Heart, LayoutDashboard, LogOut, Menu, Package, Pencil, Plus, ReceiptText, Search,
  Settings, ShieldCheck, ShoppingBag, ShoppingCart, SlidersHorizontal, Sparkles,
  Store, Trash2, TrendingUp, Truck, UserRound, Users, X } from 'lucide-react'
import { adminApi, authApi, cartApi, categoryApi, customerApi, orderApi, paymentApi,
  productApi, sellerApi, subCategoryApi, userApi, wishlistApi } from '../api/services'
import { getSession, saveSession } from '../api/client'
import { money, titleCase, placeholderColors } from '../utils/format'
import { Empty, Loading, Modal, StatusPill } from './feedback'

export function Header({ session, setSession, page, setPage, onAuth, cartCount, wishCount, notify }) {
  const [menu, setMenu] = useState(false)
  const logout = () => { saveSession(null); setSession(null); setPage('shop'); notify('Signed out safely') }
  const nav = [{ key: 'shop', label: 'Shop', roles: null }]
  if (session) nav.push({ key: 'wishlist', label: 'Wishlist', roles: null }, { key: 'cart', label: 'Cart', roles: null }, { key: 'orders', label: 'Orders', roles: null }, { key: 'account', label: 'Account', roles: null })
  if (session?.role === 'SELLER') nav.push({ key: 'seller', label: 'Seller studio', roles: ['SELLER'] })
  if (session?.role === 'ADMIN') nav.push({ key: 'admin', label: 'Admin', roles: ['ADMIN'] })
  return <header className="site-header"><div className="shell header-inner">
    <button className="brand" onClick={() => setPage('shop')} aria-label="Shopping home"><span>M</span><b>Shopping</b></button>
    <nav className={`main-nav ${menu ? 'open' : ''}`}>{nav.map((item) => <button key={item.key} className={page === item.key ? 'active' : ''} onClick={() => { setPage(item.key); setMenu(false) }}>{item.label}{item.key === 'cart' && cartCount > 0 && <small>{cartCount}</small>}{item.key === 'wishlist' && wishCount > 0 && <small>{wishCount}</small>}</button>)}</nav>
    <div className="header-actions">
      {session ? <div className="user-menu"><button className="user-chip" onClick={() => setPage('account')}><span>{session.name?.[0] || 'U'}</span><div><small>{titleCase(session.role)}</small><b>{session.name?.split(' ')[0]}</b></div></button><button className="icon-button" onClick={logout} title="Sign out"><LogOut size={18} /></button></div> : <button className="button dark small" onClick={onAuth}><CircleUserRound size={17} />Sign in</button>}
      <button className="icon-button mobile-menu" aria-label="Open navigation menu" onClick={() => setMenu(!menu)}><Menu size={21} /></button>
    </div>
  </div></header>
}

export function RoleGuard({ session, allowed, onAuth, children }) {
  if (!session) return <main className="shell page"><Empty icon={ShieldCheck} title="Sign in required" text="This area belongs to your personal account." action={<button className="button primary" onClick={onAuth}>Sign in</button>} /></main>
  if (allowed && !allowed.includes(session.role)) return <main className="shell page"><Empty icon={ShieldCheck} title="Access limited" text={`This area is available to ${allowed.map(titleCase).join(' or ')} accounts.`} /></main>
  return children
}


