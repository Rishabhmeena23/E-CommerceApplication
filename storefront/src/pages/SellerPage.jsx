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

export default function SellerPage({ session, notify }) {
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

