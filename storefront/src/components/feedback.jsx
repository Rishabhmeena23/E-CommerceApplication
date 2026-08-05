import { useEffect, useState } from 'react'
import { ArrowRight, BadgeCheck, BarChart3, Box, Check, ChevronDown, CircleUserRound,
  CreditCard, Heart, LayoutDashboard, LogOut, Menu, Package, Pencil, Plus, ReceiptText, Search,
  Settings, ShieldCheck, ShoppingBag, ShoppingCart, SlidersHorizontal, Sparkles,
  Store, Trash2, TrendingUp, Truck, UserRound, Users, X } from 'lucide-react'
import { adminApi, authApi, cartApi, categoryApi, customerApi, orderApi, paymentApi,
  productApi, sellerApi, subCategoryApi, userApi, wishlistApi } from '../api/services'
import { getSession, saveSession } from '../api/client'
import { money, titleCase, placeholderColors } from '../utils/format'

export function Modal({ open, onClose, title, subtitle, children, wide = false }) {
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

export function Toast({ toast, clear }) {
  useEffect(() => {
    if (!toast) return undefined
    const timer = setTimeout(clear, 3600)
    return () => clearTimeout(timer)
  }, [toast, clear])
  if (!toast) return null
  return <div className={`toast ${toast.type || 'success'}`}>{toast.type !== 'error' && <Check size={18} />}{toast.message}</div>
}

export function Loading({ label = 'Loading' }) {
  return <div className="loading"><span className="spinner" />{label}…</div>
}

export function Empty({ icon: Icon = Box, title, text, action }) {
  return (
    <div className="empty">
      <div className="empty-icon"><Icon size={27} /></div>
      <h3>{title}</h3><p>{text}</p>{action}
    </div>
  )
}

export function StatusPill({ value }) {
  const key = String(value || '').toLowerCase()
  return <span className={`status ${key}`}>{titleCase(value || 'Unknown')}</span>
}

export function AuthModal({ open, onClose, onAuthenticated, notify }) {
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
        {mode === 'login' ? 'Sign in to continue your Shopping journey.' : 'Join as a customer. You can apply to sell later.'}
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


