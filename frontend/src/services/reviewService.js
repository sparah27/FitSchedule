import api from './api'

export const getTrainerReviews = async (trainerId) => {
    const response = await api.get(`/trainers/${trainerId}/reviews`)
    return response.data
}

export const submitReview = async (bookingId, rating, comment) => {
    const response = await api.post(`/bookings/${bookingId}/review`, { rating, comment })
    return response.data
}