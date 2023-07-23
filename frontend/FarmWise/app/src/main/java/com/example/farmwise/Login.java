package com.example.farmwise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    FirebaseUser user = null;

    Button login;
    TextView register;
    FirebaseAuth mauth;
    RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        TextInputEditText textUsername = findViewById(R.id.loginUsername);
        TextInputEditText textPassword = findViewById(R.id.password);
        login = findViewById(R.id.btn_login);
        TextView registerNow = findViewById(R.id.registerNow);

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username, password;
                username = String.valueOf(textUsername.getText());
                password = String.valueOf(textPassword.getText());


                String registerUrl = "https://farmwise.onrender.com/api/auth/login";

                // Instantiate the cache
//                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
//
//                // Set up the network to use HttpURLConnection as the HTTP client.
//                Network network = new BasicNetwork(new HurlStack());
//
//                // Instantiate the RequestQueue with the cache and network
//                request = new RequestQueue(cache, network);
//
//                // Start the queue
//                request.start();
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put("username", username);
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
                                        Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "User doesn't exist", Toast.LENGTH_SHORT).show();
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
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "");
                        return headers;
                    }
                };

                request = Volley.newRequestQueue(getApplicationContext());
                request.add(jsonObjectRequest);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null) {
            mainActivity();
        }
    }

    private void mainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}