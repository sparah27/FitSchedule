import { useState, useEffect } from 'react'
import { getMyUpcomingBookings, getMyPastBookings, cancelBooking } from '../services/bookingService'
import { submitReview } from '../services/reviewService'

const statusColor = {
  UPCOMING: 'bg-blue-100 text-blue-700',
  CONFIRMED: 'bg-blue-100 text-blue-700',
  COMPLETED: 'bg-green-100 text-green-700',
  CANCELLED: 'bg-red-100 text-red-700',
}

export default function MyBookings() {
  const [upcoming, setUpcoming] = useState([])
  const [past, setPast] = useState([])
  const [tab, setTab] = useState('upcoming')
  const [cancelId, setCancelId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [reviewBooking, setReviewBooking] = useState(null)
  const [rating, setRating] = useState(5)
  const [comment, setComment] = useState('')
  const [reviewedIds, setReviewedIds] = useState([])
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    const fetchBookings = async () => {
      setLoading(true)
      try {
        const [upcomingData, pastData] = await Promise.all([
          getMyUpcomingBookings(),
          getMyPastBookings()
        ])
        setUpcoming(upcomingData)
        setPast(pastData)
      } catch (err) {
        console.error('Failed to fetch bookings', err)
      } finally {
        setLoading(false)
      }
    }
    fetchBookings()
  }, [])

  const handleCancel = async () => {
    try {
      await cancelBooking(cancelId)
      setUpcoming(upcoming.map(b => b.id === cancelId ? { ...b, status: 'CANCELLED' } : b))
      setCancelId(null)
    } catch (err) {
      console.error('Cancel failed', err)
      setCancelId(null)
    }
  }

  const handleSubmitReview = async () => {
    setSubmitting(true)
    try {
      await submitReview(reviewBooking.id, rating, comment)
      setReviewedIds([...reviewedIds, reviewBooking.id])
      setReviewBooking(null)
      setRating(5)
      setComment('')
    } catch (err) {
      console.error('Review failed', err)
    } finally {
      setSubmitting(false)
    }
  }

  const bookings = tab === 'upcoming' ? upcoming : past

  return (
      <div className="min-h-screen bg-gray-100">
        <div className="max-w-2xl mx-auto px-4 py-8">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">My Bookings</h1>
          <p className="text-gray-500 mb-6">Your upcoming and past training sessions</p>

          <div className="flex gap-2 mb-6">
            <button onClick={() => setTab('upcoming')}
                    className={`px-4 py-2 rounded-full text-sm font-medium transition ${tab === 'upcoming' ? 'bg-blue-600 text-white' : 'bg-white text-gray-600 border border-gray-300'}`}>
              Upcoming
            </button>
            <button onClick={() => setTab('past')}
                    className={`px-4 py-2 rounded-full text-sm font-medium transition ${tab === 'past' ? 'bg-blue-600 text-white' : 'bg-white text-gray-600 border border-gray-300'}`}>
              Past
            </button>
          </div>

          {loading ? (
              <div className="text-center text-gray-500 py-12">Loading bookings...</div>
          ) : bookings.length === 0 ? (
              <div className="text-center text-gray-400 py-12">No bookings found.</div>
          ) : (
              <div className="space-y-4">
                {bookings.map((booking) => (
                    <div key={booking.id} className="bg-white rounded-2xl shadow-sm p-5 flex items-center justify-between">
                      <div>
                        <h2 className="text-lg font-semibold text-gray-800">{booking.trainerFullName}</h2>
                        <p className="text-sm text-gray-500">{booking.trainerSpecialization}</p>
                        <p className="text-sm text-gray-500">
                          {new Date(booking.startAt).toLocaleDateString()} at {new Date(booking.startAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                        </p>
                      </div>
                      <div className="flex flex-col items-end gap-2">
                  <span className={`text-xs font-medium px-3 py-1 rounded-full ${statusColor[booking.status] || 'bg-gray-100 text-gray-600'}`}>
                    {booking.status}
                  </span>
                        {(booking.status === 'UPCOMING' || booking.status === 'CONFIRMED') && (
                            <button onClick={() => setCancelId(booking.id)}
                                    className="text-xs text-red-500 hover:underline">
                              Cancel
                            </button>
                        )}
                        {booking.status === 'COMPLETED' && !reviewedIds.includes(booking.id) && (
                            <button onClick={() => setReviewBooking(booking)}
                                    className="text-xs text-blue-500 hover:underline">
                              Leave Review
                            </button>
                        )}
                        {booking.status === 'COMPLETED' && reviewedIds.includes(booking.id) && (
                            <span className="text-xs text-green-500">Reviewed</span>
                        )}
                      </div>
                    </div>
                ))}
              </div>
          )}
        </div>

        {cancelId && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
              <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm mx-4">
                <h2 className="text-xl font-bold text-gray-800 mb-2">Cancel Booking</h2>
                <p className="text-gray-500 text-sm mb-6">Are you sure you want to cancel this session?</p>
                <div className="flex gap-3">
                  <button onClick={() => setCancelId(null)}
                          className="flex-1 border border-gray-300 text-gray-600 py-2 rounded-lg font-medium hover:bg-gray-50 transition">
                    Keep it
                  </button>
                  <button onClick={handleCancel}
                          className="flex-1 bg-red-500 text-white py-2 rounded-lg font-medium hover:bg-red-600 transition">
                    Yes, Cancel
                  </button>
                </div>
              </div>
            </div>
        )}

        {reviewBooking && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
              <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm mx-4">
                <h2 className="text-xl font-bold text-gray-800 mb-1">Leave a Review</h2>
                <p className="text-gray-500 text-sm mb-4">{reviewBooking.trainerFullName}</p>

                <div className="flex gap-2 mb-4">
                  {[1, 2, 3, 4, 5].map(star => (
                      <button key={star} onClick={() => setRating(star)}
                              className={`text-2xl ${star <= rating ? 'text-yellow-400' : 'text-gray-300'}`}>
                        ★
                      </button>
                  ))}
                </div>

                <textarea
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                    placeholder="Write a comment (optional)"
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm mb-4 focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none h-24"
                />

                <div className="flex gap-3">
                  <button onClick={() => setReviewBooking(null)}
                          className="flex-1 border border-gray-300 text-gray-600 py-2 rounded-lg font-medium hover:bg-gray-50 transition">
                    Cancel
                  </button>
                  <button onClick={handleSubmitReview} disabled={submitting}
                          className="flex-1 bg-blue-600 text-white py-2 rounded-lg font-medium hover:bg-blue-700 transition disabled:opacity-50">
                    {submitting ? 'Submitting...' : 'Submit'}
                  </button>
                </div>
              </div>
            </div>
        )}
      </div>
  )
}