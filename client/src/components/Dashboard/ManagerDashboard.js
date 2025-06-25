import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Alert from 'react-bootstrap/Alert';
import Card from 'react-bootstrap/Card';

const API_URL = '/api/timesheets/submitted';

const workTypeDisplay = {
  OFFICE: 'Work from office',
  HOME: 'Work from home',
};

const ManagerDashboard = () => {
  const [timesheets, setTimesheets] = useState([]);
  const [filteredTimesheets, setFilteredTimesheets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchEmployeeId, setSearchEmployeeId] = useState('');

  const token = localStorage.getItem('token');
  const authHeader = token ? { Authorization: `Bearer ${token}` } : {};

  useEffect(() => {
    fetchSubmittedTimesheets();
    // eslint-disable-next-line
  }, []);

  useEffect(() => {
    if (searchEmployeeId.trim() === '') {
      setFilteredTimesheets(timesheets);
    } else {
      setFilteredTimesheets(timesheets.filter(ts => 
        ts.user?.id?.toString().includes(searchEmployeeId.trim())
      ));
    }
  }, [searchEmployeeId, timesheets]);

  const fetchSubmittedTimesheets = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await axios.get(API_URL, { headers: authHeader });
      setTimesheets(res.data);
      setFilteredTimesheets(res.data);
    } catch (err) {
      setError('Failed to fetch submitted timesheets');
    }
    setLoading(false);
  };

  const handleSearch = async () => {
    if (!searchEmployeeId.trim()) {
      await fetchSubmittedTimesheets();
      return;
    }

    setError(null);
    try {
      const res = await axios.get(`${API_URL}/search?employeeId=${searchEmployeeId.trim()}`, { 
        headers: authHeader 
      });
      setFilteredTimesheets(res.data);
    } catch (err) {
      setError('Failed to search timesheets');
      setFilteredTimesheets([]);
    }
  };

  const handleClearSearch = () => {
    setSearchEmployeeId('');
    fetchSubmittedTimesheets();
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <Alert variant="danger">{error}</Alert>;

  return (
    <Container>
      <Row className="mb-4">
        <Col>
          <h1>Manager Dashboard</h1>
          <p>Welcome, {localStorage.getItem('username')}!</p>
          
          {/* Search Section */}
          <div style={{ marginBottom: 20, padding: 15, backgroundColor: '#f8f9fa', borderRadius: 5 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <label style={{ fontWeight: 'bold' }}>Search by Employee ID:</label>
              <input
                type="text"
                placeholder="Enter Employee ID"
                value={searchEmployeeId}
                onChange={(e) => setSearchEmployeeId(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                style={{ 
                  padding: '8px 12px', 
                  border: '1px solid #ddd', 
                  borderRadius: 4,
                  width: '200px'
                }}
              />
              <button 
                onClick={handleSearch}
                style={{ 
                  padding: '8px 16px', 
                  backgroundColor: '#007bff', 
                  color: 'white', 
                  border: 'none', 
                  borderRadius: 4,
                  cursor: 'pointer'
                }}
              >
                Search
              </button>
              <button 
                onClick={handleClearSearch}
                style={{ 
                  padding: '8px 16px', 
                  backgroundColor: '#6c757d', 
                  color: 'white', 
                  border: 'none', 
                  borderRadius: 4,
                  cursor: 'pointer'
                }}
              >
                Clear
              </button>
            </div>
          </div>

          {/* Results Section */}
          <Card>
            <Card.Header>
              <h5>Employee Timesheets</h5>
            </Card.Header>
            <Card.Body>
              {filteredTimesheets.length === 0 ? (
                <Alert variant="warning">
                  No timesheets found.
                </Alert>
              ) : (
                <div className="table-responsive">
                  <table className="table table-striped table-hover">
                    <thead className="table-dark">
                      <tr>
                        <th>Employee ID</th>
                        <th>Employee Name</th>
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
                      {filteredTimesheets.map(ts => (
                        <tr key={ts.id}>
                          <td><strong>{ts.user?.id}</strong></td>
                          <td>{ts.user?.fullName}</td>
                          <td>{ts.date}</td>
                          <td>{ts.day}</td>
                          <td>{ts.loginTime || '-'}</td>
                          <td>{ts.logoutTime || '-'}</td>
                          <td>
                            <span className={`badge ${ts.workType === 'OFFICE' ? 'bg-primary' : 'bg-success'}`}>
                              {workTypeDisplay[ts.workType] || ts.workType || '-'}
                            </span>
                          </td>
                          <td>
                            <span className={`badge ${ts.leave ? 'bg-warning' : 'bg-secondary'}`}>
                              {ts.leave ? 'Yes' : 'No'}
                            </span>
                          </td>
                          <td>{ts.remark || '-'}</td>
                          <td>
                            <span className={`badge ${ts.submittedToAdmin ? 'bg-success' : 'bg-info'}`}>
                              {ts.submittedToAdmin ? 'Submitted' : 'Draft'}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default ManagerDashboard; 