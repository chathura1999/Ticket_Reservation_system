using MongoDB.Driver;
using Newtonsoft.Json.Serialization;
using web_server.Models;
using web_server.Repository;

namespace web_server
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container.
            builder.Services.AddControllers().AddNewtonsoftJson(options =>
            options.SerializerSettings.ReferenceLoopHandling = Newtonsoft.Json.ReferenceLoopHandling.Ignore).AddNewtonsoftJson(options =>
            options.SerializerSettings.ContractResolver = new DefaultContractResolver());
            builder.Services.AddCors(policyBuilder =>
              policyBuilder.AddDefaultPolicy(policy =>
                  policy.WithOrigins("*").AllowAnyHeader().AllowAnyMethod().SetIsOriginAllowed(origin => true))
          );
            // Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();
            builder.Services.AddSingleton<IMongoDatabase>(options =>
            {
                var mongosettings = builder.Configuration.GetSection("MongoDBSettings").Get<MongoDBSettings>();
                var client = new MongoClient(mongosettings.ConnectionString);
                return client.GetDatabase(mongosettings.DatabaseName);
            });
            builder.Services.AddSingleton<ITrainsRepository, TrainsRepository>();
            builder.Services.AddSingleton<IUserRepository, UserRepository>();
            builder.Services.AddSingleton<ITicketBookingRepository, TicketBookingRepository>();
            var app = builder.Build();
            app.UseCors();
            // Configure the HTTP request pipeline.
            if (app.Environment.IsDevelopment())
            {
                app.UseSwagger();
                app.UseSwaggerUI();
            }

            app.UseHttpsRedirection();

            app.UseAuthorization();


            app.MapControllers();

            app.Run();
        }
    }
}