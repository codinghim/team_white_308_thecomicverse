package com.white.thecomicverse.webapp.database.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;

@Entity // This tells Hibernate to make a table out of this class
public class EpisodeImage {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int EpisodeImageID;

    private int EpisodeID;

    private int indices;

    private Blob imageData;

    private String imageString;

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public int getEpisodeImageID() {
        return this.EpisodeImageID;
    }

    public void setEpisodeImageID(int episodeImageID) {
        this.EpisodeImageID = episodeImageID;
    }

    public int getEpisodeID() {
        return this.EpisodeID;
    }

    public void setEpisodeID(int episodeID) {
        this.EpisodeID = episodeID;
    }

    public int getIndices() {return this.indices;}

    public void setIndices(int indices) {
        this.indices = indices;
    }

    public byte[] getImageData(){
        try {
            int length = (int) this.imageData.length();
            return this.imageData.getBytes(1, length);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setImageData(byte[] byteArray){
        try {
            this.imageData = new SerialBlob(byteArray);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }


}