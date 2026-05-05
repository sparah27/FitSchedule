import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { searchTrainers } from '../services/trainerService'

export default function Search() {
  const [date, setDate] = useState('')
  const [timeFrom, setTimeFrom] = useState('')
  const [timeTo, setTimeTo] = useState('')
  const [results, setResults] = useState([])
  const [searched, setSearched] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const navigate = useNavigate()

  const handleSearch = async () => {
    if (!date || !timeFrom || !timeTo) return
    setLoading(true)
    setError(null)
    setSearched(false)
    try {
      const from = `${date}T${timeFrom}:00`
      const to = `${date}T${timeTo}:00`
      const data = await searchTrainers(from, to)
      setResults(data)
      setSearched(true)
    } catch (err) {
      setError('Failed to search trainers. Please try again.')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <div className="max-w-3xl mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">Search Trainers</h1>
        <p className="text-gray-500 mb-6">Find available trainers by date and time</p>

        <div className="bg-white rounded-2xl shadow-sm p-6 mb-6">
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date</label>
              <input type="date" value={date} onChange={e => setDate(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">From</label>
              <input type="time" value={timeFrom} onChange={e => setTimeFrom(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">To</label>
              <input type="time" value={timeTo} onChange={e => setTimeTo(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
          </div>
          <button onClick={handleSearch} disabled={!date || !timeFrom || !timeTo || loading}
            className="mt-4 w-full bg-blue-600 text-white py-2 rounded-lg font-semibold hover:bg-blue-700 transition disabled:opacity-50">
            {loading ? 'Searching...' : 'Search'}
          </button>
        </div>

        {error && (
          <div className="text-center text-red-500 py-4">{error}</div>
        )}

        {searched && results.length === 0 && (
          <div className="text-center text-gray-500 py-12">No trainers available in this time window.</div>
        )}

        {results.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {results.map(trainer => (
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
