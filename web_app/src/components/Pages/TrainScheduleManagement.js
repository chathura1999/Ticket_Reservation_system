import React, { useState, useEffect } from "react";
import { Table, Button, Modal, Form } from "react-bootstrap";
import axios from "axios";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

//Train Schedule management component
function TrainScheduleManagement() {
  // Define state variables for schedule data, form data, and modal visibility
  const [schedules, setSchedules] = useState([]);
  const [formData, setFormData] = useState({
    id: "",
    startStation: "",
    endStation: "",
    startTime: "",
    endTime: "",
    createdDate: "",
  });
  const [showModal, setShowModal] = useState(false);
  const [startHour, setStartHour] = useState("00");
  const [startMinute, setStartMinute] = useState("00");
  const [startPeriod, setStartPeriod] = useState("AM");
  const [endHour, setEndHour] = useState("00");
  const [endMinute, setEndMinute] = useState("00");
  const [endPeriod, setEndPeriod] = useState("AM");
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  // Function to show the modal for deleting a schedule
  const handleShowDeleteModal = (id) => {
    // Set the id in formData
    setFormData({ id });
    setShowDeleteModal(true);
  };

  // Function to close the delete modal
  const handleCloseDeleteModal = () => {
    setShowDeleteModal(false);
  };

  // Function to fetch schedule data from the API
  const fetchSchedules = async () => {
    try {
      const response = await axios.get("http://localhost:5000/web/alltrain");
      if (response.status === 200) {
        const data = await response.data;
        setSchedules(data);
      } else {
        console.error("Error fetching schedules");
      }
    } catch (error) {
      console.error("Error:", error);
    }
  };
  const handleToggleIsActive = async (id) => {
    try {
      // Find the schedule with the matching ID
      const updatedSchedule = schedules.find((schedule) => schedule.Id === id);

      // Invert the IsActive status
      updatedSchedule.IsActive = !updatedSchedule.IsActive;

      // Send a PUT request to update the IsActive status on the server
      await axios.put(
        `http://localhost:5000/api/train/${id}?isActive=${updatedSchedule.IsActive}`,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      window.location.reload();
    } catch (error) {
      toast(
        "Cannot change the status as there are existing bookings for this train."
      );
    }
  };

  // Function to show the modal for adding/editing a schedule
  const handleShowModal = (schedule) => {
    if (schedule) {
      // Edit mode
      setFormData({
        id: schedule.Id,
        trainName: schedule.TrainName,
        startStation: schedule.StartStation,
        endStation: schedule.EndStation,
        startTime: schedule.StartTime,
        endTime: schedule.EndTime,
        createdDate: new Date(schedule.CreatedDate),
      });

      // Split the time and period for start and end times
      const [start, startPeriod] = schedule.StartTime.split(" ");
      const [end, endPeriod] = schedule.EndTime.split(" ");

      // Split hours and minutes
      const [startHour, startMinute] = start.split(":");
      const [endHour, endMinute] = end.split(":");

      // Set the state for time picker fields
      setStartHour(startHour);
      setStartMinute(startMinute);
      setStartPeriod(startPeriod);
      setEndHour(endHour);
      setEndMinute(endMinute);
      setEndPeriod(endPeriod);
    } else {
      setFormData({
        id: "",
        trainName: "",
        startStation: "",
        endStation: "",
        startTime: "",
        endTime: "",
        createdDate: "",
      });

      // Reset time picker fields
      setStartHour("00");
      setStartMinute("00");
      setStartPeriod("AM");
      setEndHour("00");
      setEndMinute("00");
      setEndPeriod("AM");
    }
    setShowModal(true);
  };

  // Function to close the modal
  const handleCloseModal = () => {
    setShowModal(false);
  };

  // Function to save a new or edited schedule
  const saveSchedule = async () => {
    try {
      // Combine the selected time components for start and end times
      const startTime = `${startHour}:${startMinute} ${startPeriod}`;
      const endTime = `${endHour}:${endMinute} ${endPeriod}`;

      // Check if any fields are empty
      if (
        !formData.startStation ||
        !formData.endStation ||
        !startTime ||
        !endTime ||
        !formData.trainName ||
        !formData.createdDate
      ) {
        alert("Please fill in all fields.");
        return;
      }

      // Check if start and end stations are the same
      if (formData.startStation === formData.endStation) {
        alert("Start Station and End Station cannot be the same.");
        return;
      }

      // Check if start and end times are the same
      if (startTime === endTime) {
        alert("Start Time and End Time cannot be the same.");
        return;
      }

      // Construct the schedule data object to send to the API
      const scheduleData = {
        Id: "",
        trainName: formData.trainName,
        startStation: formData.startStation,
        endStation: formData.endStation,
        startTime: startTime,
        endTime: endTime,
        createdDate: formData.createdDate,
      };

      // Define the API endpoint for update and add train schedules
      const endpoint = formData.id
        ? `http://localhost:5000/web/updateTrainSchedule?id=${formData.id}`
        : "http://localhost:5000/web/addTrainSchedule";

      // Use Axios to make the API request
      const response = await axios({
        method: formData.id ? "PUT" : "POST",
        url: endpoint,
        headers: {
          "Content-Type": "application/json",
        },
        data: scheduleData,
      });

      if (response.status === 200) {
        // Refresh the schedule data and close the modal
        fetchSchedules();
        setShowModal(false);
      } else {
        toast("Error saving schedule");
      }
    } catch (error) {
      toast("Schedule is already exists.");
    }
  };

  // Function to delete a schedule
  const deleteSchedule = async () => {
    try {
      const response = await axios.delete(
        `http://localhost:5000/web/deleteTrainSchedule?id=${formData.id}`
      );
      if (response.data === "Schedule deleted successfully.") {
        // Refresh the schedule data
        setShowDeleteModal(false);
        toast("Schedule deleted successfully");
        fetchSchedules();
      } else {
        console.error("Error deleting schedule");
      }
    } catch (error) {
      console.error("Error:", error);
    }
  };

  useEffect(() => {
    // Fetch schedule data when the component mounts
    fetchSchedules();
  }, []);

  return (
    <div style={{ paddingTop: 20 }}>
      <Button onClick={() => handleShowModal()}>Add Schedule</Button>
      <div style={{ padding: 10 }}></div>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>Train name</th>
            <th>Start Station</th>
            <th>End Station</th>
            <th>Start Time</th>
            <th>End Time</th>
            <th>Created Date</th>
            <th>Is active</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {schedules.map((schedule) => (
            <tr key={schedule.Id}>
              <td>{schedule.TrainName}</td>
              <td>{schedule.StartStation}</td>
              <td>{schedule.EndStation}</td>
              <td>{schedule.StartTime}</td>
              <td>{schedule.EndTime}</td>
              <td>
                {new Date(schedule.CreatedDate).toLocaleString("en-US", {
                  year: "numeric",
                  month: "2-digit",
                  day: "2-digit",
                  hour: "2-digit",
                  minute: "2-digit",
                  second: "2-digit",
                })}
              </td>
              <td>
                <Form.Check
                  type="switch"
                  id={`isActive-switch-${schedule.Id}`}
                  label=""
                  checked={schedule.IsActive} // Set the switch state based on IsActive
                  onChange={() => handleToggleIsActive(schedule.Id)}
                />
              </td>
              <td>
                <Button onClick={() => handleShowModal(schedule)}>Edit</Button>
                <Button
                  variant="danger"
                  onClick={() => handleShowDeleteModal(schedule.Id)}
                >
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Modal show={showModal} onHide={handleCloseModal}>
        <Modal.Header closeButton>
          <Modal.Title>
            {formData.id ? "Edit Schedule" : "Add Schedule"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group controlId="startStation">
              <Form.Label>Train Name</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter Train Name"
                value={formData.trainName}
                onChange={(e) =>
                  setFormData({ ...formData, trainName: e.target.value })
                }
              />
            </Form.Group>
            <Form.Group controlId="startStation">
              <Form.Label>Start Station</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter Start Station"
                value={formData.startStation}
                onChange={(e) =>
                  setFormData({ ...formData, startStation: e.target.value })
                }
              />
            </Form.Group>
            <Form.Group controlId="endStation">
              <Form.Label>End Station</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter End Station"
                value={formData.endStation}
                onChange={(e) =>
                  setFormData({ ...formData, endStation: e.target.value })
                }
              />
            </Form.Group>
            <Form.Group controlId="startTime">
              <Form.Label>Start Time</Form.Label>
              <div className="d-flex">
                <Form.Control
                  as="select"
                  value={startHour}
                  onChange={(e) => setStartHour(e.target.value)}
                >
                  {Array.from({ length: 12 }, (_, i) => (
                    <option key={i} value={i < 10 ? `0${i}` : `${i}`}>
                      {i < 10 ? `0${i}` : `${i}`}
                    </option>
                  ))}
                </Form.Control>
                <span className="mx-2">:</span>
                <Form.Control
                  as="select"
                  value={startMinute}
                  onChange={(e) => setStartMinute(e.target.value)}
                >
                  {Array.from({ length: 60 }, (_, i) => (
                    <option key={i} value={i < 10 ? `0${i}` : `${i}`}>
                      {i < 10 ? `0${i}` : `${i}`}
                    </option>
                  ))}
                </Form.Control>
                <Form.Control
                  as="select"
                  value={startPeriod}
                  onChange={(e) => setStartPeriod(e.target.value)}
                >
                  <option value="AM">AM</option>
                  <option value="PM">PM</option>
                </Form.Control>
              </div>
            </Form.Group>
            <Form.Group controlId="endTime">
              <Form.Label>End Time</Form.Label>
              <div className="d-flex">
                <Form.Control
                  as="select"
                  value={endHour}
                  onChange={(e) => setEndHour(e.target.value)}
                >
                  {Array.from({ length: 12 }, (_, i) => (
                    <option key={i} value={i < 10 ? `0${i}` : `${i}`}>
                      {i < 10 ? `0${i}` : `${i}`}
                    </option>
                  ))}
                </Form.Control>
                <span className="mx-2">:</span>
                <Form.Control
                  as="select"
                  value={endMinute}
                  onChange={(e) => setEndMinute(e.target.value)}
                >
                  {Array.from({ length: 60 }, (_, i) => (
                    <option key={i} value={i < 10 ? `0${i}` : `${i}`}>
                      {i < 10 ? `0${i}` : `${i}`}
                    </option>
                  ))}
                </Form.Control>
                <Form.Control
                  as="select"
                  value={endPeriod}
                  onChange={(e) => setEndPeriod(e.target.value)}
                >
                  <option value="AM">AM</option>
                  <option value="PM">PM</option>
                </Form.Control>
              </div>
            </Form.Group>
            <Form.Group controlId="createdDate">
              <Form.Label>Created Date</Form.Label>
              <DatePicker
                selected={formData.createdDate}
                value={formData.createdDate}
                onChange={(date) =>
                  setFormData({ ...formData, createdDate: date })
                }
                dateFormat="yyyy-MM-dd"
                timeCaption="Time"
                className="form-control"
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseModal}>
            Close
          </Button>
          <Button variant="primary" onClick={saveSchedule}>
            Save Changes
          </Button>
        </Modal.Footer>
      </Modal>
      <Modal show={showDeleteModal} onHide={handleCloseDeleteModal}>
        <Modal.Header closeButton>
          <Modal.Title>Confirm Delete</Modal.Title>
        </Modal.Header>
        <Modal.Body>Are you sure you want to delete this schedule?</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseDeleteModal}>
            No
          </Button>
          <Button variant="danger" onClick={deleteSchedule}>
            Yes
          </Button>
        </Modal.Footer>
      </Modal>
      <ToastContainer />
    </div>
  );
}

export default TrainScheduleManagement;
