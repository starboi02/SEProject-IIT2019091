package com.example.anomalydetector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    Button file,upload;
    LinearLayout linearLayout;
    ImageView image,cross;
    TextView txt,name;
    Uri uri;
    Interpreter interpreter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String fileName;
    FirebaseAuth mAuth;

    private static final int STORAGE_REQUEST = 1889;
    private static final int MY_STORAGE_PERMISSION_CODE = 101;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm ss", Locale.getDefault());

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

        View view =inflater.inflate(R.layout.home_fragment, container, false);

        mAuth=FirebaseAuth.getInstance();

        file=view.findViewById(R.id.files);
        upload=view.findViewById(R.id.upload);
        txt=view.findViewById(R.id.default_txt);
        linearLayout=view.findViewById(R.id.tag);
        image=view.findViewById(R.id.image);
        cross=view.findViewById(R.id.cross);
        name=view.findViewById(R.id.name);

        firebaseDatabase = FirebaseDatabase.getInstance();
        if(mAuth.getCurrentUser()!=null)
        databaseReference=firebaseDatabase.getReference("recent-reports/"+mAuth.getCurrentUser().getUid());

        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel("ECG-Anomaly-Detector", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        // Download complete. Depending on your app, you could enable the ML
                        // feature, or switch from the local model to the remote model, etc.

                        // The CustomModel object contains the local path of the model file,
                        // which you can use to instantiate a TensorFlow Lite interpreter.
                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile);
                        }
                    }
                });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.INVISIBLE);
                file.setVisibility(View.VISIBLE);
                upload.setVisibility(View.INVISIBLE);
                txt.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);
            }
        });

        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_STORAGE_PERMISSION_CODE);
                    }
                    else
                    {
                        Intent intent = new Intent();
                        intent.setType("*/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select CSV"), STORAGE_REQUEST);
                    }
                }


            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bufferSize = 5* Float.SIZE / Byte.SIZE;
                ByteBuffer modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());

                int bufferSizeInput = 140* Float.SIZE / Byte.SIZE;
                ByteBuffer input = ByteBuffer.allocateDirect(bufferSizeInput).order(ByteOrder.nativeOrder());
                float[] rows=new float[140];
                CSVReader dataRead = new CSVReader(getContext(),uri);
                try {
                    rows=dataRead.readCSV();
                    Log.d("input", String.valueOf(rows));
                    for(int i=0;i<rows.length;i++)
                        input.putFloat(rows[i]);

                    interpreter.run(input,modelOutput);

                    modelOutput.rewind();
                    FloatBuffer probabilities = modelOutput.asFloatBuffer();
                    float mx = Float.MIN_VALUE;
                    for (int i=0;i<probabilities.capacity();i++) {
                        Log.d("" + i, "" + probabilities.get(i));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mx=Float.max(mx,probabilities.get(i));
                        }
                    }

                    Intent intent = new Intent(getContext(),ResultActivity.class);
                    intent.putExtra("data",rows);
                    String val="";
                    if(mx==probabilities.get(0))
                        val="Normal";
                    else if(mx==probabilities.get(1))
                        val="R-on-T Premature Ventricular Contraction";
                    else if(mx==probabilities.get(2))
                        val="Premature Ventricular Contraction";
                    else if(mx==probabilities.get(3))
                        val="Supra-ventricular Premature/Ectopic Beat";
                    else
                        val="Unclassified Beat";
                    intent.putExtra("value",val);

                    float[] finalRows = rows;
                    String finalVal = val;

                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    String time= sdf.format(timestamp);

                    databaseReference.child(time).setValue(new ECGData(fileName, getListFromArray(finalRows), finalVal,time));


                    startActivity(intent);


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });



        return view;
    }

    public List<Float> getListFromArray(float[] array){
        List<Float> list = new ArrayList<>();
        for (float v : array) list.add(v);

        return list;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        fileName=result;
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==STORAGE_REQUEST && resultCode== Activity.RESULT_OK){
            uri = data.getData();
            name.setText(getFileName(uri));
            linearLayout.setVisibility(View.VISIBLE);
            file.setVisibility(View.INVISIBLE);
            upload.setVisibility(View.VISIBLE);
            txt.setVisibility(View.INVISIBLE);
            image.setVisibility(View.INVISIBLE);
        }
    }

}

class CSVReader {
    Context context;
    Uri uri;
    float[] rows = new float[140];

    public CSVReader(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }

    public float[] readCSV() throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);
        String line;
        //String csvSplitBy = ",";

        line= br.readLine();

        String[] st = line.split(",");

        for( int i=0 ;i< st.length;i++){
            rows[i]=Float.parseFloat(st[i]);
        }

        return rows;
    }
}