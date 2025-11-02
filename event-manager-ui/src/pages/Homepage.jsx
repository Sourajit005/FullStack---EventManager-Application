import React, { useState, useEffect } from 'react';
import eventService from '../services/eventService';
import EventCard from '../components/EventCard';

const Homepage = () => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        setLoading(true);
        const response = await eventService.getAllEvents();
        setEvents(response.data);
        setError(null);
      } catch (err) {
        setError('Failed to fetch events. Is the backend running?');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchEvents();
  }, []);

  if (loading) {
    return <div>Loading events...</div>;
  }

  if (error) {
    return <div className="error-message">{error}</div>;
  }

  return (
    <div>
      <h2>Upcoming Events</h2>
      <div className="event-grid">
        {events.length > 0 ? (
          events.map((event) => <EventCard key={event.id} event={event} />)
        ) : (
          <p>No events found.</p>
        )}
      </div>
    </div>
  );
};

export default Homepage;