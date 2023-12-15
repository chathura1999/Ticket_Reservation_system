using Microsoft.IdentityModel.Tokens;
using MongoDB.Bson;
using MongoDB.Driver;
using web_server.Collections;

namespace web_server.Repository
{
    public class TicketBookingRepository : ITicketBookingRepository
    {
        // MongoDB collections and repositories
        private readonly IMongoCollection<TicketBooking> _mongoTicketCollection;
        private readonly ITrainsRepository _trainsRepository;
        private readonly IUserRepository _userRepository;
        private readonly IMongoCollection<User> _mongoUserCollection;

        // Constructor to initialize dependencies
        public TicketBookingRepository(IMongoDatabase mongoDatabase, ITrainsRepository trainsRepository, IUserRepository userRepository)
        {
            _mongoTicketCollection = mongoDatabase.GetCollection<TicketBooking>("Ticket");
            _trainsRepository = trainsRepository;
            _userRepository = userRepository;
            _mongoUserCollection = mongoDatabase.GetCollection<User>("User");
        }

        // Method to create a new reservation
        public async Task CreateReservation(string userId, string trainId, TicketBooking newBooking)
        {
            // Retrieve train details based on the trainId
            var trainDetails = await _trainsRepository.GetTrainScheduleById(trainId);

            if (trainDetails == null)
            {
                throw new Exception("Train details not found.");
            }

            // Retrieve user information based on the userId
            var userInfo = await _userRepository.GetUserById(userId);

            if (userInfo == null)
            {
                throw new Exception("User information not found.");
            }


            // Check if the user has already made 4 reservations
            var userReservations = await _userRepository.GetTicketBookingCountForUser(userId);
            Console.WriteLine($"Count of reserva: {userReservations}");
            if (userReservations >= 4)
            {
                throw new Exception("Maximum 4 reservations per user are allowed.");
            }

            // Set the booking details
            newBooking.ReservationDate = newBooking.ReservationDate;
            newBooking.TrainName = trainDetails.TrainName;
            newBooking.UserId = userId;
            newBooking.UserNIC = userInfo.NIC;
            newBooking.UserName = userInfo.Username;
            newBooking.TrainName = trainDetails.TrainName;
            newBooking.TrainId = trainDetails.Id;
            newBooking.BookingStatus = newBooking.BookingStatus;
            newBooking.CreatedDate = DateTime.UtcNow;

            // Insert the new booking into the MongoDB collection
            await _mongoTicketCollection.InsertOneAsync(newBooking);
            //Add train Id to user
            await _userRepository.AddTrainIdToUser(userId, newBooking.Id);
        }

        // Method to get bookings by user ID
        public List<TicketBooking> GetBookingsByUserId(string userId)
        {
            var filter = Builders<TicketBooking>.Filter.Eq(x => x.UserId, userId);
            return _mongoTicketCollection.Find(filter).ToList();
        }

        // Method to update reservation date asynchronously
        public async Task<bool> UpdateReservationDateAsync(string id, string newReservationDate)
        {
            try
            {
                var filter = Builders<TicketBooking>.Filter.Eq("Id", id);
                var update = Builders<TicketBooking>.Update.Set("ReservationDate", newReservationDate);

                var result = await _mongoTicketCollection.UpdateOneAsync(filter, update);

                return result.ModifiedCount == 1;
            }
            catch (Exception)
            {
                return false;
            }
        }

        // Method to cancel a reservation
        public void CancelReservation(string reservationId)
        {
            var reservation = GetReservationById(reservationId);

            if (reservation != null)
            {
                // Delete the reservation from the TicketBooking collection
                _mongoTicketCollection.DeleteOne(x => x.Id == reservationId);

                // Remove the specific reservationId from the user's TicketBookingIds list
                var userId = reservation.UserId;
                var userFilter = Builders<User>.Filter.Eq(u => u.Id, userId);
                var update = Builders<User>.Update.Pull(u => u.TicketBookingIds, reservationId);
                _mongoUserCollection.UpdateOne(userFilter, update);
            }
        }

        // Method to get a reservation by ID
        public TicketBooking GetReservationById(string reservationId)
        {
            return _mongoTicketCollection.Find(x => x.Id == reservationId).FirstOrDefault();
        }

        // Method to get all reservations
        public async Task<IEnumerable<TicketBooking>> GetAllAsync()
        {
            var ticketBookings = await _mongoTicketCollection.Find(_ => true).ToListAsync();
            return ticketBookings;
        }

        //Method for change ticket status
        public async Task<bool> UpdateBookingStatusAsync(string id, string newStatus)
        {
            var filter = Builders<TicketBooking>.Filter.Eq("Id", id);
            var update = Builders<TicketBooking>.Update.Set("BookingStatus", newStatus);

            var result = await _mongoTicketCollection.UpdateOneAsync(filter, update);

            return result.ModifiedCount > 0;
        }


    }
}
