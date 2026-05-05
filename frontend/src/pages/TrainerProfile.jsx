import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getTrainerById, getTrainerAvailability } from '../services/trainerService'
import BookingModal from '../components/BookingModal'
import { createBooking } from '../services/bookingService'

export default function TrainerProfile() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [trainer, setTrainer] = useState(null)
  const [slots, setSlots] = useState([])
  const [selectedSlot, setSelectedSlot] = useState(null)
  const [bookedSlots, setBookedSlots] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const trainerData = await getTrainerById(id)
        setTrainer(trainerData)

        const from = new Date()
        const to = new Date()
        to.setDate(to.getDate() + 7)

        const fromStr = from.toISOString().slice(0, 19)
        const toStr = to.toISOString().slice(0, 19)

        const availabilityData = await getTrainerAvailability(id, fromStr, toStr)
        setSlots(availabilityData)
      } catch (err) {
        console.error('Failed to fetch trainer data', err)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [id])

  const handleConfirm = async () => {
    try {
      await createBooking(selectedSlot.id)
      setBookedSlots([...bookedSlots, selectedSlot.id])
      setSelectedSlot(null)
    } catch (err) {
      console.error('Booking failed', err)
      setSelectedSlot(null)
    }
  }

  if (loading) return <div className="p-8 text-gray-500">Loading...</div>
  if (!trainer) return <div className="p-8 text-gray-500">Trainer not found.</div>

  return (
    <div className="min-h-screen bg-gray-100">
      <div className="max-w-2xl mx-auto px-4 py-8">
        <button onClick={() => navigate('/trainers')} className="text-blue-600 hover:underline mb-6 block">
          &larr; Back to Trainers
        </button>

        <div className="bg-white rounded-2xl shadow-sm p-6 mb-4">
          <div className="flex items-center gap-6 mb-4">
            <img src={trainer.photoUrl || 'https://i.pravatar.cc/300?img=12'} alt={trainer.firstName}
              className="w-24 h-24 rounded-full object-cover" />
            <div>
              <h1 className="text-2xl font-bold text-gray-800">{trainer.firstName} {trainer.lastName}</h1>
              <p className="text-gray-500">{trainer.specialization}</p>
              <p className="text-yellow-500 font-medium">&#11088; {trainer.averageRating ?? 'N/A'}</p>
            </div>
          </div>
          <p className="text-gray-600">{trainer.bio}</p>
          {trainer.certifications && (
            <p className="text-sm text-gray-400 mt-2">Certifications: {trainer.certifications}</p>
          )}
        </div>

        <div className="bg-white rounded-2xl shadow-sm p-6">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">Available Slots</h2>
          {slots.length === 0 ? (
            <p className="text-gray-400 text-sm">No available slots in the next 7 days.</p>
          ) : (
            <div className="grid grid-cols-3 gap-3">
              {slots.map((slot) => (
                <button key={slot.id}
                  onClick={() => !bookedSlots.includes(slot.id) && setSelectedSlot(slot)}
                  className={`rounded-lg py-2 text-sm font-medium transition border ${
                    bookedSlots.includes(slot.id)
                      ? 'bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed'
                      : 'border-blue-500 text-blue-600 hover:bg-blue-50'
                  }`}>
                  {new Date(slot.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  <br />
                  <span className="text-xs text-gray-400">
                    {new Date(slot.startTime).toLocaleDateString([], { month: 'short', day: 'numeric' })}
                  </span>
                </button>
              ))}
            </div>
          )}
        </div>
      </div>

      {selectedSlot && (
        <BookingModal
          trainer={trainer}
          slot={new Date(selectedSlot.startTime).toLocaleString()}
          onConfirm={handleConfirm}
          onClose={() => setSelectedSlot(null)}
        />
      )}
    </div>
  )
}