import api from './api'

export const getUnreadNotifications = async () => {
  const response = await api.get('/notifications/unread')
  return response.data
}

export const markAllNotificationsRead = async () => {
  await api.patch('/notifications/read-all')
}
