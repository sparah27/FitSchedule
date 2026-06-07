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
import TrainerSchedule from './pages/TrainerSchedule'
import TrainerAvailability from './pages/TrainerAvailability'
import AdminDashboard from './pages/AdminDashboard'
import AdminBookings from './pages/AdminBookings'
import AdminTrainers from './pages/AdminTrainers'

function Layout() {
  const location = useLocation()
  const hideNavbar = ['/', '/register'].includes(location.pathname)

  return (
      <>
        {!hideNavbar && <Navbar />}
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* CLIENT */}
          <Route path="/trainers" element={<ProtectedRoute allowedRoles={['CLIENT']}><Trainers /></ProtectedRoute>} />
          <Route path="/trainer/:id" element={<ProtectedRoute allowedRoles={['CLIENT']}><TrainerProfile /></ProtectedRoute>} />
          <Route path="/my-bookings" element={<ProtectedRoute allowedRoles={['CLIENT']}><MyBookings /></ProtectedRoute>} />
          <Route path="/search" element={<ProtectedRoute allowedRoles={['CLIENT']}><Search /></ProtectedRoute>} />
          <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />

          {/* TRAINER */}
          <Route path="/trainer-schedule" element={<ProtectedRoute allowedRoles={['TRAINER']}><TrainerSchedule /></ProtectedRoute>} />
          <Route path="/trainer-availability" element={<ProtectedRoute allowedRoles={['TRAINER']}><TrainerAvailability /></ProtectedRoute>} />

          {/* ADMIN */}
          <Route path="/admin" element={<ProtectedRoute allowedRoles={['ADMIN']}><AdminDashboard /></ProtectedRoute>} />
          <Route path="/admin/bookings" element={<ProtectedRoute allowedRoles={['ADMIN']}><AdminBookings /></ProtectedRoute>} />
          <Route path="/admin/trainers" element={<ProtectedRoute allowedRoles={['ADMIN']}><AdminTrainers /></ProtectedRoute>} />
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