import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import useAuthStore from '../store/authStore'

const mockNotifications = [
  { id: 1, message: 'Your booking with Marko Nikolic is confirmed.', read: false },
  { id: 2, message: 'Your session with Ana Kovac is tomorrow at 13:00.', read: false },
  { id: 3, message: 'Booking cancelled successfully.', read: true },
]

export default function Navbar() {
  const navigate = useNavigate()
  const logout = useAuthStore((state) => state.logout)
  const [showNotifications, setShowNotifications] = useState(false)
  const [notifications, setNotifications] = useState(mockNotifications)
  const [menuOpen, setMenuOpen] = useState(false)

  const unread = notifications.filter(n => !n.read).length

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  const markAllRead = () => {
    setNotifications(notifications.map(n => ({ ...n, read: true })))
  }

 const navLinks = [
  { label: 'Trainers', path: '/trainers' },
  { label: 'Search', path: '/search' },
  { label: 'My Bookings', path: '/my-bookings' },
  { label: 'Profile', path: '/profile' },
]

  return (
    <nav className="bg-white shadow-sm px-6 py-4 relative">
      <div className="flex items-center justify-between">
        <h1 onClick={() => navigate('/trainers')} className="text-xl font-bold text-blue-600 cursor-pointer">
          FitSchedule
        </h1>

        <div className="hidden sm:flex gap-4 items-center">
          {navLinks.map(link => (
            <button key={link.path} onClick={() => navigate(link.path)}
              className="text-gray-600 hover:text-blue-600 text-sm font-medium">
              {link.label}
            </button>
          ))}

          <div className="relative">
            <button onClick={() => setShowNotifications(!showNotifications)}
              className="relative text-gray-600 hover:text-blue-600 text-sm font-medium">
              🔔
              {unread > 0 && (
                <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center">
                  {unread}
                </span>
              )}
            </button>

            {showNotifications && (
              <div className="absolute right-0 top-8 bg-white shadow-lg rounded-xl w-72 z-50 border border-gray-100">
                <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100">
                  <span className="font-semibold text-gray-800 text-sm">Notifications</span>
                  <button onClick={markAllRead} className="text-xs text-blue-600 hover:underline">Mark all read</button>
                </div>
                <ul>
                  {notifications.map(n => (
                    <li key={n.id} className={`px-4 py-3 text-sm border-b border-gray-50 ${n.read ? 'text-gray-400' : 'text-gray-700 font-medium'}`}>
                      {n.message}
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>

          <button onClick={handleLogout} className="text-sm font-medium text-white bg-blue-600 px-4 py-1.5 rounded-lg hover:bg-blue-700">
            Logout
          </button>
        </div>

        <button onClick={() => setMenuOpen(!menuOpen)} className="sm:hidden text-gray-600 text-2xl">
          {menuOpen ? '✕' : '☰'}
        </button>
      </div>

      {menuOpen && (
        <div className="sm:hidden mt-4 flex flex-col gap-3 border-t border-gray-100 pt-4">
          {navLinks.map(link => (
            <button key={link.path} onClick={() => { navigate(link.path); setMenuOpen(false) }}
              className="text-gray-600 hover:text-blue-600 text-sm font-medium text-left">
              {link.label}
            </button>
          ))}
          <button onClick={handleLogout} className="text-sm font-medium text-white bg-blue-600 px-4 py-2 rounded-lg hover:bg-blue-700 text-left">
            Logout
          </button>
        </div>
      )}
    </nav>
  )
}