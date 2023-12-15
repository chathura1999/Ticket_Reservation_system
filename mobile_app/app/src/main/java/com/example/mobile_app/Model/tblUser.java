package com.example.mobile_app.Model;

import java.util.List;

public class tblUser {
    private String Id;
    private String Username;
    private String Password;
    private String Role;
    private String NIC;
    private Boolean IsActive;
    private List<String> TicketBookingIds;

    public tblUser() {
    }

    public tblUser(String id, String username, String password, String role, String NIC, Boolean isActive, List<String> ticketBookingIds) {
        Id = id;
        Username = username;
        Password = password;
        Role = role;
        this.NIC = NIC;
        IsActive = isActive;
        TicketBookingIds = ticketBookingIds;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getNIC() {
        return NIC;
    }

    public void setNIC(String NIC) {
        this.NIC = NIC;
    }

    public Boolean getActive() {
        return IsActive;
    }

    public void setActive(Boolean active) {
        IsActive = active;
    }

    public List<String> getTicketBookingIds() {
        return TicketBookingIds;
    }

    public void setTicketBookingIds(List<String> ticketBookingIds) {
        TicketBookingIds = ticketBookingIds;
    }
}
