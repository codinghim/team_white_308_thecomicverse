package com.white.thecomicverse.webapp.database.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity // This tells Hibernate to make a table out of this class
public class Login {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int loginID;

    private String username;

    private String email;

    private String password;

    public int getLoginID(){
        return this.loginID;
    }
    public void setLoginId(int loginID){
        this.loginID = loginID;
    }

    public String getUsername(){
        return this.username;
    }

    public void setusername(String username){
        this.username = username;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

}