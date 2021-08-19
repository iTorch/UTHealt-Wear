package com.jp.wear.phone.mywear.Model;

import java.io.Serializable;

public class UserLogin implements Serializable {
    private int id;
    private String username;
    private PersonaLogin persona;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public PersonaLogin getPersona() {
        return persona;
    }
}
