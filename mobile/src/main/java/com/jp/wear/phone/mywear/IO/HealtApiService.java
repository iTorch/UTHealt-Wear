package com.jp.wear.phone.mywear.IO;

import com.jp.wear.phone.mywear.Body.LoginBody;
import com.jp.wear.phone.mywear.Body.LogoutBody;
import com.jp.wear.phone.mywear.Body.VitalSignsBody;
import com.jp.wear.phone.mywear.Model.Login;
import com.jp.wear.phone.mywear.Model.Logout;
import com.jp.wear.phone.mywear.Model.Persona;
import com.jp.wear.phone.mywear.Model.VitalSigns;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface HealtApiService {
    @GET("personas/")
    Call<ArrayList<Persona>> getPersonas();

    @POST("signos/")
    Call<VitalSigns> registrarSignos(@Body VitalSignsBody vitalSigns);

    @POST(".")
    Call<Login> Login(@Body LoginBody login);

    @POST("logout/")
    Call<Logout> Logout(@Body LogoutBody logout);
}
