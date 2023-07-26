package com.example.farmwise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextInputEditText textUsername, textEmail, textPassword, textPassword2;
    Button register;
    RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        textUsername = findViewById(R.id.username);
        textEmail = findViewById(R.id.email);
        textPassword = findViewById(R.id.password);
        textPassword2 = findViewById(R.id.password2);

        register = findViewById(R.id.btn_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username, email, password, password2;
                username = String.valueOf(textUsername.getText());
                email = String.valueOf(textEmail.getText());
                password = String.valueOf(textPassword.getText());
                password2 = String.valueOf(textPassword2.getText());
                TextInputLayout usernameLayout = findViewById(R.id.usernameLayout);
                TextInputLayout emailLayout = findViewById(R.id.emailLayout);
                TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);
                TextInputLayout password2Layout = findViewById(R.id.password2Layout);

                if (TextUtils.isEmpty(username)) {
                    usernameLayout.setError("Username cannot be empty");
                    return;
                }
                usernameLayout.setError(null);
                if (TextUtils.isEmpty(email)) {
                    emailLayout.setError("Email cannot be empty");
                    return;
                }
                emailLayout.setError(null);
                if (TextUtils.isEmpty(password)) {
                    passwordLayout.setError("Password cannot be empty");
                    return;
                }
                passwordLayout.setError(null);
                if (!password.equals(password2)) {
                    password2Layout.setError("password doesn't match");
                    return;
                }
                password2Layout.setError(null);

                // create new user
                String registerUrl = "https://farmwise.onrender.com/api/auth/register";

                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("username", username);
                    jsonData.put("email", email);
                    jsonData.put("password", password);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, registerUrl, jsonData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String status = response.getString("status");
                                    if (status.equals("SUCCESS")){
                                        Toast.makeText(getApplicationContext(), "Registration Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
//                                int statusCode = error.networkResponse.statusCode;
//                                String errorMessage = new String(error.networkResponse.data);
//                                // You can log or display the error message here
//                                Log.e("Volley Error", "Status Code: " + statusCode + ", Error Message: " + errorMessage);

                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
//                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", "");
                        return headers;
                    }

                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        assert response.headers != null;
                        String JWT = response.headers.get("jwtauthtoken");
//                        Log.d("JWT", " :" + JWT);
                        // Use code to set JWT token value to file
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        // set value here as JWT token retrieved
                        editor.putString("JWTKey", JWT);
                        editor.apply();

                        return super.parseNetworkResponse(response);
                    }
                };

                request = Volley.newRequestQueue(getApplicationContext());
                request.add(jsonObjectRequest);

            }
        });
    }

}