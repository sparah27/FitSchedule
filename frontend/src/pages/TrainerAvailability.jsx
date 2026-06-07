import { useEffect, useState } from 'react'
import { getTrainerAvailabilitySettings, saveTrainerAvailability } from '../services/trainerService'

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']

export default function TrainerAvailability() {
    const [availability, setAvailability] = useState(
        DAYS.map(day => ({ day, startTime: '08:00', endTime: '17:00', enabled: false }))
    )
    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [success, setSuccess] = useState(false)
    const [error, setError] = useState('')

    useEffect(() => {
        fetchAvailability()
    }, [])

    const fetchAvailability = async () => {
        try {
            const data = await getTrainerAvailabilitySettings()
            if (data && data.length > 0) {
                setAvailability(DAYS.map(day => {
                    const existing = data.find(d => d.day === day)
                    return existing
                        ? { day, startTime: existing.startTime, endTime: existing.endTime, enabled: true }
                        : { day, startTime: '08:00', endTime: '17:00', enabled: false }
                }))
            }
        } catch (err) {
            setError('Failed to load availability.')
        } finally {
            setLoading(false)
        }
    }

    const toggleDay = (index) => {
        setAvailability(prev => prev.map((a, i) => i === index ? { ...a, enabled: !a.enabled } : a))
    }

    const updateTime = (index, field, value) => {
        setAvailability(prev => prev.map((a, i) => i === index ? { ...a, [field]: value } : a))
    }

    const handleSave = async () => {
        setSaving(true)
        setSuccess(false)
        setError('')
        try {
            const payload = availability
                .filter(a => a.enabled)
                .map(a => ({ day: a.day, startTime: a.startTime, endTime: a.endTime }))
            await saveTrainerAvailability(payload)
            setSuccess(true)
        } catch (err) {
            setError('Failed to save availability.')
        } finally {
            setSaving(false)
        }
    }

    if (loading) return <div className="p-8 text-center text-gray-500">Loading...</div>

    return (
        <div className="max-w-2xl mx-auto px-4 py-8">
            <h1 className="text-2xl font-bold text-gray-800 mb-6">My Availability</h1>

            {error && <div className="bg-red-50 text-red-600 text-sm px-4 py-2 rounded-lg mb-4">{error}</div>}
            {success && <div className="bg-green-50 text-green-600 text-sm px-4 py-2 rounded-lg mb-4">Availability saved successfully.</div>}

            <div className="space-y-3">
                {availability.map((a, index) => (
                    <div key={a.day} className="bg-white rounded-xl border border-gray-200 p-4">
                        <div className="flex items-center justify-between mb-3">
                            <span className="font-medium text-gray-800">{a.day}</span>
                            <label className="flex items-center gap-2 cursor-pointer">
                                <span className="text-sm text-gray-500">{a.enabled ? 'Available' : 'Unavailable'}</span>
                                <div
                                    onClick={() => toggleDay(index)}
                                    className={`w-10 h-6 rounded-full transition-colors ${a.enabled ? 'bg-blue-500' : 'bg-gray-300'} relative cursor-pointer`}
                                >
                                    <div className={`absolute top-1 w-4 h-4 bg-white rounded-full shadow transition-all ${a.enabled ? 'left-5' : 'left-1'}`} />
                                </div>
                            </label>
                        </div>

                        {a.enabled && (
                            <div className="flex gap-4">
                                <div className="flex-1">
                                    <label className="text-xs text-gray-500 mb-1 block">Start Time</label>
                                    <input
                                        type="time"
                                        value={a.startTime}
                                        onChange={(e) => updateTime(index, 'startTime', e.target.value)}
                                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    />
                                </div>
                                <div className="flex-1">
                                    <label className="text-xs text-gray-500 mb-1 block">End Time</label>
                                    <input
                                        type="time"
                                        value={a.endTime}
                                        onChange={(e) => updateTime(index, 'endTime', e.target.value)}
                                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    />
                                </div>
                            </div>
                        )}
                    </div>
                ))}
            </div>

            <button
                onClick={handleSave}
                disabled={saving}
                className="mt-6 w-full bg-blue-600 text-white py-2 rounded-lg font-semibold hover:bg-blue-700 transition disabled:opacity-50"
            >
                {saving ? 'Saving...' : 'Save Availability'}
            </button>
        </div>
    )
}