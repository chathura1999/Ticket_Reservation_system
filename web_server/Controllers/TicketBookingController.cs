using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Globalization;
using web_server.Collections;
using web_server.Repository;

namespace web_server.Controllers
{
    [Route("api/train")]
    [ApiController]
    public class TicketBookingController : ControllerBase
    {
        private readonly ITicketBookingRepository _ticketBookingRepository;

        public TicketBookingController(ITicketBookingRepository ticketBookingRepository)
        {
            _ticketBookingRepository = ticketBookingRepository;
        }

        // POST endpoint for creating a new reservation
        [HttpPost]
        [Route("/mobile/addReservation")]
        public async Task<IActionResult> CreateScehedule(string userId, string trainId, TicketBooking newBooking)
        {
            try
            {
                await _ticketBookingRepository.CreateReservation(userId, trainId, newBooking);

                return Ok("Reservation successful.");
            }
            catch (Exception ex)
            {
                return BadRequest(new { Message = "Maximum 4 reservations per reference ID" });
            }
        }

        // GET endpoint to retrieve bookings by user ID
        [HttpGet]
        [Route("{userId}")]
        public async Task<IActionResult> GetBookingsByUserId(string userId)
        {
            var bookings = _ticketBookingRepository.GetBookingsByUserId(userId);

            if (bookings == null )
            {
                return NotFound("No booking Founds");
            }

            return Ok(bookings);
        }


        // PUT endpoint to update the reservation date
        [HttpPut]
        [Route("update-reservation-date")]
        public async Task<IActionResult> UpdateReservationDate(string id, [FromQuery] string newReservationDate)
        {
            
            var reservation = _ticketBookingRepository.GetReservationById(id);

            if (reservation == null)
            {
                return NotFound("TicketBooking not found with the given Id.");
            }

            DateTime parsedNewReservationDate;
            if (DateTime.TryParseExact(reservation.ReservationDate, "yyyy-MM-dd", CultureInfo.InvariantCulture, DateTimeStyles.None, out parsedNewReservationDate))
            {
                // Check if the new reservation date is at least 5 days in the future
                if ((parsedNewReservationDate - DateTime.Now).TotalDays >= 5)
                {
                   

                    var success = await _ticketBookingRepository.UpdateReservationDateAsync(id, newReservationDate);

                    if (success)
                    {
                        return Ok(new { Message = "ReservationDate updated successfully."});
                    }
                    else
                    {
                        return BadRequest("An error occurred while updating the reservation.");
                    }
                }
                else
                {
                    return BadRequest(new { Message = "New reservation date must be at least 5 days in the future." });
                }
            }
            else
            {
                return BadRequest("Invalid date format. Please provide the date in the format yyyy-MM-dd.");
            }
        }


        // DELETE endpoint to cancel a reservation
        [HttpDelete]
        [Route("{reservationId}")]
        public IActionResult CancelReservation(string reservationId)
        {
            // check if a reservation can be canceled at least 5 days before.
            var reservation = _ticketBookingRepository.GetReservationById(reservationId);

            if (reservation != null)
            {
                DateTime reservationDate;
                if (DateTime.TryParseExact(reservation.ReservationDate, "yyyy-MM-dd", CultureInfo.InvariantCulture, DateTimeStyles.None, out reservationDate))
                {
                    // Calculate the difference in days between the reservation date and today
                    double daysUntilReservation = (reservationDate - DateTime.Now).TotalDays;

                    if (daysUntilReservation >= 5)
                    {
                        _ticketBookingRepository.CancelReservation(reservationId);
                        return Ok("Reservation canceled successfully.");
                    }
                }
            }

            return BadRequest(new { Message = "Unable to cancel reservation.can't cancel least 5 days" });
        }


        [HttpGet]
        [Route("allTickets")]
        public async Task<ActionResult<IEnumerable<TicketBooking>>> GetAll()
        {
            var ticketBookings = await _ticketBookingRepository.GetAllAsync();
            return Ok(ticketBookings);
        }


        // Update the status of ticket

        [HttpPut]
        [Route("updateBookingStatus/{id}")]
        public async Task<IActionResult> UpdateBookingStatus(string id, [FromQuery] string newStatus)
        {
            var updated = await _ticketBookingRepository.UpdateBookingStatusAsync(id, newStatus);

            if (updated)
            {
                return Ok("BookingStatus updated successfully.");
            }
            else
            {
                return NotFound("Ticket not found or BookingStatus not updated.");
            }
        }


    }
}
