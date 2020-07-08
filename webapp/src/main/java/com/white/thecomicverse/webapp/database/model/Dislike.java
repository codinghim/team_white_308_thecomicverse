package com.white.thecomicverse.webapp.database.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;

@Entity // This tells Hibernate to make a table out of this class
public class Dislike {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int dislikeID;

    private int EpisodeID;

    private String username;

    public int getDislikeID() {
        return dislikeID;
    }

    public void setDislikeID(int dislikeID) {
        this.dislikeID = dislikeID;
    }

    public int getEpisodeID() {
        return EpisodeID;
    }

    public void setEpisodeID(int episodeID) {
        EpisodeID = episodeID;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}