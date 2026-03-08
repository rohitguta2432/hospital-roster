const API_BASE = '/api';

async function fetchJSON(url, options = {}) {
    const res = await fetch(`${API_BASE}${url}`, {
        headers: { 'Content-Type': 'application/json', ...options.headers },
        ...options,
    });
    if (!res.ok) throw new Error(`API Error: ${res.status} ${res.statusText}`);
    if (res.status === 204) return null;
    return res.json();
}

// Staff API
export const staffApi = {
    getAll: () => fetchJSON('/staff'),
    getById: (id) => fetchJSON(`/staff/${id}`),
    getByRole: (role) => fetchJSON(`/staff/role/${role}`),
    create: (data) => fetchJSON('/staff', { method: 'POST', body: JSON.stringify(data) }),
    delete: (id) => fetchJSON(`/staff/${id}`, { method: 'DELETE' }),
};

// Leave API
export const leaveApi = {
    create: (data) => fetchJSON('/leaves', { method: 'POST', body: JSON.stringify(data) }),
    getByDate: (date) => fetchJSON(`/leaves/date/${date}`),
    getByStaff: (staffId) => fetchJSON(`/leaves/staff/${staffId}`),
};

// Roster API
export const rosterApi = {
    generate: (startDate, endDate) =>
        fetchJSON('/roster/generate', {
            method: 'POST',
            body: JSON.stringify({ startDate, endDate }),
        }),
    getByDate: (date) => fetchJSON(`/roster/date/${date}`),
    getByRange: (start, end) => fetchJSON(`/roster/range?start=${start}&end=${end}`),
};
