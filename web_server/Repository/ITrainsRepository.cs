using web_server.Collections;


namespace web_server.Repository
{
    // Interface for interacting with train schedules in the repository
    public interface ITrainsRepository
    {
        // Create a new train schedule in the repository
        Task CreateSchedule(Train newTrain);

        // Find an already added train schedule by start and end stations
        Task<Train> FindAlredayAddedScehedules(string startStation, string endStation);

        // Get a list of all train schedules in the repository
        Task<List<Train>> GetAllSchedules();

        // Update an existing train schedule by its ID
        Task<bool> UpdateScheduleAsync(string id, Train updatedSchedule);

        // Get a train schedule by its unique ID
        Task<Train> GetTrainScheduleById(string id);

        // Delete a train schedule by its ID
        Task DeleteScheduleAsync(string id);

        // Update the status (active/inactive) of a train schedule by its ID
        Task UpdateTrainStatusAsync(string id, bool isActive);

        // Check if there are bookings associated with a specific train schedule by its ID
        Task<bool> HasBookingsForTrainAsync(string trainId);

        // Get a list of all train schedules (potentially for administrative purposes)
        Task<List<Train>> GetAllTrainSchedules();
    }
}
