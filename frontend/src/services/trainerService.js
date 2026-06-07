import api from './api'

export const getTrainers = async (specialization) => {
  const params = specialization && specialization !== 'All' ? { specialization } : {}
  const response = await api.get('/trainers', { params })
  return response.data
}

export const getTrainerById = async (id) => {
  const response = await api.get(`/trainers/${id}`)
  return response.data
}

export const getTrainerAvailability = async (id, from, to) => {
  const response = await api.get(`/trainers/${id}/availability`, { params: { from, to } })
  return response.data
}

export const searchTrainers = async (from, to) => {
  const response = await api.get('/trainers/search', { params: { from, to } })
  return response.data
}

export const getTrainerSchedule = async () => {
  const response = await api.get('/trainer/schedule')
  return response.data
}

export const markSessionCompleted = async (bookingId) => {
  const response = await api.put(`/trainer/bookings/${bookingId}/complete`)
  return response.data
}

export const getTrainerAvailabilitySettings = async () => {
  const response = await api.get('/trainer/availability')
  return response.data
}

export const saveTrainerAvailability = async (slots) => {
  const response = await api.post('/trainer/availability', slots)
  return response.data
}

export const blockTimeSlot = async (slotId) => {
  const response = await api.put(`/trainer/timeslots/${slotId}/block`)
  return response.data
}