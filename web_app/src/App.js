import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';


import Login from './components/auth/Login';
import { Register } from './components/auth/Register';
import TravelAccountManagement from './components/Pages/TravelAccountManagement';
import TrainScheduleManagement from './components/Pages/TrainScheduleManagement';
import BackofficeProfile from './components/Pages/BackofficeProfile';
import TravelAgenetProfile from './components/Pages/TravelAgenetProfile';
import BookingRequests from './components/Pages/BookingRequests';

function App() {
  const user = sessionStorage.getItem("user_name");
  return (
    <Router>
      <Routes>
          <Route path="/" element={<Login />}/>
          <Route path="/register" element={<Register />}/>  
          <Route path="/BookingRequests" element={<BookingRequests />}/>  
          <Route exact path="/backofficeProfile" element={<BackofficeProfile />} />
          <Route exact path="/travelAgenetProfile" element={<TravelAgenetProfile />} />
          <Route exact path="/TravelAccountManagement" element={<TravelAccountManagement />} />
          <Route exact path="/TrainScheduleManagement" element={<TrainScheduleManagement />} />
      </Routes>
         
    </Router>
  );
}

export default App;
