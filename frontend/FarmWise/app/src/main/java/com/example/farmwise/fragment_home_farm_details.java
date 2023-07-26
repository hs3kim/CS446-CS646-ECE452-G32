package com.example.farmwise;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.farmwise.databinding.FragmentHomeFarmDetailsBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_home_farm_details#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_home_farm_details extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_home_farm_details() {
        // Required empty public constructor
    }

    private FragmentHomeFarmDetailsBinding binding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_home_farm_details.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_home_farm_details newInstance(String param1, String param2) {
        fragment_home_farm_details fragment = new fragment_home_farm_details();
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
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String farmCode = sharedPreferences.getString("activeFarmCode", "");

        String reqURL = "https://farmwise.onrender.com/api/inventory/get?farmCode=" + farmCode;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, reqURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String status;
                        JSONObject data;
                        try{
                            status = response.getString("status");
                            if (status.equals("SUCCESS")) {
                                JSONArray items = response.getJSONArray("data");

                                // create item cards for owned cards
                                for (int i = 0; i < items.length(); i++){
                                    JSONObject itemJSON = items.getJSONObject(i);
                                    String product = itemJSON.getString("product");
                                    int count = itemJSON.getInt("count");
                                    addItem(product, count);
                                }
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
//                header.put("Cookie", sharedPreferences.getString("JWTKey", ""));
                header.put("Cookie", "FarmWiseKey=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySUQiOiI2NDkzYmE3ZTE0OTZmYmFjM2U0OTRiMDUiLCJ1c2VybmFtZSI6InRlc3QxIiwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSIsImlhdCI6MTY5MDA3NzM0OH0.BxrHXTrlO2wlgtKjepSErje5a-h9--9dFbTUXNeTPQg");
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeFarmDetailsBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String farmCode = sharedPreferences.getString("activeFarmCode", "");
        String farmName = sharedPreferences.getString(farmCode, "");
        TextView farmNameTextView = view.findViewById(R.id.farmName);
        farmNameTextView.setText(farmName);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add new item", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ImageView back_button = view.findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View v) {
                replaceFragment(new HomeFragment());
            }
        });

        return view;
    }

    private void addItem(String itemName, int itemCount) {
        View view = getLayoutInflater().inflate(R.layout.home_details_item_card, null);

        TextView productNameView = view.findViewById(R.id.item_name);
        productNameView.setText(itemName);
        TextView productCountView = view.findViewById(R.id.item_count);
        productCountView.setText("Count: " + Integer.toString(itemCount));

        binding.container.addView(view);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}