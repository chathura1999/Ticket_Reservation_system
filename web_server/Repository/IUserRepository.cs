using MongoDB.Driver;
using web_server.Collections;

namespace web_server.Repository
{
    public interface IUserRepository
    {
        // Retrieve a list of all users
        Task<List<User>> GetAllUsers();

        // Create a new user
        Task CreateUser(User newUser);

        // Retrieve a user by their ID
        Task<User> GetUserById(string id);

        // Update an existing user
        Task UpdateUser(User UserToUpdate);

        // Delete a user by their ID
        Task DeleteAsync(string id);

        // Retrieve a user by their NIC (National Identity Card) number
        Task<User> GetUserByNICAsync(string nic);

        // Deactivate a user by setting their isActive status to false
        Task DeactivateUser(string id);

        // Activate a user by setting their isActive status to true
        Task ActivateUser(string id, bool isActive);

        // Retrieve a user by their username
        Task<User> GetUserByUsernameAsync(string username);

        // Retrieve a user by their username and password (for authentication)
        Task<User> GetUserByUsernameAndPasswordAsync(string username, string password);

        // Retrieve a list of reservations made by a user
        Task<List<User>> GetUserReservations(string userId);

        // Add a train ID to a user's record (for tracking)
        Task AddTrainIdToUser(string userId, string trainId);

        // Get the count of ticket bookings made by a user
        Task<int> GetTicketBookingCountForUser(string userId);
    }
}
