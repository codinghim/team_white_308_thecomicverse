package com.white.thecomicverse.webapp.database.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;

@Entity // This tells Hibernate to make a table out of this class
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int subscriptionID;

    private int SeriesID;

    private String username;

    private String date;


    public int getSubscriptionID() {
        return this.subscriptionID;
    }

    public void setSubscriptionID(int subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    public int getSeriesID() {
        return this.SeriesID;
    }

    public void setSeriesID(int seriesID) {
        this.SeriesID = seriesID;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}