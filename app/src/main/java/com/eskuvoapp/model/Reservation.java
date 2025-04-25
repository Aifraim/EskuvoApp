package com.eskuvoapp.model;

public class Reservation {
    private String venueId;
    private String venueName;
    private String date;
    private String userEmail;

    public Reservation() {
    }

    public Reservation(String venueId, String venueName, String date, String userEmail) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.date = date;
        this.userEmail = userEmail;
    }

    public String getVenueId() { return venueId; }
    public String getVenueName() { return venueName; }
    public String getDate() { return date; }
    public String getUserEmail() { return userEmail; }
}
