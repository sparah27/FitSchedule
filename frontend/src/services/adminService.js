import api from './api'

export const getAdminStats = async () => {
    const response = await api.get('/admin/stats')
    return response.data
}

export const getAllBookings = async () => {
    const response = await api.get('/admin/bookings')
    return response.data
}

export const getAllTrainers = async () => {
    const response = await api.get('/admin/trainers')
    return response.data
}

export const deactivateTrainer = async (trainerId) => {
    const response = await api.put(`/admin/trainers/${trainerId}/deactivate`)
    return response.data
}

export const activateTrainer = async (trainerId) => {
    const response = await api.put(`/admin/trainers/${trainerId}/activate`)
    return response.data
}