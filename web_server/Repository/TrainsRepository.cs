using MongoDB.Driver;
using web_server.Collections;


namespace web_server.Repository
{
    public class TrainsRepository : ITrainsRepository
    {
        private readonly IMongoCollection<Train> _mongoTrainCollection;
        private readonly IMongoCollection<TicketBooking> _mongoTicketCollection;

        // Constructor that initializes the repository with MongoDB collections.
        public TrainsRepository(IMongoDatabase mongoDatabase)
        {
            _mongoTrainCollection = mongoDatabase.GetCollection<Train>("Train");
            _mongoTicketCollection = mongoDatabase.GetCollection<TicketBooking>("Ticket");
        }

        // Method to create a new train schedule.
        public async Task CreateSchedule(Train newTrain)
        {
            newTrain.IsActive = true;
            await _mongoTrainCollection.InsertOneAsync(newTrain);
        }

        // Method to find an already added train schedule by start and end stations.
        public async Task<Train> FindAlredayAddedScehedules(string startStation, string endStation)
        {
            return await _mongoTrainCollection.Find(Train => Train.StartStation == startStation && Train.EndStation == endStation).FirstOrDefaultAsync();
        }

        // Method to get all train schedules.
        public async Task<List<Train>> GetAllTrainSchedules()
        {
            return await _mongoTrainCollection.Find(_ => true).ToListAsync();

        }

        // Method to get all active train schedules.
        public async Task<List<Train>> GetAllSchedules()
        {
            var filter = Builders<Train>.Filter.Eq("IsActive", true);
            var activeSchedules = await _mongoTrainCollection.Find(filter).ToListAsync();
            return activeSchedules;
        }

        // Method to update a train schedule by its ID.
        public async Task<bool> UpdateScheduleAsync(string id, Train updatedSchedule)
        {
            var filter = Builders<Train>.Filter.Eq(s => s.Id, id);
            var update = Builders<Train>.Update
                .Set(s => s.TrainName, updatedSchedule.TrainName)
                .Set(s => s.StartStation, updatedSchedule.StartStation)
                .Set(s => s.EndStation, updatedSchedule.EndStation)
                .Set(s => s.StartTime, updatedSchedule.StartTime)
                .Set(s => s.EndTime, updatedSchedule.EndTime)
                .Set(s => s.CreatedDate, updatedSchedule.CreatedDate);




            var result = await _mongoTrainCollection.UpdateOneAsync(filter, update);

            return result.ModifiedCount > 0;
        }

        // Method to get a train schedule by its ID.
        public async Task<Train> GetTrainScheduleById(string id)
        {
            return await _mongoTrainCollection.Find(_ => _.Id == id).FirstOrDefaultAsync();
        }

        // Method to delete a train schedule by its ID.
        public async Task DeleteScheduleAsync(string id)
        {
            await _mongoTrainCollection.DeleteOneAsync(_ => _.Id == id);
        }

        // Method to check if there are bookings for a specific train.
        public async Task<bool> HasBookingsForTrainAsync(string trainId)
        {
            var hasBookings = await _mongoTicketCollection
                .Find(ticket => ticket.TrainId == trainId)
                .AnyAsync();

            return hasBookings;
        }

        // Method to update the status (active/inactive) of a train by its ID.
        public async Task UpdateTrainStatusAsync(string id, bool isActive)
        {
            var filter = Builders<Train>.Filter.Eq(train => train.Id, id);
            var update = Builders<Train>.Update.Set(train => train.IsActive, isActive);
            await _mongoTrainCollection.UpdateOneAsync(filter, update);
        }
    }
}
