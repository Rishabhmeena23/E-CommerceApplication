const API_URL = import.meta.env.VITE_API_URL || '/api'
const STORAGE_KEY = 'mercato-session'

export function getSession() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null')
  } catch {
    return null
  }
}

export function saveSession(session) {
  if (session) localStorage.setItem(STORAGE_KEY, JSON.stringify(session))
  else localStorage.removeItem(STORAGE_KEY)
}

function getErrorMessage(body, status) {
  if (typeof body === 'string' && body.trim()) return body
  if (body && typeof body === 'object') {
    if (body.message) return body.message
    if (body.error) return body.error
    if (Array.isArray(body.fieldErrors)) {
      return body.fieldErrors
        .map((item) => `${item.field || 'Field'}: ${item.message || 'is invalid'}`)
        .join(', ')
    }
  }
  if (status === 401) return 'Your session has expired. Please sign in again.'
  if (status === 403) return 'You do not have permission to perform this action.'
  if (status === 404) return 'The requested item could not be found.'
  return `The request could not be completed (${status}).`
}

export async function request(path, options = {}) {
  const session = getSession()
  const headers = new Headers(options.headers)
  if (session?.token) headers.set('Authorization', `Bearer ${session.token}`)

  let body = options.body
  if (body !== undefined && body !== null && !(body instanceof FormData)) {
    headers.set('Content-Type', 'application/json')
    body = JSON.stringify(body)
  }

  let response
  try {
    response = await fetch(`${API_URL}${path}`, { ...options, headers, body })
  } catch {
    throw new Error('Cannot reach the marketplace server. Make sure the API Gateway is running on port 8080.')
  }

  const text = await response.text()
  let data = text
  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      // Some delete endpoints intentionally return plain text.
    }
  } else {
    data = null
  }

  if (!response.ok) {
    if (response.status === 401) saveSession(null)
    throw new Error(getErrorMessage(data, response.status))
  }
  return data
}

export function queryString(params = {}) {
  const query = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== '' && value !== null && value !== undefined) query.set(key, value)
  })
  const value = query.toString()
  return value ? `?${value}` : ''
}
