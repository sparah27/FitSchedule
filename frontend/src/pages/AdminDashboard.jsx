import { useEffect, useState } from 'react'
import { getAdminStats } from '../services/adminService'

export default function AdminDashboard() {
    const [stats, setStats] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        fetchStats()
    }, [])

    const fetchStats = async () => {
        try {
            const data = await getAdminStats()
            setStats(data)
        } catch (err) {
            setError('Failed to load statistics.')
        } finally {
            setLoading(false)
        }
    }

    if (loading) return <div className="p-8 text-center text-gray-500">Loading...</div>
    if (error) return <div className="p-8 text-center text-red-500">{error}</div>

    return (
        <div className="max-w-4xl mx-auto px-4 py-8">
            <h1 className="text-2xl font-bold text-gray-800 mb-6">Admin Dashboard</h1>

            <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
                <div className="bg-white rounded-xl border border-gray-200 p-6 text-center shadow-sm">
                    <p className="text-3xl font-bold text-blue-600">{stats?.totalBookings ?? 0}</p>
                    <p className="text-sm text-gray-500 mt-1">Total Bookings</p>
                </div>
                <div className="bg-white rounded-xl border border-gray-200 p-6 text-center shadow-sm">
                    <p className="text-3xl font-bold text-green-600">{stats?.activeTrainers ?? 0}</p>
                    <p className="text-sm text-gray-500 mt-1">Active Trainers</p>
                </div>
                <div className="bg-white rounded-xl border border-gray-200 p-6 text-center shadow-sm">
                    <p className="text-3xl font-bold text-purple-600">{stats?.activeClients ?? 0}</p>
                    <p className="text-sm text-gray-500 mt-1">Active Clients</p>
                </div>
                <div className="bg-white rounded-xl border border-gray-200 p-6 text-center shadow-sm">
                    <p className="text-3xl font-bold text-red-600">{stats?.cancellationRate ?? 0}%</p>
                    <p className="text-sm text-gray-500 mt-1">Cancellation Rate</p>
                </div>
            </div>
        </div>
    )
}