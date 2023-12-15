import axios from 'axios';

import React, { useEffect, useState } from 'react';

import { Table, Form, Spinner } from 'react-bootstrap';

 

function TravelAccountManagement() {

  const [users, setUsers] = useState([]);

  const [isLoading, setIsLoading] = useState(true);

    console.log(users);

  // Fetch user data from your API

  useEffect(() => {

    axios.get('http://localhost:5000/api/users')

    .then((response) => {

      setUsers(response.data);

      setIsLoading(false);

    })

    .catch((error) => console.error('Error fetching data:', error));

  },[]);

 

 

 

 

 

 

 

 

  const handleToggleSwitch = (userId, isActive) => {

    // Update the user's isActive status in the local state immediately

    setUsers((prevUsers) =>

      prevUsers.map((user) =>

        user.id === userId ? { ...user, isActive } : user

      )

    );

 

    // Make a PUT request to your API to update the user's isActive status

    axios.put(`http://localhost:5000/api/users/web/users/activate/${userId}?isActive=${isActive}`, {

      isActive: isActive,

    })

      .then((response) => {

        window.location.reload();

      })

      .catch((error) => {

        console.error('Error updating user status:', error);

        // Revert the local state change if the API request fails

        setUsers((prevUsers) =>

          prevUsers.map((user) =>

            user.id === userId ? { ...user, isActive: !isActive } : user

          )

        );

      });

  };

 

 

  if (isLoading) {

    return <Spinner animation="border" role="status" style={{justifyContent:'center'}}/>

  }

 

  return (

    <Table striped bordered hover>

      <thead>

        <tr>

          <th>User Name</th>

          <th>Role</th>

          <th>NIC</th>

          <th>Is Active</th>

        </tr>

      </thead>

      <tbody>

        {users.map((user) => (

          <tr key={user.Id}>

            <td>{user.Username}</td>

            <td>{user.Role}</td>

            <td>{user.Nic}</td>

            <td>

              <Form.Check

                type="switch"

                id={`isActive-switch-${user.Id}`}

                label=""

                checked={user.IsActive}

                onChange={() => handleToggleSwitch(user.Id, !user.IsActive)}

              />

            </td>

          </tr>

        ))}

      </tbody>

    </Table>

  );

}

 

export default TravelAccountManagement;