using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace web_server.Collections
{
    public class User
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string Id { get; set; }

        [BsonElement("Username")]
        public string Username { get; set; }

        [BsonElement("Password")]
        public string Password { get; set; }

        [BsonElement("Role")]
        public string Role { get; set; }

        [BsonElement("NIC")]
        public string NIC { get; set; }

        [BsonElement("TicketBookingIds")]
        public List<string> TicketBookingIds { get; set; }

        [BsonElement("IsActive")]
        public bool IsActive { get; set; }
    }
}
