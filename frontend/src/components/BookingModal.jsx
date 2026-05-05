export default function BookingModal({ trainer, slot, onConfirm, onClose }) {
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm mx-4">
        <h2 className="text-xl font-bold text-gray-800 mb-4">Confirm Booking</h2>
        
        <div className="bg-gray-50 rounded-lg p-4 mb-6 space-y-2">
          <p className="text-sm text-gray-600">
            <span className="font-medium">Trainer:</span> {trainer.firstName} {trainer.lastName}
          </p>
          <p className="text-sm text-gray-600">
            <span className="font-medium">Specialization:</span> {trainer.specialization}
          </p>
          <p className="text-sm text-gray-600">
            <span className="font-medium">Time:</span> {slot}
          </p>
        </div>

        <div className="flex gap-3">
          <button onClick={onClose}
            className="flex-1 border border-gray-300 text-gray-600 py-2 rounded-lg font-medium hover:bg-gray-50 transition">
            Cancel
          </button>
          <button onClick={onConfirm}
            className="flex-1 bg-blue-600 text-white py-2 rounded-lg font-medium hover:bg-blue-700 transition">
            Confirm
          </button>
        </div>
      </div>
    </div>
  )
}