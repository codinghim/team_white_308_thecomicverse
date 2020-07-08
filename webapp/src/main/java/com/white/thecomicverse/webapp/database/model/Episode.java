package com.white.thecomicverse.webapp.database.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;

@Entity // This tells Hibernate to make a table out of this class
public class Episode {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int EpisodeID;

    private int SeriesID;

    private String EpisodeName;

    private String DateCreated;

    private int indices;

    private int numView;

    private int numLikes;

    private int numDislikes;

    private Blob thumbnail;

    private String imageData;

    private boolean canDerive;

    public boolean getCanDerive() {
        return canDerive;
    }

    public void setCanDerive(boolean canDerive) {
        this.canDerive = canDerive;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public int getEpisodeID(){
        return this.EpisodeID;
    }

    public void setEpisodeID(int EpisodeID){
        this.EpisodeID = EpisodeID;
    }

    public int getSeriesID() {
        return this.SeriesID;
    }

    public void setSeriesID(int seriesID) {
        this.SeriesID = seriesID;
    }

    public String getEpisodeName() {
        return this.EpisodeName;
    }

    public void setEpisodeName(String episodeName) {
        this.EpisodeName = episodeName;
    }

    public String getDateCreated() {
        return this.DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.DateCreated = dateCreated;
    }

    public int getNumView() {
        return this.numView;
    }

    public void setNumView(int numView) {
        this.numView = numView;
    }

    public int getNumLikes() {
        return this.numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public int getNumDislikes() {
        return this.numDislikes;
    }

    public void setNumDislikes(int numDislikes) {
        this.numDislikes = numDislikes;
    }

    public void setIndices(int indices) {this.indices = indices;}

    public int getIndices(){return this.indices;}

    public byte[] getThumbnail(){

        try {

            int length = (int) this.thumbnail.length();

            return this.thumbnail.getBytes(1, length);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void setThumbnail(byte[] byteArray){

        try {
            this.thumbnail = new SerialBlob(byteArray);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }


}