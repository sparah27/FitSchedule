import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Trainers from './pages/Trainers'
import TrainerProfile from './pages/TrainerProfile'
import MyBookings from './pages/MyBookings'
import Search from './pages/Search'
import Navbar from './components/Navbar'
import ProtectedRoute from './components/ProtectedRoute'
import Profile from './pages/Profile'

function Layout() {
  const location = useLocation()
  const hideNavbar = ['/', '/register'].includes(location.pathname)

  return (
    <>
      {!hideNavbar && <Navbar />}
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/trainers" element={<ProtectedRoute><Trainers /></ProtectedRoute>} />
        <Route path="/trainer/:id" element={<ProtectedRoute><TrainerProfile /></ProtectedRoute>} />
        <Route path="/my-bookings" element={<ProtectedRoute><MyBookings /></ProtectedRoute>} />
        <Route path="/search" element={<ProtectedRoute><Search /></ProtectedRoute>} />
        <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
      </Routes>
    </>
  )
}

function App() {
  return (
    <BrowserRouter>
      <Layout />
    </BrowserRouter>
  )
}

export default App