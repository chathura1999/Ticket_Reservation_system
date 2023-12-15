using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using web_server.Collections;
using web_server.Repository;

namespace web_server.Controllers
{
    [Route("api/train")] // Define the base route for this controller
    [ApiController] // Indicates that this class is an API controller
    public class TrainController : ControllerBase
    {
        private readonly ITrainsRepository _trainsRepository;

        public TrainController(ITrainsRepository trainsRepository)
        {
            _trainsRepository = trainsRepository;
        }

        [HttpPost]
        [Route("/web/addTrainSchedule")] // Define a specific route for creating a train schedule
        public async Task<IActionResult> CreateScehedule(Train train)
        {
            try
            {
                // Check if the username is already taken
                var existingScehdules = await _trainsRepository.FindAlredayAddedScehedules(train.StartStation, train.EndStation);
                if (existingScehdules != null)
                {
                    return BadRequest("Schedule is already exists.");
                }

                await _trainsRepository.CreateSchedule(train);

                return Ok("Schedule added successful.");
            }
            catch (Exception ex)
            {
                return BadRequest($"Schedule added failed: {ex.Message}");
            }
        }

        [HttpGet]
        public async Task<IActionResult> GetAllSchedules()
        {
            // Get all train schedules
            var schedules = await _trainsRepository.GetAllSchedules();
            return Ok(schedules);
        }

        [HttpGet]
        [Route("/web/alltrain")]// Define a specific route for getting all train schedules
        public async Task<IActionResult> GetAllTrainSchedules()
        {
            // Get all train schedules
            var schedules = await _trainsRepository.GetAllTrainSchedules();
            return Ok(schedules);
        }

        [HttpPut]
        [Route("/web/updateTrainSchedule")]// Define a specific route for updating a train schedule
        public async Task<IActionResult> UpdateSchedule(string id, [FromBody] Train updatedSchedule)
        {
            if (string.IsNullOrEmpty(id))
            {
                return BadRequest("Invalid schedule ID.");
            }
            // Attempt to update the train schedule
            var isUpdated = await _trainsRepository.UpdateScheduleAsync(id, updatedSchedule);

            if (isUpdated)
            {
                return Ok("Schedule updated successfully.");
            }

            return NotFound("Schedule not found.");
        }


        [HttpDelete]
        [Route("/web/deleteTrainSchedule")]// Define a specific route for deleting a train schedule
        public async Task<IActionResult> Delete(string id)
        {
            // Get the train schedule by ID
            var schedule = _trainsRepository.GetTrainScheduleById(id);
            if (schedule == null)
            {
                return NotFound();
            }
            // Delete the train schedule
            await _trainsRepository.DeleteScheduleAsync(id);
            return Ok("Schedule deleted successfully.");
        }



        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateTrainStatus(string id, [FromQuery] bool isActive)
        {
            try
            {
                // Check if there are any bookings for this train
                var hasBookings = await _trainsRepository.HasBookingsForTrainAsync(id);

                if (hasBookings)
                {
                    
                    return Conflict("Cannot change the status as there are existing bookings for this train.");
                }

                // If no bookings exist, update the train status
                await _trainsRepository.UpdateTrainStatusAsync(id, isActive);
                return NoContent();
            }
            catch (Exception ex)
            {
               
                return BadRequest($"Error updating train status: {ex.Message}");
            }
        }


    }
}
