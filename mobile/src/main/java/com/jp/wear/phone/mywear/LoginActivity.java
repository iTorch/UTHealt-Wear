package com.jp.wear.phone.mywear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jp.wear.phone.mywear.Body.LoginBody;
import com.jp.wear.phone.mywear.IO.HealtApiAdapter;
import com.jp.wear.phone.mywear.Model.Login;

import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText txtUser, txtPassword;
    Button btn_ir_login, btn_sesion;
    Login login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtUser = findViewById(R.id.txt_user);
        txtPassword = findViewById(R.id.txt_password);
        //btn_ir_login=findViewById(R.id.btn_register);
        btn_sesion=findViewById(R.id.iniciarS);

        /*
        btn_ir_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });*/

        btn_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginBody body = new LoginBody();
                body.setUsername(txtUser.getText().toString());
                body.setPassword(txtPassword.getText().toString());

                Call<Login> loginCall = HealtApiAdapter.getApiService().Login(body);
                loginCall.enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        if (response.body() != null){
                            login = response.body();
                            if (response.isSuccessful()){
                                if (response.code() == 201){
                                    Toast.makeText(LoginActivity.this,login.getMensaje(),Toast.LENGTH_SHORT).show();
                                    Intent menu = new Intent(LoginActivity.this, MainActivity.class);
                                    menu.putExtra("user", login);
                                    startActivity(menu);
                                }
                            }
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable t) {

                        Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });

    }
}