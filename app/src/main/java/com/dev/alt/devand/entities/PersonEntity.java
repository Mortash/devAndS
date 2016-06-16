package com.dev.alt.devand.entities;

import android.util.Log;

public class PersonEntity {
    //private variables
    private String _login;
    private String _mail;
    private String _socialKey;
    private int _loggedIn;

    // Empty constructor
    public PersonEntity(){
    }

    // constructor
    public PersonEntity(String login, String mail){
        this._login = login;
        this._mail = mail;
    }

    // constructor
    public PersonEntity(String login, String mail, String socialKey, int loggedIn){
        Log.d("create person", login);
        this._login = login;
        this._mail = mail;
        this._socialKey = socialKey;
        this._loggedIn = loggedIn;
    }

    // getting name
    public String getLogin(){
        return this._login;
    }

    // setting name
    public void setLogin(String login){
        this._login = login;
    }

    // getting phone number
    public String getMail(){
        return this._mail;
    }

    // setting phone number
    public void setMail(String mail){
        this._mail = mail;
    }

    // getting phone number
    public String getSocialKey(){
        return this._socialKey;
    }

    // setting phone number
    public void setSocialKey(String socialKey){
        this._socialKey = socialKey;
    }

    // getting phone number
    public int getLoggedIn(){
        return this._loggedIn;
    }

    // setting phone number
    public void setLoggedIn(int loggedIn){
        this._loggedIn = loggedIn;
    }
}
