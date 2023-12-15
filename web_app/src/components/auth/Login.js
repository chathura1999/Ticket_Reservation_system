import React, { useState } from "react";

import { useNavigate } from "react-router-dom";

import axios from "axios";

import "./loginstyles.css";

import jwt_decode from "jwt-decode";

import "react-toastify/dist/ReactToastify.css";

import { ToastContainer, toast } from "react-toastify";

 

//Login Page Component

const Login = () => {

  const Navigate = useNavigate();

  const [Username, setUsername] = useState("");

  const [Password, setPassword] = useState("");

  const [Id, setId] = useState("");

  const [NIC, setNIC] = useState("");

  const [Role, setRole] = useState("");

  const [TicketBookingIds, setTicketBookingIds] = useState([]);

 

  // Event handler for username input change

  const handleUsernameChange = (e) => {

    setUsername(e.target.value);

  };

 

  // Event handler for password input change

  const handlePasswordChange = (e) => {

    setPassword(e.target.value);

  };

 

  // Event handler for the login button click

  const handleLogin = async () => {

    // Create a request object with user data

    const loginRequest = {

      Id,

      NIC,

      Role,

      Username,

      Password,

      TicketBookingIds,

    };

    try {

      // Send a POST request to the login endpoint with the user data

      const response = await axios.post(

        "http://localhost:5000/web/login",

        loginRequest

      );

 

      // Store the JWT token in local storage

      localStorage.setItem("Token", JSON.stringify(response.data.token));

 

      // Parse and decode the JWT token

      const currentUser = JSON.parse(localStorage.getItem("Token"));

      const decodedToken = await jwt_decode(currentUser);

 

      // Check the user's role and navigate accordingly

      if (decodedToken.Role === "backoffice") {

        Navigate("/backofficeProfile");

      } else if (decodedToken.Role === "TravelAgent") {

        Navigate("/travelAgenetProfile");

      } else {

        alert("You don't have access.");

        return null;

      }

      // Display a success toast notification

      toast("Login successfully");

    } catch (error) {

      // Display an error toast notification and log the error

      toast(`${error.response.data.Message}`);

    }

  };

  return (

    <div className="chat-container">

      <div className="chat-header">

        <h2>Travel Srilanka Login</h2>

      </div>

      <div className="chat-messages">

        <div className="chat-message">

          <div className="message-text">Welcome to Travel Srilanka!</div>

        </div>

      </div>

      <div className="chat-input">

        <input

          type="text"

          name="Username"

          placeholder="Username"

          value={Username}

          onChange={handleUsernameChange}

        />

        <input

          type="password"

          name="Password"

          placeholder="Password"

          value={Password}

          onChange={handlePasswordChange}

        />

        <button onClick={handleLogin}>Login</button>

      </div>

      <ToastContainer />

    </div>

  );

};

 

export default Login;