import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API_URL = '/api/timesheets';

const getDayName = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-US', { weekday: 'long' });
};

const getCurrentWeekDates = () => {
  const now = new Date();
  const monday = new Date(now);
  monday.setDate(now.getDate() - ((now.getDay() + 6) % 7));
  return Array.from({ length: 7 }, (_, i) => {
    const d = new Date(monday);
    d.setDate(monday.getDate() + i);
    return d.toISOString().slice(0, 10);
  });
};

const workTypeDisplay = {
  OFFICE: 'Work from office',
  HOME: 'Work from home',
};

const UserDashboard = () => {
  const [timesheets, setTimesheets] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [editData, setEditData] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [addingDate, setAddingDate] = useState(null);

  // Get JWT token from localStorage
  const token = localStorage.getItem('token');
  const authHeader = token ? { Authorization: `Bearer ${token}` } : {};

  // Fetch timesheets on mount
  useEffect(() => {
    fetchTimesheets();
    // eslint-disable-next-line
  }, []);

  const fetchTimesheets = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await axios.get(API_URL, { headers: authHeader });
      setTimesheets(res.data);
    } catch (err) {
      setError('Failed to fetch timesheets');
    }
    setLoading(false);
  };

  const handleEdit = (ts) => {
    setEditingId(ts.id);
    setEditData({ ...ts });
  };

  const handleEditChange = (e) => {
    const { name, value, type, checked } = e.target;
    setEditData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleEditSave = async (id) => {
    try {
      await axios.put(`${API_URL}/${id}`, editData, { headers: authHeader });
      setEditingId(null);
      fetchTimesheets();
    } catch (err) {
      setError('Failed to update timesheet');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this timesheet?')) return;
    try {
      await axios.delete(`${API_URL}/${id}`, { headers: authHeader });
      fetchTimesheets();
    } catch (err) {
      setError('Failed to delete timesheet');
    }
  };

  const handleSubmit = async (id) => {
    try {
      await axios.put(`${API_URL}/submit/${id}`, {}, { headers: authHeader });
      fetchTimesheets();
    } catch (err) {
      setError('Failed to submit timesheet');
    }
  };

  const handleAddEntry = async (date) => {
    setAddingDate(date);
    try {
      const res = await axios.post(API_URL, { date, day: getDayName(date) }, { headers: authHeader });
      setAddingDate(null);
      fetchTimesheets();
    } catch (err) {
      setError('Failed to add timesheet entry');
      setAddingDate(null);
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div style={{ color: 'red' }}>{error}</div>;

  // Map timesheets by date for easy lookup
  const tsByDate = Object.fromEntries(timesheets.map(ts => [ts.date, ts]));
  const weekDates = getCurrentWeekDates();

  return (
    <div>
      <h2>User Dashboard</h2>
      <table border="1" cellPadding="8" style={{ width: '100%', marginTop: 20 }}>
        <thead>
          <tr>
            <th>Date</th>
            <th>Day</th>
            <th>Login Time</th>
            <th>Logout Time</th>
            <th>Work Type</th>
            <th>Leave</th>
            <th>Remark</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {weekDates.map(date => {
            const ts = tsByDate[date];
            if (ts) {
              const isSubmitted = ts.submittedToAdmin;
              return (
                <tr key={ts.id} style={isSubmitted ? { background: '#e0ffe0' } : {}}>
                  <td>{ts.date}</td>
                  <td>{ts.day || getDayName(ts.date)}</td>
                  <td>
                    {editingId === ts.id ? (
                      <input type="time" name="loginTime" value={editData.loginTime || ''} onChange={handleEditChange} disabled={isSubmitted} />
                    ) : (
                      ts.loginTime
                    )}
                  </td>
                  <td>
                    {editingId === ts.id ? (
                      <input type="time" name="logoutTime" value={editData.logoutTime || ''} onChange={handleEditChange} disabled={isSubmitted} />
                    ) : (
                      ts.logoutTime
                    )}
                  </td>
                  <td>
                    {editingId === ts.id ? (
                      <select name="workType" value={editData.workType || ''} onChange={handleEditChange} disabled={isSubmitted}>
                        <option value="">Select</option>
                        <option value="OFFICE">Work from office</option>
                        <option value="HOME">Work from home</option>
                      </select>
                    ) : (
                      workTypeDisplay[ts.workType] || ts.workType
                    )}
                  </td>
                  <td>
                    {editingId === ts.id ? (
                      <input type="checkbox" name="leave" checked={!!editData.leave} onChange={handleEditChange} disabled={isSubmitted} />
                    ) : (
                      ts.leave ? 'Yes' : 'No'
                    )}
                  </td>
                  <td>
                    {editingId === ts.id ? (
                      <input type="text" name="remark" value={editData.remark || ''} onChange={handleEditChange} disabled={isSubmitted} />
                    ) : (
                      ts.remark
                    )}
                  </td>
                  <td>{isSubmitted ? 'Submitted' : 'Draft'}</td>
                  <td>
                    {editingId === ts.id ? (
                      <>
                        <button onClick={() => handleEditSave(ts.id)} disabled={isSubmitted}>Save</button>
                        <button onClick={() => setEditingId(null)}>Cancel</button>
                      </>
                    ) : (
                      <>
                        <button onClick={() => handleEdit(ts)} disabled={isSubmitted}>Edit</button>{' '}
                        <button onClick={() => handleDelete(ts.id)} disabled={isSubmitted}>Delete</button>{' '}
                        {!isSubmitted && <button onClick={() => handleSubmit(ts.id)}>Submit</button>}
                      </>
                    )}
                  </td>
                </tr>
              );
            } else {
              return (
                <tr key={date} style={{ background: '#fffbe0' }}>
                  <td>{date}</td>
                  <td>{getDayName(date)}</td>
                  <td colSpan={6}>No entry</td>
                  <td>
                    <button onClick={() => handleAddEntry(date)} disabled={addingDate === date}>Add Entry</button>
                  </td>
                </tr>
              );
            }
          })}
        </tbody>
      </table>
    </div>
  );
};

export default UserDashboard; 