'use client';

import { useState, useEffect } from 'react';
import { staffApi } from '@/lib/api';

export default function StaffPage() {
    const [staff, setStaff] = useState([]);
    const [filteredStaff, setFilteredStaff] = useState([]);
    const [search, setSearch] = useState('');
    const [roleFilter, setRoleFilter] = useState('ALL');
    const [showModal, setShowModal] = useState(false);
    const [form, setForm] = useState({ name: '', email: '', role: 'DOCTOR' });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadStaff();
    }, []);

    useEffect(() => {
        let filtered = staff;
        if (roleFilter !== 'ALL') {
            filtered = filtered.filter((s) => s.role === roleFilter);
        }
        if (search) {
            const q = search.toLowerCase();
            filtered = filtered.filter(
                (s) => s.name.toLowerCase().includes(q) || s.email.toLowerCase().includes(q)
            );
        }
        setFilteredStaff(filtered);
    }, [staff, search, roleFilter]);

    async function loadStaff() {
        try {
            setLoading(true);
            const data = await staffApi.getAll();
            setStaff(data);
        } catch {
            setStaff([]);
        } finally {
            setLoading(false);
        }
    }

    async function handleAddStaff(e) {
        e.preventDefault();
        try {
            await staffApi.create(form);
            setForm({ name: '', email: '', role: 'DOCTOR' });
            setShowModal(false);
            loadStaff();
        } catch (err) {
            alert('Failed to add staff member.');
        }
    }

    async function handleDelete(id) {
        if (!confirm('Are you sure you want to delete this staff member?')) return;
        try {
            await staffApi.delete(id);
            loadStaff();
        } catch {
            alert('Failed to delete.');
        }
    }

    const roleChipClass = (role) => `chip chip-${role.toLowerCase()}`;

    return (
        <>
            <div className="page-header">
                <div>
                    <h2>Staff Directory</h2>
                    <p className="page-header-subtitle">Manage hospital staff members</p>
                </div>
                <button className="btn btn-green" onClick={() => setShowModal(true)}>
                    ➕ Add Staff
                </button>
            </div>

            {/* Filter Bar */}
            <div className="filter-bar">
                <input
                    type="text"
                    className="form-input"
                    placeholder="🔍 Search by name or email..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                />
                <select
                    className="form-select"
                    value={roleFilter}
                    onChange={(e) => setRoleFilter(e.target.value)}
                >
                    <option value="ALL">All Roles</option>
                    <option value="DOCTOR">Doctor</option>
                    <option value="NURSE">Nurse</option>
                    <option value="WARDEN">Warden</option>
                </select>
                <span className="count-badge">{filteredStaff.length} members</span>
            </div>

            {/* Staff Table */}
            <div className="glass-card" style={{ overflow: 'hidden' }}>
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th style={{ textAlign: 'right' }}>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {loading ? (
                            <tr>
                                <td colSpan={4} style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
                                    Loading...
                                </td>
                            </tr>
                        ) : filteredStaff.length === 0 ? (
                            <tr>
                                <td colSpan={4} style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
                                    {staff.length === 0
                                        ? 'No staff members found. Add some to get started!'
                                        : 'No results match your filter.'}
                                </td>
                            </tr>
                        ) : (
                            filteredStaff.map((s) => (
                                <tr key={s.id}>
                                    <td>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                                            <div
                                                style={{
                                                    width: 36,
                                                    height: 36,
                                                    borderRadius: '50%',
                                                    background: 'var(--accent-blue-glow)',
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    justifyContent: 'center',
                                                    fontSize: 14,
                                                    fontWeight: 700,
                                                    color: 'var(--accent-blue)',
                                                }}
                                            >
                                                {s.name.charAt(0).toUpperCase()}
                                            </div>
                                            <span style={{ fontWeight: 500 }}>{s.name}</span>
                                        </div>
                                    </td>
                                    <td style={{ color: 'var(--text-secondary)' }}>{s.email}</td>
                                    <td>
                                        <span className={roleChipClass(s.role)}>{s.role}</span>
                                    </td>
                                    <td style={{ textAlign: 'right' }}>
                                        <button
                                            className="btn btn-ghost btn-sm"
                                            onClick={() => handleDelete(s.id)}
                                            title="Delete"
                                        >
                                            🗑️
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {/* Add Staff Modal */}
            {showModal && (
                <div className="modal-overlay" onClick={() => setShowModal(false)}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3>Add Staff Member</h3>
                            <button className="btn btn-ghost btn-sm" onClick={() => setShowModal(false)}>
                                ✕
                            </button>
                        </div>
                        <form onSubmit={handleAddStaff}>
                            <div className="modal-body">
                                <div className="form-group">
                                    <label>Full Name</label>
                                    <input
                                        className="form-input"
                                        type="text"
                                        placeholder="Dr. Jane Smith"
                                        value={form.name}
                                        onChange={(e) => setForm({ ...form, name: e.target.value })}
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Email</label>
                                    <input
                                        className="form-input"
                                        type="email"
                                        placeholder="jane.smith@hospital.com"
                                        value={form.email}
                                        onChange={(e) => setForm({ ...form, email: e.target.value })}
                                        required
                                    />
                                </div>
                                <div className="form-group">
                                    <label>Role</label>
                                    <select
                                        className="form-select"
                                        value={form.role}
                                        onChange={(e) => setForm({ ...form, role: e.target.value })}
                                    >
                                        <option value="DOCTOR">Doctor</option>
                                        <option value="NURSE">Nurse</option>
                                        <option value="WARDEN">Warden</option>
                                    </select>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-ghost" onClick={() => setShowModal(false)}>
                                    Cancel
                                </button>
                                <button type="submit" className="btn btn-green">
                                    Add Staff
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </>
    );
}
