using web_server.Collections;

namespace web_server.Repository
{
    public interface ITicketBookingRepository
    {
        // Create a new reservation for a user on a specific train.
        Task CreateReservation(string userId, string trainId, TicketBooking newBooking);

        // Retrieve a list of bookings for a specific user.
        List<TicketBooking> GetBookingsByUserId(string userId);

        // Update the reservation date for a booking identified by its id.
        Task<bool> UpdateReservationDateAsync(string id, string newReservationDate);

        // Retrieve a reservation by its unique reservationId.
        TicketBooking GetReservationById(string reservationId);

        // Cancel a reservation using its unique reservationId.
        void CancelReservation(string reservationId);

        // Retrieve all reservations.
        Task<IEnumerable<TicketBooking>> GetAllAsync();

        //Update satatus of reservation
        Task<bool> UpdateBookingStatusAsync(string id, string newStatus);
    }
}
