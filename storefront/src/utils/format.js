export const money = (value) =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 })
    .format(Number(value || 0))

export const titleCase = (value = '') =>
  value.toLowerCase().replaceAll('_', ' ').replace(/\b\w/g, (letter) => letter.toUpperCase())

export const placeholderColors = [
  'linear-gradient(145deg, #e7efe9 0%, #b9cdbd 100%)',
  'linear-gradient(145deg, #f1e8dc 0%, #d6bda0 100%)',
  'linear-gradient(145deg, #ece7f2 0%, #c9bdd8 100%)',
  'linear-gradient(145deg, #e5edf0 0%, #afc7ce 100%)',
]
