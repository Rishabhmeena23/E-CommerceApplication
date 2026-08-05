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

export function ProductVisual({ product, className = '' }) {
  const image = product.images?.find((item) => item.primaryImage) || product.images?.[0]
  return (
    <div className={`product-visual ${className}`} style={!image?.url ? { background: placeholderColors[(product.id || 0) % placeholderColors.length] } : undefined}>
      {image?.url ? <img src={image.url} alt={image.altText || product.name} /> : (
        <div className="visual-placeholder"><span>{product.brand || 'Shopping'}</span><Package size={44} strokeWidth={1.2} /></div>
      )}
    </div>
  )
}

export function ProductCard({ product, onView, onAdd, onWish, canShop }) {
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

export function ProductDetail({ product, onClose, onAdd, onWish }) {
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


