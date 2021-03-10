package com.example.anomalydetector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button btn;
    TextView textView;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent=getIntent();

        String name= intent.getExtras().getString("name");



        btn=findViewById(R.id.logOut);
        textView=findViewById(R.id.name);
        mAuth=FirebaseAuth.getInstance();

        textView.setText("Welcome ");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(MainActivity.this,"Logged out successfully!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                finish();
            }
        });

    }
}
