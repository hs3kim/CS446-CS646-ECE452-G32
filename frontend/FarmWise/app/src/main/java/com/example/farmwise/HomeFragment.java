package com.example.farmwise;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.farmwise.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

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

    AlertDialog dialog;

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

        buildDialog();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Clicked on FAB", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                dialog.show();
            }
        });


        return view;
    }

    public void ondDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void buildDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.home_add_dialog, null);

        EditText farmName = view.findViewById(R.id.farmNameEdit);
        EditText farmCode = view.findViewById(R.id.farmCodeEdit);

        builder.setView(view);
        builder.setTitle("Join a farm")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addFarm(farmName.getText().toString(), farmCode.getText().toString());
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
        dialog = builder.create();
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