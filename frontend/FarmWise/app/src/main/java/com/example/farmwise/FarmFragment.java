package com.example.farmwise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.farmwise.PocketSphinxActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FarmFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView statusTextView;

    public FarmFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FarmFragment newInstance(String param1, String param2) {
        FarmFragment fragment = new FarmFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_farm, container, false);


        // Find the TextView by its ID
        statusTextView = view.findViewById(R.id.statusTextView);

        // Find the button by its ID
        Button crop_recognition_button = view.findViewById(R.id.farmbutton);

        crop_recognition_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the PocketSphinxActivity when the button is clicked
                Intent intent = new Intent(requireContext(), PocketSphinxActivity.class);
                startActivity(intent);
            }
        });

        Button upload_button = view.findViewById(R.id.uploadbutton);

        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // upload
                if (isConnectedToInternet()) {
                    List<String> recognizedWordsList = updateRecognizedWordsToBackend();
                    sendToBackend(recognizedWordsList);
//                    clearRecognizedCropsFile();
                } else {
                    showStatusMessage("Please connect to internet first");
                }
            }
        });

        return view;
//        return inflater.inflate(R.layout.fragment_farm, container, false);
    }
    private void showStatusMessage(String message) {
        if (statusTextView != null) {
            statusTextView.setText(message);
            statusTextView.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideStatusMessage();
                }
            }, 1500);
        }
    }
    private void hideStatusMessage() {
        if (statusTextView != null) {
            statusTextView.setVisibility(View.GONE);
        }
    }
    private void sendToBackend(List<String> recognizedWordsList) {
        // Get list of farms
        String reqURL = "https://farmwise.onrender.com/api/inventory/update";
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String activeFarmCode = sharedPreferences.getString("activeFarmCode", "");
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("farmCode", activeFarmCode);
            JSONArray textsArray = new JSONArray(recognizedWordsList);
            requestBody.put("texts", textsArray);
            final String mRequestBody = requestBody.toString();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, reqURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);
                            String status;
                            JSONObject data;
                            try{
                                status = response.getString("status");
                                if (status.equals("SUCCESS")) {
                                    clearRecognizedCropsFile();
                                    showStatusMessage("Upload successful");
                                }
                            }
                            catch (JSONException e) {
                                status = "error parsing JSON";
                                System.out.println(status);
                            } catch (Exception e) {
                                System.out.println("error");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("response error");
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap header = new HashMap();
                    header.put("Content-Type", "application/json");
                    header.put("Cookie", "");
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
            System.out.println("Adding request");
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                return networkCapabilities != null &&
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            }
        }
        return false;
    }
    private List<String> updateRecognizedWordsToBackend() {
        List<String> recognizedWordsList = new ArrayList<>();
        String filename = "recognized_crops.txt";
        File file = new File(getActivity().getExternalFilesDir(null), filename);

        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    recognizedWordsList.add(line);
                    System.out.println(line); // Print each line to the console
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("recognized_words.txt does not exist.");
        }
        System.out.println(recognizedWordsList);
        return recognizedWordsList;
    }
    private void clearRecognizedCropsFile() {
        String filename = "recognized_crops.txt";
        File file = new File(getActivity().getExternalFilesDir(null), filename);

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(""); // Write an empty string to truncate the file
            fileWriter.close();
            System.out.println("Cleared contents of recognized_crops.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
