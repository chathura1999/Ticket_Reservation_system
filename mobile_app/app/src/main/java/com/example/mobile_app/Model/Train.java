package com.example.mobile_app.Model;

import java.util.Date;

public class Train {

    private String Id;

    private String TrainName;

    private String StartStation;

    private String EndStation;

    private String StartTime;

    private String EndTime;

    private Date CreatedDate;

    public Train() {
    }

    public Train(String id, String trainName, String startStation, String endStation, String startTime, String endTime, Date createdDate) {
        Id = id;
        TrainName = trainName;
        StartStation = startStation;
        EndStation = endStation;
        StartTime = startTime;
        EndTime = endTime;
        CreatedDate = createdDate;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTrainName() {
        return TrainName;
    }

    public void setTrainName(String trainName) {
        TrainName = trainName;
    }

    public String getStartStation() {
        return StartStation;
    }

    public void setStartStation(String startStation) {
        StartStation = startStation;
    }

    public String getEndStation() {
        return EndStation;
    }

    public void setEndStation(String endStation) {
        EndStation = endStation;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public Date getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(Date createdDate) {
        CreatedDate = createdDate;
    }
}
