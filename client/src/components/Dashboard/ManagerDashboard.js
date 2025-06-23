import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API_URL = '/api/timesheets/submitted';

const workTypeDisplay = {
  OFFICE: 'Work from office',
  HOME: 'Work from home',
};

const ManagerDashboard = () => {
  const [timesheets, setTimesheets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const token = localStorage.getItem('token');
  const authHeader = token ? { Authorization: `Bearer ${token}` } : {};

  useEffect(() => {
    fetchSubmittedTimesheets();
    // eslint-disable-next-line
  }, []);

  const fetchSubmittedTimesheets = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await axios.get(API_URL, { headers: authHeader });
      setTimesheets(res.data);
    } catch (err) {
      setError('Failed to fetch submitted timesheets');
    }
    setLoading(false);
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div style={{ color: 'red' }}>{error}</div>;

  return (
    <div>
      <h2>Manager Dashboard</h2>
      <table border="1" cellPadding="8" style={{ width: '100%', marginTop: 20 }}>
        <thead>
          <tr>
            <th>User ID</th>
            <th>User Name</th>
            <th>Date</th>
            <th>Day</th>
            <th>Login Time</th>
            <th>Logout Time</th>
            <th>Work Type</th>
            <th>Leave</th>
            <th>Remark</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {timesheets.map(ts => (
            <tr key={ts.id} style={{ background: '#e0ffe0' }}>
              <td>{ts.user?.id}</td>
              <td>{ts.user?.fullName}</td>
              <td>{ts.date}</td>
              <td>{ts.day}</td>
              <td>{ts.loginTime}</td>
              <td>{ts.logoutTime}</td>
              <td>{workTypeDisplay[ts.workType] || ts.workType}</td>
              <td>{ts.leave ? 'Yes' : 'No'}</td>
              <td>{ts.remark}</td>
              <td>{ts.submittedToAdmin ? 'Submitted' : 'Draft'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ManagerDashboard; 