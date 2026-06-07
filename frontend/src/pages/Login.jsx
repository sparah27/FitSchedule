import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import useAuthStore from '../store/authStore'
import { loginUser } from '../services/authService'

function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const login = useAuthStore((state) => state.login)

  const handleSubmit = async (e) => {
    console.log('handleSubmit called', e)
    e.preventDefault()
    console.log('preventDefault called')
    setError('')

    if (!email || !password) {
      setError('Please fill in all fields.')
      return
    }

    setLoading(true)
    try {
      const data = await loginUser(email, password)
      login({
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        role: data.role,
        userId: data.userId
      }, data.token)

      if (data.role === 'TRAINER') {
        navigate('/trainer-schedule')
      } else if (data.role === 'ADMIN') {
        navigate('/admin')
      } else {
        navigate('/trainers')
      }
    } catch (err) {
      console.log('Login error:', err)
      setError('Invalid email or password.')
    } finally {
      setLoading(false)
    }
  }

  return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="bg-white p-8 rounded-2xl shadow-md w-full max-w-md">
          <h1 className="text-3xl font-bold text-center text-gray-800 mb-2">FitSchedule</h1>
          <p className="text-center text-gray-500 mb-6">Sign in to your account</p>

          {error && (
              <div className="bg-red-50 text-red-600 text-sm px-4 py-2 rounded-lg mb-4">
                {error}
              </div>
          )}

          <form onSubmit={handleSubmit} autoComplete="off" className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  autoComplete="off"
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="you@example.com"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
              <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  autoComplete="off"
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="••••••••"
              />
            </div>

            <button
                type="button"
                onClick={(e) => {
                  console.log('button clicked')
                  handleSubmit(e)
                }}
                disabled={loading}
                className="w-full bg-blue-600 text-white py-2 rounded-lg font-semibold hover:bg-blue-700 transition disabled:opacity-50"
            >
              {loading ? 'Signing in...' : 'Sign In'}
            </button>
          </form>

          <p className="text-center text-sm text-gray-500 mt-4">
            Don't have an account?{' '}
            <a href="/register" className="text-blue-600 hover:underline">Register</a>
          </p>
        </div>
      </div>
  )
}

export default Login