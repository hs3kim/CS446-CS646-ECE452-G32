package com.example.farmwise;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.farmwise.databinding.FragmentHomeBinding;
import com.example.farmwise.databinding.FragmentPreMarketBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreMarketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreMarketFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PreMarketFragment() {
        // Required empty public constructor
    }

    private FragmentPreMarketBinding binding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PreMarketFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PreMarketFragment newInstance(String param1, String param2) {
        PreMarketFragment fragment = new PreMarketFragment();
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
        binding = FragmentPreMarketBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

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

                                // Only create farm cards for owned farms
                                JSONArray ownedFarms = data.getJSONArray("owns");
                                for (int i = 0; i < ownedFarms.length(); i++){
                                    JSONObject farm = ownedFarms.getJSONObject(i);
                                    String farmCode = farm.getString("code");
                                    String farmName = sharedPreferences.getString(farmCode, farm.getString("name"));
                                    addFarm(farmName, farmCode);
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
                header.put("Cookie", sharedPreferences.getString("JWTKey", ""));

                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonObjectRequest);

        return view;
    }

    private void addFarm(String farmName, String farmCode) {
        View view = getLayoutInflater().inflate(R.layout.home_farm_card, null);

        // Clickable buttons
        CardView card = view.findViewById(R.id.farmCard);

        // Set farm name
        TextView farmNameView = view.findViewById(R.id.farm_name);
        farmNameView.setText(farmName);

        // Set farm code
        TextView farmCodeView = view.findViewById(R.id.farm_code);
        farmCodeView.setText("#" + farmCode);

        //remove delete icons
        view.findViewById(R.id.delete_farm).setVisibility(View.GONE);
        view.findViewById(R.id.leave_farm).setVisibility(View.GONE);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View v) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("activeFarmCode", farmCode);
                editor.commit();

                replaceFragment(new MarketFragment());
            }
        });

        binding.container.addView(view);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();

    }
}