package com.example.mobile_app.Model;

import java.util.Date;

public class TicketBooking {

    private String Id;
    private String ReservationDate;
    private String UserId;
    private String UserNIC;
    private String UserName;
    private String TrainId;
    private String TrainName;
    private String BookingStatus;
    private Date CreatedDate;

    public TicketBooking() {
    }

    public TicketBooking(String id, String reservationDate, String userId, String userNIC, String userName, String trainId, String trainName, String bookingStatus, Date createdDate) {
        Id = id;
        ReservationDate = reservationDate;
        UserId = userId;
        UserNIC = userNIC;
        UserName = userName;
        TrainId = trainId;
        TrainName = trainName;
        BookingStatus = bookingStatus;
        CreatedDate = createdDate;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getReservationDate() {
        return ReservationDate;
    }

    public void setReservationDate(String reservationDate) {
        ReservationDate = reservationDate;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserNIC() {
        return UserNIC;
    }

    public void setUserNIC(String userNIC) {
        UserNIC = userNIC;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getTrainId() {
        return TrainId;
    }

    public void setTrainId(String trainId) {
        TrainId = trainId;
    }

    public String getTrainName() {
        return TrainName;
    }

    public void setTrainName(String trainName) {
        TrainName = trainName;
    }

    public String getBookingStatus() {
        return BookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        BookingStatus = bookingStatus;
    }

    public Date getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(Date createdDate) {
        CreatedDate = createdDate;
    }
}
