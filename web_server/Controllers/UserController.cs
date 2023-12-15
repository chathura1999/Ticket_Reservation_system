using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using web_server.Collections;
using web_server.Repository;

namespace web_server.Controllers
{
    [Route("api/users")]
    [ApiController]
    public class UserController : ControllerBase
    {
        private readonly IUserRepository _userRepository;
        private readonly IConfiguration _configuration;

        public UserController(IUserRepository userRepository, IConfiguration configuration)
        {
            _userRepository = userRepository;
            _configuration = configuration;
        }

        // GET: /api/users
        [HttpGet]
        public async Task<IActionResult> Get()
        {
            // Retrieve all users
            var user = await _userRepository.GetAllUsers();
            return Ok(user);
        }

        // GET: /api/users/{id}
        [HttpGet]
        [Route("{id}")]
        public async Task<IActionResult> Get(string id)
        {
            // Retrieve a user by their ID
            var user = await _userRepository.GetUserById(id);
            return Ok(user);
        }

        // POST: /api/users
        [HttpPost]
        public async Task<IActionResult> Post(User newUser)
        {
            // Check if a user with the same NIC (National Identification Card) already exists
            var user = await _userRepository.GetUserByNICAsync(newUser.NIC);
            if (user != null)
            {
                return BadRequest("A user with the same NIC already exists.");
            }

            // Set default user role and activation status, then create the user
            newUser.Role = "user";
            newUser.IsActive = true;
            await _userRepository.CreateUser(newUser);
            return CreatedAtAction(nameof(Get), new { id = newUser.Id }, new { Message = "Register successfully", Data = newUser });
        }

        // PUT: /api/users
        [HttpPut]
        public async Task<IActionResult> Put(User UserToUpdate)
        {
            // Update an existing user
            var user = _userRepository.GetUserById(UserToUpdate.Id);
            if (user == null)
            {
                return NotFound();
            }
            await _userRepository.UpdateUser(UserToUpdate);
            return NoContent();

        }

        // DELETE: /api/users
        [HttpDelete]
        public async Task<IActionResult> Delete(string id)
        {
            // Delete a user by their ID
            var user = _userRepository.GetUserById(id);
            if (user == null)
            {
                return NotFound();
            }
            await _userRepository.DeleteAsync(id);
            return NoContent();
        }

        // PUT: /api/users/mobile/user/deactivate/{id}
        [HttpPut]
        [Route("mobile/user/deactivate/{id}")]
        public async Task<IActionResult> DeactivateUser(string id)
        {
            // Deactivate a user by their ID
            var user = await _userRepository.GetUserById(id);
            if (user == null)
            {
                return NotFound();
            }

            _userRepository.DeactivateUser(id);
            return Ok();
        }

        // PUT: /api/users/web/users/activate/{id}
        [HttpPut]
        [Route("web/users/activate/{id}")]
        public async Task<IActionResult> ActivateUser(string id, bool isActive)
        {
            // Activate or deactivate a user by their ID
            var user = await _userRepository.GetUserById(id);
            if (user == null)
            {
                return NotFound();
            }

            _userRepository.ActivateUser(id, isActive);
            return Ok();
        }

        // Activate or deactivate a user by their ID
        [HttpPost]
        [Route("/login")]
        public async Task<IActionResult> Login(User credentials)
        {
            // Check if the user exists
            var user = await _userRepository.GetUserByUsernameAsync(credentials.Username);
            if (user == null)
            {
                return NotFound("User not found.");
            }

            // Check the password
            if (user.Password != credentials.Password)
            {
                return Unauthorized("Invalid password.");
            }
            // Check if the user is active
            if (user.IsActive == false)
            {
                return Unauthorized("User is not active.");
            }

            // Authentication successful, you can generate and return a token
            return Ok(user);
        }

        // POST: /api/users/web/register
        [HttpPost]
        [Route("/web/register")]
        public async Task<IActionResult> Register(User user)
        {
            try
            {
                // Check if the username is already taken
                var existingUser = await _userRepository.GetUserByUsernameAsync(user.Username);
                if (existingUser != null)
                {
                    return BadRequest("Username already exists.");
                }

                await _userRepository.CreateUser(user);

                return Ok("Registration successful.");
            }
            catch (Exception ex)
            {
                return BadRequest($"Registration failed: {ex.Message}");
            }
        }

        // POST: /api/users/web/login
        [HttpPost]
        [Route("/web/login")]
        public async Task<IActionResult> WebLogin(User request)

        {
            try
            {
                var user = await _userRepository.GetUserByUsernameAndPasswordAsync(request.Username, request.Password);


                if (user == null)
                {
                    return Unauthorized(new { Message = "Invalid username or password." });
                }

                // Create claims for the user
                var claims = new[]
                {
                    new Claim("Id", user.Id),
                    new Claim("Username", user.Username),
                    new Claim("Role", user.Role)
                };

                var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration["JwtSettings:Key"]));
                var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);
                var expires = DateTime.Now.AddMinutes(Convert.ToDouble(_configuration["JwtSettings:ExpirationMinutes"]));

                var token = new JwtSecurityToken(
                    _configuration["JwtSettings:Issuer"],
                    _configuration["JwtSettings:Issuer"],
                    claims,
                    expires: expires,
                    signingCredentials: creds
                );

                return Ok(new
                {
                    token = new JwtSecurityTokenHandler().WriteToken(token),
                    expiration = expires
                });
            }
            catch (Exception ex)
            {
                return BadRequest($"Login failed: {ex.Message}");
            }
        }

    }
}
