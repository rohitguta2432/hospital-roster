'use client';

import { useState, useEffect } from 'react';
import { leaveApi, staffApi } from '@/lib/api';

export default function LeavesPage() {
    const [leaves, setLeaves] = useState([]);
    const [staff, setStaff] = useState([]);
    const [activeTab, setActiveTab] = useState('ALL');
    const [showModal, setShowModal] = useState(false);
    const [form, setForm] = useState({
        staffId: '',
        leaveDate: '',
        emergency: false,
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        try {
            setLoading(true);
            const today = new Date().toISOString().split('T')[0];
            const [staffData, leavesData] = await Promise.all([
                staffApi.getAll().catch(() => []),
                leaveApi.getByDate(today).catch(() => []),
            ]);
            setStaff(staffData);
            setLeaves(leavesData);
        } catch {
            // Backend not connected
        } finally {
            setLoading(false);
        }
    }

    async function handleSubmitLeave(e) {
        e.preventDefault();
        try {
            await leaveApi.create({
                staffId: parseInt(form.staffId),
                leaveDate: form.leaveDate,
                emergency: form.emergency,
            });
            setForm({ staffId: '', leaveDate: '', emergency: false });
            setShowModal(false);
            loadData();
        } catch {
            alert('Failed to submit leave request.');
        }
    }

    const filteredLeaves = leaves.filter((l) => {
        if (activeTab === 'ALL') return true;
        if (activeTab === 'EMERGENCY') return l.emergency;
        if (activeTab === 'PLANNED') return !l.emergency;
        return true;
    });

    const emergencyCount = leaves.filter((l) => l.emergency).length;
    const plannedCount = leaves.filter((l) => !l.emergency).length;

    return (
        <>
            <div className="page-header">
                <div>
                    <h2>Leave Management</h2>
                    <p className="page-header-subtitle">Manage planned and emergency leave requests</p>
                </div>
                <button className="btn btn-amber" onClick={() => setShowModal(true)}>
                    📋 Request Leave
                </button>
            </div>

            {/* Tabs */}
            <div className="tabs">
                <button
                    className={`tab ${activeTab === 'ALL' ? 'active' : ''}`}
                    onClick={() => setActiveTab('ALL')}
                >
                    All Requests<span className="tab-badge">{leaves.length}</span>
                </button>
                <button
                    className={`tab ${activeTab === 'PLANNED' ? 'active' : ''}`}
                    onClick={() => setActiveTab('PLANNED')}
                >
                    Planned<span className="tab-badge">{plannedCount}</span>
                </button>
                <button
                    className={`tab ${activeTab === 'EMERGENCY' ? 'active' : ''}`}
                    onClick={() => setActiveTab('EMERGENCY')}
                >
                    Emergency<span className="tab-badge">{emergencyCount}</span>
                </button>
            </div>

            {/* Leave Table */}
            <div className="glass-card" style={{ overflow: 'hidden' }}>
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Staff Name</th>
                            <th>Role</th>
                            <th>Leave Date</th>
                            <th>Type</th>
                        </tr>
                    </thead>
                    <tbody>
                        {loading ? (
                            <tr>
                                <td colSpan={4} style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
                                    Loading...
                                </td>
                            </tr>
                        ) : filteredLeaves.length === 0 ? (
                            <tr>
                                <td colSpan={4} style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
                                    No leave requests found.
                                </td>
                            </tr>
                        ) : (
                            filteredLeaves.map((l) => (
                                <tr key={l.id}>
                                    <td>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                                            <div
                                                style={{
                                                    width: 36,
                                                    height: 36,
                                                    borderRadius: '50%',
                                                    background: l.emergency ? 'var(--accent-red-glow)' : 'var(--accent-blue-glow)',
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    justifyContent: 'center',
                                                    fontSize: 14,
                                                    fontWeight: 700,
                                                    color: l.emergency ? 'var(--accent-red)' : 'var(--accent-blue)',
                                                }}
                                            >
                                                {l.staff?.name?.charAt(0) || '?'}
                                            </div>
                                            <span style={{ fontWeight: 500 }}>{l.staff?.name || 'Unknown'}</span>
                                        </div>
                                    </td>
                                    <td>
                                        <span className={`chip chip-${(l.staff?.role || 'doctor').toLowerCase()}`}>
                                            {l.staff?.role || 'N/A'}
                                        </span>
                                    </td>
                                    <td style={{ color: 'var(--text-secondary)' }}>{l.leaveDate}</td>
                                    <td>
                                        {l.emergency ? (
                                            <span className="chip chip-emergency">🚨 Emergency</span>
                                        ) : (
                                            <span className="chip chip-planned">📅 Planned</span>
                                        )}
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {/* Request Leave Modal */}
            {showModal && (
                <div className="modal-overlay" onClick={() => setShowModal(false)}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3>Request Leave</h3>
                            <button className="btn btn-ghost btn-sm" onClick={() => setShowModal(false)}>
                                ✕
                            </button>
                        </div>
                        <form onSubmit={handleSubmitLeave}>
                            <div className="modal-body">
                                <div className="form-group">
                                    <label>Staff Member</label>
                                    <select
                                        className="form-select"
                                        value={form.staffId}
                                        onChange={(e) => setForm({ ...form, staffId: e.target.value })}
                                        required
                                    >
                                        <option value="">Select a staff member...</option>
                                        {staff.map((s) => (
                                            <option key={s.id} value={s.id}>
                                                {s.name} ({s.role})
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label>Leave Date</label>
                                    <input
                                        className="form-input"
                                        type="date"
                                        value={form.leaveDate}
                                        onChange={(e) => setForm({ ...form, leaveDate: e.target.value })}
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Leave Type</label>
                                    <div className="toggle-group">
                                        <button
                                            type="button"
                                            className={`toggle-option ${!form.emergency ? 'active' : ''}`}
                                            onClick={() => setForm({ ...form, emergency: false })}
                                        >
                                            📅 Planned Leave
                                        </button>
                                        <button
                                            type="button"
                                            className={`toggle-option emergency ${form.emergency ? 'active' : ''}`}
                                            onClick={() => setForm({ ...form, emergency: true })}
                                        >
                                            🚨 Emergency Leave
                                        </button>
                                    </div>
                                </div>

                                {form.emergency && (
                                    <div className="warning-banner">
                                        ⚠️ Emergency leaves will trigger <strong>automatic roster rescheduling</strong> and
                                        notify replacement staff via email immediately.
                                    </div>
                                )}
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-ghost" onClick={() => setShowModal(false)}>
                                    Cancel
                                </button>
                                <button type="submit" className={`btn ${form.emergency ? 'btn-amber' : 'btn-primary'}`}>
                                    {form.emergency ? '🚨 Submit Emergency' : '📋 Submit Request'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </>
    );
}
