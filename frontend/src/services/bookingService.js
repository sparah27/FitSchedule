import api from './api'

export const createBooking = async (timeSlotId) => {
  const response = await api.post('/bookings', { timeSlotId })
  return response.data
}

export const getMyUpcomingBookings = async () => {
  const response = await api.get('/bookings/me')
  return response.data
}

export const getMyPastBookings = async () => {
  const response = await api.get('/bookings/me/past')
  return response.data
}

export const cancelBooking = async (id) => {
  const response = await api.delete(`/bookings/${id}`)
  return response.data
}