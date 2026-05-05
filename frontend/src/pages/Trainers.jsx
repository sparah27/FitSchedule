import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getTrainers } from '../services/trainerService'

const specializations = ['All', 'Strength', 'Yoga', 'Cardio', 'Pilates']

export default function Trainers() {
  const [trainers, setTrainers] = useState([])
  const [filter, setFilter] = useState('All')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const navigate = useNavigate()

  useEffect(() => {
    const fetchTrainers = async () => {
      setLoading(true)
      setError(null)
      try {
        const data = await getTrainers(filter)
        setTrainers(data)
      } catch (err) {
        const status = err.response?.status
        const msg = err.response?.data?.message
        setError(`Failed to load trainers (${status ?? 'network error'}${msg ? ': ' + msg : ''}). Please try again.`)
        console.error('Failed to fetch trainers', err)
      } finally {
        setLoading(false)
      }
    }
    fetchTrainers()
  }, [filter])

  return (
    <div className="min-h-screen bg-gray-100">
      <div className="max-w-4xl mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">Find a Trainer</h1>
        <p className="text-gray-500 mb-6">Browse and book personal training sessions</p>

        <div className="flex gap-2 mb-6 flex-wrap">
          {specializations.map((s) => (
            <button key={s} onClick={() => setFilter(s)}
              className={`px-4 py-2 rounded-full text-sm font-medium transition ${filter === s ? 'bg-blue-600 text-white' : 'bg-white text-gray-600 border border-gray-300'}`}>
              {s}
            </button>
          ))}
        </div>

        {loading ? (
          <div className="text-center text-gray-500 py-12">Loading trainers...</div>
        ) : error ? (
          <div className="text-center text-red-500 py-12">{error}</div>
        ) : trainers.length === 0 ? (
          <div className="text-center text-gray-500 py-12">No trainers found.</div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {trainers.map((trainer) => (
              <div key={trainer.id} onClick={() => navigate(`/trainer/${trainer.id}`)}
                className="bg-white rounded-2xl shadow-sm p-4 flex items-center gap-4 cursor-pointer hover:shadow-md transition">
                <img src={trainer.photoUrl || 'https://i.pravatar.cc/300?img=12'} alt={trainer.firstName}
                  className="w-16 h-16 rounded-full object-cover" />
                <div>
                  <h2 className="text-lg font-semibold text-gray-800">{trainer.firstName} {trainer.lastName}</h2>
                  <p className="text-sm text-gray-500">{trainer.specialization}</p>
                  <p className="text-sm text-yellow-500 font-medium">&#11088; {trainer.averageRating ?? 'N/A'}</p>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}