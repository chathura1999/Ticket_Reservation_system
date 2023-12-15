using MongoDB.Driver;
using web_server.Collections;
using web_server.Repository;


namespace web_server.Repository
{
    public class UserRepository : IUserRepository
    {
        private readonly IMongoCollection<User> _mongoUserCollection;

        public UserRepository(IMongoDatabase mongoDatabase)
        {
            // Initialize the MongoDB collection for users.
            _mongoUserCollection = mongoDatabase.GetCollection<User>("User");
        }

        // Retrieve all users from the collection.
        public async Task<List<User>> GetAllUsers()
        {
            return await _mongoUserCollection.Find(_ => true).ToListAsync();
        }

        // Create a new user in the collection.
        public async Task CreateUser(User newUser)
        {
            await _mongoUserCollection.InsertOneAsync(newUser);
        }

        // Retrieve a user by their ID.
        public async Task<User> GetUserById(string id)
        {
            return await _mongoUserCollection.Find(_ => _.Id == id).FirstOrDefaultAsync();
        }

        // Retrieve a user by their NIC (National Identity Card) asynchronously.
        public async Task<User> GetUserByNICAsync(string nic)
        {
            return await _mongoUserCollection.Find(user => user.NIC == nic).FirstOrDefaultAsync();
        }

        // Update a user's information.
        public async Task UpdateUser(User UserToUpdate)
        {
            await _mongoUserCollection.ReplaceOneAsync(_ => _.Id == UserToUpdate.Id, UserToUpdate);
        }

        // Delete a user by their ID.
        public async Task DeleteAsync(string id)
        {
            await _mongoUserCollection.DeleteOneAsync(_ => _.Id == id);
        }

        // Deactivate a user by setting their 'IsActive' property to false.
        public async Task DeactivateUser(string id)
        {
            var update = Builders<User>.Update.Set(u => u.IsActive, false);
            _mongoUserCollection.UpdateOne(u => u.Id == id, update);
        }

        // Activate or deactivate a user based on their ID and a provided boolean value.
        public async Task ActivateUser(string id, bool isActive)
        {
            var update = Builders<User>.Update.Set(u => u.IsActive, isActive);
            _mongoUserCollection.UpdateOne(u => u.Id == id, update);
        }

        // Retrieve a user by their username asynchronously.
        public async Task<User> GetUserByUsernameAsync(string username)
        {
            return await _mongoUserCollection.Find(user => user.Username == username).FirstOrDefaultAsync();
        }

        // Retrieve a user by their username and password asynchronously.
        public async Task<User> GetUserByUsernameAndPasswordAsync(string username, string password)
        {
            return await _mongoUserCollection.Find(user => user.Username == username && user.Password == password).FirstOrDefaultAsync();
        }

        // Retrieve a list of user reservations by their user ID.
        public async Task<List<User>> GetUserReservations(string userId)
        {
            try
            {
                var filter = Builders<User>.Filter.Eq(tb => tb.Id, userId);
                return await _mongoUserCollection.Find(filter).ToListAsync();
            }
            catch (Exception ex)
            {

                Console.WriteLine($"Error in GetUserReservations");
                throw;
            }
        }

        // Get the count of ticket bookings for a user based on their user ID.
        public async Task<int> GetTicketBookingCountForUser(string userId)
        {
            try
            {
                var filter = Builders<User>.Filter.Eq(u => u.Id, userId);
                var user = await _mongoUserCollection.Find(filter).FirstOrDefaultAsync();

                if (user != null && user.TicketBookingIds != null)
                {
                    return user.TicketBookingIds.Count;
                }

                return 0;
            }
            catch (Exception ex)
            {

                Console.WriteLine($"Error in GetTicketBookingCountForUser: {ex.Message}");
                throw;
            }
        }

        // Add a train ID to a user's list of ticket booking IDs.
        public async Task AddTrainIdToUser(string userId, string trainId)
        {
            try
            {
                var filter = Builders<User>.Filter.Eq(u => u.Id, userId);
                var update = Builders<User>.Update.Push(u => u.TicketBookingIds, trainId);

                await _mongoUserCollection.UpdateOneAsync(filter, update);
            }
            catch (Exception ex)
            {

                Console.WriteLine($"Error in AddTrainIdToUser: {ex.Message}");
                throw;
            }
        }

    }
}
