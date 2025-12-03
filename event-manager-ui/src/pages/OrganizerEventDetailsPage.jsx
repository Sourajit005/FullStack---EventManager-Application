import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import eventService from '../services/eventService';

// --- Styles ---
const attendeeTableStyle = {
  width: '100%',
  marginTop: '20px',
  borderCollapse: 'collapse',
};
const thStyle = {
  border: '1px solid #ddd',
  padding: '12px',
  backgroundColor: '#f9f9f9',
  textAlign: 'left',
};
const tdStyle = {
  border: '1px solid #ddd',
  padding: '12px',
};

const OrganizerEventDetailsPage = () => {
  const { id } = useParams();
  const [event, setEvent] = useState(null);
  const [attendees, setAttendees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [eventResponse, attendeesResponse] = await Promise.all([
          eventService.getEventById(id),
          eventService.getEventAttendees(id),
        ]);
        
        setEvent(eventResponse.data);
        setAttendees(attendeesResponse.data);
        setError(null);
      } catch (err) {
        setError('Failed to load event data. Are you the organizer?');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  if (loading) return <div>Loading event details...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (!event) return <div>Event not found.</div>;

  return (
    <div className="event-details">
      <h2>{event.title}</h2>
      <p>
        <strong>When:</strong> {new Date(event.eventDate).toLocaleString()}
      </p>
      <p>
        <strong>Where:</strong> {event.location}
      </p>
      <p>
        <strong>Total Spots:</strong> {event.availableSpots + attendees.length}
      </p>
      <p>
        <strong>Spots Left:</strong> {event.availableSpots}
      </p>
      <p>
        <strong>Total Registrations:</strong> {attendees.length}
      </p>

      <hr style={{ margin: '30px 0' }} />
      <h3>Attendee List</h3>
      {attendees.length > 0 ? (
        <table style={attendeeTableStyle}>
          <thead>
            <tr>
              <th style={thStyle}>Ticket Number</th>
              <th style={thStyle}>Username</th>
              <th style={thStyle}>Email</th>
              <th style={thStyle}>Registered On</th>
            </tr>
          </thead>
          <tbody>
            {attendees.map((attendee) => (
              <tr key={attendee.registrationId}>
                <td style={tdStyle}>#{attendee.registrationId}</td>
                <td style={tdStyle}>{attendee.username}</td>
                <td style={tdStyle}>{attendee.email}</td>
                <td style={tdStyle}>
                  {new Date(attendee.registrationDate).toLocaleString()}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>No attendees have registered yet.</p>
      )}
    </div>
  );
};

export default OrganizerEventDetailsPage;