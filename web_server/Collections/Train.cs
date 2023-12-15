using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace web_server.Collections
{
    public class Train
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string Id { get; set; }
        [BsonElement("TrainName")]
        public string TrainName { get; set; }

        [BsonElement("StartStation")]
        public string StartStation { get; set; }

        [BsonElement("EndStation")]
        public string EndStation { get; set; }

        [BsonElement("StartTime")]
        public string StartTime { get; set; }

        [BsonElement("EndTime")]
        public string EndTime { get; set; }

        [BsonElement("IsActive")]
        public bool IsActive { get; set; }

        [BsonElement("CreatedDate")]
        public DateTime CreatedDate { get; set; }

    }
}
