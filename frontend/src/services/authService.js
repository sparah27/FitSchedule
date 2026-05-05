import api from './api'

export const loginUser = async (email, password) => {
  const response = await api.post('/auth/login', { email, password })
  return response.data
}

export const registerUser = async (form) => {
  const response = await api.post('/auth/register', form)
  return response.data
}