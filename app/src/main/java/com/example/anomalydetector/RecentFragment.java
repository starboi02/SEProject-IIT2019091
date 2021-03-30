package com.example.anomalydetector;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    TextView txt;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public RecentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentFragment newInstance(String param1, String param2) {
        RecentFragment fragment = new RecentFragment();
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
        View view =inflater.inflate(R.layout.fragment_recent, container, false);
        recyclerView=view.findViewById(R.id.recyclerView);
        txt=view.findViewById(R.id.txt);

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mAuth=FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("recent-reports/"+mAuth.getCurrentUser().getUid());

        databaseReference.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<ECGData> data = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        data.add(child.getValue(ECGData.class));
                    }

                    MyAdapter adapter = new MyAdapter(getContext(), data);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);

                    if(data.size()==0)
                        txt.setVisibility(View.VISIBLE);

                    progressDialog.dismiss();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txt.setVisibility(View.VISIBLE);
            }
        });





        return view;
    }
}