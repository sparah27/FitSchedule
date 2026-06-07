import { useEffect, useState } from 'react'
import { getTrainerSchedule, markSessionCompleted } from '../services/trainerService'
import useAuthStore from '../store/authStore'

export default function TrainerSchedule() {
    const user = useAuthStore((state) => state.user)
    const [bookings, setBookings] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        fetchSchedule()
    }, [])

    const fetchSchedule = async () => {
        try {
            const data = await getTrainerSchedule()
            setBookings(data)
        } catch (err) {
            setError('Failed to load schedule.')
        } finally {
            setLoading(false)
        }
    }

    const handleMarkCompleted = async (bookingId) => {
        try {
            await markSessionCompleted(bookingId)
            setBookings(prev =>
                prev.map(b => b.id === bookingId ? { ...b, status: 'COMPLETED' } : b)
            )
        } catch (err) {
            alert('Failed to mark as completed.')
        }
    }

    if (loading) return <div className="p-8 text-center text-gray-500">Loading...</div>
    if (error) return <div className="p-8 text-center text-red-500">{error}</div>

    const upcoming = bookings.filter(b => b.status === 'CONFIRMED')
    const past = bookings.filter(b => b.status === 'COMPLETED' || b.status === 'CANCELLED')

    return (
        <div className="max-w-4xl mx-auto px-4 py-8">
            <h1 className="text-2xl font-bold text-gray-800 mb-6">My Schedule</h1>

            <h2 className="text-lg font-semibold text-gray-700 mb-3">Upcoming Sessions</h2>
            {upcoming.length === 0 ? (
                <p className="text-gray-500 mb-6">No upcoming sessions.</p>
            ) : (
                <div className="space-y-3 mb-8">
                    {upcoming.map(b => (
                        <div key={b.id} className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 flex justify-between items-center">
                            <div>
                                <p className="font-medium text-gray-800">{b.clientFullName}</p>
                                <p className="text-sm text-gray-500">{new Date(b.startAt).toLocaleString()}</p>
                            </div>
                            <button
                                onClick={() => handleMarkCompleted(b.id)}
                                className="bg-green-500 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-green-600 transition"
                            >
                                Mark Completed
                            </button>
                        </div>
                    ))}
                </div>
            )}

            <h2 className="text-lg font-semibold text-gray-700 mb-3">Past Sessions</h2>
            {past.length === 0 ? (
                <p className="text-gray-500">No past sessions.</p>
            ) : (
                <div className="space-y-3">
                    {past.map(b => (
                        <div key={b.id} className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 flex justify-between items-center">
                            <div>
                                <p className="font-medium text-gray-800">{b.clientFullName}</p>
                                <p className="text-sm text-gray-500">{new Date(b.startAt).toLocaleString()}</p>
                            </div>
                            <span className={`text-sm font-medium px-3 py-1 rounded-full ${
                                b.status === 'COMPLETED' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                            }`}>
                {b.status}
              </span>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}