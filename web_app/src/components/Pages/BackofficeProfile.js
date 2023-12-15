import React from "react";
import { Container, Row, Col, Card } from "react-bootstrap";
import "./backofficeStyles.css";
import Login from "../auth/Login";
import TrainScheduleManagement from "./TrainScheduleManagement";
import { useState } from "react";
import NavBar from "../common/NavBar";
import jwtDecode from "jwt-decode";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";


//Backoffice Profile
const BackofficeProfile = () => {
  // Initialize state to keep track of the selected component
  const [selectedComponent, setSelectedComponent] = useState(
    "TrainScheduleManagement"
  );

  // Get the navigation function from React Router
  const history = useNavigate();

  // Check if the JWT token is expired
  const token = localStorage.getItem("Token");

  // Function to check if a token is expired
  const isTokenExpiredCheck = (token) => {
    try {
      const decodedToken = jwtDecode(token);
      // Convert to seconds
      const currentTime = Date.now() / 1000;
      return decodedToken.exp < currentTime;
    } catch (error) {
      // Token decoding failed, consider it expired
      return true;
    }
  };

  // Determine if the token is expired
  const isTokenExpired = token ? isTokenExpiredCheck(token) : false;

  useEffect(() => {
    // Remove the previous token from localStorage when it's expired
    if (token) {
      if (isTokenExpired === true) {
        localStorage.removeItem("Token");
        localStorage.clear();
        history("/");
      }
    } else {
      history("/");
    }
  }, [isTokenExpired, token]);

  // Function to render the selected component based on state
  const renderSelectedComponent = () => {
    switch (selectedComponent) {
      case "Login":
        return <Login />;
      case "TrainScheduleManagement":
        return <TrainScheduleManagement />;
      default:
        return null;
    }
  };

  return (
    <div>
      <NavBar />
      <Container fluid>
        <Row>
          <Col sm={3} className="sidebar">
            <Card
              onClick={() => setSelectedComponent("TrainScheduleManagement")}
            >
              <Card.Header>Train Management</Card.Header>
            </Card>

            <Card onClick={() => setSelectedComponent("Login")}>
              <Card.Header>Travel Agent Management</Card.Header>
            </Card>
          </Col>
          <Col sm={9} className="content">
            {renderSelectedComponent()}
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default BackofficeProfile;
