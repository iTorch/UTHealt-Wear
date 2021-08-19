package com.jp.wear.phone.mywear.Model;

import java.io.Serializable;
import java.util.Objects;

public class Login implements Serializable {
    String token;
    UserLogin user;
    String mensaje;
    String error;

    public String getToken() {
        return token;
    }

    public UserLogin getUser() {
        return user;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getError() {
        return error;
    }

}
