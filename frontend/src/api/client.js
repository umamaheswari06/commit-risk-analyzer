import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

// Normalizes backend error shape { timestamp, status, error, message } into a plain message string.
function extractMessage(error) {
  if (error.response?.data?.message) return error.response.data.message
  if (error.message === 'Network Error') return 'Could not reach the backend. Is it running on port 8080?'
  return error.message || 'Something went wrong.'
}

export async function analyzeCommit({ diff, commitMessage }) {
  try {
    const { data } = await api.post('/analyze', { diff, commitMessage })
    return data
  } catch (err) {
    throw new Error(extractMessage(err))
  }
}

export async function getHistory() {
  try {
    const { data } = await api.get('/analyses')
    return data
  } catch (err) {
    throw new Error(extractMessage(err))
  }
}

export async function getAnalysisById(id) {
  try {
    const { data } = await api.get(`/analyses/${id}`)
    return data
  } catch (err) {
    throw new Error(extractMessage(err))
  }
}

export async function getDashboardStats() {
  try {
    const { data } = await api.get('/dashboard/stats')
    return data
  } catch (err) {
    throw new Error(extractMessage(err))
  }
}

export default api
