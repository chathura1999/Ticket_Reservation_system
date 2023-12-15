using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace web_server.Collections
{
    public class TicketBooking
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string Id { get; set; }

        [BsonElement("ReservationDate")]
        public string ReservationDate { get; set; }

        [BsonElement("UserId")]
        public string UserId { get; set; }

        [BsonElement("UserNIC")]
        public string UserNIC { get; set; }

        [BsonElement("UserName")]
        public string UserName { get; set; }

        [BsonElement("TrainId")]
        public string TrainId { get; set; }

        [BsonElement("TrainName")]
        public string TrainName { get; set; }

        [BsonElement("BookingStatus")]
        public string BookingStatus { get; set; }

        [BsonElement("CreatedDate")]
        public DateTime CreatedDate { get; set; }
    }
}
