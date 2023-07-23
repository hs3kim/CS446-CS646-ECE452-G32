package com.example.farmwise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.farmwise.databinding.FragmentHomeFarmDetailsBinding;
import com.google.android.material.snackbar.Snackbar;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeFarmDetailsBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add new item", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        binding.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.itemCardFrame.removeView(binding.inventoryItemCard);
            }
        });

        binding.deleteItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.itemCardFrame.removeView(binding.inventoryItemCard2);
            }
        });

        return view;
    }


}