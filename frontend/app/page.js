'use client';

import { useState, useEffect } from 'react';
import { rosterApi, staffApi, leaveApi } from '@/lib/api';

const DAYS = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
const SHIFTS = [
    { type: 'MORNING', label: 'Morning', time: '08:00 - 16:00', cssClass: 'morning' },
    { type: 'EVENING', label: 'Evening', time: '16:00 - 00:00', cssClass: 'evening' },
    { type: 'NIGHT', label: 'Night', time: '00:00 - 08:00', cssClass: 'night' },
];

function getWeekDates() {
    const today = new Date();
    const monday = new Date(today);
    monday.setDate(today.getDate() - today.getDay() + 1);
    return Array.from({ length: 7 }, (_, i) => {
        const d = new Date(monday);
        d.setDate(monday.getDate() + i);
        return d.toISOString().split('T')[0];
    });
}

function formatDate(dateStr) {
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
}

export default function DashboardPage() {
    const [roster, setRoster] = useState([]);
    const [stats, setStats] = useState({ totalStaff: 0, onLeave: 0, activeShifts: 0, coverage: 100 });
    const [generating, setGenerating] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const weekDates = getWeekDates();

    useEffect(() => {
        loadData();
    }, []);

    async function loadData() {
        try {
            setLoading(true);
            const [staffData, rosterData, leavesData] = await Promise.all([
                staffApi.getAll().catch(() => []),
                rosterApi.getByRange(weekDates[0], weekDates[6]).catch(() => []),
                leaveApi.getByDate(new Date().toISOString().split('T')[0]).catch(() => []),
            ]);

            setRoster(rosterData);
            setStats({
                totalStaff: staffData.length,
                onLeave: leavesData.length,
                activeShifts: rosterData.length,
                coverage: rosterData.length > 0 ? 100 : 0,
            });
        } catch (err) {
            setError('Backend not connected. Start the Spring Boot server on port 8080.');
        } finally {
            setLoading(false);
        }
    }

    async function handleGenerate() {
        setGenerating(true);
        try {
            const data = await rosterApi.generate(weekDates[0], weekDates[6]);
            setRoster(data);
            setStats((prev) => ({ ...prev, activeShifts: data.length, coverage: 100 }));
        } catch (err) {
            setError('Failed to generate roster. Is the backend running?');
        } finally {
            setGenerating(false);
        }
    }

    function getCellData(date, shiftType) {
        const assignments = roster.filter(
            (a) => a.shiftDate === date && a.shiftType === shiftType
        );
        const doctors = assignments.filter((a) => a.staffRole === 'DOCTOR').length;
        const nurses = assignments.filter((a) => a.staffRole === 'NURSE').length;
        const wardens = assignments.filter((a) => a.staffRole === 'WARDEN').length;
        return { doctors, nurses, wardens, total: assignments.length };
    }

    return (
        <>
            <div className="page-header">
                <div>
                    <h2>Roster Dashboard</h2>
                    <p className="page-header-subtitle">
                        Week of {formatDate(weekDates[0])} — {formatDate(weekDates[6])}
                    </p>
                </div>
                <button className="btn btn-primary" onClick={handleGenerate} disabled={generating}>
                    {generating ? '⏳ Generating...' : '✨ Generate Roster'}
                </button>
            </div>

            {error && (
                <div className="warning-banner" style={{ marginBottom: 20 }}>
                    ⚠️ {error}
                </div>
            )}

            {/* Stats Cards */}
            <div className="stats-grid">
                <div className="glass-card stat-card">
                    <div className="stat-icon blue">👥</div>
                    <div className="stat-info">
                        <h3>{stats.totalStaff}</h3>
                        <p>Total Staff</p>
                    </div>
                </div>
                <div className="glass-card stat-card">
                    <div className="stat-icon red">📅</div>
                    <div className="stat-info">
                        <h3>{stats.onLeave}</h3>
                        <p>On Leave Today</p>
                    </div>
                </div>
                <div className="glass-card stat-card">
                    <div className="stat-icon amber">⏰</div>
                    <div className="stat-info">
                        <h3>{stats.activeShifts}</h3>
                        <p>Active Assignments</p>
                    </div>
                </div>
                <div className="glass-card stat-card">
                    <div className="stat-icon green">🛡️</div>
                    <div className="stat-info">
                        <h3>{stats.coverage}%</h3>
                        <p>Coverage Status</p>
                    </div>
                </div>
            </div>

            {/* Roster Grid */}
            <div className="glass-card" style={{ overflow: 'hidden' }}>
                <div className="roster-grid">
                    {/* Header row */}
                    <div className="roster-grid-header" style={{ background: 'var(--bg-secondary)' }}>Shift</div>
                    {weekDates.map((date, i) => (
                        <div key={date} className="roster-grid-header">
                            <div style={{ fontWeight: 700, color: 'var(--text-primary)' }}>{DAYS[i]}</div>
                            <div style={{ fontSize: '11px', marginTop: '2px' }}>{formatDate(date)}</div>
                        </div>
                    ))}

                    {/* Shift rows */}
                    {SHIFTS.map((shift) => (
                        <>
                            <div key={`label-${shift.type}`} className="roster-grid-label">
                                <span className={`chip chip-${shift.cssClass}`} style={{ fontSize: '11px', padding: '2px 8px' }}>
                                    {shift.label}
                                </span>
                                <span className="shift-time">{shift.time}</span>
                            </div>
                            {weekDates.map((date) => {
                                const cell = getCellData(date, shift.type);
                                return (
                                    <div key={`${date}-${shift.type}`} className="roster-cell">
                                        {cell.total > 0 ? (
                                            <>
                                                <div className="roster-cell-count">
                                                    <span className="dot" style={{ background: 'var(--role-doctor)' }} />
                                                    <span>{cell.doctors} Doctors</span>
                                                </div>
                                                <div className="roster-cell-count">
                                                    <span className="dot" style={{ background: 'var(--role-nurse)' }} />
                                                    <span>{cell.nurses} Nurses</span>
                                                </div>
                                                <div className="roster-cell-count">
                                                    <span className="dot" style={{ background: 'var(--role-warden)' }} />
                                                    <span>{cell.wardens} Wardens</span>
                                                </div>
                                            </>
                                        ) : (
                                            <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>No data</span>
                                        )}
                                    </div>
                                );
                            })}
                        </>
                    ))}
                </div>
            </div>
        </>
    );
}
