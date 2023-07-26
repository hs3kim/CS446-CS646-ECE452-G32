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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.farmwise.databinding.FragmentMarketBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarketFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MarketFragment() {
        // Required empty public constructor
    }

    private FragmentMarketBinding binding;
    private int selectedItemCount;
    private float selectedItemValue;

    AlertDialog editDialog;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarketFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarketFragment newInstance(String param1, String param2) {
        MarketFragment fragment = new MarketFragment();
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
        binding = FragmentMarketBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String farmCode = sharedPreferences.getString("activeFarmCode", "");
        // Get list of items
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

                                // create item cards
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
                header.put("Cookie", sharedPreferences.getString("JWTKey", ""));

                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(jsonObjectRequest);

        ImageView clear_button = view.findViewById(R.id.clear_button);
        TextView total_revenue = view.findViewById(R.id.revenue);
        total_revenue.setText("Today's Revenue: $" + sharedPreferences.getString("total_revenue", "0"));
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_revenue.setText("Today's Revenue: $0");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("total_revenue", "0");
                editor.commit();
            }
        });

        return view;
    }

    private void addItem(String itemName, int itemCount) {
        View view = getLayoutInflater().inflate(R.layout.market_item_card, null);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        TextView productNameView = view.findViewById(R.id.item_name);
        productNameView.setText(itemName);
        TextView productCountView = view.findViewById(R.id.item_count);
        productCountView.setText("Available: " + Integer.toString(itemCount));
        TextView productValueView = view.findViewById(R.id.item_value);
        productValueView.setText("item value: $" + sharedPreferences.getString("value_" + itemName, "0"));

        // buttons
        ImageView editButtonCount = view.findViewById(R.id.edit_button_count);
        ImageView editButtonValue = view.findViewById(R.id.edit_button_value);
        editButtonCount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View item_edit_view = getLayoutInflater().inflate(R.layout.market_item_edit_dialog, null);

                EditText itemsSoldText = item_edit_view.findViewById(R.id.itemValueEdit);
                itemsSoldText.setHint("Number sold");

                builder.setView(item_edit_view);
                builder.setTitle("Enter Number of " + itemName + " Sold")
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText itemCountTextFinal = item_edit_view.findViewById(R.id.itemValueEdit);
                                String itemsSoldTextFinalStr = itemCountTextFinal.getText().toString();

                                List<String> itemsSoldList = new ArrayList<String>();
                                itemsSoldList.add("sold " + itemsSoldTextFinalStr + " " + itemName);
                                JSONArray textsArray = new JSONArray(itemsSoldList);

                                String reqURL = "https://farmwise.onrender.com/api/inventory/update";
                                JSONObject jsonReqBody = new JSONObject();
                                try {
                                    jsonReqBody.put("farmCode", sharedPreferences.getString("activeFarmCode", ""));
                                    jsonReqBody.put("texts", textsArray);
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                        (Request.Method.POST, reqURL, jsonReqBody, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {


                                                String status;
                                                try {
                                                    status = response.getString("status");
                                                    View homeView = binding.getRoot();
                                                    String snackbarMsg = "";
                                                    if (status.equals("SUCCESS")) {
                                                        // create toast for successs
                                                        snackbarMsg = status;
                                                        int newCount = itemCount - Integer.parseInt(itemsSoldTextFinalStr);
                                                        productCountView.setText("Available: " + Integer.toString(newCount));

                                                        TextView total_revenue = homeView.findViewById(R.id.revenue);
                                                        float current_revenue_value = Float.parseFloat(sharedPreferences.getString("total_revenue", "0"));
                                                        float item_value = Float.parseFloat(sharedPreferences.getString("value_" + itemName, "0"));
                                                        float append_revenue_value = (Integer.parseInt(itemsSoldTextFinalStr))*item_value;
                                                        float total_revenue_value = current_revenue_value + append_revenue_value;

                                                        total_revenue.setText("Today's Revenue: $" + String.valueOf(total_revenue_value));

                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("total_revenue", String.valueOf(total_revenue_value));
                                                        editor.commit();

                                                    } else {
                                                        snackbarMsg = response.getString("status");
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
                                    public String getBodyContentType() {
                                        return "application/json; charset=utf-8";
                                    }
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                                requestQueue.add(jsonObjectRequest);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.create().show();
            }

        });

        editButtonValue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View item_edit_view = getLayoutInflater().inflate(R.layout.market_item_edit_dialog, null);

                selectedItemValue = Float.parseFloat(sharedPreferences.getString("value_" + itemName, "0"));
                EditText itemValueText = item_edit_view.findViewById(R.id.itemValueEdit);
                itemValueText.setText(Float.toString(selectedItemValue));

                builder.setView(item_edit_view);
                builder.setTitle("Modify Item Value")
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                EditText itemValueTextFinal = item_edit_view.findViewById(R.id.itemValueEdit);
                                String itemValueTextFinalStr = itemValueTextFinal.getText().toString();
                                editor.putString("value_" + itemName, itemValueTextFinalStr);
                                editor.commit();
                                productValueView.setText("item value: $" + itemValueTextFinalStr);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.create().show();

            }
        });


        binding.container.addView(view);
    }
    private void buildEditDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.market_item_edit_dialog, null);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        EditText itemValueEdit = view.findViewById(R.id.itemValueEdit);

        builder.setView(view);
        builder.setTitle("Modify Value / Count")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strItemValue = itemValueEdit.getText().toString();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        editDialog = builder.create();
    }
}