import { useState } from 'react'
import useAuthStore from '../store/authStore'

export default function Profile() {
  const user = useAuthStore((state) => state.user)
  const [form, setForm] = useState({
    firstName: user?.name || '',
    lastName: '',
    email: user?.email || '',
    phone: '',
  })
  const [saved, setSaved] = useState(false)

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
    setSaved(false)
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    setSaved(true)
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <div className="max-w-lg mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">My Profile</h1>
        <p className="text-gray-500 mb-6">Update your personal information</p>

        <div className="bg-white rounded-2xl shadow-sm p-6">
          <div className="flex justify-center mb-6">
            <div className="w-20 h-20 rounded-full bg-blue-100 flex items-center justify-center text-3xl text-blue-600 font-bold">
              {form.firstName?.charAt(0) || 'U'}
            </div>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="flex gap-4">
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-700 mb-1">First Name</label>
                <input type="text" name="firstName" value={form.firstName} onChange={handleChange}
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-700 mb-1">Last Name</label>
                <input type="text" name="lastName" value={form.lastName} onChange={handleChange}
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input type="email" name="email" value={form.email} onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Phone</label>
              <input type="tel" name="phone" value={form.phone} onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="+387 61 000 000" />
            </div>

            <button type="submit"
              className="w-full bg-blue-600 text-white py-2 rounded-lg font-semibold hover:bg-blue-700 transition">
              Save Changes
            </button>

            {saved && (
              <p className="text-green-600 text-sm text-center font-medium">Profile updated successfully!</p>
            )}
          </form>
        </div>
      </div>
    </div>
  )
}