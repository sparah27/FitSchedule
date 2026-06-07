import { useEffect, useState } from 'react'
import { getAllBookings } from '../services/adminService'

export default function AdminBookings() {
    const [bookings, setBookings] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [filter, setFilter] = useState('ALL')

    useEffect(() => {
        fetchBookings()
    }, [])

    const fetchBookings = async () => {
        try {
            const data = await getAllBookings()
            setBookings(data)
        } catch (err) {
            setError('Failed to load bookings.')
        } finally {
            setLoading(false)
        }
    }

    if (loading) return <div className="p-8 text-center text-gray-500">Loading...</div>
    if (error) return <div className="p-8 text-center text-red-500">{error}</div>

    const filtered = filter === 'ALL' ? bookings : bookings.filter(b => b.status === filter)

    return (
        <div className="max-w-5xl mx-auto px-4 py-8">
            <h1 className="text-2xl font-bold text-gray-800 mb-6">All Bookings</h1>

            <div className="flex gap-2 mb-6">
                {['ALL', 'CONFIRMED', 'COMPLETED', 'CANCELLED'].map(s => (
                    <button
                        key={s}
                        onClick={() => setFilter(s)}
                        className={`px-4 py-1.5 rounded-full text-sm font-medium transition ${
                            filter === s ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                        }`}
                    >
                        {s}
                    </button>
                ))}
            </div>

            {filtered.length === 0 ? (
                <p className="text-gray-500">No bookings found.</p>
            ) : (
                <div className="space-y-3">
                    {filtered.map(b => (
                        <div key={b.id} className="bg-white rounded-xl border border-gray-200 p-4 shadow-sm">
                            <div className="flex justify-between items-center">
                                <div>
                                    <p className="font-medium text-gray-800">{b.clientFullName}</p>
                                    <p className="text-sm text-gray-500">Trainer: {b.trainerFullName}</p>
                                    <p className="text-sm text-gray-500">{new Date(b.startAt).toLocaleString()}</p>
                                </div>
                                <span className={`text-sm font-medium px-3 py-1 rounded-full ${
                                    b.status === 'CONFIRMED' ? 'bg-blue-100 text-blue-700' :
                                        b.status === 'COMPLETED' ? 'bg-green-100 text-green-700' :
                                            'bg-red-100 text-red-700'
                                }`}>
                  {b.status}
                </span>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}