package com.dev.alt.devand.entities;

import android.util.Log;

public class PictureEntity {

    //private variables
    private int _idpicture;
    private int _idwalk;
    private String _takendate;
    private boolean _autolocation;
    private String _pathpicture;
    private double _latitude;
    private double _longitude;
    private String _login;

    // Empty constructor
    public PictureEntity(){
    }

    // constructor
    public PictureEntity(int idpicture, int idwalk, String takendate, boolean autolocation, String pathpicture, double latitude, double longitude, String login){
        this._idpicture = idpicture;
        this._idwalk = idwalk;
        this._takendate = takendate;
        this._autolocation = autolocation;
        this._pathpicture = pathpicture;
        this._latitude = latitude;
        this._longitude = longitude;
        this._login = login;
    }

    public int getIdPicture() {
        return _idpicture;
    }
    public void set_idpicture(int idPicture) {
        this._idpicture = idPicture;
    }

    public int getIdWalk() {
        return _idwalk;
    }
    public void setIdWalk(int idWalk) {
        this._idwalk = idWalk;
    }

    public String getTakendate() {
        return _takendate;
    }
    public void setString(String takendate) {
        this._takendate = takendate;
    }

    public boolean getAutolocation() {
        return _autolocation;
    }
    public void set(boolean autolocation) {
        this._autolocation = autolocation;
    }

    public String getPathPicture() {
        return _pathpicture;
    }
    public void setPathPicture(String pathpicture) {
        this._pathpicture = pathpicture;
    }

    public double getLatitude() {
        return _latitude;
    }
    public void setLatitude(double latitude) {
        this._latitude = latitude;
    }

    public double getLongitude() {
        return _longitude;
    }
    public void setLongitude(double longitude) {
        this._longitude = longitude;
    }

    public String getLogin() {
        return _login;
    }
    public void setLogin(String login) {
        this._login = login;
    }
}
