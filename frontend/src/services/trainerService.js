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