package com.example.farmwise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
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
import com.example.farmwise.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentHomeBinding binding;

    AlertDialog joinDialog;
    AlertDialog createDialog;
    AlertDialog choiceDialog;

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
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();


        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("10005", "FarmNameInSharedPref");
//        editor.commit();

        // Get list of farms
        String reqURL = "https://farmwise.onrender.com/api/user/get";

        TextView farmListText = view.findViewById(R.id.farmList);
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

                                // create farm cards for owned farms
                                JSONArray ownedFarms = data.getJSONArray("owns");
                                for (int i = 0; i < ownedFarms.length(); i++){
                                    JSONObject farm = ownedFarms.getJSONObject(i);
                                    String farmCode = farm.getString("code");
                                    String farmName = sharedPreferences.getString(farmCode, farm.getString("name"));
                                    addFarm(farmName, farmCode);
                                }

                                // create farm cards for farms working at
                                JSONArray worksAtFarms = data.getJSONArray("worksAt");
                                for (int i = 0; i < worksAtFarms.length(); i++){
                                    JSONObject farm = worksAtFarms.getJSONObject(i);
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

        buildChoiceDialog();
        buildCreateDialog();
        buildJoinDialog();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Clicked on FAB", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                joinDialog.show();
                choiceDialog.show();
            }
        });


        return view;
    }

    public void ondDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void buildJoinDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.home_add_dialog, null);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        EditText farmName = view.findViewById(R.id.farmNameEdit);
        EditText farmCode = view.findViewById(R.id.farmCodeEdit);

        builder.setView(view);
        builder.setTitle("Join a farm")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addFarm(farmName.getText().toString(), farmCode.getText().toString());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(farmCode.getText().toString(),farmName.getText().toString());
                        editor.commit();

                        farmName.setText("");
                        farmCode.setText("");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        farmName.setText("");
                        farmCode.setText("");
                    }
                });
        joinDialog = builder.create();
    }

    private void buildCreateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.home_create_dialog, null);


        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);


        builder.setView(view);
        builder.setTitle("Create a farm")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String reqURL = "https://farmwise.onrender.com/api/farm/new";
                        JSONObject jsonReqBody = new JSONObject();
                        EditText farmName = view.findViewById(R.id.farmNameEdit);
                        try{
                            jsonReqBody.put("name", farmName.getText().toString());
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
                                            if (status.equals("SUCCESS")) {
                                                JSONObject data = response.getJSONObject("data");
                                                String retFarmCode = data.getString("code");
                                                String retFarmName = data.getString("name");

                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString(retFarmCode, retFarmName);
                                                editor.commit();

                                                addFarm(retFarmName, retFarmCode);
                                            }
                                        }
                                        catch (JSONException e) {
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
                        farmName.setText("");

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText farmName = view.findViewById(R.id.farmNameEdit);
                        farmName.setText("");
                    }
                });

        createDialog = builder.create();
    }

    private void buildChoiceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.home_choice_dialog, null);
        String[] choices = {"Create a farm", "Join a farm"};
        builder.setTitle("Join / Create a Farm")
                .setItems(choices, new DialogInterface.OnClickListener(){
                   public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            // create farm selected
                            createDialog.show();
                        } else if (which == 1){
                            joinDialog.show();
                        }
                   }
                });

        choiceDialog = builder.create();
    }

    private void addFarm(String farmName, String farmCode) {
        View view = getLayoutInflater().inflate(R.layout.home_farm_card, null);

        // Clickable buttons
        ImageButton delete = view.findViewById(R.id.delete_farm);
        CardView card = view.findViewById(R.id.farmCard);

        // Set farm name
        TextView farmNameView = view.findViewById(R.id.farm_name);
        farmNameView.setText(farmName);

        // Set farm code
        TextView farmCodeView = view.findViewById(R.id.farm_code);
        farmCodeView.setText("#" + farmCode);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.container.removeView(view);
            }
        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View v) {
                Snackbar.make(view, "View " + farmName, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

                replaceFragment(new fragment_home_farm_details());
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