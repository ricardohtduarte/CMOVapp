package com.cmov.acme.api.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mauro on 25/10/2017.
 */

public class LoginResponse {
    @SerializedName("token")
    private String token;


    public String getToken() {
        return token;
    }

    public LoginResponse(String token) {
        this.token=token;
    }
}
