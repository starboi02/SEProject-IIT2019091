package com.example.anomalydetector;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
//    private static final int STORAGE_REQUEST = 1889;
    private static final int MY_STORAGE_PERMISSION_CODE = 101;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        nvDrawer = (NavigationView) findViewById(R.id.nav_view);


        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()==null) {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            finish();
        }

        setupDrawerContent(nvDrawer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_STORAGE_PERMISSION_CODE);
            }
        }

//        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
//                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
//                .build();
//        FirebaseModelDownloader.getInstance()
//                .getModel("ECG-Anomaly-Detector", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
//                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
//                    @Override
//                    public void onSuccess(CustomModel model) {
//                        // Download complete. Depending on your app, you could enable the ML
//                        // feature, or switch from the local model to the remote model, etc.
//
//                        // The CustomModel object contains the local path of the model file,
//                        // which you can use to instantiate a TensorFlow Lite interpreter.
//                        File modelFile = model.getFile();
//                        if (modelFile != null) {
//                            interpreter = new Interpreter(modelFile);
//                        }
//                    }
//                });

//        upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,"Report Upload","Uploading...");
//                progressDialog.show();
//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        progressDialog.dismiss();
//                        cross.setVisibility(View.INVISIBLE);
//                        camera.setVisibility(View.VISIBLE);
//                        file.setVisibility(View.VISIBLE);
//                        upload.setVisibility(View.INVISIBLE);
//                        default_txt.setVisibility(View.VISIBLE);
//                        imageView.setImageDrawable(null);
//                        Toast.makeText(MainActivity.this,"Report Uploaded Successfully!",Toast.LENGTH_SHORT).show();
//                    }
//                }, 3000);
//
//            }
//        });

    }


    private void setupDrawerContent(NavigationView navigationView) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new HomeFragment()).commit();
        setTitle("Home");
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_recent:
                fragmentClass = RecentFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                Toast.makeText(MainActivity.this,"Logged out successfully!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                finish();
//            case R.id.nav_feedback:
//                Intent intent = new Intent(Intent.ACTION_SENDTO);
//                intent.setData(Uri.parse("mailto:"));
//                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"varunbhardwaj.064@gmail.com"});
//                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback of AnomalyDetector App");
//                startActivity(intent);
//                break;
        }

        if(fragmentClass==null)
            return;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        //menuItem.setChecked(true);
        // Set action bar title
        if(menuItem.getTitle()!="Feedback")
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==MY_STORAGE_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, "Storage permission denied! You wont be able to upload files.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null) {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            finish();
        }
    }
}
