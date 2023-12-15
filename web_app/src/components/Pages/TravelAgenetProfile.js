import React from "react";

import { Container, Row, Col, Card } from "react-bootstrap";

import "./backofficeStyles.css";

import Login from "../auth/Login";

import TravelAccountManagement from "./TravelAccountManagement";

import { useState } from "react";

import NavBar from "../common/NavBar";

import jwtDecode from "jwt-decode";

import { useNavigate } from "react-router-dom";

import { useEffect } from "react";

import TravelAgentNavBar from "../common/TravelAgentNavBar";

import BookingRequests from "./BookingRequests";

 

//Travel Agent Profile

const TravelAgenetProfile = () => {

  const [selectedComponent, setSelectedComponent] = useState(

    "TravelAccountManagement"

  );

 

  const renderSelectedComponent = () => {

    switch (selectedComponent) {

      case "BookingRequests":

        return <BookingRequests />;

      case "TravelAccountManagement":

        return <TravelAccountManagement />;

      default:

        return null;

    }

  };

 

  const history = useNavigate();

 

  // Check if the JWT token is expired

  const token = localStorage.getItem("Token");

 

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

 

  return (

    <div>

      <TravelAgentNavBar />

      <Container fluid>

        <Row>

          <Col sm={3} className="sidebar">

            <Card

              onClick={() => setSelectedComponent("TravelAccountManagement")}

            >

              <Card.Header>User Account Management</Card.Header>

            </Card>

 

            <Card onClick={() => setSelectedComponent("BookingRequests")}>

              <Card.Header>Requested Booking Management</Card.Header>

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

 

export default TravelAgenetProfile;