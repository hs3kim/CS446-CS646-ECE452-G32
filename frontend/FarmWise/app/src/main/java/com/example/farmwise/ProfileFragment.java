package com.example.farmwise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.farmwise.databinding.FragmentHomeBinding;
import com.example.farmwise.databinding.FragmentProfileBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentProfileBinding binding;
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        ImageView back_button = view.findViewById(R.id.imageView3);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View v) {
                replaceFragment(new HomeFragment());
            }
        });


        TextView editEmailTextViewInit = view.findViewById(R.id.textemail);

        // Get email
        String reqURL = "https://farmwise.onrender.com/api/user/get";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, reqURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String status;
                        JSONObject data;
                        try{
                            status = response.getString("status");
                            if (status.equals("SUCCESS")) {
                                data = response.getJSONObject("data");
                                editEmailTextViewInit.setText(data.getString("email"));

                            }
                        }
                        catch (JSONException e) {
                            status = "error parsing JSON";
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        farmListText.setText("error");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap header = new HashMap();
                header.put("Content-Type", "application/json");
                header.put("Cookie", sharedPreferences.getString("JWTKey", ""));

                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonObjectRequest);


        ImageView editEmail = view.findViewById(R.id.edit_email);
        ImageView editPassword = view.findViewById(R.id.edit_password);

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                View homeView = binding.getRoot();
//            Snackbar.make(homeView, "hello", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View editEmailView = getLayoutInflater().inflate(R.layout.home_create_dialog, null);
                EditText editEmailText = editEmailView.findViewById(R.id.farmNameEdit);
                editEmailText.setHint("New Email");

                builder.setView(editEmailView);
                builder.setTitle("Edit Email")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String reqURL = "https://farmwise.onrender.com/api/user/update";
                                JSONObject jsonReqBody = new JSONObject();
                                try {
                                    jsonReqBody.put("email", editEmailText.getText().toString());
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final String mRequestBody = jsonReqBody.toString();
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                        (Request.Method.POST, reqURL, null, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                String status;
                                                try {
                                                    status = response.getString("status");
                                                    View homeView = binding.getRoot();
                                                    String snackbarMsg = "";
                                                    if (status.equals("SUCCESS")) {
                                                        // create toast for success
                                                        snackbarMsg = status;
                                                        TextView editEmailTextView= view.findViewById(R.id.textemail);
                                                        editEmailTextView.setText(editEmailText.getText().toString());
                                                    } else {
                                                        snackbarMsg = response.getString("statusMsg");
                                                    }
                                                    Snackbar.make(homeView, snackbarMsg, Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                } catch (JSONException e) {
                                                    status = "error parsing JSON";
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // display error msg
                                            }
                                        }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        HashMap header = new HashMap();
                                        header.put("Content-Type", "application/json");
                                            header.put("Cookie", sharedPreferences.getString("JWTKey", ""));
                                        return header;
                                    }
                                    @Override
                                    public byte[] getBody() {
                                        try {
                                            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                                        }
                                        catch (UnsupportedEncodingException uee) {
                                            return null;
                                        }
                                    }
                                    @Override
                                    public String getBodyContentType() {
                                        return "application/json; charset=utf-8";
                                    }
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                                requestQueue.add(jsonObjectRequest);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled dialog
                            }
                        });
                builder.create().show();
            }
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View editPasswordView = getLayoutInflater().inflate(R.layout.change_pass_dialog, null);
                EditText usernameEdit = editPasswordView.findViewById(R.id.usernameEdit);
                EditText currentPasswordEdit = editPasswordView.findViewById(R.id.currentPasswordEdit);
                EditText newPasswordEdit = editPasswordView.findViewById(R.id.newPasswordEdit);

                builder.setView(editPasswordView);
                builder.setTitle("Edit password")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String reqURL = "https://farmwise.onrender.com/api/auth/change-password";
                                JSONObject jsonReqBody = new JSONObject();
                                try {
                                    jsonReqBody.put("username", usernameEdit.getText().toString());
                                    jsonReqBody.put("currentPassword", currentPasswordEdit.getText().toString());
                                    jsonReqBody.put("newPassword", newPasswordEdit.getText().toString());
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final String mRequestBody = jsonReqBody.toString();
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                        (Request.Method.POST, reqURL, null, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                String status;
                                                try {
                                                    status = response.getString("status");
                                                    View homeView = binding.getRoot();
                                                    String snackbarMsg = "";
                                                    if (status.equals("SUCCESS")) {
                                                        // create toast for success
                                                        snackbarMsg = status;
                                                    } else {
                                                        snackbarMsg = response.getString("statusMsg");
                                                    }
                                                    Snackbar.make(homeView, snackbarMsg, Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                } catch (JSONException e) {
                                                    status = "error parsing JSON";
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // display error msg
                                            }
                                        }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        HashMap header = new HashMap();
                                        header.put("Content-Type", "application/json");
                                        header.put("Cookie", sharedPreferences.getString("JWTKey", ""));
                                        return header;
                                    }
                                    @Override
                                    public byte[] getBody() {
                                        try {
                                            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                                        }
                                        catch (UnsupportedEncodingException uee) {
                                            return null;
                                        }
                                    }
                                    @Override
                                    public String getBodyContentType() {
                                        return "application/json; charset=utf-8";
                                    }
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                                requestQueue.add(jsonObjectRequest);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled dialog
                            }
                        });
                builder.create().show();
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();

    }
}