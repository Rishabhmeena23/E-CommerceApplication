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

export default function AdminPage({ notify }) {
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

