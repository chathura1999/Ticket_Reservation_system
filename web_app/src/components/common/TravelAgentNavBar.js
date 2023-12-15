import React, { useState } from "react";

import {

  MDBContainer,

  MDBNavbar,

  MDBNavbarBrand,

  MDBNavbarToggler,

  MDBIcon,

  MDBNavbarNav,

  MDBNavbarItem,

  MDBNavbarLink,

  MDBCollapse,

} from "mdb-react-ui-kit";

 

import jwtDecode from "jwt-decode";

import { useNavigate } from "react-router-dom";

import { useEffect } from "react";

 

function TravelAgentNavBar() {

  // State for controlling the collapse of the navigation menu

  const [showBasic, setShowBasic] = useState(false);

  const [showNavRight, setShowNavRight] = useState(false);

 

  // Access the navigation history

  const history = useNavigate();

 

  // Check if the JWT token is expired

  const token = localStorage.getItem("Token");

 

  // Function to check if the JWT token is expired

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

 

  // Function to handle user logout

  const logout = () => {

    // Clear all local storage data

    localStorage.clear();

    // Refresh the page after logout

    window.location.reload();

  };

 

  useEffect(() => {

    // Remove the previous token from localStorage when it's expired

    if (token) {

      if (isTokenExpired === true) {

        localStorage.removeItem("Token");

        // Clear all local storage data

        localStorage.clear();

        // Redirect to the home page

        history("/");

      }

    } else {

      // If no token is found, redirect to the home

      history("/");

    }

  }, [isTokenExpired, token]);

 

  return (

    <MDBNavbar expand="lg" className="sticky-top" light bgColor="dark">

      <MDBContainer fluid>

        <MDBNavbarBrand

          href="/travelAgenetProfile"

          style={{ fontSize: "25px" }}

          className="pt-2 navbar-brand h1 fw-bold"

        >

          <MDBIcon

            fas

            icon="fas fa-train-subway"

            className="text-danger"

            size="2x"

          />{" "}

          <span className="text-danger">&nbsp;Train</span>

          <span className="text-white">-Manage</span>

        </MDBNavbarBrand>

 

        <MDBNavbarToggler

          aria-controls="navbarSupportedContent"

          aria-expanded="false"

          aria-label="Toggle navigation"

          onClick={() => setShowBasic(!showBasic)}

        >

          <MDBIcon icon="bars" fas />

        </MDBNavbarToggler>

 

        <MDBCollapse navbar show={showBasic}>

          <MDBNavbarNav className="mr-auto mb-2 mb-lg-0">

            <MDBNavbarItem>

              <MDBNavbarLink

                style={{ color: "#DCDCDC" }}

                active

                aria-current="page"

                href="/travelAgenetProfile"

              >

                Dashboard

              </MDBNavbarLink>

            </MDBNavbarItem>

            <MDBNavbarItem>

              <MDBNavbarLink

                style={{ color: "#DCDCDC" }}

                active

                aria-current="page"

                href="/travelAgenetProfile"

              >

                Home

              </MDBNavbarLink>

            </MDBNavbarItem>

          </MDBNavbarNav>

 

          <MDBCollapse navbar show={showNavRight}>

            <MDBNavbarNav right fullWidth={false} className="mb-2 mb-lg-0">

              <MDBNavbarItem>

                <MDBNavbarLink

                  style={{ color: "#DCDCDC", cursor: "pointer" }}

                  active

                  aria-current="page"

                  onClick={logout}

                >

                  Logout

                </MDBNavbarLink>

              </MDBNavbarItem>

            </MDBNavbarNav>

          </MDBCollapse>

        </MDBCollapse>

      </MDBContainer>

    </MDBNavbar>

  );

}

 

export default TravelAgentNavBar;