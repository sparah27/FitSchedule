import { useEffect, useState } from 'react'
import { getAllTrainers, deactivateTrainer } from '../services/adminService'

export default function AdminTrainers() {
    const [trainers, setTrainers] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        fetchTrainers()
    }, [])

    const fetchTrainers = async () => {
        try {
            const data = await getAllTrainers()
            setTrainers(data)
        } catch (err) {
            setError('Failed to load trainers.')
        } finally {
            setLoading(false)
        }
    }

    const handleDeactivate = async (trainerId) => {
        if (!window.confirm('Are you sure you want to deactivate this trainer?')) return
        try {
            await deactivateTrainer(trainerId)
            setTrainers(prev =>
                prev.map(t => t.id === trainerId ? { ...t, active: false } : t)
            )
        } catch (err) {
            alert('Failed to deactivate trainer.')
        }
    }

    if (loading) return <div className="p-8 text-center text-gray-500">Loading...</div>
    if (error) return <div className="p-8 text-center text-red-500">{error}</div>

    return (
        <div className="max-w-4xl mx-auto px-4 py-8">
            <h1 className="text-2xl font-bold text-gray-800 mb-6">Manage Trainers</h1>

            {trainers.length === 0 ? (
                <p className="text-gray-500">No trainers found.</p>
            ) : (
                <div className="space-y-3">
                    {trainers.map(t => (
                        <div key={t.id} className="bg-white rounded-xl border border-gray-200 p-4 shadow-sm flex justify-between items-center">
                            <div>
                                <p className="font-medium text-gray-800">{t.firstName} {t.lastName}</p>
                                <p className="text-sm text-gray-500">{t.email}</p>
                                <p className="text-sm text-gray-500">{t.specialization}</p>
                            </div>
                            <div className="flex items-center gap-3">
                <span className={`text-sm font-medium px-3 py-1 rounded-full ${
                    t.active ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
                }`}>
                  {t.active ? 'Active' : 'Inactive'}
                </span>
                                {t.active && (
                                    <button
                                        onClick={() => handleDeactivate(t.id)}
                                        className="bg-red-500 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-red-600 transition"
                                    >
                                        Deactivate
                                    </button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}