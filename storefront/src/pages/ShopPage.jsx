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

export default function ShopPage({ session, onAuth, notify, setCartCount, setWishCount }) {
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


